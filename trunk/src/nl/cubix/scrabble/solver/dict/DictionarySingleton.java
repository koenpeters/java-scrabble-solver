package nl.cubix.scrabble.solver.dict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.cubix.scrabble.config.ConfigListener;
import nl.cubix.scrabble.solver.dict.cleaner.Cleanable;
import nl.cubix.scrabble.solver.dict.cleaner.CleanerType;
import nl.cubix.scrabble.solver.dict.node.DictionaryNode;
import nl.cubix.scrabble.solver.dict.node.DictionaryNodeType;
import nl.cubix.scrabble.util.ParamValidationUtil;
import nl.cubix.scrabble.util.TimingSingleton;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.javamex.classmexer.MemoryUtil;

/**
 * @author Koen Peters, Cubix Concepts
 */
public class DictionarySingleton  {
	private Map <String, DictionaryNode> dictionaries = new HashMap<String, DictionaryNode>();
	private static Logger log = Logger.getLogger(DictionarySingleton.class);
	private static final String ADDITIONAL_WORDS_FILENAME_ADDITION = "-ext";
	private static final DictionaryNodeType dictionaryNodeType = ConfigListener.getConfiguration().getDictionaryNodeType();
	
	// Private constructor prevents instantiation from other classes
	private DictionarySingleton() {
		readAllDictionariesFromDisk();
	}
	
	/**
	* SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	* or the first access to SingletonHolder.INSTANCE, not before.
	*/
	private static class SingletonHolder { 
	  public static final DictionarySingleton instance = new DictionarySingleton();
	}
 
	public static DictionarySingleton getInstance() {
	  return SingletonHolder.instance;
	}

	public static void freeUpSpace() {
		log.info("destroying dictionaries");
		SingletonHolder.instance.dictionaries = null;
	}
	
	public DictionaryNode getDictionary(String language) {
		if (!dictionaries.keySet().contains(language)) {
			throw new IllegalArgumentException("Unknown dictionary " + language + ". Possible dictionary are: " + getAllDictionaryNames());
		}
		return dictionaries.get(language);
	}
	
	public List<String> getDictionaryNames() {
		List<String> result = new ArrayList<String>();
		result.addAll(dictionaries.keySet());
		Collections.sort(result);
		return result;
	}
	
	public DictionaryNode getDictionarySubsetOfPrefix(String prefix, DictionaryNode context) {
		ParamValidationUtil.validateParamNotNull(prefix, "prefix");
		ParamValidationUtil.validateParamNotNull(context, "context");
		
		return getPrefixFromDictionary(prefix, context, 0);
	}
	
	public boolean exists(String word, DictionaryNode context) {
		DictionaryNode prefix = getDictionarySubsetOfPrefix(word, context);
		
		return prefix != null && prefix.isWord();
	}
	
	/* ************************ */
	/*		 PRIVATE METHODS	 */
	/* ************************ */
	
