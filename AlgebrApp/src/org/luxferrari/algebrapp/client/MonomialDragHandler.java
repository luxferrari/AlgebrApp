package org.luxferrari.algebrapp.client;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HorizontalPanel;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;


public class MonomialDragHandler implements DragHandler {

	Polynomial source;
	//boolean commute = false;
	HorizontalPanel daddy = null;

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
			
			if(source.getParent() instanceof HorizontalPanel){
				int index;				
				daddy = (HorizontalPanel)source.getParent();
				index = daddy.getWidgetIndex(source);
				
				if(index == POLY_BETWEEN_PARENTHESIS && source.getLength() < 2){
					daddy.getWidget(index - 1).setVisible(false);
					daddy.getWidget(index + 1).setVisible(false);
				}
			}
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
		Widget dropTarget = null;
		Polynomial target = null;

		try {
			dropTarget = event.getContext().dropController.getDropTarget();
		} catch (Exception e) {
			if(DEBUG){System.err.println("onPreviewDragEnd -> getDropTarget -> "+event+ " - Exception: "+e);}	
		}


		if(dropTarget instanceof Polynomial){	// Permette spostamenti all'interno di un polinomio
			target = (Polynomial)dropTarget;
			
			if (!source.equals(target)){
				
				if(source.getParent() instanceof HorizontalPanel){
					int index;
					daddy = (HorizontalPanel)source.getParent();
					index = daddy.getWidgetIndex(source);
					if(index == POLY_BETWEEN_PARENTHESIS && source.getLength() < 2){
						daddy.getWidget(index - 1).setVisible(true);
						daddy.getWidget(index + 1).setVisible(true);
					}
				}
				
				msgPanel(constants.notAllowed());
				throw new VetoDragException();
			}
		}
		else{
			msgPanel("!?");
			throw new VetoDragException();	
		}		
	}

	@Override
	public void onDragEnd(DragEndEvent event) {
		// Dopo il drop		
		
		
		try {
			Widget dropTarget = event.getContext().finalDropController.getDropTarget();
			if(dropTarget instanceof Polynomial){
				//((Polynomial) dropTarget).refreshPolynomial();
				mainPoly.refreshPolynomial();
				
			}			
		} catch (Exception e) {
			if(DEBUG){System.err.println("onDragEnd -> "+event+ " - Exception: "+ e + " Message: "+e.getMessage());		
			}
		}
	}
}


