package com.poly.service;

import java.util.List;

public interface MailService {
    public static class Mail {
        private String from = "WebShop <web-shop@gmail.com>";
        private String to;
        private String cc;
        private String bcc;
        private String subject;
        private String body;
        private String filenames;
        
        // Builder pattern implementation
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private Mail mail = new Mail();
            
            public Builder from(String from) {
                mail.from = from;
                return this;
            }
            
            public Builder to(String to) {
                mail.to = to;
                return this;
            }
            
            public Builder cc(String cc) {
                mail.cc = cc;
                return this;
            }
            
            public Builder bcc(String bcc) {
                mail.bcc = bcc;
                return this;
            }
            
            public Builder subject(String subject) {
                mail.subject = subject;
                return this;
            }
            
            public Builder body(String body) {
                mail.body = body;
                return this;
            }
            
            public Builder filenames(String filenames) {
                mail.filenames = filenames;
                return this;
            }
            
            public Mail build() {
                return mail;
            }
        }
        
        // Getters and setters
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
        public String getCc() { return cc; }
        public void setCc(String cc) { this.cc = cc; }
        public String getBcc() { return bcc; }
        public void setBcc(String bcc) { this.bcc = bcc; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
        public String getFilenames() { return filenames; }
        public void setFilenames(String filenames) { this.filenames = filenames; }
    }
    
    void send(Mail mail);
    
    default void send(String to, String subject, String body) {
        Mail mail = Mail.builder().to(to).subject(subject).body(body).build();
        this.send(mail);
    }
    
    // For part 2 - queue functionality
    void push(Mail mail);
    
    default void push(String to, String subject, String body) {
        this.push(Mail.builder().to(to).subject(subject).body(body).build());
    }
}