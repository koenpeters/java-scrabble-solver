<%@page import="java.io.File"%><%@page import="nl.cubix.scrabble.boardimporter.BoardExtracter"%><%	

	BoardExtracter be = new BoardExtracter();
	be.extract(new File("C:\\temp\\testSpul\\board (8).jpg"));
	
%>