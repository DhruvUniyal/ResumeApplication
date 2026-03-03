package com.example.resume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.resume.helper.PDFHelper;

@SpringBootApplication
public class ResumeApplication implements CommandLineRunner{
	private static final Logger logger = LoggerFactory.getLogger(ResumeApplication.class);

	public static void main(String[] args) {
		logger.info("Application is starting...");
		SpringApplication.run(ResumeApplication.class, args);
	}
	
	 @Override
	    public void run(String... args) {
	        PDFHelper.createDocument("C://Users//Dhruv//OneDrive//Desktop//test.pdf");
	    }
}
