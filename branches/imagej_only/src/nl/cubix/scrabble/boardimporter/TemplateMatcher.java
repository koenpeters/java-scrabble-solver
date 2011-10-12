package nl.cubix.scrabble.boardimporter;

import static name.audet.samuel.javacv.jna.cv.cvMatchTemplate;
import static name.audet.samuel.javacv.jna.cxcore.IPL_DEPTH_32F;
import static name.audet.samuel.javacv.jna.cxcore.cvConvertScale;
import static name.audet.samuel.javacv.jna.cxcore.cvCreateImage;
import static name.audet.samuel.javacv.jna.cxcore.cvReleaseImage;
import static name.audet.samuel.javacv.jna.cxcore.cvSize;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.filter.MaximumFinder;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;

import name.audet.samuel.javacv.jna.cxcore.IplImage;
import nl.cubix.scrabble.solver.datastructures.Coordinate;

import org.apache.log4j.Logger;

/*
	this plugin implement the template match method from OpenCV library. The interface between OpenCV and Java is 
	using Samuel Audet's JavaCV code from: http://code.google.com/p/javacv/
	The detailed algorithm of each matching method can be found at opencv's documentation page:
	http://opencv.willowgarage.com/documentation/object_detection.html?highlight=matchtemplate#cvMatchTemplate
	It supports 8 bit and 16 bit grayscale only. 
	By TSENG Qingzong (qztseng /at/ gmail.com) 
 */
public class TemplateMatcher {

	Logger logger = Logger.getLogger(TemplateMatcher.class);
	
    public static Set<Coordinate> getCoordinates(ImagePlus img1, ImagePlus img2) {
    	
    	Set<Coordinate> result = new HashSet<Coordinate>();
		double mmt = 0.1f;
		double mmth = 0.98f;
        
        FloatProcessor rFp = doMatch(img1, img2, 3);

        MaximumFinder fd = new MaximumFinder();
        
        fd.findMaxima(rFp, mmt, mmth, 4, false, false);

        ResultsTable rt = ResultsTable.getResultsTable();

        int npoints = rt.getCounter();

        for (int i = 0; i < npoints; i++) {
        	Coordinate coordinate =new Coordinate((int) rt.getValue("Y", i), (int) rt.getValue("X", i));
        	result.add(coordinate);
        }
        
        return result;
    }
    private static FloatProcessor doMatch(ImagePlus src, ImagePlus tpl, int method) {

        BufferedImage	bi = null,
                		bi2 = null;

        FloatProcessor resultFp;

        if (src.getBitDepth() == tpl.getBitDepth() && src.getBitDepth() == 8) {
            ImageProcessor sp = src.getProcessor();
            ImageProcessor sp2 = tpl.getProcessor();
            bi = sp.getBufferedImage();
            bi2 = sp2.getBufferedImage();

        } else if (src.getBitDepth() == tpl.getBitDepth() && src.getBitDepth() == 16) {

            ShortProcessor sp = (ShortProcessor) src.getProcessor();
            ShortProcessor sp2 = (ShortProcessor) tpl.getProcessor();
            bi = sp.get16BitBufferedImage();
            bi2 = sp2.get16BitBufferedImage();
        } else {

            IJ.error("Images needs to have the same bit depth, and only 8bit and 16bit images are accepted");
        }
        IplImage iplSrc = IplImage.createFrom(bi);
        IplImage iplTpl = IplImage.createFrom(bi2);
        IplImage res = cvCreateImage(cvSize(iplSrc.width - iplTpl.width + 1, iplSrc.height - iplTpl.height + 1), IPL_DEPTH_32F, 1);

        if (iplSrc.depth == 16) {
            IplImage iplSrcF = cvCreateImage(cvSize(iplSrc.width, iplSrc.height), IPL_DEPTH_32F, 1);

            cvConvertScale(iplSrc, iplSrcF, 1 / 65535.0, 0);

            IplImage iplTplF = cvCreateImage(cvSize(iplTpl.width, iplTpl.height), IPL_DEPTH_32F, 1);
            cvConvertScale(iplTpl, iplTplF, 1 / 65535.0, 0);

            cvMatchTemplate(iplSrcF, iplTplF, res, method);
            cvReleaseImage(iplSrcF.pointerByReference());
            cvReleaseImage(iplTplF  .pointerByReference());
        } else {
            cvMatchTemplate(iplSrc, iplTpl, res, method);
        }

        FloatBuffer fb = res.getFloatBuffer();
        float[] f = new float[res.width * res.height];
        fb.get(f, 0, f.length);
        resultFp = new FloatProcessor(res.width, res.height, f, null);

        cvReleaseImage(res.pointerByReference());
        iplSrc.release();
        iplTpl.release();

        return resultFp;
    }
}
