package nl.cubix.scrabble.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.cubix.scrabble.boardimporter.GameDetector.GameDectector;
import nl.cubix.scrabble.boardimporter.GameDetector.TemplateType;
import nl.cubix.scrabble.boardimporter.extracter.ExtractedImage;
import nl.cubix.scrabble.boardimporter.extracter.Extracter;
import nl.cubix.scrabble.config.ConfigListener;
import nl.cubix.scrabble.solver.Solver;
import nl.cubix.scrabble.solver.datastructures.Word;
import nl.cubix.scrabble.util.TimingSingleton;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {

	private Logger log = Logger.getLogger(UploadServlet.class);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

		TimingSingleton timingSingleton = TimingSingleton.getInstance();
		timingSingleton.resetAll(this);
		if (!ServletFileUpload.isMultipartContent(req)) {
			throw new ServletException("The uploading of a wordfeud board must be use multipart content");
		}

		timingSingleton.start(this, 1);
		// Put the request parameters in a convenient map. Also save the attachment to disk
		Map<String, FileItem> params = saveRequest(req);
		timingSingleton.stop(this, 1);
		
		timingSingleton.start(this, 2);
		// Get the values of the sent request parameters
		String deviceType = params.get("deviceType").getString();
		String language = params.get("language").getString();
		File imageOfBoard = ((DiskFileItem)params.get("imageOfBoard")).getStoreLocation();
		Integer maxNrOfSolutions = Integer.parseInt(params.get("maxNrOfSolutions").getString());
		
		// Get all the solutions to the sent screen
		Solution solution = solve(imageOfBoard, deviceType, language);
		timingSingleton.stop(this, 2);
		
		// Save the solutions and templateType for the view to process
		req.setAttribute("templateType", solution.templateType);
		req.setAttribute("solutions", solution.solutions);
		req.setAttribute("tray", solution.extractedImage.getTray());
		req.setAttribute("maxNrOfSolutions", maxNrOfSolutions);
		req.setAttribute("durationUpload", timingSingleton.getTime(this, 1));
		req.setAttribute("durationSolve", timingSingleton.getTime(this, 2));

		// Redirect to the view
		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/view/showSolutions.jsp"); 
		rd.include(req, resp);
		log.info(timingSingleton.toString(this));
	}
	
	private Map<String, FileItem> saveRequest(HttpServletRequest req) throws ServletException {
		
		String uploadFolder = ConfigListener.getConfiguration().getUploadFolder();
		File repository = new File(uploadFolder);

		FileItemFactory factory = new DiskFileItemFactory(3000, repository);
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Parse the request for fields
		List<FileItem> items;
		try {
			items = upload.parseRequest(req);
		} catch (FileUploadException e) {
			throw new ServletException(e);
		}
		
		Map<String, FileItem> result = new HashMap<String, FileItem>();
		for (FileItem item : items) {
			result.put(item.getFieldName(), item);
		}
		return result;
	}
	
	
	private Solution solve(File imageOfBoard, String deviceType, String language) throws ServletException {
		
		
		GameDectector dectector = new GameDectector();
		TemplateType templateType = dectector.detect(imageOfBoard, deviceType, language);
		
		Extracter extracter = new Extracter();
		ExtractedImage extractedImage = extracter.extract(imageOfBoard, templateType);
		
		
		String dictionary = "";
		String scoringSystemName = "";
		
		if (language.equalsIgnoreCase("nl")) {
			dictionary = "nl-opentaal";
			scoringSystemName = "nl-wordfeud";
		} else if (language.equalsIgnoreCase("en")) {
			dictionary = "en-twl06";
			scoringSystemName = "en-wordfeud";
		} else {
			throw new ServletException("Unknown language: " + language);
		}

		Solver solver = new Solver();
		List<Word> solutions =  solver.solve(extractedImage.getBoard(), extractedImage.getTray(), dictionary, scoringSystemName);
		
		Solution result = new Solution();
		result.templateType = templateType;
		result.solutions = solutions;
		result.extractedImage = extractedImage;
		return result;
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getWriter().write("Only multipart POST requests are accepted");
	}
	
	private class Solution {
		List<Word> solutions;
		ExtractedImage extractedImage;
		TemplateType templateType;
	}
}