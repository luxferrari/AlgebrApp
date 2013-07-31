package org.luxferrari.algebrapp.client;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;

import org.luxferrari.algebrapp.client.RandomGenerator;


public final class AlgebrApp implements EntryPoint {	
	
	/*------------------------------------------------------
	 * 		Initial settings
	 *------------------------------------------------------*/

	@Override
	public void onModuleLoad() {

		/*// Generic UncaughtExceptionHandler
		GWT.setUncaughtExceptionHandler(new   
				GWT.UncaughtExceptionHandler() {  
			public void onUncaughtException(Throwable e) {  
				msgPanel(e.getStackTrace().toString());  
			}  
		});*/

		// Set the window title and the header text
		Window.setTitle(constants.AlgebrAppTitle());
		RootPanel.get("AlgebrAppHeader").add(new Label(constants.AlgebrAppHeader()));


		RootPanel.get().add(wrapPanel);
		wrapPanel.add(mainPanel);
		wrapPanel.addStyleName("wrap-panel");
		mainPanel.addStyleName("main-panel");

		/*------------------------------------------------------
		 * 		Drag and drop 
		 *------------------------------------------------------*/

		mainDragController.setBehaviorConstrainedToBoundaryPanel(true);
		mainDragController.setBehaviorDragStartSensitivity(3);


		/*------------------------------------------------------
		 * 		Create expression
		 *------------------------------------------------------*/


		// Aggiunge il DragHandler

		MonomialDragHandler dragHandler = new MonomialDragHandler();
		mainDragController.addDragHandler(dragHandler);



		refreshExpression();
		
		// Refresh button

		Button refresh = new Button();
		refresh.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				msgPanel();
				refreshExpression();
			}
		});
		refresh.addStyleName("refresh");
		refresh.setTitle(constants.refreshButtonTooltip());
		mainPanel.add(refresh);

		operate.setText(constants.operateButtonText());
		operate.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				msgPanel();
				checkOperation();
			}
		});
		operate.setTitle(constants.operateButtonTooltip());
		mainPanel.add(operate);		


		/*Button test = new Button("test");
		test.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				HashMap map2 = mainPoly.contentMap();

			}
		});
		mainPanel.add(test);	*/

		mainPanel.add(msgFrame);


	}



	public void refreshExpression(){
		
		mainDragController.unregisterDropControllers();
		rndGenerator = new RandomGenerator();

		//	Dispose
		if(getMainPoly() != null){
			mainPanel.remove(getMainPoly());
			getMainPoly().disposeOfMembers();
		}		

		// TODO cambiare metodo con quello definitivo
		//setMainPoly(createTestPoly());
		setMainPoly(rndGenerator.randomPolynomial(10, 3, 10, 2, 1));
		mainPoly.refreshPolynomial();
		getMainPoly().addStyleName("main-poly");
		mainPanel.insert(getMainPoly(), MAINPOLY_IN_MAINPANEL); 
	}

	private Polynomial createTestPoly() {
		// TODO metodo temporaneo
		int ordine = 2;
		int rangeCoefficienti = 10;
		int numLettere = 2;

		final Polynomial poly = new Polynomial();
		poly.addMonomial(rndGenerator.randomMonomials(2, ordine, rangeCoefficienti, numLettere));

		/*Polynomial factor_1 = new Polynomial();
		Polynomial factor_2 = new Polynomial();
		factor_1.addMonomial(rndGenerator.randomMonomials(2, ordine, rangeCoefficienti, numLettere));
		factor_2.addMonomial(rndGenerator.randomMonomials(2, ordine, rangeCoefficienti, numLettere));
		Product prod_1 = new Product(factor_1, factor_2);			

		poly.insertProduct(prod_1, 2);*/

		Polynomial factor_3 = new Polynomial();
		Polynomial factor_4 = new Polynomial();
		Monomial f3 = rndGenerator.randomMonomials(1, ordine, rangeCoefficienti, numLettere)[0];
		Monomial f4 = rndGenerator.randomMonomials(1, ordine, rangeCoefficienti, numLettere)[0];
		
		System.err.println(f3.getCoefficient()+" * "+f4.getCoefficient());
		
		factor_3.addMonomial(f3);
		factor_4.addMonomial(f4);
		Product prod_2 = new Product(factor_3, factor_4);		

		poly.insertProduct(prod_2, 0);		

		poly.refreshPolynomial();
		return poly;
	}

	public static void checkOperation(){

		if(selectedWidgets.isEmpty()){
			msgPanel("Nessun elemento selezionato!!!");
		}
		else{
			selectedItemsList = new SelectedItemsList(selectedWidgets);
			selectedWidgets.clearSelected();

			errorType checkAddition = selectedItemsList.canPerformAddition();
			errorType checkProduct =  null;			
			final Product p = selectedItemsList.returnProduct();
			if(p == null) checkProduct = errorType.PRECEDENZE;
			else if(!p.isProductReduced()) checkProduct = errorType.NON_RIDOTTO;
			else checkProduct = errorType.NONE;

			if(checkAddition == errorType.NON_SIMILI){
				msgPanel("Non puoi eseguire il calcolo, gli addendi non sono monomi simili!!!");
				return;
			}
			if(checkProduct == errorType.NON_RIDOTTO){
				msgPanel("I fattori non sono stati calcolati completamente!!!");
				return;
			}			
			if(checkProduct == errorType.PRECEDENZE && checkAddition == errorType.PRECEDENZE){
				msgPanel("Non puoi eseguire questo calcolo, non rispetta le precedenze!!!");
				return;
			}

			dial = new DialogBox();
			Button b = new Button("Ok");
			b.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {	
					dial.hide();
				}
			});

			final VerticalPanel vPanel = new VerticalPanel();
			final HorizontalPanel hPanel = new HorizontalPanel();			


			if(checkAddition == errorType.NONE){

				Polynomial addition = new Polynomial(selectedItemsList.getAdditionPolynomial(), false);
				addition.addStyleName("popup-calculation");
				vPanel.add(addition);

				ArrayList<Monomial> choices = selectedItemsList.additionResultsList();
				rightAnswerAddition = choices.get(0);
				shuffle(choices, rndGenerator);

				for(Monomial item : choices){
					hPanel.add(makeButtonFromMonomial(item));
				}
			}
			else if(checkProduct == errorType.NONE){

				Product product = new Product(p, false);
				product.addStyleName("popup-calculation");
				vPanel.add(product);
				product.refreshProduct();

				ArrayList<Polynomial> choices = p.productResultsList();
				rightAnswerProduct = choices.get(0);
				shuffle(choices, rndGenerator);

				for(Polynomial item : choices){
					vPanel.add(makeButtonFromPoly(item));
				}
			}

			vPanel.add(hPanel);
			vPanel.add(b);
			dial.add(vPanel);
			dial.setModal(true);
			int cx = mainPoly.getAbsoluteLeft() + mainPoly.getOffsetWidth() / 2;
			int cy = mainPoly.getAbsoluteTop() + mainPoly.getOffsetHeight() / 2;
			int x = cx - dial.getOffsetWidth() / 2;
			int y = cy - dial.getOffsetHeight() / 2;

			dial.setPopupPosition(x,y);
			dial.show();
		}			
	}

	public static Button makeButtonFromMonomial(final Monomial m){		
		Button result = new Button();
		result.setHTML(m.getHTML());
		result.addStyleName("math answer-button");
		result.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(m.equals(rightAnswerAddition)){
					dial.hide();
					msgPanel("Corretto!");
					executeAddition();
					dial = null;

				}
				else{
					dial.hide();
					msgPanel("Non corretto");
					dial = null;
				}
			}

		});
		return result;
	}	

	private static void executeAddition() {
		Monomial first = (Monomial)selectedItemsList.get(0);
		Polynomial p = first.getParentPoly();
		int position = p.getWidgetIndex(first);		
		p.removeMonomial((List)selectedItemsList);
		if(rightAnswerAddition.getCoefficient() != 0){
			p.insertMonomial(new Monomial(rightAnswerAddition, true), position);
		}
		selectedItemsList = null;
		checkSuccess();
	}

	public static Button makeButtonFromPoly(final Polynomial p){
		Button result = new Button();
		result.addStyleName("math answer-button");
		result.setHTML(p.getElement().getInnerHTML());	
		result.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(p.isEqualTo(rightAnswerProduct)){
					dial.hide();
					msgPanel("Corretto!");
					executeProduct();
					dial = null;
				}
				else{
					dial.hide();
					msgPanel("Non corretto");
					dial = null;
				}
			}
		});			
		return result;
	}

	private static void executeProduct(){
		Product p = selectedItemsList.returnProduct();		
		Polynomial container = p.getParentPoly();
		int position = container.getWidgetIndex(p);		
		p.getFirstFactorContent().disposeOfMembers();
		p.getSecondFactorContent().disposeOfMembers();
		p.removeFromParent();
		container.insertPolynomial(rightAnswerProduct, position);
		selectedItemsList= null;
		checkSuccess();
	}

	private static void checkSuccess(){
		mainPoly.refreshPolynomial();
		if(mainPoly.checkReduced()) {
			msgPanel();
			msgPanel("Successo!!!");

		}
	}
}
