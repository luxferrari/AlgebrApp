package org.luxferrari.algebrapp.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;

public class ContactForm extends PopupPanel {

	private TextArea message = new TextArea();
	private TextBox emailAddress = new TextBox();

	public ContactForm(){
		super();
		this.addStyleName("contact");
		//this.setWidth("600px");

		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel emailContainer = new HorizontalPanel();
		emailContainer.addStyleName("email");
		emailContainer.setVerticalAlignment(ALIGN);
		HorizontalPanel buttonContainer = new HorizontalPanel();
		buttonContainer.addStyleName("buttons");
		
		Label messageLabel = new Label(constants.messageLabel());
		messageLabel.addStyleName("messageLabel");
		message.setWidth("600px");
		message.setHeight("250px");
		message.setTitle("Inserire qui il testo del messaggio");
		message.addStyleName("messageText");

		Label emailAddressLabel = new Label(constants.emailAddressLabel());
		emailAddressLabel.addStyleName("emailAddressLabel");
		emailAddress.setTitle("Inserire qui l'indirizzo e-mail");
		emailAddress.setWidth("400px");
		emailAddress.addStyleName("emailAddress");
		emailAddress.addFocusHandler(new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent event) {
				if(emailAddress.getStyleName().contains("invalidInput")){
					emailAddress.setText("");
					emailAddress.removeStyleName("invalidInput");		
				}		
			}
		});

		Button sendButton = new Button(constants.sendButtonText());
		sendButton.setTitle(constants.sendButtonTooltip());
		sendButton.setStyleName("ui sendButton");
		sendButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(validateForm()) {
					sendMail();
				};
			}
		});

		Button cancelButton = new Button(constants.cancelButtonText());
		cancelButton.setTitle(constants.cancelButtonTooltip());
		cancelButton.setStyleName("ui cancelButton");
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		vp.add(messageLabel);
		vp.add(message);
		emailContainer.add(emailAddressLabel);
		emailContainer.add(emailAddress);
		vp.add(emailContainer);
		buttonContainer.add(cancelButton);
		buttonContainer.add(sendButton);
		vp.add(buttonContainer);
		//container.add(vp);
		this.add(vp);		
	}

	protected void sendMail() {				

		AlgebrAppServletAsync clientInfoSrv = GWT.create(AlgebrAppServlet.class);

		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				hide();
				msgPanel(constants.sendFailed());
			}

			public void onSuccess(Void result) {
				hide();
				msgPanel(constants.sendSuccess());				
			}	
		};		
		
		String emailText = message.getText();
		emailText += "\n* * *\n" + Window.Navigator.getUserAgent() + " -- "+Window.Navigator.getPlatform();
				
		try {
			msgPanel(constants.pleaseWait());
			clientInfoSrv.sendMail(emailText, callback);			

		} catch (Exception e) {
			setEmailErrorStyle(false);
		}
	}

	protected boolean validateForm() {
		String email = emailAddress.getText();
		boolean isEmailValid = email.equals("") || email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
		setEmailErrorStyle(isEmailValid);		
		
		boolean messageExists = message.getText().length() > 1;
		setMessageErrorStyle(messageExists);
		
		return isEmailValid && messageExists;
	}

	private void setEmailErrorStyle(boolean isValid){
		if(isValid) {
			emailAddress.removeStyleName("invalidInput");
		} else {
			emailAddress.setText(constants.invalidEmail());
			emailAddress.addStyleName("invalidInput");	
		} 	
	}

	private void setMessageErrorStyle(boolean isValid){
		if(isValid) {
			message.removeStyleName("invalidInput");
		} else {
			message.setText(constants.invalidMessage());
			message.addStyleName("invalidInput");	
		} 	
	}



}


