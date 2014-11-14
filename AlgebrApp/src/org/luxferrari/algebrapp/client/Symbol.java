package org.luxferrari.algebrapp.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;


public class Symbol extends Button{

	private boolean isClickable = true;
	private boolean isPolySelected = false;
	private Widget parent = null;

	public Symbol(String html, boolean clickable) {
		super();
		this.isClickable = clickable;
		this.setStyleName("symbol math");
		this.setHTML(html);		

		if(isClickable){
			
			addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) { 
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
	
	public Widget getMyParent() {
		return parent;
	}

	public void setMyParent(Widget parent) {
		this.parent = parent;
	}

	public void select() {
		if(isClickable){
			if(this.getMyParent() instanceof Product){
				((Polynomial)((HorizontalPanel)this.getParent()).getWidget(1)).select();				
			}
			else if(this.getMyParent() instanceof SubPolynomial){
				((Polynomial)((SubPolynomial)this.getMyParent()).getWidget(1)).select();
			}
			isPolySelected = true;
		}
	}
	public void deselect() {
		if(isClickable){if(this.getMyParent() instanceof Product){
			((Polynomial)((HorizontalPanel)this.getParent()).getWidget(1)).deselect();				
		}
		else if(this.getMyParent() instanceof SubPolynomial){
			((Polynomial)((SubPolynomial)this.getMyParent()).getWidget(1)).deselect();
		}
			isPolySelected = false;
		}
	}

	public void setTooltip(){
		this.setTitle(constants.tooltipPolynomial());
	}
}

