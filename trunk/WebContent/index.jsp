<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Solve board</title>
	<meta name="description" content="" /> 
	<meta name="keywords" content="" />
	<script>
		window.onload = function() {
			document.forms.wordForm.elements[0].focus();
		};
	</script>
</head>
<body>
	<form method="POST" name="wordForm" enctype="multipart/form-data" action="/scrabble/solve">
		<label>Screen capture image: <input type="file" name="imageOfBoard"/></label><br/>
		<label>Device type:
			<select name="deviceType">
				<option value="iphone">iPhone</option>
				<option value="ipod">iPod</option>
				<option value="android">Android</option>
			</select>
		</label><br/>
		<label>Language:
			<select name="language">
				<option value="nl">Nederlands</option>
				<option value="en">English</option>
			</select>
		</label><br/>
		<label>Maximum nr of solutions:
			<input name="maxNrOfSolutions" type="text" value="10" />
		</label><br/>
		<button type="submit" />Solve!</button><br/>
	</form>
	
</body>
</html>