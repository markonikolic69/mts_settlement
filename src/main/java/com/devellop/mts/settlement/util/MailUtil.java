package com.devellop.mts.settlement.util;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;


import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;

public class MailUtil {
	
	private Properties properties = null;
	
	public static Logger logger = Logger.getLogger(MailUtil.class);
	
	protected void initializeProperties()
	{
		properties = new Properties();
		try
		{
			FileInputStream stream = new FileInputStream("application.properties");
			properties.load(stream);
			stream.close();
		}
		catch (IOException e) {
			System.out.println("No properties, no mail sent");
		}
		
	}
	public MailUtil()
	{
		initializeProperties();
	}
	
	
    public void sendStampaTransaction(String text, String from, String subject, File toSend) throws MessagingException{

              logger.info(
                  "Try to send Stampa TRANSACTION mail, tekst :" +
                  text + ", from : " + from);
              

            java.util.Properties props = new java.util.Properties();
            String _smtp_host = properties.getProperty("mail.smtp.host","mail.devellop.com");
            String _smtp_port = properties.getProperty("mail.smtp.port","587");
            String _smtp_user = properties.getProperty("mail.smtp.user","no-reply@devellop.com");
            String _smtp_pass = properties.getProperty("mail.smtp.password","markomarkomarko");
            String _smtp_localhost = properties.getProperty("mail.smtp.localhost","replica");
            
            
            logger.info("_smtp_host = " + _smtp_host);
            logger.info("_smtp_port = " + _smtp_port);
            logger.info("_smtp_user = " + _smtp_user);
            logger.info("_smtp_pass = " + _smtp_pass);
            logger.info("_smtp_localhost = " + _smtp_localhost);

            props.put("mail.smtp.host", _smtp_host);
            props.put("mail.smtp.port", "" + _smtp_port);
            props.put("mail.smtp.localhost",_smtp_localhost);

            props.put("mail.smtp.auth", "true");
            Session session = Session.getDefaultInstance(props, new SMTAuthenticator(_smtp_user, _smtp_pass));

            // Construct the message
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(from));

            String to = properties.getProperty("stampa.transaction.recipient","edopune@stampa.rs");
            logger.info("to = " + to);
            msg.setRecipient(Message.RecipientType.TO,
                              new InternetAddress(to));

            msg.setSubject(subject);
            // create the message part
            MimeBodyPart messageBodyPart =
                    new MimeBodyPart();

            //fill message
            messageBodyPart.setText(
                    text);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);



            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source =
                    new FileDataSource(toSend);
            messageBodyPart.setDataHandler(
                    new DataHandler(source));
            messageBodyPart.setFileName(toSend.getName());
            multipart.addBodyPart(messageBodyPart);

            msg.setContent(multipart);

            // Send the message
            try{
                Transport.send(msg);
                logger.info(
                          "STAMPA transaction mail sent");
                    
            }catch(Throwable e) {
                throw new MessagingException("Unable to send message, unknown problem, details :" +
                                             e.getMessage());
            }
    }
    
    private class SMTAuthenticator extends Authenticator{

        private String _user = "";
        private String _pass = "";
        private SMTAuthenticator(String user, String pass){
          _user = user;
          _pass = pass;

        }


        public PasswordAuthentication getPasswordAuthentication(){
          return new PasswordAuthentication(_user,_pass);
        }


      }

}
