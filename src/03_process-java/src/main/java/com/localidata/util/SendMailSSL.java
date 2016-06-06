package com.localidata.util;
import java.util.Properties;



import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

/**
 * 
 * @author Localidata
 */
public class SendMailSSL {
	private static Logger log = Logger.getLogger(SendMailSSL.class);
	private Properties props=null;
	
	
	public SendMailSSL()
	{
		props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
	}
	
	public void enviar(final String usuario, final String password, String destinos,String asunto, String texto)
	{	
		
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(usuario,password);
			}
		});
		
		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(usuario));
			
			message.setRecipients(Message.RecipientType.TO,	InternetAddress.parse(destinos));
			
			
			message.setSubject(asunto);
			message.setText(texto);

			Transport.send(message);


		} catch (MessagingException e) {
			log.error("Error sending mail:");
			log.error("\t"+e.getMessage());
		}
		
	}
	
	
	
	public static void main(String[] args) {
		SendMailSSL send=new SendMailSSL();
		send.enviar("estadisticasLocalidata@gmail.com", "Est@d1st1c@s13", "developer@localidata.com", "test", "hoy es viernes");
		
		
	}
}