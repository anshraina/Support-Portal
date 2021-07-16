package com.ansh.supportportal.service;

import static com.ansh.supportportal.constant.EmailConstant.CC;
import static com.ansh.supportportal.constant.EmailConstant.DEFAULT_PORT;
import static com.ansh.supportportal.constant.EmailConstant.EMAIL_SUBJECT;
import static com.ansh.supportportal.constant.EmailConstant.FROM_EMAIL;
import static com.ansh.supportportal.constant.EmailConstant.GMAIL_SMTP_SERVER;
import static com.ansh.supportportal.constant.EmailConstant.PASSWORD;
import static com.ansh.supportportal.constant.EmailConstant.SIMPLE_MAIL_TRANSFER_PROTOCOL;
import static com.ansh.supportportal.constant.EmailConstant.SMTP_AUTH;
import static com.ansh.supportportal.constant.EmailConstant.SMTP_HOST;
import static com.ansh.supportportal.constant.EmailConstant.SMTP_PORT;
import static com.ansh.supportportal.constant.EmailConstant.SMTP_STARTTLS_ENABLE;
import static com.ansh.supportportal.constant.EmailConstant.SMTP_STARTTLS_REQUIRED;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ansh.supportportal.constant.EmailConstant;
import com.sun.mail.smtp.SMTPTransport;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
        Message message = createEmail(firstName, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getMailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, EmailConstant.USERNAME, PASSWORD);
        smtpTransport.sendMessage(message,message.getAllRecipients()); 
        smtpTransport.close();
        
    }
    private Session getMailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }

    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getMailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setSentDate(new Date());
        message.setText("Hello " + firstName + "Your password is " + password + "\n \n Regards, \n \n" + "Support");
        message.saveChanges();
        return message;
        
    }
}
