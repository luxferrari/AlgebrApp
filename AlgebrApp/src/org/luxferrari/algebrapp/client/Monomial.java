package org.luxferrari.algebrapp.client;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;

/**
 * @author Lucio
 *
 */
public class Monomial extends Button implements Selectable{

	private String[] literals = null; 	//parte letterale (array di stringhe)
	private int coefficient = 0;		//coefficiente
	private boolean isScalar = false;
	private boolean isLeftFactor = false;
	private boolean isRightFactor = false;
	private Polynomial parentPoly = null;
	public boolean showPlusSign = true;
	private boolean selected = false;


	/** Costruttore di un oggetto di tipo Monomial
	 * @param c	coefficiente del monomio
	 * @param l parte letterale del polinomio
	 * @param isDraggable 
	 */
	public Monomial(int c, String[] l, boolean isDraggable) {
		super();
		coefficient = c;
		Arrays.sort(l);
		literals = removeOnes(l);

		this.refreshHTML();
		this.deselect();
		this.setTitle(constants.tooltipMonomial());

		if(isDraggable){
			getMainDragController().makeDraggable(this);
			addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {  

					msgPanel();
					//il primo clic seleziona, il secondo clic fa partire un'operazione --- 

					if(selected){
						deselect();
					}
					else{
						select();
					}
				}
			});		
		}    
	}

	public Monomial(int c, String[] l){
		this(c, l, true);
	}

	// Copy constructor

	public Monomial(Monomial m){
		this(m.coefficient, m.literals, true);
		this.isLeftFactor = m.isLeftFactor;
		this.isRightFactor = m.isRightFactor;
	}

	public Monomial(Monomial m, boolean b){
		this(m.coefficient, m.literals, b);
		this.isLeftFactor = m.isLeftFactor;
		this.isRightFactor = m.isRightFactor;
	}


	// Getters and setters

	String[] getLiterals(){
		return literals;
	}

	public int getCoefficient(){
		return coefficient;
	}

	public void setCoefficient(int c){
		coefficient = c;
	}	

	public Boolean isScalar(){
		return isScalar;
	}

	public Boolean isLeftFactor(){
		return isLeftFactor;
	}

	public Boolean isRightFactor(){
		return isRightFactor;
	}

	public void setLeftFactor(boolean b) {
		isLeftFactor = b;
	}

	public void setRightFactor(boolean b) {
		isRightFactor = b;
	}

	private String getSign(){
		String returnString = "";
		if (this.coefficient>0){
			returnString = "+ ";
		}
		else if (this.coefficient<0){
			returnString = "&#150; ";
		}
		return returnString;
	}
	// Set button states 
	public void select(){
		this.selected = true;
		this.setStyleName("monomial math selected dragdrop-handle");
		selectedWidgets.setSelected(this);
		this.getElement().getElementsByTagName("sub").getItem(0).setAttribute("visibility", "visible");
		selectedWidgets.refreshSelectionIndexes();
	}

	public void deselect(){
		this.selected = false;
		this.setStyleName("monomial math unselected dragdrop-handle");
		selectedWidgets.removeSelected(this);
		Element index = this.getElement().getElementsByTagName("sub").getItem(0);
		index.setAttribute("visibility", "hidden");
		index.setInnerHTML("");
		selectedWidgets.refreshSelectionIndexes();
	}

	public boolean isSelected(){
		return this.selected;
	}	

	public void refreshIndex(String index){
		this.getElement().getElementsByTagName("sub").getItem(0).setInnerHTML(index);
	}

	/** Uses the string of literals to build a representation, e.g. aab -> a^2b. Might not be extremely efficient...
	 * @param c coefficient
	 * @param l string of literals
	 * @param showPlusSign (don't show a 'plus' if it's the first member of a polynomial, +2a -3b -> 2a - 3b)
	 * @return a hopefully well formatted html string
	 */
	private String buildHTMLTitle(){

		String html="<div class = \"math\">";
		int power;
		int lng = this.literals.length;


		/*
		 *	Check whether all literals are 1 (=x^0)
		 */

		if(literals.length == 0) isScalar = true;

		/*
		 *			Set sign 				 
		 */

		if (showPlusSign || this.coefficient < 0){
			html += getSign();
		}		

		/*
		 *			Set coefficient (none if 1)			 
		 */

		if (this.coefficient > 1) { 
			html += Integer.toString(this.coefficient);
		}			
		else if (this.coefficient < -1){
			html += Integer.toString(-this.coefficient);
		}		
		else if (this.coefficient == 0){
			html += "0";
		}

		/*
		 *			Set literal part 				 
		 */

		if(this.coefficient!=0) {
			for (int k = 0; k < lng; k++){
				if(!this.literals[k].equals("1")){		
					if (k < lng - 1){
						if (this.literals[k].equals(this.literals[k+1])) continue;
					}						  
					power = countOccurrence(this.literals[k], this.literals);    	  
					if(power==1){
						html += this.literals[k];
					}
					else{								
						html += this.literals[k]+"<sup>"+power+"</sup>";
					}
				}
			}
			if (isScalar && (this.coefficient==1 || this.coefficient==-1)){
				html+="1";							
			}
		}


		html += "<sup>&nbsp;</sup></div>";

		html += "<sub class='index'></sub>";

		return html;
	}


	private int countOccurrence(String target, String[] array){
		int occur = 0;
		for (int k=0;k<array.length;k++){
			if (array[k].equals(target)){
				occur++;
			}    		
		}
		return occur;    	   	 
	}

	public void refreshHTML() {
		this.setHTML(buildHTMLTitle());
	}

	public boolean isSimilar(Monomial m){
		return Arrays.equals(literals, m.getLiterals());	
	}	

	public Polynomial getParentPoly() {
		return parentPoly;
	}

	public void setParentPoly(Polynomial parentPoly) {
		this.parentPoly = parentPoly;
	}	

	public Monomial multiply(Monomial m){		
		return new Monomial(this.getCoefficient() * m.getCoefficient(), joinStrArrays(this.getLiterals(), m.getLiterals()));
	}

	public boolean isContainedIn(List<Monomial> list){
		boolean result = false;
		for(Monomial item : list){
			if(item.isEqualTo(this)){
				result = true;
				break;
			}
		}
		return result;
	}

	public boolean isEqualTo(Monomial m){
		return this.getCoefficient() == m.getCoefficient() && (this.isSimilar(m) || this.getCoefficient() == 0);
	}
}

