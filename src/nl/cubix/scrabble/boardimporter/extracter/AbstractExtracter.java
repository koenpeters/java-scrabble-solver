package nl.cubix.scrabble.boardimporter.extracter;

import ij.process.ByteProcessor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.cubix.scrabble.config.ConfigListener;
import nl.cubix.scrabble.config.CropRectangle;

import org.apache.log4j.Logger;

abstract class AbstractExtracter {

	private Logger log = Logger.getLogger(AbstractExtracter.class);
	protected final int BLACK = -16777216;
	protected final int WHITE = -1;
	
	
	protected BufferedImage cropAndBlackAndWhite(BufferedImage image, CropRectangle cropRectangle) {

		// Set threshold to turn the image into a black and white image to increase the contrast
		ByteProcessor byteProcessor = new ByteProcessor(image);
		byteProcessor.setThreshold(142, 255, ByteProcessor.BLACK_AND_WHITE_LUT);

		// Crop the image to the given ROI
		return byteProcessor.getBufferedImage().getSubimage(cropRectangle.getX(), cropRectangle.getY(), cropRectangle.getWidth(), cropRectangle.getHeight());
	}
	
	protected void applyPhotographicNegative(BufferedImage image) {
        Color col;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgba = image.getRGB(x, y);
                col = new Color(rgba, true); //get the color data of the specific pixel
                col = new Color(
                		Math.abs(col.getRed() - 255)
                        ,Math.abs(col.getGreen() - 255)
                        ,Math.abs(col.getBlue() - 255)); //Swaps values
                image.setRGB(x, y, col.getRGB());
            }
        }
    }
	
	protected void writeImage(BufferedImage image, String namePrefix) {
		String testImageDumpFolder = ConfigListener.getConfiguration().getTestImageDumpFolder();
		File outputfile = new File(testImageDumpFolder + namePrefix + "_" + System.currentTimeMillis() + ".png");
		log.info(outputfile.getAbsoluteFile());
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	protected boolean isUniformColor(BufferedImage image) {
		int black = 0;
		int white = 0;
		for (int x=0; x < image.getHeight(); x++) {
			for (int y=0; y < image.getHeight(); y++) {
				int rgba = image.getRGB(x, y);
				if (rgba == BLACK) {
					black++;
				} else if (rgba == WHITE) {
					white++;
				} else {
					throw new RuntimeException("Not a black and white image.");
				}
			}			
		}
		return Math.min(black, white) < 10;
	}
}
