package com.poly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadTestController {

    @PostMapping("/testUpload")
    @ResponseBody // Important: Add @ResponseBody
    public String testUpload(@RequestParam("image") MultipartFile file) {
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            System.out.println("Uploaded file: " + fileName);
            return "File uploaded: " + fileName;
        } else {
            return "No file uploaded.";
        }
    }
    @GetMapping("/testUploadForm")
    public String showUploadForm() {
        return "test_upload"; // Trả về view test_upload.html
    }
}