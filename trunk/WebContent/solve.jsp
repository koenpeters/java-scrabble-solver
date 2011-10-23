
<%@page import="nl.cubix.scrabble.solver.scoring.Scoring"%>
<%@page import="nl.cubix.scrabble.solver.scoring.ScoringSingleton"%>
<%@page import="nl.cubix.scrabble.solver.datastructures.Word"%>
<%@page import="java.util.List"%>
<%@page import="nl.cubix.scrabble.solver.Solver"%>
<%@page import="nl.cubix.scrabble.boardimporter.ExtractedImage"%><%@page import="nl.cubix.scrabble.config.ConfigListener"%>
<%@page import="org.apache.log4j.Logger"%><%@page import="java.io.File"%><%@page import="nl.cubix.scrabble.boardimporter.Extracter"%><%
	Logger log = Logger.getLogger("solve.jsp");
	
	String fileName = "nl-wordfeud-iphone-640-960-007.jpg";
	Integer maxNrOfResultsToShow = 10;
	
	File image = new File(ConfigListener.getConfiguration().getTestDataFolder() + "/" + fileName);

	Extracter boardExtracter = new Extracter();
	ExtractedImage extractedImage = boardExtracter.extract(image);
	log.info(extractedImage.getBoard());
	log.info(extractedImage.getBoard().getAllWordsOnBoard());
	log.info(extractedImage.getTray());
	
	Solver solver = new Solver();
	List<Word> words = solver.solve(extractedImage.getBoard(), extractedImage.getTray(), "en-twl06", "en-wordfeud");
	
	ScoringSingleton scoringSingleton = ScoringSingleton.getInstance();
	Scoring scoring = scoringSingleton.getScoringSystem("en-wordfeud");
	
	log.info(words.size());
	for (int i=0; i< Math.min(words.size() - 1, maxNrOfResultsToShow); i++) {
		Word word = words.get(i);
		out.println(word + "\nThe solution '" + word.getPrimaryWord() + "' is worth " + word.getScore(scoring) + " points.\n\n");
	}
%>