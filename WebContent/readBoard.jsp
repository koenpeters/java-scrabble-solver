<%@page import="java.io.File"%><%@page import="nl.cubix.scrabble.boardimporter.BoardExtracter"%><%@page import="nl.cubix.scrabble.boardimporter.TemplateMatcher"%><%	

	BoardExtracter be = new BoardExtracter();
	be.extract(new File("c:\\board.gif"));
	
%>