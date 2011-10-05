<%@page import="org.solvr.scrabble.scoring.Scoring"%>
<%
Scoring s = new Scoring();
%>
<%= s.getPoints('a') %>1