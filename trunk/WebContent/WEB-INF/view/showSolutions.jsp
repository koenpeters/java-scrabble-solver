<%@page import="nl.cubix.scrabble.boardimporter.GameDetector.TemplateType"%>
<%@page import="java.util.Enumeration"%><html>
<%@page import="nl.cubix.scrabble.solver.datastructures.Word"%>
<%@page import="nl.cubix.scrabble.solver.scoring.Scoring"%>
<%@page import="java.util.List"%>
<%	List<Word> solutions = (List<Word>)request.getAttribute("solutions");
	Integer maxNrOfSolutions = Math.max(100, (Integer)request.getAttribute("maxNrOfSolutions"));
	TemplateType templateType = (TemplateType)request.getAttribute("templateType");
%>

<!DOCTYPE html>

<head>
	<meta charset="UTF-8">
	<title>Does word exist tester</title>
	<meta name="description" content="" /> 
	<meta name="keywords" content="" />
</head>
<body>
	Device type: <%= templateType.getDevice().getDeviceType() %><br/>
	Language: <%= templateType.getScoringSystem().getLanguage() %><br/>
	Maximum nr of solutions: <%= maxNrOfSolutions %><br/>
	Total nr of solutions: <%= solutions.size() %><br/>
	Top Solutions:<br/>
	<pre><%
		for (int i=0; i < Math.min(10, solutions.size()); i++) {
			out.println("Word: " + solutions.get(i).getPrimaryWord());
			out.println("Score: " + solutions.get(i).getScore(templateType.getScoringSystem()));
			out.println(solutions.get(i));
			out.println("<br/><br/><br/>");
		}
		%>
	</pre>
</body>
</html>