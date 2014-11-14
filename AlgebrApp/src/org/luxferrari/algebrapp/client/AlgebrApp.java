package org.luxferrari.algebrapp.client;


import gwtquery.plugins.ui.widgets.Accordion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;
import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.ui.Ui.Ui;

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

		// URL settings	

		if(Window.Location.getParameter("s") != null){
			String par = Window.Location.getParameter("s");
			if(par.toLowerCase().equals("true") || par.equals("1")) SIMPLIFIED = true;
			else if(par.toLowerCase().equals("false") || par.equals("0")) SIMPLIFIED = false;
			else if(DEBUG) System.err.println("Parametro 's' invalido, return "+Window.Location.getParameter("s"));
		}	

		if(Window.Location.getParameter("f") != null){
			String par = Window.Location.getParameter("f");
			if(par.toLowerCase().equals("true") || par.equals("1")) FORCE_NEIGHBOURS = true;
			else if(par.toLowerCase().equals("false") || par.equals("0")) FORCE_NEIGHBOURS = false;
			else if(DEBUG) System.err.println("Parametro 'f' invalido, return "+Window.Location.getParameter("f"));
		}	

		int urlLevel = -1;		
		if(Window.Location.getParameter("l") != null){
			try {
				urlLevel = Integer.valueOf(Window.Location.getParameter("l"));
			} catch (NumberFormatException e) {
				if(DEBUG) System.err.println("Parametro 'l' invalido, return "+Window.Location.getParameter("l"));
			}
		}		
		if(urlLevel > -1 && urlLevel < MAX_LEVEL) {
			INITIAL_SLIDER_VALUE = urlLevel;
			SIMPLIFIED = urlLevel > 2 ? true : false;
		}


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



		// Help button

		help.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showHelp();
			}
		});
		help.addStyleName("ui help");
		help.setTitle(constants.helpButtonTooltip());
		RootPanel.get("AlgebrAppHeader").add(help);

		// Statistics button

		statistics.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showStatistics();
			}
		});
		statistics.addStyleName("ui statistics");
		statistics.setTitle(constants.statisticsButtonTooltip());
		RootPanel.get("AlgebrAppHeader").add(statistics);

		// Contact button

		contact.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showContactForm();
			}
		});
		contact.addStyleName("ui contact");
		contact.setTitle(constants.statisticsButtonTooltip());
		mainPanel.add(contact);

		Label copyright = new Label();
		copyright.getElement().setInnerHTML("&copy;L.F.2010-2013");
		copyright.getElement().setId("copy");
		mainPanel.add(copyright);

		// Aggiunge il DragHandler

		MonomialDragHandler dragHandler = new MonomialDragHandler();
		mainDragController.addDragHandler(dragHandler);

		// Settings menu

		createSettingsMenu();

		// Polinomio

		refreshExpression();



		// Print safe


		Event.addNativePreviewHandler(new NativePreviewHandler() {

			PopupPanel show = new PopupPanel(true, true);

			@Override			
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				NativeEvent ne = event.getNativeEvent(); 
				if (Z_ONLY && ne.getCtrlKey() && (ne.getKeyCode()=='P' || ne.getKeyCode()=='p')) { 
					ne.preventDefault(); 
					showPastablePoly();
				} 
			}

			private void showPastablePoly() {
				Label title = new Label(constants.pastableText());

				String html = mainPoly.getElement().getInnerHTML();				
				html = html.replaceAll("(?i)<script.*?</script>", "");
				html = html.replaceAll("(?i)<([a-zA-Z0-9-_]*)(\\s[^>]*)>", "<$1>");
				html = html.replaceAll("(?i)<(?!(/?(sup)))[^>]*>", "");	
				HTML pastable = new HTML(html);

				FlowPanel pastableContainer = new FlowPanel();

				show.setGlassEnabled(true);
				show.setAnimationEnabled(true);

				title.addStyleName("pastable-title");
				pastable.addStyleName("math pastable");

				pastableContainer.add(title);
				pastableContainer.add(pastable);
				show.clear();
				show.add(pastableContainer);
				show.center();

				markText(pastable.getElement());

				ClickHandler handler = new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						show.hide();
					}
				};

				show.sinkEvents(Event.ONCLICK);
				show.addHandler(handler, ClickEvent.getType());
			} 

			private native void markText(Element elem) /*-{
		    if ($doc.selection && $doc.selection.createRange) {
		        var range = $doc.selection.createRange();
		        range.moveToElementText(elem);
		        range.select();
		    } else if ($doc.createRange && $wnd.getSelection) {
		        var range = $doc.createRange();
		        range.selectNode(elem);
		        var selection = $wnd.getSelection();
		        selection.removeAllRanges();
		        selection.addRange(range);
		    }
		}-*/;

		}); 
	}



	public static void refreshExpression(){

		selectedWidgets.clearSelected();
		mainDragController.unregisterDropControllers();
		rndGenerator = new RandomGenerator();	

		// Reset success button

		if(!success.isVisible()){
			success.setVisible(true);
			refresh.removeStyleName("glowingButton");
		}

		//	Dispose
		if(getMainPoly() != null){
			mainPanel.remove(getMainPoly());
			getMainPoly().destroy();
		}	

		// Regenerate		
		if(errorCounter > 0) getCurrentHistory()[2] = errorCounter;
		errorHistory += errorCounter;
		errorCounter = 0;

		int level = INITIAL_SLIDER_VALUE;
		if(levelSlider.getValue() != null) level = levelSlider.getValue();
		if(level < 3) {
			NO_LETTERS = true;
		}
		else {
			NO_LETTERS = false;
		}

		double[] settings = settingsArray[level];
		setMainPoly(rndGenerator.randomPolynomial(settings));
		rndGenerator.primeFactorsChoices();
		mainPoly.refresh();

		//setMainPoly(createTestPoly());

		mainPoly.addStyleName("mainPoly");
		if(Z_ONLY){
			mainPoly.removeStyleName("Q");
			mainPoly.addStyleName("Z");
		}
		else{
			mainPoly.removeStyleName("Z");
			mainPoly.addStyleName("Q");
		}
		mainPanel.insert(mainPoly, MAINPOLY_IN_MAINPANEL); 

		// Create new statistics
		Integer[] a = new Integer[]{0,0,0,0,0};
		a[0] = level;
		a[1] = mainPoly.getTotalLength();
		a[2] = errorCounter;

		// Add current statistics to history or replace latest - if no 'moves' done

		if(getCurrentHistory() == null || getCurrentHistory()[4] > 0){ 
			history.add(a);
		}
		else{
			history.remove(history.size() - 1);
			history.add(a);
		}
	}

	private static Polynomial createTestPoly() {
		// TODO metodo temporaneo
		int ordine = 1;
		int rangeCoefficienti = 10;
		int numLettere = 1;

		final Polynomial poly = new Polynomial();
		poly.addItem(rndGenerator.randomMonomials(2, ordine, rangeCoefficienti, numLettere, false));

		Polynomial factor_1 = new Polynomial();
		Polynomial factor_2 = new Polynomial();
		//NO_LETTERS = false;
		factor_1.addItem(rndGenerator.randomMonomials(2, 1, rangeCoefficienti, 2, true));
		factor_2.addItem(rndGenerator.randomMonomials(2, 1, rangeCoefficienti, 2, true));
		Product prod_1 = new Product(factor_1, factor_2);	
		poly.insertProduct(prod_1, 0);

		
		Polynomial p2 = new Polynomial();
		
		p2.addItem(rndGenerator.randomMonomials(1, ordine, rangeCoefficienti, numLettere, false));
		
		poly.insertItem(p2, 0, true, true, 15);

		poly.refresh();
		
		return poly;
	}

	public static void checkOperation(){

		if(selectedWidgets.size() == 0) msgPanel(constants.noSelection());		
		else{
			selectionCopy = new SelectedItemsList(selectedWidgets);
			selectedWidgets.clearSelected();

			errorType checkSimplifyMonomial = null;	

			if(selectionCopy.size() == 1){

				if(((MathItem)selectionCopy.get(0)).getParentProduct() != null){
					msgPanel(constants.orderOfOperations());
					return;
				}				
				else if(!selectionCopy.containsSubPoly() && selectionCopy.get(0) instanceof Monomial){
					if(!((Monomial)selectionCopy.get(0)).isSimplified()) checkSimplifyMonomial = errorType.NONE;
					else {
						msgPanel(constants.incorrectSelection());
						return;
					}
				}	
				else if(!selectionCopy.containsSubPoly()){
					msgPanel(constants.incorrectSelection());
					return;
				}
			}

			errorType checkSubPoly = null;

			if(selectionCopy.containsSubPoly()){
				if(selectionCopy.getSubPoly().getContent().isReduced()) checkSubPoly = errorType.NONE;
				else checkSubPoly = errorType.NOT_REDUCED;
			}

			errorType checkAddition = selectionCopy.canPerformAddition();

			errorType checkProduct =  null;			
			final Product p = selectionCopy.returnProduct();
			if(p == null) checkProduct = errorType.ORDER_OF_OPERATIONS;
			else if(!p.isProductReduced()) checkProduct = errorType.NOT_REDUCED;
			else checkProduct = errorType.NONE;


			if(checkSimplifyMonomial == errorType.NONE){				

				Monomial m = new Monomial((Monomial)selectionCopy.get(0), false);
				Polynomial calculation = new Polynomial(false);
				calculation.addItem(m);
				dial = new AnswerWindow(calculation);

				ArrayList<Monomial> choices = new ArrayList<Monomial>();
				choices.add(new Monomial(m, false, true));
				choices.add(new Monomial(m.getCoefficient().multiply(-1), m.getLiterals(), false, true));

				rightAnswerAddition = choices.get(0);
				shuffle(choices, rndGenerator);

				for(Monomial item : choices){
					dial.hAdd(makeButtonFromMonomial(item, false));
				}

				dial.center();
			}
			else if(checkSubPoly == errorType.NONE){

				SubPolynomial subPoly = new SubPolynomial(selectionCopy.getSubPoly());
				SubPolynomial calculation = new SubPolynomial(subPoly);
				dial = new AnswerWindow(calculation);

				ArrayList<Polynomial> choices = subPoly.polyResultList();
				rightAnswerPoly = choices.get(0);
				shuffle(choices, rndGenerator);

				for(Polynomial item : choices){
					dial.vAdd(makeButtonFromPoly(item, false));
				}	

				dial.center();
			}
			else if(checkAddition == errorType.NONE){

				Polynomial addition = new Polynomial(selectionCopy.getAdditionPolynomial(), false);
				addition.addStyleName("popup-calculation");				
				addition.refresh();				
				dial = new AnswerWindow(addition);

				selectionCopy.normalizeAddition();

				ArrayList<Monomial> choices = selectionCopy.additionResultsList();
				choices.removeAll(Collections.singleton(null));
				rightAnswerAddition = choices.get(0);
				shuffle(choices, rndGenerator);

				for(Monomial item : choices){
					dial.hAdd(makeButtonFromMonomial(item, true));
				}

				dial.center();
			}
			else if(checkProduct == errorType.NONE){
				Product product = new Product(p, false, false);
				product.addStyleName("popup-calculation");				
				product.refresh();
				dial = new AnswerWindow(product);

				ArrayList<Polynomial> choices = p.productResultsList();
				choices.removeAll(Collections.singleton(null));
				rightAnswerPoly = choices.get(0);
				shuffle(choices, rndGenerator);

				for(Polynomial item : choices){
					dial.vAdd(makeButtonFromPoly(item, true));
				}

				dial.center();
			}
			else if(checkAddition == errorType.NOT_NEIGHBOURS){
				msgPanel(constants.notNeighbours());
				return;
			}
			else if(checkProduct == errorType.NOT_REDUCED || checkSubPoly == errorType.NOT_REDUCED){
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
		}			
	}


	public static Button makeButtonFromMonomial(final Monomial m, final boolean executeAddition){		
		Button result = new Button();
		result.setHTML(m.getHTML());
		result.addStyleName("math answer-button");
		result.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(m.equals(rightAnswerAddition)){
					dial.hide();
					msgPanel(constants.correct());
					if(executeAddition) executeAddition();
					else changeSign();
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void executeAddition() {
		Monomial first = (Monomial)selectionCopy.get(0);		
		Polynomial p = additionParentPoly;
		int position = p.getWidgetIndex(first);		
		if(position == -1){
			position = first.getParentPoly().getParentPoly().getWidgetIndex(first.getParentPoly().getParentSubPoly());
		}
		p.removeMonomial((List)selectionCopy);		
		Monomial result = new Monomial(rightAnswerAddition, true, true);

		// check whether result shall be non simplified
		for(Selectable item : selectionCopy){
			if(!((Monomial)item).isSimplified()) {
				result.isSimplified(false);
				result.hasPlus(true);
			}
		}

		p.insertItem(result, position);
		additionParentPoly = null;
		selectionCopy = null;
		mainPoly.refresh();

	}

	private static void changeSign() {
		Monomial m = (Monomial)selectionCopy.get(0);		

		m.isSimplified(true);

		selectionCopy = null;
		mainPoly.refresh();

	}

	public static Button makeButtonFromPoly(final Polynomial p, final boolean executeProduct){
		Button result = new Button();
		result.addStyleName("math answer-button");
		result.setHTML(p.getElement().getInnerHTML());	
		result.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(p.isEqualTo(rightAnswerPoly)){
					dial.hide();
					msgPanel(constants.correct());
					if(executeProduct) executeProduct();
					else removeParenthesis();
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
		Product p = selectionCopy.returnProduct();		
		Polynomial container = p.getParentPoly();
		int position = container.getWidgetIndex(p);		
		p.destroy();
		if(!SIMPLIFIED){
			for(Monomial item : rightAnswerPoly.getChildrenMonomials()){
				item.isSimplified(false);
				item.hasPlus(true);
			}
		}
		container.insertItem(rightAnswerPoly, position);
		selectionCopy= null;
		mainPoly.refresh();
	}

	private static void removeParenthesis(){
		SubPolynomial p = selectionCopy.getSubPoly();
		Polynomial container = p.getParentPoly();
		int position = container.getWidgetIndex(p);
		p.destroy();
		container.insertItem(rightAnswerPoly, position);
		selectionCopy= null;
		mainPoly.refresh();
	}

	private static void checkSuccess(){
		if(mainPoly.isReduced()) {
			refresh.addStyleName("glowingButton");
			success.setVisible(false);
			if(errorCounter == 1) msgPanel(messages.calculationCompletedWithOneError());
			else msgPanel(messages.calculationCompleted(""+errorCounter));
			getCurrentHistory()[3] = 1;
		}
		else{
			msgPanel(constants.noSuccess());
		}
	}

	// Settings menu

	private static void createSettingsMenu(){

		FlowPanel menuPanel = new FlowPanel();
		FlowPanel menuFirstPanel = new FlowPanel();
		FlowPanel buttonPanel = new FlowPanel();
		FlowPanel zOnlyPanel = new FlowPanel();
		FlowPanel zOnlyContainer = new FlowPanel();
		RadioButton toggleZ;
		RadioButton toggleQ;	
		FlowPanel simplifiedPanel = new FlowPanel();
		FlowPanel simplifiedContainer = new FlowPanel();
		final Button simplified;		

		// Refresh button		
		refresh.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				refreshExpression();
			}
		});
		refresh.addStyleName("ui refresh");
		refresh.setTitle(constants.refreshButtonTooltip());
		buttonPanel.add(refresh);

		// Success button
		success.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {	
				checkSuccess();
			}
		});
		success.addStyleName("ui success");
		success.setTitle(constants.successButtonTooltip());
		buttonPanel.add(success);

		// Operate button		
		operate.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				msgPanel();
				checkOperation();
				getCurrentHistory()[4]++;
			}
		});
		operate.addStyleName("ui operate");
		operate.setTitle(constants.operateButtonTooltip());
		buttonPanel.add(operate);	

		// Button panel
		buttonPanel.setStyleName("buttonPanel");
		toolPanel.add(buttonPanel);


		//	Slider
		levelSlider =  new IncrementSlider();
		levelSlider.addStyleName("levelSlider");

		//	Radio button USE_Z

		toggleZ = new RadioButton("z-only");
		toggleQ = new RadioButton("z-only");
		toggleZ.setValue(Z_ONLY);
		toggleZ.setHTML("&#8484;");
		toggleZ.addStyleName("toggleButton");
		toggleZ.setTitle(constants.toggleZTooltip());
		toggleQ.setValue(!Z_ONLY);
		toggleQ.setHTML("&#8474;");
		toggleQ.addStyleName("toggleButton");
		toggleQ.setTitle(constants.toggleQTooltip());
		toggleZ.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Z_ONLY = true;
				refreshExpression();
			}
		});
		toggleQ.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Z_ONLY = false;
				refreshExpression();
			}
		});		
		zOnlyPanel.add(toggleZ);
		zOnlyPanel.add(toggleQ);
		zOnlyPanel.getElement().setId("z-only");
		zOnlyContainer.add(zOnlyPanel);
		zOnlyContainer.add(new Label(constants.textZOnly()));
		zOnlyContainer.addStyleName("zOnlyContainer");


		//	Button NO_LETTERS

		simplified = new Button("");
		simplified.setHTML(SIMPLIFIED ? constants.yes() : constants.no());
		simplified.addStyleName("checkButton");
		simplified.setTitle(constants.simplifiedTooltip());
		simplified.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				SIMPLIFIED = SIMPLIFIED ? false : true;
				simplified.setHTML(SIMPLIFIED ? constants.yes() : constants.no());				
				refreshSlider();
				refreshExpression();
			}
		});
		simplifiedPanel.add(simplified);
		simplifiedPanel.getElement().setId("simplified");
		simplifiedContainer.add(simplifiedPanel);
		simplifiedContainer.add(new Label(constants.textSimplified()));
		simplifiedContainer.addStyleName("simplifiedContainer");

		// Collapsible menu panel
		menuPanel.addStyleName("menuPanel");
		menuPanel.getElement().setId("settingsMenu");
		HTML menuHeader = new HTML();
		menuHeader.setHTML("<h5 class='menuHeader'>" + constants.menuTitle() + "</h5>");

		menuPanel.add(menuHeader);
		menuFirstPanel.add(simplifiedContainer);	
		menuFirstPanel.add(zOnlyContainer);
		menuFirstPanel.add(levelSlider);
		menuPanel.add(menuFirstPanel);

		toolPanel.add(menuPanel);

		$("#settingsMenu", toolPanel.getElement()).as(Ui).accordion(Accordion.Options.create().collapsible(true).active(false).autoHeight(false).clearStyle(true));
		$("#z-only", toolPanel.getElement() ).as(Ui).buttonset();
	}

	public static void refreshSlider(){		
		if(!SIMPLIFIED) MAX_LEVEL = 2;
		else MAX_LEVEL = 10;
		levelSlider.refreshElement();
	}

	private static <T> void shuffle(List<T> list, Random rnd) {
		int size = list.size();
		for (int i=size; i>1; i--)
			swap(list, i-1, rnd.nextInt(i));
	} 

	private static <T> void swap(List<T> list, int i, int j) {
		if(i == j){ 
			return;
		}
		T item_1 = list.get(i);
		T item_2 = list.get(j);
		list.remove(i);
		list.remove(j);
		if(i < j){
			list.add(i, item_2);
			list.add(j, item_1);
		}
		else{
			list.add(j, item_1);
			list.add(i, item_2);
		}		
	}
}
