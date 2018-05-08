<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style>
	.fileDrop {
		width: 100%;
		height: 200px;
		border: 1px dotted blue;
	}
	
	small {
		margin-left: 3px;
		font-weight: bold;
		color: gray;
	}
</style>

<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>

<script>
	function checkIamgeType(fileName) {
		var pattern = /jpg|gif|png|jpeg/i;
		
		return fileName.match(pattern);
	}
	
	function getOriginalFileName(fileName) {
		if (checkIamgeType(fileName)) { return; }
		
		var idx = fileName.indexOf("_") + 1;
		return fileName.substr(idx);
	}
	
	function getImageLink(fileName) {
		if (!checkIamgeType(fileName)) { return; }
		
		var front = fileName.substr(0, 12);
		var end = fileName.substr(14);
		
		return front + end;
	}
	
	$(document).ready(function() {
	 	$(".fileDrop").on("dragenter dragover", function(e) {
			e.preventDefault();
		});
		
		$(".fileDrop").on("drop", function(e) {
			e.preventDefault();
			
			var files = e.originalEvent.dataTransfer.files;
			var file = files[0];
			
			console.log("file : " + file);
			
			var formData = new FormData();
			formData.append("file", file);
			
			$.ajax({
				url: '/uploadAjax',
				type: 'POST',
				data: formData,
				dataType: 'text',
				processData: false,
				contentType: false,
				success: function(data) {
					var str =  "";
					
					console.log(data);
					console.log(checkIamgeType(data));
					
					if (checkIamgeType(data)) {
						str = "<div>"
							+ "<a href='displayFile?fileName=" + getImageLink(data) + "' target='_blank'>"
						 	+ "<img src='displayFile?fileName=" + data + "' />"
						 	+ "</a>"
						 	+ data 
						 	+ "</div>";
					} else {
						str = "<div>"
							+ "<a href='displayFile?fileName=" + data + "'>"
							+ getOriginalFileName(data)
							+ "</a>"
						 	+ "</div>";
					}
					
					$(".uploadedList").append(str);
				}
			});
		});	
	});
</script>

</head>
<body>
	<h3>Ajax File Upload</h3>
	<div class="fileDrop"></div>
	
	<div class="uploadedList"></div>
</body>
</html>