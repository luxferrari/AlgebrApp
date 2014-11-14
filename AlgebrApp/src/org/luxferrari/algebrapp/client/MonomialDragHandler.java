package org.luxferrari.algebrapp.client;

import java.awt.dnd.DropTarget;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HorizontalPanel;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;
import static com.google.gwt.query.client.GQuery.$;


public class MonomialDragHandler implements DragHandler {

	Polynomial source;
	HorizontalPanel daddy;
	Monomial draggable;
	int init_x = 0;
	int init_y = 0; 
			
	@Override
	public void onPreviewDragStart(DragStartEvent event)
			throws VetoDragException {

		// Memorizza nella stringa sourceHash l'origine del monomio durante il drag
			
		Widget dragSource = null;

		try {
			dragSource = event.getContext().draggable.getParent();
		} catch (Exception e) {
			if(DEBUG){System.err.println("Event: "+event+ " - Exception: "+e);}	
		}

		if(dragSource instanceof Polynomial){
			source = (Polynomial)dragSource;
			
			draggable = (Monomial)event.getContext().draggable;
			init_x = draggable.getAbsoluteLeft();
			init_y = draggable.getAbsoluteTop();
			
			
			}
		else{
			throw new VetoDragException(); // Non dovrebbe accadere
		}
	}

	@Override
	public void onDragStart(DragStartEvent event) {

	}

	@Override
	public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {

		// Accetta come dropTarget soltanto un polinomio, altrimenti il drag viene annullato
		InsertPanel dropTarget = null;
		Polynomial target = null;

		try {
			dropTarget = (InsertPanel) event.getContext().dropController.getDropTarget();
		} catch (Exception e) {
			if(DEBUG){System.err.println("onPreviewDragEnd -> getDropTarget -> "+event+ " - Exception: "+e);}	
		}


		if(dropTarget instanceof Polynomial){	// Permette spostamenti all'interno di un polinomio
			target = (Polynomial)dropTarget;
			if (!source.equals(target)){		
				
				msgPanel(constants.notAllowed());
				errorCounter++;				
				revert(event);
			}			
		}
		else{
			msgPanel("!?");	
			revert(event);
		}		
	}
	
	private void revert(DragEndEvent event) throws VetoDragException {
		Integer x = event.getContext().desiredDraggableX ; 
		Integer y = event.getContext().desiredDraggableY ; 
		
		$(draggable).css("left", x - init_x + "").css("top", y - init_y + "");
		$(draggable).animate("{left: 0 ; top: 0 }", 500);
		
		throw new VetoDragException();
	}
		
	@Override
	public void onDragEnd(DragEndEvent event) {
		// Dopo il drop				
		
		try {
			InsertPanel dropTarget = (InsertPanel) event.getContext().finalDropController.getDropTarget();
			if(dropTarget instanceof Polynomial){
				mainPoly.refresh();			
			}
		} catch (Exception e) {			
			if(DEBUG){System.err.println("onDragEnd -> "+event+ " - Exception: "+ e + " Message: "+e.getMessage());		
			}
		}
	}
}


