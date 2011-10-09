package nl.cubix.scrabble.boardimporter.template;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import nl.cubix.scrabble.solver.config.ConfigListener;
import nl.cubix.scrabble.solver.util.TimingSingleton;

import org.apache.log4j.Logger;

/**
 * @author Koen Peters, Cubix Concepts
 */
public class TemplateSingleton  {
	private Map<TemplateType, TemplateSet> templates = new HashMap<TemplateType, TemplateSet>();
	private static Logger log = Logger.getLogger(TemplateSingleton.class);
	
	// Private constructor prevents instantiation from other classes
	private TemplateSingleton() {
		readAllTemplatesFromDisk();
	}
	
	/**
	* SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	* or the first access to SingletonHolder.INSTANCE, not before.
	*/
	private static class SingletonHolder { 
	  public static final TemplateSingleton instance = new TemplateSingleton();
	}
 
	public static TemplateSingleton getInstance() {
	  return SingletonHolder.instance;
	}

	public static void freeUpSpace() {
		log.info("destroying templates");
		SingletonHolder.instance.templates = null;
	}
	
		
	/* ************************ */
	/*		 PRIVATE METHODS	 */
	/* ************************ */
	
	private void readAllTemplatesFromDisk() {
		String templatesFolderStr = ConfigListener.getConfiguration().getTemplatesFolder();
		
		final File templatesFolder = new File(templatesFolderStr);
		File[] gameTypeFolders = getDirectories(templatesFolder);
		
		for (File gameTypeFolder: gameTypeFolders) {
			
			File[] boardSizeFolders = getDirectories(gameTypeFolder);
			for (File boardSizeFolder: boardSizeFolders) {
				
				try {
					int boardSize = Integer.parseInt(boardSizeFolder.getName());
					TemplateType templateType = new TemplateType(gameTypeFolder.getName(), boardSize);
					TemplateSet templateSet = readOneTemplateSetFromDisk(boardSizeFolder, templateType);
					
					if (templateSet.areAllValuesSet()) {
						templates.put(templateType, templateSet);
					} else {
						throw new RuntimeException("Not all templates are set for templateType " + templateType);
					}
					
				} catch (NumberFormatException e) {
					throw new RuntimeException("Error reading templates. The directoryname " 
							+ boardSizeFolder.getName() + " cannot be parsed as a number. It should be the width"
							+ " of the screen in pixels these templates are created for.", e);
				}
				
			}
		}

	}
	
	private TemplateSet readOneTemplateSetFromDisk(File templateSetDirectory, TemplateType templateType) {
		TemplateSet result = new TemplateSet(templateType);

		int timingId = 0;
		String timingGroup = "templates";
		TimingSingleton timing = TimingSingleton.getInstance();
		timing.reset(timingGroup, timingId);
		timing.start(timingGroup, timingId);
		
		File[] templateSetSubDirectories = getDirectories(templateSetDirectory);
		for (File templateSetSubDirectory: templateSetSubDirectories) {

			String dirName = templateSetSubDirectory.getName().toLowerCase();
			
			File[] templateFiles = getFiles(templateSetSubDirectory);
			for (File templateFile: templateFiles) {
				
				if (dirName.equals(TemplateSetPart.BOARD_LETTERS_DIRECTORY_NAME.getDirectoryName())) {
					result.addBoardLetter(templateFile);
				} else if (dirName.equals(TemplateSetPart.BOARD_TILES_DIRECTORY_NAME.getDirectoryName())) {
					result.addBoardTile(templateFile);
				} else if (dirName.equals(TemplateSetPart.TRAY_LETTERS_DIRECTORY_NAME.getDirectoryName())) {
					result.addTrayLetter(templateFile);
				} 
				
			}
		}

		timing.stop(timingGroup, timingId);
		log.info("Read " + templateType + ", in " + timing.getTime(timingGroup, timingId)+ " msec.");
		
		return result;
	}
	
	private File[] getDirectories(File folder) {
		return folder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
	}

	private File[] getFiles(File folder) {
		return folder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		});
	}
	
	private enum TemplateSetPart {
		BOARD_LETTERS_DIRECTORY_NAME("boardletters")
		,BOARD_TILES_DIRECTORY_NAME("boardtiles")
		,TRAY_LETTERS_DIRECTORY_NAME("trayletters");
		
		private String directoryName;
		private TemplateSetPart(String directoyName) {
			this.directoryName = directoyName;
		}
		
		public String getDirectoryName() {
			return directoryName;
		}
	}

}