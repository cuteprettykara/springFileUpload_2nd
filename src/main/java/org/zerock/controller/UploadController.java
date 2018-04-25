package org.zerock.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

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
	public ResponseEntity<String> uploadAjax(MultipartFile file) {
		logger.info("originalName: {}", file.getOriginalFilename());
		logger.info("size: {}", file.getSize());
		logger.info("contentType: {}", file.getContentType());
		
		return new ResponseEntity<>(file.getOriginalFilename(), HttpStatus.CREATED);
	}
}
