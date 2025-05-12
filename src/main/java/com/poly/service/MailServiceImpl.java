package com.poly.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service("mailService")
public class MailServiceImpl implements MailService {
    
    @Autowired
    JavaMailSender mailSender;
    
    // Queue for emails
    List<Mail> queue = new ArrayList<>();
    
    @Override
    public void send(Mail mail) {
        try {
            // 1. Create Mail
            MimeMessage message = mailSender.createMimeMessage();
            
            // 2. Create helper object to write mail content
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            
            // 2.1. Set sender information
            helper.setFrom(mail.getFrom());
            helper.setReplyTo(mail.getFrom());
            
            // 2.2. Set recipient information
            helper.setTo(mail.getTo());
            if (!isNullOrEmpty(mail.getCc())) {
                helper.setCc(mail.getCc());
            }
            if (!isNullOrEmpty(mail.getBcc())) {
                helper.setBcc(mail.getBcc());
            }
            
            // 2.3. Set subject and content
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getBody(), true);
            
            // 2.4. Attach files
            String filenames = mail.getFilenames();
            if (!isNullOrEmpty(filenames)) {
                for (String filename : filenames.split("[,;]+")) {
                    File file = new File(filename.trim());
                    helper.addAttachment(file.getName(), file);
                }
            }
            
            // 3. Send mail
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private boolean isNullOrEmpty(String text) {
        return (text == null || text.trim().length() == 0);
    }
    
    @Override
    public void push(Mail mail) {
        queue.add(mail);
    }
    
    @Scheduled(fixedDelay = 500)
    public void run() {
        while (!queue.isEmpty()) {
            try {
                this.send(queue.remove(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}