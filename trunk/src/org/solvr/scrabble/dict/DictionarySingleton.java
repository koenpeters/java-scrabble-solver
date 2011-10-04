package org.solvr.scrabble.dict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.solvr.scrabble.config.ConfigListener;
import org.solvr.scrabble.util.ParamValidationUtil;
import org.solvr.scrabble.util.TimingSingleton;

/**
 * @author Koen Peters, Cubix Concepts
 */
public class DictionarySingleton  {
	private Map<String, DictionaryNode> dictionaries = new HashMap<String, DictionaryNode>();
	private static Logger log = Logger.getLogger(DictionarySingleton.class);
	
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
	
	public DictionaryNode getDictionarySubsetOfPrefix(String tail, DictionaryNode context) {
		ParamValidationUtil.validateParamNotNull(tail, "tail");
		ParamValidationUtil.validateParamNotNull(context, "context");
		
		return getPrefixFromDictionary(tail, context, 0);
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
				return pathname.isFile();
			}
		});
		
		for (File dictionaryFile: dictionaryFiles) {
			
			DictionaryNode dictionary = readOneDictionaryFileFromDisk(dictionaryFile);
			String language = FilenameUtils.removeExtension(dictionaryFile.getName());
			dictionaries.put(language, dictionary);
		}
	}
	
	private DictionaryNode readOneDictionaryFileFromDisk(File dictionaryFile) {
		if (!dictionaryFile.exists() || !dictionaryFile.canRead()) {
			throw new RuntimeException("DIctionaryfile " + dictionaryFile.getAbsolutePath() + " cannot be read.");
		}
		
		int timingId = 1;
		String timingGroup = "dict";
		TimingSingleton timing = TimingSingleton.getInstance();
		timing.reset(timingGroup, timingId);
		timing.start(timingGroup, timingId);
		
		DictionaryNode dictionary = new DictionaryNode();
		
		int nrOfWords = 0;
		
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
							line = line.trim().toLowerCase();
							if (line.length() > 0) {
								addWordToDictionary(line, dictionary, 0);
								nrOfWords++;
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
		
		timing.stop(timingGroup, timingId);
		log.info("Read " + dictionaryFile.getName() + ", containing " + nrOfWords + " words in " + timing.getTime(timingGroup, timingId)+ " msec.");
		
		return dictionary;
	}
	
	private void addWordToDictionary(String word, DictionaryNode context, int pointer) {
		if (pointer < word.length()) {
			char letter = word.charAt(pointer);
			DictionaryNode newContext = context.getChild(letter);
			if (newContext == null) {
				newContext = new DictionaryNode();
				context.setDictionaryNode(letter, newContext);
			}
			addWordToDictionary(word, newContext, pointer + 1);
		} else {
			context.markAsWord();
		}
	}

	private DictionaryNode getPrefixFromDictionary(String word, DictionaryNode context, int pointer) {
		if (pointer < word.length()) {
			char letter = word.charAt(pointer);
			DictionaryNode newContext = context.getChild(letter);
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