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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AlgebrAppGlobals {

	/*------------------------------------------------------
	 * 		Global variables & magic numbers
	 *------------------------------------------------------*/

	public static final int POLY_BETWEEN_PARENTHESIS = 1;
	public static final int MAINPOLY_IN_MAINPANEL = 0;
	public static final boolean DEBUG = true;
	public static final boolean SHOWDOT = true;
	public static final int SHOWN_ADDITIONS_NUMBER = 4;
	public static final int INCORRECT_ADDITION_CASES = 4;
	public static final int INCORRECT_LITERALS_CASES = 3;
	public static final int SHOWN_PRODUCTS_NUMBER = 3;
	public static final int INCORRECT_PRODUCTS_CASES = 3;

	public static VerticalPanel mainPanel = new VerticalPanel();
	public static AbsolutePanel wrapPanel =  new AbsolutePanel();	
	public static Label msgFrame = new Label();	
	public static Button operate = new Button();

	public static String[][] arrLiterals = {{"1", "a", "b"},{"x", "y", "z"},{"a", "b", "c"},{"1","x","y"}, {"1","x","y"}, {"1","&#960;","r"},{"1","1","1"}};	

	public static AlgebrAppConstants constants = GWT.create(AlgebrAppConstants.class);

	public enum errorType {
		PRECEDENZE, 
		NON_SIMILI,
		NON_RIDOTTO,
		NONE,
	}
	
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
	public static DialogBox dial = new DialogBox();


	/*------------------------------------------------------
	 * 		Global methods
	 *------------------------------------------------------*/

	public static final void msgPanel(String msg) {
		if (!msgFrame.isVisible()){msgFrame.setVisible(true);}	
		msgFrame.setText(msg);
	}

	public static final void msgPanel() {		
		msgFrame.setText("");
		msgFrame.setVisible(false);
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

