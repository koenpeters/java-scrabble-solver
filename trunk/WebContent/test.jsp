<%@page import="nl.cubix.scrabble.solver.dict.cleaner.CleanerFactory"%><%@page import="nl.cubix.scrabble.solver.dict.cleaner.Cleanable"%><%
	Cleanable cleaner = CleanerFactory.getInstance("");
	String word = "{'ä', 'a'}, {'à', 'a'}, {'á', 'a'}, {'â', 'a'},{'ë', 'e'}, {'è', 'e'}, {'é', 'e'}, {'ê', 'e'},{'ö', 'o'}, {'ò', 'o'}, {'ó', 'o'}, {'ô', 'o'},{'ü', 'u'}, {'ù', 'u'}, {'ú', 'u'}, {'û', 'u'},{'ï', 'i'}, {'ì', 'i'}, {'í', 'i'}, {'î', 'i'},{'ÿ', 'y'},{'ç', 'c'}";
	for(char c: word.toCharArray()) {
		out.println(cleaner.cleanup(c));
	}
%>