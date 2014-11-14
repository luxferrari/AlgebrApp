package org.luxferrari.algebrapp.client;

import java.util.ArrayList;
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
public class Monomial extends Button implements Selectable, MathItem{

	private String[] literals = null; 	//parte letterale (array di stringhe)
	private Fraction coefficient;		//coefficiente
	private boolean isScalar = false;
	private boolean isLeftFactor = false;
	private boolean isRightFactor = false;
	private Polynomial parentPoly = null;
	private boolean showPlusSign = true;
	private boolean selected = false;
	private boolean isSimplified = SIMPLIFIED;
	private boolean hasPlus = true;
	private boolean isDraggable;

	/** Costruttore di un oggetto di tipo Monomial
	 * @param c	coefficiente del monomio
	 * @param l parte letterale del polinomio
	 * @param isDraggable 
	 */
	public Monomial(Fraction c, String[] l, boolean isDraggable, boolean makeSimplified) {
		super();
		this.coefficient = new Fraction(c);
		Arrays.sort(l);
		this.literals = removeOnes(l);

		this.isDraggable = isDraggable;
		this.isSimplified = makeSimplified;	
		if(!isSimplified) hasPlus = rndGenerator.nextInt(2) == 0 ? true : false;
		if(levelSlider.getValue() != null) hasPlus = levelSlider.getValue() == 0 ? true : hasPlus;

		this.refresh();
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
		else{
			this.setStyleName("monomial math");
		}
	}

	public Monomial(Fraction c, String[] l, boolean isDraggable){
		this(c, l, isDraggable, true);
	}

	public Monomial(Fraction c, String[] l){
		this(c, l, true, true);
	}

	// Copy constructor

	public Monomial(Monomial m){
		this(m.coefficient, m.literals, m.isDraggable, m.isSimplified);
		this.isLeftFactor = m.isLeftFactor;
		this.isRightFactor = m.isRightFactor;		
		if(!isSimplified) {
			this.hasPlus = m.hasPlus;
			this.refresh();
		}
	}

	public Monomial(Monomial m, boolean isDraggable, boolean isSimplified){
		this(m.coefficient, m.literals, isDraggable, isSimplified);
		this.isLeftFactor = m.isLeftFactor;
		this.isRightFactor = m.isRightFactor;
		if(!isSimplified) {
			this.hasPlus = m.hasPlus;
			this.refresh();
		}
	}

	public Monomial(Monomial m, boolean isDraggable){
		this(m.coefficient, m.literals, isDraggable, m.isSimplified);
		this.isLeftFactor = m.isLeftFactor;
		this.isRightFactor = m.isRightFactor;
		if(!isSimplified) {
			this.hasPlus = m.hasPlus;
			this.refresh();
		}
	}

	// Getters and setters

	String[] getLiterals(){
		return literals;
	}

	public Fraction getCoefficientCopy(){
		return new Fraction(coefficient);
	}

	public Fraction getCoefficient(){
		return coefficient;
	}

	public void setCoefficient(Fraction c){
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

	public boolean showPlusSign(){
		return showPlusSign;
	}
	
	public void showPlusSign(boolean b){
		showPlusSign = b;
	}
	public boolean isSimplified(){
		return this.isSimplified;
	}

	public void isSimplified(boolean makeSimplified){
		this.isSimplified = makeSimplified;
		if(!makeSimplified) hasPlus = rndGenerator.nextInt(2) == 0 ? true : false;
		else hasPlus = true;
		this.refresh();
	}

	public void invertSignAndValue(){
		this.hasPlus = !this.hasPlus;
		this.coefficient = this.coefficient.multiply(-1);
		this.refresh();
	}


	private String getSign(){
		String returnString = "";
		if (this.coefficient.value() > 0){
			returnString = PLUS;
		}
		else if (this.coefficient.value() < 0){
			returnString = MINUS;
		}
		return returnString;
	}

	private String getOppositeSign(){
		String returnString = "";
		if (this.coefficient.value() < 0){
			returnString = PLUS;
		}
		else if (this.coefficient.value() > 0){
			returnString = MINUS;
		}
		return returnString;
	}
	// Set button states 
	public void select(){
		if(selected) return;
		selectedWidgets.deselectAncestors(this);

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

	public boolean hasPlus(){
		return hasPlus;
	}
	
	public void hasPlus(boolean b){
		hasPlus = b;
		this.refresh();
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

		String html = "<div class = 'math'>";
		if(!this.coefficient.isInteger()) html = "<div class = 'math fraction'>";
		int power;
		int lng = this.literals.length;
		int parenthesisSize = Z_ONLY ? 0 : 2;


		/*
		 *	Check whether all literals are 1 (=x^0)
		 */

		if(literals.length == 0) isScalar = true;

		/*
		 *			
		 **/

		if(!isSimplified && coefficient.value() != 0){
			
			String signBeforeParenthesis = "";
			if(!hasPlus) signBeforeParenthesis = MINUS;
			else if(showPlusSign) signBeforeParenthesis = PLUS;
			html += "<span class='sign'>" + signBeforeParenthesis + "</span>";

			html += "<span class='parenthesis size" + parenthesisSize + "'>(</span>";

			String sign = hasPlus ? getSign() : getOppositeSign();
			html += "<span class='sign'>" + sign + "</span>";		
		}

		/*
		 *			Set sign 				 
		 */

		else if (showPlusSign || this.coefficient.value() <= 0){
			html += "<span class='sign'>" + getSign() + "</span>";
		}		

		/*
		 *			Set coefficient 		 
		 */

		if (this.coefficient.value() != 0 && this.coefficient.absValue() != 1) { 
			html += this.coefficient;
		}	
		else if(this.coefficient.value() == 0 ) html += "0";

		/*
		 *			Set literal part 				 
		 */

		if(this.coefficient.value() != 0) {
			for (int k = 0; k < lng; k++){
				if(!this.literals[k].equals("1")){		
					if (k < lng - 1){
						if (this.literals[k].equals(this.literals[k+1])) continue;
					}						  
					power = countOccurrence(this.literals[k], this.literals);    	  
					if(power==1){
						html += "<span class='literalPart'>" + this.literals[k] + "</span>";
					}
					else{								
						html += "<span class='literalPart'>" + this.literals[k]+"</span><sup>"+power+"</sup>";
					}
				}
			}
			if (isScalar && (this.coefficient.value() == 1 || this.coefficient.value() == -1)){
				html+="1";							
			}
		}


		html += "<sup>&nbsp;</sup>";

		if(!isSimplified && coefficient.value() != 0) html += "<span class='parenthesis size" + parenthesisSize + "'>)</span>";

		html += "</div><sub class='index monomialIndex'></sub>";

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

	public void refresh(boolean resetShowPlusSign) {
		if(resetShowPlusSign){
			if(this.getParentPoly() != null && this.getParentPoly().getWidget(0) == this){
				this.showPlusSign = false; 			
			}
			else {
				this.showPlusSign = true;
			}
		}		
		this.setHTML(buildHTMLTitle());
	}
	
	public void refresh(){
		refresh(true);
	}
	

	public void destroy(){
		this.removeFromParent();
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
	
	public Product getParentProduct(){
		return parentPoly.getParentProduct();
	}

	public Monomial multiply(Monomial m){		
		return new Monomial(this.getCoefficientCopy().multiply(m.getCoefficientCopy()), joinStrArrays(this.getLiterals(), m.getLiterals()));
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
		return this.getCoefficient().equals(m.getCoefficient()) && (this.isSimilar(m) || this.getCoefficient().value() == 0);
	}
	
	private String[] removeOnes(String[] arr){		
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
}

