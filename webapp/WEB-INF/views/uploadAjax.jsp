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

<!-- handlebars -->
<script src="/resources/handlebars/handlebars-v4.0.11.js"></script>

<script id="template" type="text/x-handlebars-template">
	<div>
	{{#if isImage}}
		<a href='{{getLink}}' target='_blank'>
	{{else}}
		<a href='{{getLink}}'>
	{{/if}}

			<img src='{{imgsrc}}' />
		</a>
		<span>{{fileName}}</span>
		<small data-src='{{fullName}}' style='cursor:pointer'>X</small>
	</div>
</script>

<script>
	function checkIamgeType(fileName) {
		var pattern = /jpg|gif|png|jpeg/i;
		
		return fileName.match(pattern);
	}
	
	function getFileInfo(fullName) {
		var fileName, imgsrc, getLink;
		var fileLink;
		var isImage = false;
		
		if (checkIamgeType(fullName)) {
			imgsrc = "/displayFile?fileName=" + fullName;
			fileLink = fullName.substr(14);
			
			var front = fullName.substr(0, 12);
			var end = fullName.substr(14);
			
			getLink = "/displayFile?fileName=" + front + end;
			
			isImage = true;
			
		} else {
			imgsrc = "/resources/img/file.png";
			fileLink = fullName.substr(12);
			
			getLink = "/displayFile?fileName=" + fullName;
		}
		
		fileName = fileLink.substr(fileLink.indexOf("_")+1);
		console.log("fileName: [" + fileName + "]");
		
		return {
			fileName: fileName,
			imgsrc: imgsrc,
			getLink: getLink,
			fullName: fullName,
			isImage : isImage
		};
	}
	
	$(document).ready(function() {
		var template = Handlebars.compile($("#template").html());
		
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
					var fileInfo =  getFileInfo(data);
					
					var html = template(fileInfo);
					
					$(".uploadedList").append(html);
				}
			});
		});
		
		$(".uploadedList").on("click", "small", function(e) {
			var objThis = $(this);
			
			$.ajax({
				url: "deleteFile",
				type: "post",
				data: {fileName:$(this).attr("data-src")},
				dataType: "text",
				success: function(result) {
					if (result == "deleted") {
						objThis.parent("div").remove();
					}
				}
			});
		})
	});
</script>

</head>
<body>
	<h3>Ajax File Upload</h3>
	<div class="fileDrop"></div>
	
	<div class="uploadedList"></div>
</body>
</html>