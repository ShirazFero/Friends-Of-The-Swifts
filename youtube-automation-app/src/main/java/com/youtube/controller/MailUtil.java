package com.youtube.controller;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.youtube.utils.Constants;

public class MailUtil {

	public static void sendMail(String recepient ,String subject, String content) throws AddressException, MessagingException {
		System.out.println("preparing message...");
		Properties properties = new Properties();
		properties.put("mail.smtp.auth","true");
		properties.put("mail.smtp.starttls.enable","true");
		properties.put("mail.smtp.host","smtp.gmail.com");
		properties.put("mail.smtp.port","587");
		
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Constants.appEmail,Constants.myBytes);
			}
		});
		
		Message message = prepareMessage(session ,Constants.appEmail ,recepient,subject,content);
		
		Transport.send(message);
		System.out.println("message sent succssefully...");
	}

	private static Message prepareMessage(Session session, String myAccountEmail, String recepient ,String subject, String content) throws AddressException, MessagingException {
		// TODO Auto-generated method stub
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(myAccountEmail));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
		message.setSubject(subject);
		message.setText(content);
		return message;
	}
}