	private void readAllDictionariesFromDisk() {
		String dictionaryFolderStr = ConfigListener.getConfiguration().getDictionariesFolder();
		
		final File dictionaryFolder = new File(dictionaryFolderStr);
			
		File[] dictionaryFiles = dictionaryFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String fileNameWithoutExtension = FilenameUtils.removeExtension(pathname.getName());
				return pathname.isFile() && !fileNameWithoutExtension.endsWith(ADDITIONAL_WORDS_FILENAME_ADDITION);
			}
		});
		
		for (File dictionaryFile: dictionaryFiles) {
			DictionaryNode dictionary = dictionaryNodeType.getRootInstance();

			int timingId = 1;
			TimingSingleton timing = TimingSingleton.getInstance();
			timing.reset(this, timingId);
			timing.start(this, timingId);
			
			// Read the main dictionary file
			readOneDictionaryFileFromDisk(dictionaryFile, dictionary, false);

			// Check if there is an additional file with additions to this main dictionary file
			String path = dictionaryFile.getAbsolutePath();
			int extensionIndex = FilenameUtils.indexOfExtension(path);
			File additonalWords = new File(path.substring(0, extensionIndex) + ADDITIONAL_WORDS_FILENAME_ADDITION + path.substring(extensionIndex));
			if (additonalWords.exists()) {
				readOneDictionaryFileFromDisk(additonalWords, dictionary, true);
			}

			// We're done creating the whole dictionary. We can now optimize it internally
			// because we know nothing will we added to it anymore.
			dictionary.postFillOptimize();

			timing.stop(this, timingId);
			
			String memoryFootprint = "";
			if (ConfigListener.getConfiguration().getShowMemoryFootprint()) {
				try {
					memoryFootprint = ", using " + MemoryUtil.deepMemoryUsageOf(dictionary) + " bytes of memory";
				} catch (Exception e) {
					log.info("Error determining the memory footprint of dictionary " + dictionaryFile.getName() + "." + 
							" Are you sure you added the -javaagent:c:\\path\\to\\classmexer.jar to the tomcat VM arguments?");
				}
			}
			
			log.info("Read " + dictionaryFile.getName() + ", containing " + dictionary.getNrOfWords() + " words and " + dictionary.getNrOfNodes() 
					+ " nodes in " + timing.getTime(this, timingId) + " msec" + memoryFootprint);
			
			
			// The result of applying both files to the same dictionary is now stored in the list of available dictionaries.
			String fileNameWithoutExtension = FilenameUtils.removeExtension(dictionaryFile.getName());
			dictionaries.put(fileNameWithoutExtension, dictionary);
		}
	}
	
	
	
	private DictionaryNode readOneDictionaryFileFromDisk(File dictionaryFile, DictionaryNode dictionary, boolean isAdditional) {
		if (!dictionaryFile.exists() || !dictionaryFile.canRead()) {
			throw new RuntimeException("Dictionaryfile " + dictionaryFile.getAbsolutePath() + " cannot be read.");
		}
		
		
		// Extract the language code from the filename. It is the first part before 
		// the first "-" character. Use the code to determine what type of cleaner
		// we need for this dictionary
		String[] parts = dictionaryFile.getName().split("-");
		CleanerType cleanerType = CleanerType.valueOf(parts[0].toUpperCase());
		Cleanable cleaner = cleanerType.getInstance();
		
		InputStream is;
		try {
			is = new FileInputStream(dictionaryFile);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				try {
					String line = "";
					while (line != null) {
						line = br.readLine();
						if (line != null) {
							line = line.trim();
								
							if (isAdditional && line.startsWith("-")) {
								// if we are processing the additional file and the word starts with a minus (-)
								// then we need to remove the word form the dictionary. 
								removeWordFromDictionary(line.substring(1), dictionary, 0, true, cleaner);
							} else {
								addWordToDictionary(line, dictionary, 0, true, cleaner, 0);
							}
						}
					}
					
				} finally {
					br.close();
				}
				
			} finally {
				is.close();
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return dictionary;
	}
	
	/*
	 * @return true if word was added to context. False otherwise
	 */
	private boolean addWordToDictionary(String word, DictionaryNode context, int pointer, boolean isAllCaps, Cleanable cleaner, int nettLength) {
		if (pointer < word.length()) {
			
			char letter = word.charAt(pointer);
			
			// We skip words that are in all caps, because they are 
			// abbreviations and cannot be used as normal words
			isAllCaps = isAllCaps && (letter >= 'A' && letter <= 'Z');
			
			// Fix non a-z chars
			letter = cleaner.cleanup(letter);
			
			if (letter == Cleanable.SKIP_THIS_WORD) {
				return false;
			}
			if (letter == Cleanable.SKIP_THIS_CHARACTER) {
				// This is a character that will be skipped in this language. 
				// Go straight to the next one.
				return addWordToDictionary(word, context, pointer + 1, isAllCaps, cleaner, nettLength);
				
			} else {
				
				// We found a valid letter. Add it to the dictionary
				DictionaryNode newContext = context.getSubNode(letter);
				if (newContext == null) {
					newContext = dictionaryNodeType.getInstance(context, letter);
				}
				if (!addWordToDictionary(word, newContext, pointer + 1, isAllCaps, cleaner, nettLength + 1)) {
					return false;
				}
				context.setSubNode(letter, newContext);
			}
		} else if (isAllCaps) {
			return false;
		} else {
			// We only add words of which at least two characters have been added to the dictionary
			if (nettLength > 1) {
				context.isWord(true);
			}
		}
		return true;
	}

	/*
	 * @return true if 'context' is empty (no children and is not a word itself. False otherwise
	 */
	private boolean removeWordFromDictionary(String word, DictionaryNode context, int pointer, boolean isAllCaps, Cleanable cleaner) {
		if (pointer < word.length()) {
			
			char letter = word.charAt(pointer);
			isAllCaps = isAllCaps && (letter >= 'A' && letter <= 'Z');
			
			letter = cleaner.cleanup(letter);
			
			if (letter == Cleanable.SKIP_THIS_WORD) {
				return false;
			}
			if (letter == Cleanable.SKIP_THIS_CHARACTER) {
				// This is a character that will be skipped in this language. 
				// Go straight to the next one.
				return removeWordFromDictionary(word, context, pointer + 1, isAllCaps, cleaner);
				
			} else {
				// We found a valid letter. Add it to the dictionary
				DictionaryNode newContext = context.getSubNode(letter);
				if (newContext != null) {
					
					if (removeWordFromDictionary(word, newContext, pointer + 1, isAllCaps, cleaner)) {
						// newContext is empty. It may be destroyed
						context.removeSubNode(letter);
						
						if (context.hasNoSubNodes() && !context.isWord() ) {
							// The current node (context) is also empty. It may be destroyed 
							// as well by its parent
							return true;
						}
					} 
				}
			}
		} else if (isAllCaps) {
			return false;
		} else {
			if (context.hasNoSubNodes()) {
				// The current node (context) is also empty. It may be destroyed 
				// as well by its parent
				return true;
			}
			context.isWord(false);
		}
		return false;
	}

	private DictionaryNode getPrefixFromDictionary(String word, DictionaryNode context, int pointer) {
		if (pointer < word.length()) {
			char letter = word.charAt(pointer);
			DictionaryNode newContext = context.getSubNode(letter);
			if (newContext == null) {
				return null;
			}
			return getPrefixFromDictionary(word, newContext, pointer + 1);
		} else {
			return context;
		}
	}
	
	private String getAllDictionaryNames() {
		StringBuilder result = new StringBuilder();
		for (String key: dictionaries.keySet()) {
			result
				.append(result.length() == 0? "": ", ")
				.append(key);
		}
		return result.toString();
	}
}