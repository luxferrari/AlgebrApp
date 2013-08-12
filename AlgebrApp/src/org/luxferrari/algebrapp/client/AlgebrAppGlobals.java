package org.luxferrari.algebrapp.client;

import static org.luxferrari.algebrapp.client.AlgebrApp.*;
import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.RandomAccess;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AlgebrAppGlobals {

	/*------------------------------------------------------
	 * 		Global variables & magic numbers
	 *------------------------------------------------------*/

	public static final int POLY_BETWEEN_PARENTHESIS = 1;
	public static final int MAINPOLY_IN_MAINPANEL = 0;
	public static final boolean DEBUG = true;
	public static boolean SHOWDOT = true;
	public static int Z_ONLY = 0;
	public static final int SHOWN_ADDITIONS_NUMBER = 4;
	public static final int INCORRECT_ADDITION_CASES = 4;
	public static final int INCORRECT_LITERALS_CASES = 3;
	public static final int SHOWN_PRODUCTS_NUMBER = 3;
	public static final int INCORRECT_PRODUCTS_CASES = 3;
	public static final int LEVELS_NUMBER = 10;

	public static int errorCounter = 0;
	public static AlgebrAppConstants constants = GWT.create(AlgebrAppConstants.class);
	public static AlgebrAppMessages messages = GWT.create(AlgebrAppMessages.class);
	public static AbsolutePanel wrapPanel =  new AbsolutePanel();	
	public static VerticalPanel mainPanel = new VerticalPanel();
	public static FlowPanel toolPanel = new FlowPanel();
	public static FlowPanel buttonPanel = new FlowPanel();
	public static Label msgFrame = new Label();	
	public static Button operate = new Button();
	public static IncrementSlider levelSlider = new IncrementSlider();

	public static String[][] arrLiterals = {
		{"1","1","1"},
		{"1", "a", "b"},
		{"x", "y", "z"},
		{"a", "b", "c"},
		{"1","x","y"}, 
		{"u","v","z"}, 
		{"1","&#960;","r"}
	};		

	public enum errorType {
		ORDER_OF_OPERATIONS, 
		NOT_SIMILAR,
		NOT_REDUCED,
		NONE,
	}

	public static double[][] settingsArray = {
		//only-Z 		totalLength		order			coeffRange		#literals		productFrequency

		{1,				5,				1,				10,				1,				0	},	// Level 0
		{1,				6,				1,				20,				1,				0.8	},	// Level 1
		{1,				8,				1,				30,				1,				1	},	// Level 2
		{0,				5,				2,				10,				2,				0	},	// Level 3
		{0,				5,				3,				10,				2,				0	},	// Level 4
		{0,				8,				2,				20,				2,				1	},	// Level 5
		{0,				8,				2,				20,				2,				1	},	// Level 6
		{0,				10,				3,				20,				2,				0.8	},	// Level 7
		{0,				10,				3,				20,				3,				1	},	// Level 8
		{0,				12,				3,				20,				3,				1	},	// Level 9
		{0,				15,				4,				20,				3,				1	},	// Level 10
	};

	/*------------------------------------------------------
	 * 		Multiple selection
	 *------------------------------------------------------*/

	public static PickupDragController mainDragController = new PickupDragController(wrapPanel, true);
	public static SelectedItemsList selectedWidgets = new SelectedItemsList();

	public static Polynomial mainPoly = null;	
	public static RandomGenerator rndGenerator = new RandomGenerator();

	public static Monomial rightAnswerAddition;
	public static Polynomial rightAnswerProduct;
	public static SelectedItemsList selectedItemsList;
	public static PopupPanel dial = new PopupPanel();
	public static MessagePopup msgBox = new MessagePopup();

	public static Timer closePopup = new Timer() {
		public void run() {
			msgBox.hide();
		}
	};


	/*------------------------------------------------------
	 * 		Global methods
	 *------------------------------------------------------*/


	public static final void msgPanel(String msg) {
		msgBox.setText(msg);
		msgBox.center();
		closePopup.schedule(5000);
		/*if (!msgFrame.isVisible()){msgFrame.setVisible(true);}
		msgFrame.setText(msg);*/
	}

	public static final void msgPanel() {	
		msgBox.hide();
		/*msgFrame.setText("");
		msgFrame.setVisible(false);*/
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

	public static String[] removeOnes(String[] arr){		
		List<String> list = new ArrayList<String>(Arrays.asList(arr));
		while(list.size() > 0 && list.contains("1")){
			list.remove("1");
		}
		String[] result = new String[list.size()];
		for(int k = 0; k < list.size(); k++){
			result[k] = list.get(k);
		}
		return result;
	}

	public static String[] diffStrArrays(String[] arr1, String[] arr2) {

		System.err.println("diffStrArrays");

		List<String> l1 = Arrays.asList(arr1);
		List<String> l2 = Arrays.asList(arr2);

		l1.removeAll(l2);

		for(String item : l1){
			System.err.print(item + "; ");
		}
		System.err.println();

		return (String[]) l1.toArray();
	}


	public static <T> void shuffle(List<T> list, Random rnd) {
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

