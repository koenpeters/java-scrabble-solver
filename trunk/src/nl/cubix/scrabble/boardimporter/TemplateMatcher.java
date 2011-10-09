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

import name.audet.samuel.javacv.jna.cxcore.IplImage;
import nl.cubix.scrabble.solver.util.TimingSingleton;

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
	
	public void test() {
		ImagePlus img1 = IJ.openImage("c:\\board480.gif");
		ImagePlus img2 = IJ.openImage("c:\\a-small.gif");
		
		TimingSingleton ts = TimingSingleton.getInstance();
		ts.resetAll(TemplateMatcher.class.toString());
		for (int i=0; i <300; i++) {
			ts.start(TemplateMatcher.class.toString(), 1);
			getCoordinates(img1, img2);
			ts.stop(TemplateMatcher.class.toString(), 1);
		}
		logger.info(ts.toString(TemplateMatcher.class.toString()));
	}
	
    public void getCoordinates(ImagePlus img1, ImagePlus img2) {
    	
    	int method = 3;
		double mmt = 0.1f;
		double mmth = 0.98f;
        
        FloatProcessor rFp = doMatch(img1, img2, method);

        MaximumFinder fd = new MaximumFinder();
        
        fd.findMaxima(rFp, mmt, mmth, 4, false, false);

        ResultsTable rt = ResultsTable.getResultsTable();

        int npoints = rt.getCounter();

        int[] xpoints = new int[npoints];
        int[] ypoints = new int[npoints];

        for (int i = 0; i < npoints; i++) {
            xpoints[i] = (int) rt.getValue("X", i);
            ypoints[i] = (int) rt.getValue("Y", i);
           // logger.info(xpoints[i] + ", " + ypoints[i]);
        }
    }

    public static FloatProcessor doMatch(ImageProcessor src, ImageProcessor tpl, int method, boolean showR) {

        BufferedImage bi = null,
                bi2 = null;
        FloatProcessor resultFp;

        ShortProcessor sp = (ShortProcessor) src.convertToShort(false);
        ShortProcessor sp2 = (ShortProcessor) tpl.convertToShort(false);
        bi = sp.get16BitBufferedImage();
        bi2 = sp2.get16BitBufferedImage();
        
        IplImage iplSrc = IplImage.createFrom(bi);
        IplImage iplTpl = IplImage.createFrom(bi2);
        IplImage res = cvCreateImage(cvSize(iplSrc.width - iplTpl.width + 1, iplSrc.height - iplTpl.height + 1), IPL_DEPTH_32F, 1);

        IplImage iplSrcF = cvCreateImage(cvSize(iplSrc.width, iplSrc.height), IPL_DEPTH_32F, 1);
        cvConvertScale(iplSrc, iplSrcF, 1 / 65535.0, 0);
        IplImage iplTplF = cvCreateImage(cvSize(iplTpl.width, iplTpl.height), IPL_DEPTH_32F, 1);
        cvConvertScale(iplTpl, iplTplF, 1 / 65535.0, 0);

        IJ.showStatus("Matching Template...");
        cvMatchTemplate(iplSrcF, iplTplF, res, method);

        cvReleaseImage(iplSrcF.pointerByReference());
        cvReleaseImage(iplTplF.pointerByReference());

        FloatBuffer fb = res.getFloatBuffer();
        float[] f = new float[res.width * res.height];
        fb.get(f, 0, f.length);
        resultFp = new FloatProcessor(res.width, res.height, f, null);

         /*Added 2011-5-20 to fix the memory leak issue*/
        cvReleaseImage(res.pointerByReference());
        iplSrc.release();
        iplTpl.release();

        return resultFp;

    }

    public static FloatProcessor doMatch(ImagePlus src, ImagePlus tpl, int method) {

        BufferedImage bi = null,
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
            //IJ.log("create 32F");

            IplImage iplSrcF = cvCreateImage(cvSize(iplSrc.width, iplSrc.height), IPL_DEPTH_32F, 1);


            //IJ.log("convert to 32F");

            cvConvertScale(iplSrc, iplSrcF, 1 / 65535.0, 0);
            //IJ.log("finish converting");

            IplImage iplTplF = cvCreateImage(cvSize(iplTpl.width, iplTpl.height), IPL_DEPTH_32F, 1);
            cvConvertScale(iplTpl, iplTplF, 1 / 65535.0, 0);

            //IJ.log("Iplimage1F depth="+iplSrcF.depth);
            //IJ.log("Iplimage2F depth="+iplTplF.depth);
            //IJ.log("start matching");
            IJ.showStatus("Matching Template...");


            /*
            CV_TM_SQDIFF        = 0,
            CV_TM_SQDIFF_NORMED = 1,
            CV_TM_CCORR         = 2,
            CV_TM_CCORR_NORMED  = 3,
            CV_TM_CCOEFF        = 4,
            CV_TM_CCOEFF_NORMED = 5;


             */


            cvMatchTemplate(iplSrcF, iplTplF, res, method);
            //IJ.log("finish matching");
            cvReleaseImage(iplSrcF.pointerByReference());
            cvReleaseImage(iplTplF  .pointerByReference());

        } else {
            //IJ.log("start matching");
            IJ.showStatus("Matching Template...");
            cvMatchTemplate(iplSrc, iplTpl, res, method);
        }

        FloatBuffer fb = res.getFloatBuffer();
        float[] f = new float[res.width * res.height];
        fb.get(f, 0, f.length);
        resultFp = new FloatProcessor(res.width, res.height, f, null);

        /*Added 2011-5-20 to fix the memory leak issue*/
        cvReleaseImage(res.pointerByReference());
        iplSrc.release();
        iplTpl.release();

        return resultFp;

    }

    public static int[] findMax(ImageProcessor ip, int sW) {
        int[] coord = new int[2];
        float max = ip.getPixel(0, 0);
        int sWh, sWw;

        if (sW == 0) {
            sWh = ip.getHeight();
            sWw = ip.getWidth();
        } else {
            sWh = sW;
            sWw = sW;
        }


        for (int j = (ip.getHeight() - sWh) / 2; j < (ip.getHeight() + sWh) / 2; j++) {
            for (int i = (ip.getWidth() - sWw) / 2; i < (ip.getWidth() + sWw) / 2; i++) {
                if (ip.getPixel(i, j) > max) {
                    max = ip.getPixel(i, j);
                    coord[0] = i;
                    coord[1] = j;
                }
            }
        }
        return (coord);
    }

    public static int[] findMin(ImageProcessor ip, int sW) {
        int[] coord = new int[2];
        float min = ip.getPixel(0, 0);
        int sWh, sWw;

        if (sW == 0) {
            sWh = ip.getHeight();
            sWw = ip.getWidth();
        } else {
            sWh = sW;
            sWw = sW;
        }


        for (int j = (ip.getHeight() - sWh) / 2; j < (ip.getHeight() + sWh) / 2; j++) {
            for (int i = (ip.getWidth() - sWw) / 2; i < (ip.getWidth() + sWw) / 2; i++) {
                if (ip.getPixel(i, j) < min) {
                    min = ip.getPixel(i, j);
                    coord[0] = i;
                    coord[1] = j;
                }
            }
        }
        return (coord);
    }
    
    public void showAbout() {
		IJ.showMessage("cvMatch Template", "This plugin implements the tempalte matching function from\n"
			+ 							   "the OpenCV library. It will try to find an object (template)\n"
			+							   "within a given image (image).Six different matching algorithms\n"
			+ 						       "(methods)could be used. The matching result could be printed\n"
			+ 						       "in the log window or the result table. \n"
			+ 						       "You can also decide to display the correlation map. The coordinates\n"
			+ 						       "of the maximum (or minimum for the square difference methods)\n"
			+ 						       "correspond to the best match.\n"
			+ 						       "By checking the multimatch option, not only the best match will\n"
			+ 						       "be shown, but also all the similar pattern above the defined\n"
			+ 						       "threshold will be shown (Find maximum function on the correlation map)\n"
			+ 							   "More details on \nhttps://sites.google.com/site/qingzongtseng/template-matching-ij-plugin"
			);
	}
}
