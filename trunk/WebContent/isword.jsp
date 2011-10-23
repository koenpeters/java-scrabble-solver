<%@page import="nl.cubix.scrabble.solver.dict.DictionarySingleton"%><%@page import="nl.cubix.scrabble.solver.dict.DictionaryNode"%><%
	String word = request.getParameter("word");
	String dictionaryName = request.getParameter("dictionaryName");
	boolean listSubWords = request.getParameter("listSubWords") != null;

	DictionaryNode result = null;
	DictionarySingleton dictionarySingleton = DictionarySingleton.getInstance();
	if (word == null) {
		word = "";
	} else {
		word = word.toLowerCase();
		word = word.replaceAll("[^a-z]","");
		if (dictionaryName == null) {
			dictionaryName = dictionarySingleton.getDictionaryNames().get(0);
		}
		DictionaryNode wholeDictionary = dictionarySingleton.getDictionary(dictionaryName);
		result = dictionarySingleton.getDictionarySubsetOfPrefix(word, wholeDictionary);
	}
%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Does word exist tester</title>
	<meta name="description" content="" /> 
	<meta name="keywords" content="" />
	<script>
		window.onload = function() {
			document.forms.wordForm.elements[0].focus();
		};
	</script>
</head>
<body>
	<form method="GET" name="wordForm">
		<label>Word: <input type="text" value="<%= word %>" name="word"/></label>
		<label>dictionaries:
			<select name="dictionaryName">
				<% for (String name: dictionarySingleton.getDictionaryNames()) { %>
					<option value="<%= name %>" <%= (name.equals(dictionaryName)? "selected": "") %>><%= name %></option>
				<% } %>
			</select>
		</label>
		<label>List all words starting with this word?: <input type="checkbox" value="true" name="listSubWords" <%= (listSubWords? "checked": "") %>/></label>
		<button type="submit" />Go!</button>
	</form>
	
	<% 	if (word != null) { %>
			<br/>
			Does word exist: <%= (result == null || !result.isWord()? "nope": "yup") %>
			<br/>
			<%	if (listSubWords) { %>
					All words starting with this word:
					<blockquote>
					<%	if (result != null) {
							String allWords = result.toString(word);
							out.println(allWords.replaceAll("\\n", "<br/>"));
						} %>
					</blockquote>
			<%	} %>
	 <%	} %>
</body>
</html>