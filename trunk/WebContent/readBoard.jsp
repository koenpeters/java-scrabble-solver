
<%@page import="nl.cubix.scrabble.config.ConfigListener"%>
<%@page import="org.apache.log4j.Logger"%><%@page import="java.io.File"%><%@page import="nl.cubix.scrabble.boardimporter.BoardExtracter"%><%	

	Logger log = Logger.getLogger("readBox.jsp");
	
	File dir = new File(ConfigListener.getConfiguration().getTestDataFolder());
	//File dir = new File("C:/temp/test/");

	BoardExtracter be = new BoardExtracter();
	for (File file: dir.listFiles()) {
		if (file.isFile()) {
			log.info("reading " + file.getName());
			be.extract(file);
		}
	}
%>