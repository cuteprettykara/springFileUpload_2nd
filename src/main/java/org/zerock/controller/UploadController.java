package org.zerock.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.util.MediaUtils;
import org.zerock.util.UploadFileUtils;

@Controller
public class UploadController {
	
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	@Autowired
	ServletContext context;
	
	private static final String UPLOAD_DIRECTORY = "upload";
	private String uploadPath = null;
	
	public UploadController() {
//		uploadPath = context.getRealPath("/") + UPLOAD_DIRECTORY;
		uploadPath = System.getProperty("user.home") + File.separator + UPLOAD_DIRECTORY;
		logger.info("uploadPath : {}", uploadPath);
	}
	
	@RequestMapping(value="/uploadForm", method=RequestMethod.GET)
	public void uploadForm() {
	}
	
	@RequestMapping(value="/uploadForm", method=RequestMethod.POST)
	public String uploadForm(MultipartFile file, Model model) throws IOException {
		logger.info("originalName: {}", file.getOriginalFilename());
		logger.info("size: {}", file.getSize());
		logger.info("contentType: {}", file.getContentType());
		
		String savedName= uploadFile(file.getOriginalFilename(), file.getBytes());
		
		model.addAttribute("savedName", savedName);
		
		return "uploadResult";
	}

	private String uploadFile(String originalFilename, byte[] fileData) throws IOException {
		UUID uid = UUID.randomUUID();
		
		String savedName = uid.toString() + "_" + originalFilename;
		
		File target = new File(uploadPath, savedName);
		
		FileCopyUtils.copy(fileData, target);
		
		return savedName;
	}
	
	@RequestMapping(value="/uploadAjax", method=RequestMethod.GET)
	public void uploadAjax() {
	}
	
	@RequestMapping(value="/uploadAjax", method=RequestMethod.POST, produces="text/plain;charset=UTF-8")
	public ResponseEntity<String> uploadAjax(MultipartFile file) throws Exception {
		logger.info("originalName: {}", file.getOriginalFilename());
		logger.info("size: {}", file.getSize());
		logger.info("contentType: {}", file.getContentType());
		
		String savedName= UploadFileUtils.uploadFile(uploadPath, file.getOriginalFilename(), file.getBytes());
		
		return new ResponseEntity<>(savedName, HttpStatus.CREATED);
	}
	
	@ResponseBody
	@RequestMapping("/displayFile")
	public ResponseEntity<byte[]> displayFile(String fileName) throws Exception {
		InputStream in = null;
		ResponseEntity<byte[]> entity = null;
		 
		logger.info("File Name : {}", fileName);
		logger.info("Full Name : {}", uploadPath+fileName);
		 
		try {
			String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
			MediaType mType = MediaUtils.getMediaType(formatName);
			HttpHeaders headers = new HttpHeaders();
			 
			in = new FileInputStream(uploadPath+fileName);
			
			if (mType == null) {
				fileName = fileName.substring(fileName.indexOf("_") + 1);
				headers.add("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"" );
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			} else {
				headers.setContentType(mType);				
			}
			
			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
			
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		} finally {
			in.close();
		}
		 
		return entity;
	}
	
	@ResponseBody
	@RequestMapping(value="/deleteFile", method=RequestMethod.POST)
	public ResponseEntity<String> deleteFile(String fileName) {
		String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
		MediaType mType = MediaUtils.getMediaType(formatName);
		
		if (mType != null) {
			String front = fileName.substring(0, 12);
			String end = fileName.substring(14);
			new File(uploadPath + (front+end).replace('/', File.separatorChar)).delete();
		}
		
		String temp = uploadPath + fileName.replace('/', File.separatorChar);
		logger.info(temp);
		new File(uploadPath + fileName.replace('/', File.separatorChar)).delete();
		
		return new ResponseEntity<>("deleted", HttpStatus.OK);
	}
}
