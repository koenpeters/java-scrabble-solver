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

		if (!ServletFileUpload.isMultipartContent(req)) {
			throw new ServletException("The uploading of a wordfeud board must be use multipart content");
		}

		// Put the request parameters in a convenient map. Also save the attachment to disk
		Map<String, FileItem> params = saveRequest(req);
		
		// Get the values of the sent request parameters
		String deviceType = params.get("deviceType").getString();
		String language = params.get("language").getString();
		File imageOfBoard = ((DiskFileItem)params.get("imageOfBoard")).getStoreLocation();
		Integer maxNrOfSolutions = Integer.parseInt(params.get("maxNrOfSolutions").getString());
		
		// Figure out what type of game this is
		TemplateType templateType = detect(imageOfBoard, deviceType, language);
		
		// Get all the solutions to the sent screen
		List<Word> solutions = solve(imageOfBoard, templateType);
		
		// Save the solutions and templateType for the view to process
		req.setAttribute("templateType", templateType);
		req.setAttribute("solutions", solutions);
		req.setAttribute("maxNrOfSolutions", maxNrOfSolutions);

		// Redirect to the view
		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/view/showSolutions.jsp"); 
		rd.include(req, resp);
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
			log.info(item.getFieldName());
		}
		return result;
	}
	
	private TemplateType detect(File imageOfBoard, String deviceType, String language) {
		GameDectector dectector = new GameDectector();
		return dectector.detect(imageOfBoard, deviceType, language);
	}
	
	private List<Word> solve(File image, TemplateType templateType) throws ServletException {
		
		Extracter extracter = new Extracter();
		ExtractedImage extractedImage = extracter.extract(image, templateType);
		
		String dictionary = "";
		String scoringSystemName = "";
		String language = templateType.getScoringSystem().getLanguage();
		
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
		return solver.solve(extractedImage.getBoard(), extractedImage.getTray(), dictionary, scoringSystemName);
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getWriter().write("Only multipart POST requests are accepted");
	}
}