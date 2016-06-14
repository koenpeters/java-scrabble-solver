package nl.cubix.scrabble.boardimporter.extracter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import nl.cubix.scrabble.boardimporter.GameDetector.TemplateType;
import nl.cubix.scrabble.config.ConfigListener;
import nl.cubix.scrabble.solver.datastructures.Board;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

public class ExtracterTest {

	Logger log = Logger.getLogger(ExtracterTest.class);
	List<TestSet> testSets = new ArrayList<TestSet>();
	Extracter boardExtracter = new Extracter();
	
	@Test
	public void extract() {
		for (TestSet testSet: testSets) {
			log.info("testing " + testSet.image.getName());
			assert testSet.extractedImage.equals(boardExtracter.extract(testSet.image, testSet.templateType));
		}
	}
  
	@BeforeClass
	public void setUp() {
		// Determine the location of all the test data.
		ConfigListener configListener = new ConfigListener();
		configListener.contextInitialized(null);
		String testDataFolder = ConfigListener.getConfiguration().getTrainingImagesFolder();
		File testDir = new File(testDataFolder);
		
		// get all the testimages
		File[] images = testDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".jpg") || file.getName().endsWith(".gif");
			}
		});
		
		// create all the TestSet objects that contain the image and the expected result
		for (File image: images) {
			TestSet testSet = new TestSet();
			
			Board board = new Board(4);
			String tray = "";

			testSet.image = image;
			testSet.extractedImage = new ExtractedImage(board, tray);
			testSets.add(testSet);
		}
  }
  
  private class TestSet {
	  public File image;
	  public ExtractedImage extractedImage;
	  public TemplateType templateType;
  }
}
