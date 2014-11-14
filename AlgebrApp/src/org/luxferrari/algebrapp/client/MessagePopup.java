package org.luxferrari.algebrapp.client;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.msgBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class MessagePopup extends PopupPanel {

	Label text = new Label();


	public MessagePopup(String s, boolean clickToHide) {
		super();
		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
		this.addStyleName("messagePopup");
		text.setText(s);
		text.addStyleName("messagePopupText");

		if(clickToHide) {
			text.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) { 
					msgBox.hide();
				}
			});
		}

		this.add(text);


	}

	public MessagePopup(String s){
		this(s, true);
	}

	public MessagePopup(){
		this("");
	}

	public void setText(String s){
		text.setText(s);
	}

}
