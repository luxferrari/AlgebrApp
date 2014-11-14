package org.luxferrari.algebrapp.client;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.ui.Ui.Ui;
import gwtquery.plugins.ui.widgets.Accordion;

import java.util.ArrayList;
import java.util.Arrays;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.query.client.Function;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AlgebrAppGlobals {					//  Veeery bad practice, they say. 

	/*------------------------------------------------------
	 * 		Global variables & magic numbers
	 *------------------------------------------------------*/

	public static final int POLY_BETWEEN_PARENTHESIS = 1;
	public static final int MAINPOLY_IN_MAINPANEL = 0;
	public static final boolean DEBUG = true;
	public static boolean SHOWDOT = true;

	public static boolean Z_ONLY = true;	
	public static boolean NO_LETTERS = false;
	public static boolean SIMPLIFIED = true;
	public static boolean FORCE_NEIGHBOURS = false;

	public static int MAX_LEVEL = 10;
	public static int INITIAL_SLIDER_VALUE = 3;

	public static final int SHOWN_ADDITIONS_NUMBER = 4;
	public static final int INCORRECT_ADDITION_CASES = 4;
	public static final int INCORRECT_LITERALS_CASES = 3;
	public static final int SHOWN_PRODUCTS_NUMBER = 4;
	public static final int INCORRECT_PRODUCTS_CASES = 4;
	public static final int SHOWN_POLYNOMIALS_NUMBER = 4;
	public static final String PLUS = " + ";
	public static final String MINUS = " &#150; ";

	public static final VerticalAlignmentConstant ALIGN = com.google.gwt.user.client.ui.HasVerticalAlignment.ALIGN_MIDDLE;

	public static boolean canRefresh = true;

	public static Integer errorCounter = 0;
	public static Integer errorHistory = 0;
	public static ArrayList<Integer[]> history = new ArrayList<Integer[]>();
	public static AlgebrAppConstants constants = GWT.create(AlgebrAppConstants.class);
	public static AlgebrAppMessages messages = GWT.create(AlgebrAppMessages.class);
	public static AbsolutePanel wrapPanel =  new AbsolutePanel();	
	public static VerticalPanel mainPanel = new VerticalPanel();
	public static FlowPanel toolPanel = new FlowPanel();

	public static Button help = new Button();
	public static Button statistics = new Button();
	public static Button contact = new Button();
	public static Button operate = new Button();
	public static Button refresh = new Button();
	public static Button success = new Button();
	public static IncrementSlider levelSlider;

	public static String[][] arrLiterals = {
		{"1","1","1"},
		{"a", "1", "b"},
		{"x", "y", "z"},
		{"a", "b", "c"},
		{"x","1","y"}, 
		{"u","v","z"}, 
		{"r","&#960;","1"}
	};		

	public enum errorType {
		ORDER_OF_OPERATIONS, 
		NOT_SIMILAR,
		NOT_REDUCED,
		NOT_NEIGHBOURS,		
		NONE,
	}

	public static double[][] settingsArray = {
		//totalLength		order			coeffRange		#literals		productFrequency	subPolyFreq		forProduct
		{5,					1,				10,				1,				0,					0,				0,},		// Level 0
		{6,					1,				15,				1,				0,					0.8,			0,},		// Level 1
		{8,					1,				20,				1,				1,					1,				0,},		// Level 2
		{5,					1,				5,				2,				0,					0,				0,},		// Level 3
		{5,					2,				10,				2,				0,					1,				0,},		// Level 4
		{8,					2,				15,				2,				0.7,				1,				0,},		// Level 5
		{8,					2,				15,				3,				1,					0.5,			0,},		// Level 6
		{10,				2,				20,				2,				0.8,				1,				0,},		// Level 7
		{10,				3,				20,				2,				1,					1,				0,},		// Level 8
		{12,				3,				20,				2,				1,					1,				0,},		// Level 9
		{12,				3,				20,				3,				1,					1,				0,},		// Level 10
	};

	public static int[] primesArray = {2, 3, 5, 7, 11, 13, 17};
	public static int[] chosenFactors;
	public static int primeFactorsNumber = 1;

	public static PickupDragController mainDragController = new PickupDragController(wrapPanel, true);
	public static SelectedItemsList selectedWidgets = new SelectedItemsList();

	public static Polynomial mainPoly = null;	
	public static RandomGenerator rndGenerator = new RandomGenerator();

	public static Monomial rightAnswerAddition;
	public static Polynomial rightAnswerProduct;
	public static Polynomial rightAnswerPoly;
	public static Polynomial additionParentPoly;
	public static SelectedItemsList selectionCopy;
	public static AnswerWindow dial;
	public static MessagePopup msgBox = new MessagePopup();

	public static Timer popupClose = new Timer() {
		public void run() {
			msgBox.hide();
		}
	};


	/*------------------------------------------------------
	 * 		Global methods
	 *------------------------------------------------------*/

	/**
	 * 0: livello </br>
	 * 1: lunghezza </br>
	 * 2: errori </br>
	 * 3: successo </br>
	 * 4: operazioni </br>
	 * @return
	 */
	public static Integer[] getCurrentHistory(){
		if(history.size() == 0) return null;
		else return history.get(history.size() - 1);
	}


	public static final void showHelp(){
		PopupPanel helpWindow = new PopupPanel();
		helpWindow.setModal(true);
		helpWindow.setGlassEnabled(true);
		helpWindow.setAutoHideEnabled(true);
		helpWindow.setAnimationEnabled(true);

		FlowPanel helpPanel = new FlowPanel();
		helpPanel.getElement().setId("helpMenu");

		FlowPanel helpPageAdd = new FlowPanel();
		FlowPanel helpPageMultiply = new FlowPanel();
		FlowPanel helpPageSubPoly = new FlowPanel();
		FlowPanel helpPageEnd = new FlowPanel();

		HTML headerPageAdd = new HTML();
		headerPageAdd.setHTML("<h5 class='menuHeader'>" + constants.headerPageAdd() + "</h5>");
		HTML headerPageMultiply = new HTML();
		headerPageMultiply.setHTML("<h5 class='menuHeader'>" + constants.headerPageMultiply() + "</h5>");
		HTML headerPageSubPoly = new HTML();
		headerPageSubPoly.setHTML("<h5 class='menuHeader'>" + constants.headerPageSubPoly() + "</h5>");
		HTML headerPageEnd = new HTML();
		headerPageEnd.setHTML("<h5 class='menuHeader'>" + constants.headerPageEnd() + "</h5>");

		helpPageAdd.add(new Image("/pictures/under-construction.png"));
		helpPageAdd.add(new HTML(constants.helpPageAddText()));

		helpPageMultiply.add(new HTML(constants.helpPageMultiplyText()));

		helpPageSubPoly.add(new HTML(constants.helpPageSubPolyText()));

		helpPageEnd.add(new HTML(constants.helpPageEndText()));

		helpPanel.add(headerPageAdd);
		helpPanel.add(helpPageAdd);
		helpPanel.add(headerPageMultiply);
		helpPanel.add(helpPageMultiply);
		helpPanel.add(headerPageSubPoly);
		helpPanel.add(helpPageSubPoly);
		helpPanel.add(headerPageEnd);
		helpPanel.add(helpPageEnd);

		helpWindow.add(helpPanel);

		$("#helpMenu", helpWindow.getElement()).as(Ui).accordion(Accordion.Options.create().collapsible(true).autoHeight(false).clearStyle(true));

		helpWindow.center();		
	}

	public static final void showStatistics(){
		final PopupPanel statisticsWindow = new PopupPanel();
		statisticsWindow.setModal(true);
		statisticsWindow.setGlassEnabled(true);
		statisticsWindow.setAutoHideEnabled(false);
		statisticsWindow.setAnimationEnabled(true);

		FlowPanel statisticsPanel = new FlowPanel();
		statisticsPanel.addStyleName("stats");

		Button close = new Button();
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				statisticsWindow.hide();
			}
		});		
		close.addStyleName("close");
		statisticsPanel.add(close);

		Label title = new Label(constants.statisticsTitle());
		title.addStyleName("title");
		statisticsPanel.add(title);		

		String html = "<table id='stats'><tr class='title'><td>#</td><td>Livello</td><td>Lunghezza</td><td>Errori</td><td>Calcolo terminato?</tr>";
		int gameNum = 1;
		int successPos = 3;
		int totalSuccesses = 0;
		getCurrentHistory()[2] = errorCounter;
		int errHistoryCopy = errorHistory;
		errHistoryCopy += errorCounter;
		int points = 0;
		for(Integer[] item : history){
			points += (item[0]+item[1]) / 2 * item[3] - item[2];
		}

		for(Integer[] game : history){
			html += "<tr class='dataRow'><td>"+gameNum+"</td>";
			for(int k = 0; k < successPos; k++){
				html += "<td>" + game[k] + "</td>";
			}
			if(game[successPos] == 1) {
				html += "<td>" + constants.yes() + "</td>";
				totalSuccesses++;
			}
			else if(game[successPos] == 0) html += "<td>" + constants.no() + "</td>";
			html += "</tr>";
			gameNum++;
		}

		html += "<tr class='bottomLine'><td></td><td></td><td></td><td>" + errHistoryCopy + "</td><td>" + totalSuccesses + "</tr>";
		html += "</table>";		

		ScrollPanel scrollPanel = new ScrollPanel(new HTML(html));		
		statisticsPanel.add(scrollPanel);
		Label score = new Label(constants.score() + points);
		score.addStyleName("score");
		statisticsPanel.add(score);
		statisticsWindow.add(statisticsPanel);		

		$(".dataRow td", statisticsPanel.getElement()).hover(new Function() {
			public void f(Element e){
				$(e).parent().css("background", "#ddd");
			}
		}, new Function() {
			public void f(Element e){
				$(e).parent().css("background", "transparent");
			}
		});
		
		
		
		int winHeight = Window.getClientHeight();
		statisticsWindow.setPopupPosition(-800, -winHeight);
		statisticsWindow.show();

		if(scrollPanel.getOffsetHeight() > Window.getClientHeight() - 200) {			
			scrollPanel.setHeight((winHeight - 200) + "px");
		}		

		statisticsWindow.center();
	}


	public static final void showContactForm(){
		ContactForm contactForm = new ContactForm();
		contactForm.setModal(true);
		contactForm.setGlassEnabled(true);
		contactForm.setAutoHideEnabled(false);
		contactForm.setAnimationEnabled(true);
		contactForm.center();
	}
	
	
	public static final void msgPanel(String msg) {
		msgBox.setText(msg);
		msgBox.center();
		popupClose.schedule(5000);
	}

	public static final void msgPanel() {	
		msgBox.hide();
	}

	public static final Polynomial getMainPoly(){
		return mainPoly;
	}

	public static final void setMainPoly(Polynomial poly){
		mainPoly = poly;
	}

	public static PickupDragController getMainDragController(){
		return mainDragController;
	}	

	public static String[] joinStrArrays(String[] arr1, String[] arr2){
		String[] result = new String[arr1.length + arr2.length];
		int k = 0;
		for (String l : arr1){
			result[k] = l;
			k++;
		}
		for (String l : arr2){
			result[k] = l;
			k++;
		}		
		Arrays.sort(result);
		return result;
	}
	

	/*public static String[] diffStrArrays(String[] arr1, String[] arr2) {

		System.err.println("diffStrArrays");

		List<String> l1 = Arrays.asList(arr1);
		List<String> l2 = Arrays.asList(arr2);

		l1.removeAll(l2);

		for(String item : l1){
			System.err.print(item + "; ");
		}
		System.err.println();

		return (String[]) l1.toArray();
	}*/

	
	
	// Private constructor
	
	private AlgebrAppGlobals(){
		
	}


}

