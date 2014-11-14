package org.luxferrari.algebrapp.client;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class Product extends HorizontalPanel implements MathItem{

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
		this.setVerticalAlignment(ALIGN);
		this.addStyleName("product");
		firstFactor.setVerticalAlignment(ALIGN);
		firstFactor.addStyleName("factor firstFactor");
		secondFactor.setVerticalAlignment(ALIGN);
		secondFactor.addStyleName("factor secondFactor");
		firstFactorContent = p1;
		firstFactorContent.setParentProduct(this);
		secondFactorContent = p2;
		secondFactorContent.setParentProduct(this);

		firstFactorParenthesisLevel = firstFactorContent.getParenthesisLevel();
		secondFactorParenthesisLevel = secondFactorContent.getParenthesisLevel();	
		
		if(firstFactorContent.isDropTarget() && secondFactorContent.isDropTarget()){
			//dropController = new HorizontalPanelDropController(this);
			//getMainDragController().registerDropController(this.dropController);	
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
	
	public Product getParentProduct(){
		return parentPoly.getParentProduct();
	}

	public void setParentPoly(Polynomial parent) {
		this.parentPoly = parent;
	}
	
	public void destroy(){
		this.getFirstFactorContent().destroy();
		this.getSecondFactorContent().destroy();		
		//getMainDragController().unregisterDropController(this.dropController);		
		this.removeFromParent();
	}

	public int getParenthesisLevel(){
		if(firstFactorParenthesisLevel > secondFactorParenthesisLevel) return firstFactorParenthesisLevel;
		else return secondFactorParenthesisLevel;
	}
	
	public void refresh(){

		this.firstFactor.clear();
		this.secondFactor.clear();
		this.clear();
		firstFactorContent.refresh();
		secondFactorContent.refresh();

		String sign = "<span class='sign'> + </span>";		

		if(this.getParentPoly().getWidgetIndex(this) == 0) showsign = false; 
		else showsign = true;

		this.addStyleName("child"+this.getParentPoly().getWidgetIndex(this));

		if(!showsign) sign = "";

		// Populate the first factor of this product, a HorizontalPanel holding a polynomial -> ( -> )
		if(firstFactorContent.getTotalLength() > 1 ){
			firstFactorContent.setLeftFactor(true);
			firstFactor.add(firstFactorContent);
			Symbol openParenthesis = new Symbol(sign + "<span class='parenthesis size" + firstFactorParenthesisLevel + "'>(</span>");
			openParenthesis.setMyParent(this);
			firstFactor.insert(openParenthesis, 0);
			Symbol closedParenthesis = new Symbol(")");
			closedParenthesis.addStyleName("parenthesis size"+firstFactorParenthesisLevel);
			closedParenthesis.setMyParent(this);
			firstFactor.add(closedParenthesis);
			firstFactorParenthesisLevel = firstFactorContent.getParenthesisLevel() + 1;			
		}
		else{
			firstFactor.add(firstFactorContent);

			if(firstFactorContent.getWidget(0) instanceof Monomial){
				
				Monomial m = ((Monomial)firstFactorContent.getWidget(0));
				m.setLeftFactor(true);
				
				if(!m.isSimplified() || m.getCoefficient().value() >= 0){
					m.showPlusSign(showsign ? true : false);
					m.refresh(false);
					firstFactorParenthesisLevel = firstFactorContent.getParenthesisLevel();
				}
				else{	
					Symbol openParenthesis = new Symbol(sign + "<span class='parenthesis size" + firstFactorParenthesisLevel + "'>(</span>");
					openParenthesis.setMyParent(this);
					firstFactor.insert(openParenthesis, 0);
					Symbol closedParenthesis = new Symbol(")");
					closedParenthesis.addStyleName("parenthesis size"+firstFactorParenthesisLevel);
					closedParenthesis.setMyParent(this);
					firstFactor.add(closedParenthesis);
					firstFactorParenthesisLevel = firstFactorContent.getParenthesisLevel() + 1;
				}
			}
			//else if(!(secondFactorContent.getWidget(0) instanceof SubPolynomial)){			
			
			/*else{
				Symbol signOnly = new Symbol(sign);
				signOnly.setMyParent(this);
				firstFactor.insert(signOnly, 0);
			}*/

		}
		firstFactor.add(new HTML("<sub class='index factorIndex'></sub>"));
		this.add(firstFactor);

		// If needed, show multiplication symbol
		if(SHOWDOT){

			Symbol dot = new Symbol(" &middot; ", false);
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
		if(secondFactorContent.getTotalLength() > 1){
			secondFactorContent.setRightFactor(true);
			secondFactor.add(secondFactorContent);
			Symbol openParenthesis = new Symbol("(");
			openParenthesis.addStyleName("parenthesis size"+secondFactorParenthesisLevel);
			openParenthesis.setMyParent(this);
			secondFactor.insert(openParenthesis, 0);
			Symbol closedParenthesis = new Symbol(")");
			closedParenthesis.addStyleName("parenthesis size"+secondFactorParenthesisLevel);
			closedParenthesis.setMyParent(this);
			secondFactor.add(closedParenthesis);
			secondFactorParenthesisLevel = secondFactorContent.getParenthesisLevel() + 1;	
		}
		else{
			secondFactor.add(secondFactorContent);
			if(secondFactorContent.getWidget(0) instanceof Monomial){
				Monomial m = ((Monomial)secondFactorContent.getWidget(0));
				m.setRightFactor(true);
				
				if((!m.isSimplified() && m.hasPlus()) || m.getCoefficient().value() >= 0){
					
					m.showPlusSign(false);					
					m.refresh(false);
					secondFactorParenthesisLevel = secondFactorContent.getParenthesisLevel();	
				}
				else{	
					Symbol openParenthesis = new Symbol("(");
					openParenthesis.addStyleName("parenthesis size"+secondFactorParenthesisLevel);
					openParenthesis.setMyParent(this);
					secondFactor.insert(openParenthesis, 0);
					Symbol closedParenthesis = new Symbol(")");
					closedParenthesis.addStyleName("parenthesis size"+secondFactorParenthesisLevel);
					closedParenthesis.setMyParent(this);
					secondFactor.add(closedParenthesis);
					secondFactorParenthesisLevel = secondFactorContent.getParenthesisLevel() + 1;	

				}
			}
			//else if(!(secondFactorContent.getWidget(0) instanceof SubPolynomial)){				
			

		}
		secondFactor.add(new HTML("<sub class='index factorIndex'></sub>"));		
		this.add(secondFactor);
		this.setTooltip();
	}


	public Polynomial product(){
		Polynomial result = new Polynomial(false);		

		if(!this.isProductReduced()){return null;} // Sanity check

		int L1 = this.getFirstFactorContent().getTotalLength();
		int L2 = this.getSecondFactorContent().getTotalLength();

		for(int j = 0; j < L1; j++){
			for(int k = 0; k < L2; k++){
				result.addItem(((Monomial)this.getFirstFactorContent().getWidget(j)).multiply((Monomial)this.getSecondFactorContent().getWidget(k)));
			}
		}
		return result.getReducedPoly();
	}



	public ArrayList<Polynomial> productResultsList() {

		ArrayList<Polynomial> result = new ArrayList<Polynomial>();
		result.add(this.product());
		Polynomial case0 = incorrectProduct(0);
		if(!case0.isContainedIn(result)){ result.add(case0);}

		int counter = 0;	
		while(result.size() < SHOWN_PRODUCTS_NUMBER){

			counter++;							// dopo 20 iterazioni userà solo il caso 2 
			int r = counter > 20 ? 3 : 1 + rndGenerator.nextInt(INCORRECT_PRODUCTS_CASES - 1);

			Polynomial candidate = incorrectProduct(r);			
			if(!candidate.isContainedIn(result)){
				result.add(candidate);				
			}			
		}
		return result;
	}

	private Polynomial incorrectProduct(int c){
		int L1 = this.getFirstFactorContent().getTotalLength();
		int L2 = this.getSecondFactorContent().getTotalLength();
		Polynomial result = new Polynomial(false);		

		switch(c){
		case 0:																				
			if(L1 == 1 && L2 == 1){															// Prodotto di monomi, somma i coefficienti...
				Monomial m1 = (Monomial)this.getFirstFactorContent().getWidget(0);
				Monomial m2 = (Monomial)this.getSecondFactorContent().getWidget(0);
				result.add(new Monomial(m1.getCoefficient().add(m2.getCoefficient()), joinStrArrays(m1.getLiterals(), m2.getLiterals())));
				break;
			}

			for(int k = 0; k < L1; k++){													// Moltiplica solo con il primo membro del fattore destro, addiziona l'altro
				result.addItem(((Monomial)this.getFirstFactorContent().getWidget(k)).multiply((Monomial)this.getSecondFactorContent().getWidget(0)));
			}
			for(int k = 1; k < L2; k++){
				result.addItem(new Monomial((Monomial)this.getSecondFactorContent().getWidget(k)));			
			}
			break;

		case 1:	

			for(int j = 0; j < L1; j++){
				for(int k = 0; k < L2; k++){
					Monomial m1 = (Monomial)this.getFirstFactorContent().getWidget(j);
					Monomial m2 = (Monomial)this.getSecondFactorContent().getWidget(k);
					Fraction coeff;
					String[] lit = null;
					Monomial m = null;

					if(m1.getCoefficient().value() < 0 || m2.getCoefficient().value() < 0){
						coeff = m1.getCoefficient().multiply(m2.getCoefficient()).multiply(-1);						
						lit = joinStrArrays(m1.getLiterals(), m2.getLiterals());
						m = new Monomial(coeff, lit, false);
					}
					else{
						m = m1.multiply(m2);
					}	
					result.addItem(m);
				}
			}
			break;

		case 2:
			for(int j = 0; j < L1; j++){
				for(int k = 0; k < L2; k++){
					Monomial m1 = (Monomial)this.getFirstFactorContent().getWidget(j);
					Monomial m2 = (Monomial)this.getSecondFactorContent().getWidget(k);

					Fraction c1 = rndGenerator.nextInt(2) == 0 ? new Fraction(1) : m1.getCoefficient();
					Fraction c2 = rndGenerator.nextInt(2) == 0 ? new Fraction(1) : m2.getCoefficient();

					String[] l1 = {};
					if(rndGenerator.nextInt(2) == 0) { 
						l1 = m1.getLiterals();
					}
					String[] l2 = {};
					if(rndGenerator.nextInt(2) == 0) { 
						l2 = m2.getLiterals();
					}

					Monomial m = new Monomial(c1.multiply(c2), joinStrArrays(l1, l2), false);

					result.addItem(m);
				}
			}
			break;
		case 3:
			for(int j = 0; j < L1; j++){
				for(int k = 0; k < L2; k++){
					Monomial m1 = (Monomial)this.getFirstFactorContent().getWidget(j);
					Monomial m2 = (Monomial)this.getSecondFactorContent().getWidget(k);

					Fraction c1 = rndGenerator.nextInt(2) == 0 ? m1.getCoefficient() : m1.getCoefficient().add(rndGenerator.nextInt(3) - 1);
					Fraction c2 = rndGenerator.nextInt(2) == 0 ? m1.getCoefficient() : m2.getCoefficient().add(rndGenerator.nextInt(3) - 1);

					String[] l1 = {"1"};
					if(rndGenerator.nextInt(2) == 0) { 
						l1 = m1.getLiterals();
					}
					String[] l2 = {"1"};
					if(rndGenerator.nextInt(2) == 0) { 
						l2 = m2.getLiterals();
					}

					Monomial m = new Monomial(c1.multiply(c2), joinStrArrays(l1, l2), false);

					result.addItem(m);
				}
			}
		}
		return result.getReducedPoly();
	}

	public boolean isProductReduced(){
		return this.getFirstFactorContent().isReduced() && this.getSecondFactorContent().isReduced();
	}



	public int getTotalLength(){
		int count = 0;
		count += firstFactorContent.getTotalLength();
		count += secondFactorContent.getTotalLength();
		return count;
	}

	public void commuteFactors() {

		selectedWidgets.clearSelected();

		firstFactor.clear();
		secondFactor.clear();
		this.clear();

		Polynomial a = new Polynomial(firstFactorContent);
		int b = firstFactorParenthesisLevel;
		firstFactorContent.destroy();
		firstFactorContent = secondFactorContent;
		firstFactorParenthesisLevel = secondFactorParenthesisLevel;
		secondFactorContent = a;
		secondFactorParenthesisLevel = b;
		secondFactorContent.setParentProduct(this);

		refresh();
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
