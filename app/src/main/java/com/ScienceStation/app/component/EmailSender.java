package com.ScienceStation.app.component;

import com.ScienceStation.app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    private final JavaMailSender javaMailSender;


    private final String appUrl="http://localhost:8080";

    @Autowired
    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void confirmRegistration(String token,String recipient){
        String subject = "Registration Conformation";
        String confirmationUrl = appUrl+"/api/users/confirmAccount/"+token;
        String message =  "Registration successful! Please click on this link to activate your account\n"+confirmationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipient);
        email.setSubject(subject);
        email.setText(message);
        javaMailSender.send(email);
    }
    public void sendNotificationToUser(User u,String message){
        String subject = "Notification Message from Science Station";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(u.getEmail());
        email.setSubject(subject);
        email.setText(message);
        javaMailSender.send(email);
    }


}
