package org.luxferrari.algebrapp.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;


public class Symbol extends Button{

	private boolean isClickable = true;
	private boolean isPolySelected = false;

	public Symbol(String html, boolean clickable) {
		super();
		this.isClickable = clickable;
		this.setStyleName("symbol math");
		this.setHTML(html);		

		if(isClickable){
			
			addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) { 
					msgPanel();
					if(isPolySelected){
						deselect();
					}
					else{
						select();
					}
				}
			});		
		}
	}

	public Symbol(String html){
		this(html, true);
	}

	// Set selection states 
	
	public void select() {
		if(isClickable){			
			((Polynomial)((HorizontalPanel)this.getParent()).getWidget(1)).select();
			isPolySelected = true;
		}
	}
	public void deselect() {
		if(isClickable){
			((Polynomial)((HorizontalPanel)this.getParent()).getWidget(1)).deselect();
			isPolySelected = false;
		}
	}

	public void setTooltip(){
		this.setTitle(constants.tooltipPolynomial());
	}
}

