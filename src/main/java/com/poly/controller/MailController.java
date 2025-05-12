package com.poly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.service.MailService;

@Controller
public class MailController {
    
    @Autowired
    MailService mailService;
    
    @GetMapping("/mail/form")
    public String mailForm() {
        return "info";
    }
    
    @ResponseBody
    @RequestMapping("/mail/send-direct")
    public String sendDirect(@RequestParam(required = false) String to,
                             @RequestParam(required = false) String subject,
                             @RequestParam(required = false) String body) {
        try {
            mailService.send(to != null ? to : "receiver@gmail.com", 
                            subject != null ? subject : "Subject", 
                            body != null ? body : "Body");
            return "Mail đã được gửi đi trực tiếp";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    
    @ResponseBody
    @RequestMapping("/mail/send-queue")
    public String sendQueue(@RequestParam(required = false) String to,
                           @RequestParam(required = false) String subject,
                           @RequestParam(required = false) String body) {
        try {
            mailService.push(to != null ? to : "receiver@gmail.com", 
                            subject != null ? subject : "Subject", 
                            body != null ? body : "Body");
            return "Mail đã được xếp vào hàng đợi";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    
    @PostMapping("/mail/send")
    public String send(Model model,
                      @RequestParam("from") String from,
                      @RequestParam("to") String to,
                      @RequestParam("cc") String cc,
                      @RequestParam("bcc") String bcc,
                      @RequestParam("subject") String subject,
                      @RequestParam("body") String body,
                      @RequestParam("filenames") String filenames,
                      @RequestParam(value = "direct", required = false) Boolean direct) {
        
        MailService.Mail mail = MailService.Mail.builder()
                .from(from)
                .to(to)
                .cc(cc)
                .bcc(bcc)
                .subject(subject)
                .body(body)
                .filenames(filenames)
                .build();
        
        try {
            if (direct != null && direct) {
                mailService.send(mail);
                model.addAttribute("message", "Mail đã được gửi trực tiếp");
            } else {
                mailService.push(mail);
                model.addAttribute("message", "Mail đã được xếp vào hàng đợi");
            }
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        
        return "info";
    }
}