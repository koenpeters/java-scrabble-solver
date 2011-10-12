<%@page import="nl.cubix.scrabble.solver.datastructures.Coordinate"%><%@page import="nl.cubix.scrabble.solver.datastructures.Board"%><%@page import="nl.cubix.scrabble.solver.util.TimingSingleton"%><%@page import="nl.cubix.scrabble.solver.scoring.Scoring"%><%@page import="nl.cubix.scrabble.solver.scoring.ScoringSingleton"%><%@page import="nl.cubix.scrabble.solver.datastructures.Box"%><%@page import="nl.cubix.scrabble.solver.datastructures.Word"%><%@page import="java.util.ArrayList"%><%@page import="nl.cubix.scrabble.solver.datastructures.BoxTypeEnum"%><%@page import="java.util.List"%><%@page import="nl.cubix.scrabble.solver.dict.DictionarySingleton"%><%@page import="nl.cubix.scrabble.solver.config.ConfigListener"%><%@page import="nl.cubix.scrabble.solver.Solver"%>
<%
	ScoringSingleton scoringSingleton = ScoringSingleton.getInstance();
	Scoring scoring = scoringSingleton.getScoringSystem("en-wordfeud");
	Board board = scoring.getBoard().clone();
/*
	board.addLetters(
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n"
	);
*/
/*
	board.addLetters(
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"......BELT.....\n" +
	"......O.I......\n" +
	"......O.P...B..\n" +
	"...FARM.STOOL..\n" +
	"...A....T...O..\n" +
	"..EXEN..I...W..\n" +
	"........c......\n" +
	"........KID...."
	);
*/
/*
	board.addLetters(
	"optics.........\n" +
	"r...a..........\n" +
	"i.toiled.......\n" +
	"g...q..e.......\n" +
	"i...u..v.......\n" +
	"n..re.tabu.....\n" +
	"...es...e......\n" +
	"...n...yay.....\n" +
	"cant....max....\n" +
	"a.........i....\n" +
	"n...rajahs.....\n" +
	"e...i..........\n" +
	"defend.........\n" +
	"....s..........\n" +
	"....e..........\n"
	);
*/	
board.addLetters(
	"...............\n" +
	"...............\n" +
	"........wagen..\n" +
	".......b..r....\n" +
	".......l..u....\n" +
	".....z.a.mi....\n" +
	".....o.zeis....\n" +
	"....ineens.....\n" +
	"...ic..r.......\n" +
	"....o..........\n" +
	"....n..........\n" +
	"...............\n" +
	"...............\n" +
	"...............\n" +
	"...............\n"
	);

	String tray = "nhfldes";
	
	TimingSingleton timing = TimingSingleton.getInstance();
	String timerGroup = "index.jsp";
	int timerId = 1;
	timing.resetAll(timerGroup);

	int nrOfTests = 10;
	int maxNrOfResultsToShow = 30;
	
	List<Word> words = null;
	for (int i=0; i < nrOfTests; i++) {
		timing.start(timerGroup, timerId);
		words = new Solver().solve(board, tray, "en-twl06", "en-wordfeud");
		timing.stop(timerGroup, timerId);
	}
	
	out.println(
		"Look at this page in source view for best results.</br></br></br>\n\n\n"
		+ "Done creating all " + words.size() + " solutions " 
		+ "for '" + tray + "' " 
		+ "on board\n" + board.toString() + "\n"
		+ "We did " + nrOfTests + " test(s).\n"
		+ timing.toString(timerGroup)
		+ "\n\nShowing the first " + maxNrOfResultsToShow + " results."
	);
	
	for (int i=0; i< Math.min(words.size() - 1, maxNrOfResultsToShow); i++) {
		Word word = words.get(i);
		out.println(word + "\nThe solution '" + word.getPrimaryWord() + "' is worth " + word.getScore(scoring) + " points.\n\n");
	}
%>