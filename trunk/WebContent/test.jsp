<%@page import="nl.cubix.scrabble.solver.dict.cleaner.CleanerFactory"%><%@page import="nl.cubix.scrabble.solver.dict.cleaner.Cleanable"%><%
	Cleanable cleaner = CleanerFactory.getInstance("");
	String word = "{'�', 'a'}, {'�', 'a'}, {'�', 'a'}, {'�', 'a'},{'�', 'e'}, {'�', 'e'}, {'�', 'e'}, {'�', 'e'},{'�', 'o'}, {'�', 'o'}, {'�', 'o'}, {'�', 'o'},{'�', 'u'}, {'�', 'u'}, {'�', 'u'}, {'�', 'u'},{'�', 'i'}, {'�', 'i'}, {'�', 'i'}, {'�', 'i'},{'�', 'y'},{'�', 'c'}";
	for(char c: word.toCharArray()) {
		out.println(cleaner.cleanup(c));
	}
%>