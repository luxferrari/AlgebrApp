package org.luxferrari.algebrapp.server;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.luxferrari.algebrapp.client.AlgebrAppServlet;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.DEBUG;

public class AlgebrAppServletImpl extends RemoteServiceServlet implements AlgebrAppServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2176914091851160784L;

	private String getClientInfo() {
		String result =  "\nHost: "+this.getThreadLocalRequest().getRemoteHost() + "\n";
		result += "Address: "+this.getThreadLocalRequest().getRemoteAddr() + "\n";
		result += "User: "+this.getThreadLocalRequest().getRemoteUser() + "\n";
		if (DEBUG) System.err.println("getClientInfo() returns "+result);
		return result;		
	}

	public void sendMail(String text) throws Exception{
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		System.err.println("sendMail");

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("luxferrari@gmail.com", "AlgebrApp"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress("luxferrari@gmail.com", "Me"));
			msg.setSubject("AlgebrApp Contact Form");

			String emailText = text;
			emailText += getClientInfo();			

			msg.setText(emailText);
			Transport.send(msg);

		} catch (Exception e) {
			throw e;
		} 
	}


}