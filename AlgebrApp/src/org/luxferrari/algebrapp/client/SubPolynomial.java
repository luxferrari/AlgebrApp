package org.luxferrari.algebrapp.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;

public class SubPolynomial extends HorizontalPanel implements MathItem{
	private Polynomial content = null;
	private Polynomial parentPoly = null;
	private int denominator = 1;
	private boolean hasPlus = true;
	private boolean hasSign = true;
	private boolean continuousFraction = false;
	private int parenthesisLevel = 0;

	public SubPolynomial(Polynomial p, int denominator, boolean hasPlus, boolean continuousFraction){
		super();
		this.setVerticalAlignment(ALIGN);
		this.addStyleName("subPoly");
		content = p;
		p.setParentSubPoly(this);
		this.hasPlus = hasPlus;
		this.hasSign = true;		
		this.continuousFraction = continuousFraction;
		this.denominator = denominator;
		parenthesisLevel = content.getParenthesisLevel() + 1;
		
		if(content.isDropTarget()){
			//dropController = new HorizontalPanelDropController(this);
			//getMainDragController().registerDropController(this.dropController);	
		}	

		rebuildHTML();

	}

	private void rebuildHTML() {
		this.clear();
		Widget begin = null;
		Widget end = null;

		String sign = "";
		if(hasSign) {
			sign = "<span class='sign'> + </span> ";
			if(!hasPlus) sign = "<span class='sign'> &#150 </span>";	
		}
		

		this.add(content);

		boolean hasParenthesis = !continuousFraction || this.content.getWidgetCount() == 1;

		String open = "";		
		if(hasParenthesis) open = "<span class='parenthesis size" + parenthesisLevel + "'>(</span>";

		begin = new Symbol(sign + open);
		((Symbol)begin).setMyParent(this);
		if(!Z_ONLY) begin.addStyleName("fraction");
		this.insert(begin, 0);
		
		if(continuousFraction){
			this.add(new HTML(""+denominator));
		}

		if(hasParenthesis){
			end = new Symbol(")");
			end.addStyleName("parenthesis size" + parenthesisLevel);
			((Symbol)end).setMyParent(this);
			this.add(end);
		}

		this.add(new HTML("<sub class='index subPolyIndex'></sub>"));


		if(continuousFraction){
			this.addStyleName("continuousFraction");
			this.getElement().getFirstChildElement().getFirstChildElement().getFirstChildElement().getNextSiblingElement().getNextSiblingElement().setAttribute("class", "bottom");
			this.getElement().getFirstChildElement().getFirstChildElement().getFirstChildElement().getNextSiblingElement().setAttribute("class",  "top");
		}
		
	}

	public SubPolynomial(Polynomial p, boolean hasPlus){
		this(p, 1, hasPlus, false);
	}

	// Copy constructor


	public SubPolynomial(SubPolynomial sub){
		this(new Polynomial(sub.getContent(), false), sub.getDenominator(), sub.hasPlus(), sub.isContinuousFraction());
		this.setParentPoly(sub.getParentPoly());
		this.refresh();
	}

	public Polynomial getContent() {
		return content;
	}

	public void setContent(Polynomial content) {
		this.content = content;
		/*if(content.isDropTarget()){
			dropController = new HorizontalPanelDropController(this);
			getMainDragController().registerDropController(this.dropController);	
		}
		else{
			getMainDragController().unregisterDropController(this.dropController);	
		}*/
	}	

	
	public int getDenominator() {
		return denominator;
	}

	public void setDenominator(int denominator) {
		this.denominator = denominator;
	}

	public int getParenthesisLevel() {
		return content.getParenthesisLevel() + 1;
	}

	public Polynomial getParentPoly() {
		return parentPoly;
	}

	public Product getParentProduct(){
		return parentPoly.getParentProduct();
	}
	
	public void setParentPoly(Polynomial parentPoly) {
		this.parentPoly = parentPoly;
		this.content.setParentPoly(parentPoly);
	}

	public boolean hasPlus() {
		return hasPlus;
	}

	public void hasPlus(boolean hasPlus) {
		this.hasPlus = hasPlus;
	}

	public boolean isContinuousFraction() {
		return continuousFraction;
	}

	public void setContinuousFraction(boolean continuousFraction) {
		this.continuousFraction = continuousFraction;
	}

	public void removeSign(){
		this.remove(0);
		String html = "";
		if(!continuousFraction) html = "<span class='size" + parenthesisLevel + "'>(</span>";
		Symbol begin = new Symbol(html);
		begin.setMyParent(this);
		this.insert(begin, 0);
		hasSign = false;
	}

	public void restoreSign(){
		this.remove(0);
		String sign = "+";
		String open = "";
		if(!hasPlus) sign = "&#150";
		if(!continuousFraction) open = "<span class='size" + parenthesisLevel + "'>(</span>";
		Symbol begin = new Symbol(sign + open);
		begin.setMyParent(this);
		this.insert(begin, 0);
		hasSign = true;
	}
	
	public void destroy(){
		content.destroy();
		content.removeFromParent();
		//getMainDragController().unregisterDropController(this.dropController);		
		this.removeFromParent();
	}

	public ArrayList<Polynomial> polyResultList(){

		if(this.isContinuousFraction()) {
			this.normalizeContinuousFraction();
		}

		ArrayList<Polynomial> result = new ArrayList<Polynomial>();
		int sign = hasPlus() ? 1: -1; 
		Polynomial first = new Polynomial(false);		

		for(Monomial item : this.getContent().getChildrenMonomials()){
			first.addItem(new Monomial(item.getCoefficient().multiply(sign), item.getLiterals(), false));
		}
		result.add(first);

		Polynomial second = new Polynomial(false);
		for(Monomial item : this.getContent().getChildrenMonomials()){
			second.addItem(new Monomial(item.getCoefficient().multiply(sign * -1), item.getLiterals(), false));
		}
		result.add(second);

		Polynomial third = new Polynomial(false);
		for(Monomial item : this.getContent().getChildrenMonomials()){
			if(this.getContent().getChildrenMonomials().indexOf(item) == 0) {
				third.addItem(new Monomial(item.getCoefficient().multiply(sign), item.getLiterals(), false));
			}
			else third.addItem(new Monomial(item.getCoefficient().multiply(sign * -1), item.getLiterals(), false));
		}
		if(!third.isContainedIn(result)) result.add(third);

		return result;
	}

	public void normalizeContinuousFraction() {
		List<Monomial> list = this.content.getChildrenMonomials();

		for(Monomial item : list){
			item.getCoefficient().setDenominator(denominator);
			item.refresh();
		}

		continuousFraction = false;
	}

	public void refresh() {
		
		if(this.getContent().getWidgetCount() == 1 && this.getContent().getFirstMonomial() != null){
			int position = this.getParentPoly().getWidgetIndex(this);
			Monomial m = new Monomial(this.getContent().getFirstMonomial(), true, false);
			if(this.denominator > 1) m.getCoefficient().setDenominator(this.denominator);
			if(!this.hasPlus) m.invertSignAndValue();			
			if(m.getCoefficient().value() != 0) this.getParentPoly().insertItem(m, position);
			this.destroy();
			this.getParentPoly().refresh();
		}
		
		this.rebuildHTML();
		this.getContent().refresh();
		
		if(this.getParentPoly().getWidgetIndex(this) == 0 && hasSign && hasPlus){
			removeSign();
		}
		else if(this.getParentPoly().getWidgetIndex(this) > 0 && !hasSign){
			restoreSign();
		}
	}
}
