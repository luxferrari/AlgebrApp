package org.luxferrari.algebrapp.client;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.constants;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AnswerWindow extends PopupPanel{
	
	private VerticalPanel vPanel = new VerticalPanel();
	private HorizontalPanel hPanel = new HorizontalPanel();
	
	public AnswerWindow(HorizontalPanel h){
		super();
		this.setGlassEnabled(true);
		Label calculateText = new Label(constants.calculateText());
		Label clickResultText = new Label(constants.clickResultText());	
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		vPanel.add(calculateText);		
		h.addStyleName("popup-calculation");
		vPanel.add(h);
		vPanel.add(clickResultText);
		
		this.add(vPanel);
		vPanel.add(hPanel);
		this.setModal(true);
		this.setAutoHideEnabled(true);
		this.setAnimationEnabled(true);	
		
	}
	
	public void vAdd(Widget w){
		vPanel.add(w);		
	}
	
	public void hAdd(Widget w){
		hPanel.add(w);
	}
}
