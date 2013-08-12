package org.luxferrari.algebrapp.client;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
		wrapPanel.addStyleName("wrapPanel");
		mainPanel.addStyleName("mainPanel");

		toolPanel.setStyleName("toolPanel");
		mainPanel.add(toolPanel);

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


		// Polinomio

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
		buttonPanel.add(refresh);

		// Success button
		Button success = new Button();
		success.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {	
				checkSuccess();
			}
		});
		success.addStyleName("success");
		success.setTitle(constants.successButtonTooltip());
		buttonPanel.add(success);

		// Operate button		
		operate.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				msgPanel();
				checkOperation();
			}
		});
		operate.addStyleName("operate");
		operate.setTitle(constants.operateButtonTooltip());
		buttonPanel.add(operate);	

		// Button panel
		buttonPanel.setStyleName("buttonPanel");
		toolPanel.add(buttonPanel);

		// Slider		
		levelSlider.addStyleName("levelSlider");
		toolPanel.add(levelSlider);
	}



	public static void refreshExpression(){

		mainDragController.unregisterDropControllers();
		rndGenerator = new RandomGenerator();

		//	Dispose
		if(getMainPoly() != null){
			mainPanel.remove(getMainPoly());
			getMainPoly().disposeOfMembers();
		}	
		
		// Regenerate
		int level = 1;
		if(levelSlider.getValue() != null) level = levelSlider.getValue();
		double[] settings = settingsArray[level];
		Z_ONLY = (int) settings[0];
		setMainPoly(rndGenerator.randomPolynomial((int)settings[1], (int)settings[2], (int)settings[3], (int)settings[4], (double)settings[5]));
		mainPoly.refreshPolynomial();
		getMainPoly().addStyleName("mainPoly");
		mainPanel.insert(getMainPoly(), MAINPOLY_IN_MAINPANEL); 
	}

	/*private Polynomial createTestPoly() {
		// TODO metodo temporaneo
		int ordine = 2;
		int rangeCoefficienti = 10;
		int numLettere = 2;

		final Polynomial poly = new Polynomial();
		poly.addMonomial(rndGenerator.randomMonomials(2, ordine, rangeCoefficienti, numLettere));

		Polynomial factor_1 = new Polynomial();
		Polynomial factor_2 = new Polynomial();
		factor_1.addMonomial(rndGenerator.randomMonomials(2, ordine, rangeCoefficienti, numLettere));
		factor_2.addMonomial(rndGenerator.randomMonomials(2, ordine, rangeCoefficienti, numLettere));
		Product prod_1 = new Product(factor_1, factor_2);			

		poly.insertProduct(prod_1, 2);

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
	}*/

	public static void checkOperation(){

		if(selectedWidgets.isEmpty()){
			msgPanel(constants.noSelection());
		}
		else{
			selectedItemsList = new SelectedItemsList(selectedWidgets);
			selectedWidgets.clearSelected();

			errorType checkAddition = selectedItemsList.canPerformAddition();
			errorType checkProduct =  null;			
			final Product p = selectedItemsList.returnProduct();
			if(p == null) checkProduct = errorType.ORDER_OF_OPERATIONS;
			else if(!p.isProductReduced()) checkProduct = errorType.NOT_REDUCED;
			else checkProduct = errorType.NONE;

			dial = new PopupPanel();

			VerticalPanel vPanel = new VerticalPanel();
			HorizontalPanel hPanel = new HorizontalPanel();

			Label calculateText = new Label(constants.calculateText());
			Label clickResultText = new Label(constants.clickResultText());			


			vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

			if(checkAddition == errorType.NONE){

				vPanel.add(calculateText);

				Polynomial addition = new Polynomial(selectedItemsList.getAdditionPolynomial(), false);
				addition.addStyleName("popup-calculation");
				vPanel.add(addition);

				vPanel.add(clickResultText);

				ArrayList<Monomial> choices = selectedItemsList.additionResultsList();
				rightAnswerAddition = choices.get(0);
				shuffle(choices, rndGenerator);

				for(Monomial item : choices){
					hPanel.add(makeButtonFromMonomial(item));
				}
				vPanel.add(hPanel);
			}
			else if(checkProduct == errorType.NONE){

				vPanel.add(calculateText);

				Product product = new Product(p, false, false);
				product.addStyleName("popup-calculation");
				vPanel.add(product);
				product.refreshProduct();

				vPanel.add(clickResultText);

				ArrayList<Polynomial> choices = p.productResultsList();
				rightAnswerProduct = choices.get(0);
				shuffle(choices, rndGenerator);

				for(Polynomial item : choices){
					vPanel.add(makeButtonFromPoly(item));
				}
			}
			else if(checkProduct == errorType.NOT_REDUCED){
				msgPanel(constants.notReduced());
				errorCounter++;
				return;
			}
			else if(checkAddition == errorType.NOT_SIMILAR){
				msgPanel(constants.notSimilar());
				errorCounter++;
				return;
			}
			else if(checkProduct == errorType.ORDER_OF_OPERATIONS && checkAddition == errorType.ORDER_OF_OPERATIONS){
				msgPanel(constants.orderOfOperations());
				errorCounter++;
				return;
			}

			//vPanel.add(b);
			dial.add(vPanel);
			dial.setModal(true);
			dial.setAutoHideEnabled(true);
			dial.setAnimationEnabled(true);	

			dial.center();
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
					msgPanel(constants.correct());
					executeAddition();
					dial = null;

				}
				else{
					dial.hide();
					msgPanel(constants.incorrect());
					errorCounter++;
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
	}

	public static Button makeButtonFromPoly(final Polynomial p){
		Button result = new Button();
		result.addStyleName("math answer-button");
		result.setHTML(p.getElement().getInnerHTML());	
		result.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(p.isEqualTo(rightAnswerProduct)){
					dial.hide();
					msgPanel(constants.correct());
					executeProduct();
					dial = null;
				}
				else{
					dial.hide();
					msgPanel(constants.incorrect());
					errorCounter++;
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
	}

	private static void checkSuccess(){
		mainPoly.refreshPolynomial();
		if(mainPoly.checkReduced()) {
			if(errorCounter == 1) msgPanel(messages.calculationCompletedWithOneError());
			else msgPanel(messages.calculationCompleted(""+errorCounter));
		}
		else{
			msgPanel(constants.noSuccess());
		}
	}
}
