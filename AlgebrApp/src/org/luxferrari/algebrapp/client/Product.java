package org.luxferrari.algebrapp.client;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.INCORRECT_PRODUCTS_CASES;
import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.SHOWDOT;
import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.SHOWN_PRODUCTS_NUMBER;
import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.constants;
import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.joinStrArrays;
import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.rndGenerator;
import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.selectedWidgets;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class Product extends HorizontalPanel{

	private Polynomial firstFactorContent = null;
	private Polynomial secondFactorContent = null;
	private HorizontalPanel firstFactor = new HorizontalPanel();
	private HorizontalPanel secondFactor = new HorizontalPanel();
	private int firstFactorParenthesisLevel = 0;
	private int secondFactorParenthesisLevel = 0;
	private Polynomial parentPoly = null;
	private boolean showsign = true;
	
	public Product(Polynomial p1, Polynomial p2) {	
		super();
		firstFactorContent = p1;
		firstFactorContent.setParentProduct(this);
		secondFactorContent = p2;
		secondFactorContent.setParentProduct(this);
		
		if(firstFactorContent.getChildrenProducts().size() > 0){
			int leftMax = 0;	
			for(Product item : firstFactorContent.getChildrenProducts()){
				leftMax = item.getParenthesisLevel() > leftMax ? item.getParenthesisLevel() : leftMax;
			}
			firstFactorParenthesisLevel = leftMax+1;			
		}
		if(secondFactorContent.getChildrenProducts().size() > 0){
			int rightMax = 0;	
			for(Product item : secondFactorContent.getChildrenProducts()){
				rightMax = item.getParenthesisLevel() > rightMax ? item.getParenthesisLevel() : rightMax;
			}
			secondFactorParenthesisLevel = rightMax+1;			
		}
	}

	// Copy constructor

	public Product(Product p, boolean isDropTarget, boolean drawSign){
		this(new Polynomial(p.getFirstFactorContent(), isDropTarget), new Polynomial(p.getSecondFactorContent(), isDropTarget));
		this.setParentPoly(p.getParentPoly());
		this.firstFactorParenthesisLevel = p.firstFactorParenthesisLevel;
		this.secondFactorParenthesisLevel = p.secondFactorParenthesisLevel;
		this.showsign = drawSign;
	}  
	
	public Product(Product p, boolean isDropTarget){
		this(p, isDropTarget, true);
	}

	public Polynomial getFirstFactorContent(){
		return firstFactorContent;
	}

	public Polynomial getSecondFactorContent(){
		return secondFactorContent;
	}
	
	public Polynomial getParentPoly(){
		return parentPoly;
	}
	
	public void setParentPoly(Polynomial parent) {
		this.parentPoly = parent;
	}
	
	public int getParenthesisLevel(){
		if(firstFactorParenthesisLevel > secondFactorParenthesisLevel) return firstFactorParenthesisLevel;
		else return secondFactorParenthesisLevel;
	}

	public void refreshProduct(){
				
		this.firstFactor.clear();
		this.secondFactor.clear();
		this.clear();
		firstFactorContent.refreshPolynomial();
		secondFactorContent.refreshPolynomial();
		
		String sign = "+";
		if(this.getParentPoly().getWidgetIndex(this) == 0) showsign = false; 
		if(!showsign) sign = "";

		// Populate the first factor of this product, a HorizontalPanel holding a polynomial -> ( -> )
		if(firstFactorContent.getLength() > 1 ){
			firstFactorContent.setLeftFactor(true);
			firstFactor.add(firstFactorContent);
			Symbol openParenthesis = new Symbol(sign + "<span class='size" + firstFactorParenthesisLevel + "'>(</span>");
			firstFactor.insert(openParenthesis, 0);
			Symbol closedParenthesis = new Symbol(")");
			closedParenthesis.addStyleName("size"+firstFactorParenthesisLevel);
			firstFactor.add(closedParenthesis);
			firstFactor.add(new HTML("<sub class='index'></sub>"));
		}
		else{
			Monomial m = ((Monomial)firstFactorContent.getWidget(0));
			m.setLeftFactor(true);
			firstFactor.add(firstFactorContent);
			if(m.getCoefficient() > 0){
				m.showPlusSign = showsign ? true : false;
				m.refreshHTML();				
			}
			else{			
				Symbol openParenthesis = new Symbol(sign + "<span class='size" + firstFactorParenthesisLevel+"'>(</span>");
				firstFactor.insert(openParenthesis, 0);
				Symbol closedParenthesis = new Symbol(")");
				closedParenthesis.addStyleName("size"+firstFactorParenthesisLevel);
				firstFactor.add(closedParenthesis);
			}
		}		
		this.add(firstFactor);

		// If needed, show multiplication symbol
		if(SHOWDOT){

			Symbol dot = new Symbol("&middot;", false);
			dot.addStyleName("clickable");
			dot.setTitle(constants.tooltipCommutative());
			dot.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					commuteFactors();
				}
			});

			this.add(dot);
		}

		// Populate second factor
		if(secondFactorContent.getLength() > 1){
			secondFactorContent.setRightFactor(true);
			secondFactor.add(secondFactorContent);
			Symbol openParenthesis = new Symbol("(");
			openParenthesis.addStyleName("size"+secondFactorParenthesisLevel);
			secondFactor.insert(openParenthesis, 0);
			Symbol closedParenthesis = new Symbol(")");
			closedParenthesis.addStyleName("size"+secondFactorParenthesisLevel);
			secondFactor.add(closedParenthesis);
			secondFactor.add(new HTML("<sub class='index'></sub>"));
		}
		else{
			Monomial m = ((Monomial)secondFactorContent.getWidget(0));
			m.setRightFactor(true);
			secondFactor.add(secondFactorContent);
			if(m.getCoefficient() > 0){
				m.showPlusSign = false;
				m.refreshHTML();				
			}
			else{				
				Symbol openParenthesis = new Symbol("(");
				openParenthesis.addStyleName("size"+secondFactorParenthesisLevel);
				secondFactor.insert(openParenthesis, 0);
				Symbol closedParenthesis = new Symbol(")");
				closedParenthesis.addStyleName("size"+secondFactorParenthesisLevel);
				secondFactor.add(closedParenthesis);
			}
		}
		this.add(secondFactor);
		this.setTooltip();
	}


	public Polynomial product(){
		Polynomial result = new Polynomial(false);		

		if(!this.isProductReduced()){return null;} // Sanity check

		int L1 = this.getFirstFactorContent().getLength();
		int L2 = this.getSecondFactorContent().getLength();

		for(int j = 0; j < L1; j++){
			for(int k = 0; k < L2; k++){
				result.addMonomial(((Monomial)this.getFirstFactorContent().getWidget(j)).multiply((Monomial)this.getSecondFactorContent().getWidget(k)));
			}
		}
		return result;
	}



	public ArrayList<Polynomial> productResultsList() {
		ArrayList<Polynomial> result = new ArrayList<Polynomial>();
		result.add(this.product());
		Polynomial case0 = incorrectProduct(0);
		if(!case0.isContainedIn(result)){ result.add(case0);}
		
		int counter = 0;	
		while(result.size() < SHOWN_PRODUCTS_NUMBER){
			
			counter++;							// dopo 5 iterazioni userà solo il caso 3 
			int r = counter > 5 ? 3 : 1 + rndGenerator.nextInt(INCORRECT_PRODUCTS_CASES - 1);
			
			Polynomial candidate = incorrectProduct(r);			
			if(!candidate.isContainedIn(result)){
				result.add(candidate);				
			}			
		}
		return result;
	}

	private Polynomial incorrectProduct(int c){
		int L1 = this.getFirstFactorContent().getLength();
		int L2 = this.getSecondFactorContent().getLength();
		Polynomial result = new Polynomial(false);		

		switch(c){
		case 0:																				
			if(L1 == 1 && L2 == 1){															// Prodotto di monomi, somma i coefficienti...
				Monomial m1 = (Monomial)this.getFirstFactorContent().getWidget(0);
				Monomial m2 = (Monomial)this.getSecondFactorContent().getWidget(0);
				result.add(new Monomial(m1.getCoefficient() + m2.getCoefficient(), joinStrArrays(m1.getLiterals(), m2.getLiterals())));
				break;
			}
			
			for(int k = 0; k < L1; k++){													// Moltiplica solo con il primo membro del fattore destro, addiziona l'altro
				result.addMonomial(((Monomial)this.getFirstFactorContent().getWidget(k)).multiply((Monomial)this.getSecondFactorContent().getWidget(0)));
			}
			for(int k = 1; k < L2; k++){
				result.addMonomial(new Monomial((Monomial)this.getSecondFactorContent().getWidget(k)));			
			}
			break;

		case 1:	
			
			for(int j = 0; j < L1; j++){
				for(int k = 0; k < L2; k++){
					Monomial m1 = (Monomial)this.getFirstFactorContent().getWidget(j);
					Monomial m2 = (Monomial)this.getSecondFactorContent().getWidget(k);
					int coeff = 0;
					String[] lit = null;
					Monomial m = null;
					
					if(m1.getCoefficient() < 0 || m2.getCoefficient() < 0){
						coeff = -m1.getCoefficient() * m2.getCoefficient();
						lit = joinStrArrays(m1.getLiterals(), m2.getLiterals());
						m = new Monomial(coeff, lit, false);
					}
					else{
						m = m1.multiply(m2);
					}	
					result.addMonomial(m);
				}
			}
			break;
			
		case 2:
			for(int j = 0; j < L1; j++){
				for(int k = 0; k < L2; k++){
					Monomial m1 = (Monomial)this.getFirstFactorContent().getWidget(j);
					Monomial m2 = (Monomial)this.getSecondFactorContent().getWidget(k);
										
					int c1 = rndGenerator.nextInt(2) == 0 ? 1 : m1.getCoefficient();
					int c2 = rndGenerator.nextInt(2) == 0 ? 1 : m2.getCoefficient();
					
					String[] l1 = {};
					if(rndGenerator.nextInt(2) == 0) { 
						l1 = m1.getLiterals();
					}
					String[] l2 = {};
					if(rndGenerator.nextInt(2) == 0) { 
						l2 = m2.getLiterals();
					}
					
					Monomial m = new Monomial(c1*c2, joinStrArrays(l1, l2), false);
					
					result.addMonomial(m);
				}
			}
			break;
			
		}
		return result;
	}

	public boolean isProductReduced(){
		return this.getFirstFactorContent().checkReduced() && this.getSecondFactorContent().checkReduced();
	}



	public int getLength(){
		int count = 0;
		count += firstFactorContent.getLength();
		count += secondFactorContent.getLength();
		return count;
	}

	public void commuteFactors() {

		selectedWidgets.clearSelected();

		firstFactor.clear();
		secondFactor.clear();
		this.clear();

		Polynomial a = new Polynomial(firstFactorContent);
		int b = firstFactorParenthesisLevel;
		firstFactorContent.disposeOfMembers();
		firstFactorContent = secondFactorContent;
		firstFactorParenthesisLevel = secondFactorParenthesisLevel;
		secondFactorContent = a;
		secondFactorParenthesisLevel = b;
		secondFactorContent.setParentProduct(this);
		
		refreshProduct();
	}

	public void setTooltip(){
		for(int k = 0; k < this.firstFactor.getWidgetCount(); k++){
			if(this.firstFactor.getWidget(k) instanceof Symbol){
				((Symbol)firstFactor.getWidget(k)).setTooltip();
			}
		}
		for(int k = 0; k < this.secondFactor.getWidgetCount(); k++){
			if(this.secondFactor.getWidget(k) instanceof Symbol){
				((Symbol)secondFactor.getWidget(k)).setTooltip();
			}
		}
	}
}
