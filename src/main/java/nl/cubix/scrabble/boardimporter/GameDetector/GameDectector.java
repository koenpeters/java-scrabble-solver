package nl.cubix.scrabble.boardimporter.GameDetector;

import ij.IJ;
import ij.ImagePlus;

import java.io.File;

public class GameDectector {

	public TemplateType detect(File imageOfBoard, String deviceType, String language) {
		
		ImagePlus imagePlus = IJ.openImage(imageOfBoard.getAbsolutePath());
		
		int width = imagePlus.getWidth();
		int height = imagePlus.getHeight();

		return new TemplateType(language, "wordfeud", deviceType, width, height);
	}
}
