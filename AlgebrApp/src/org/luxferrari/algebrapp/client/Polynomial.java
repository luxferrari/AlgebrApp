package org.luxferrari.algebrapp.client;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.allen_sauer.gwt.dnd.client.drop.HorizontalPanelDropController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Polynomial extends HorizontalPanel implements Selectable, MathItem{    

	private HorizontalPanelDropController dropController;
	boolean selected = false;
	private Boolean isLeftFactor = false;
	private Boolean isRightFactor = false;
	private Product parentProduct = null;
	private Polynomial parentPoly = null;
	private SubPolynomial parentSubPoly = null;
	private boolean isDropTarget = false;


	public Polynomial(boolean makeDropTarget){
		super();
		this.setVerticalAlignment(ALIGN);
		isDropTarget = makeDropTarget;
		this.addStyleName("polynomial");
		if(makeDropTarget){
			dropController = new HorizontalPanelDropController(this);
			getMainDragController().registerDropController(this.dropController);	
		}			
	}

	public Polynomial(){
		this(true);
	}

	// Copy constructor

	public Polynomial(Polynomial poly, boolean makeDropTarget){
		this(makeDropTarget);
		Iterator<Widget> i = poly.getChildren().iterator();
		while(i.hasNext()){
			Widget item = i.next();
			if(item instanceof Monomial){
				this.addItem(new Monomial((Monomial)item, makeDropTarget));
			}
			else if(item instanceof Product){				
				this.add(new Product((Product)item, makeDropTarget));
			}
			else if(item instanceof Polynomial){
				this.add(new Polynomial((Polynomial)item, makeDropTarget));
			}
		}
	}

	public Polynomial(Polynomial p){
		this(p, true);
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

	public boolean isDropTarget(){
		return isDropTarget;
	}

	public int getParenthesisLevel(){
		int parenthesisLevel = 0;
		if(!Z_ONLY && this.getChildrenProducts().size() == 0 && this.getChildrenPolynomials().size() == 0) parenthesisLevel = 2;
		for(Product item : this.getChildrenProducts()){
			parenthesisLevel = item.getParenthesisLevel() > parenthesisLevel ? item.getParenthesisLevel() : parenthesisLevel;
		}		

		for(Polynomial item : this.getChildrenPolynomials()){
			parenthesisLevel = item.getParenthesisLevel() >= parenthesisLevel ? item.getParenthesisLevel(): parenthesisLevel;
		}	 
		return parenthesisLevel + 1;
	}

	public Product getParentProduct() {
		return parentProduct;
	}

	public void setParentProduct(Product parentProduct) {
		this.parentProduct = parentProduct;
	}

	public Polynomial getParentPoly() {
		return parentPoly;
	}	

	public void setParentPoly(Polynomial parentPoly) {
		this.parentPoly = parentPoly;
	}

	public boolean hasParentPoly(){
		return parentPoly != null;
	}

	public SubPolynomial getParentSubPoly() {
		return parentSubPoly;
	}

	public void setParentSubPoly(SubPolynomial parentSubPoly) {
		this.parentSubPoly = parentSubPoly;
	}

	public Widget getFirstItem(){
		return this.getWidget(0);
	}

	public Monomial getFirstMonomial(){
		return this.getChildrenMonomials().get(0);
	}

	public int getTotalLength(){ // Numero totale di monomi
		int count = 0;
		int members = this.getWidgetCount();;
		for(int k = 0; k < members; k++){
			if (this.getWidget(k) instanceof Monomial){
				count++;
			}
			else if (this.getWidget(k) instanceof Product){
				count += ((Product)this.getWidget(k)).getTotalLength();
			}
			else if (this.getWidget(k) instanceof SubPolynomial){
				count += ((SubPolynomial)this.getWidget(k)).getContent().getTotalLength();
			}
		}
		return count;
	}    

	public HorizontalPanelDropController getDropController(){
		return this.dropController;
	}

	public void setDropController (HorizontalPanelDropController d){
		this.dropController = d;
	}

	public void insertItem(Monomial m, int i){
		m.setParentPoly(this);
		if(i > this.getWidgetCount()) i = this.getWidgetCount();
		this.insert(m, i);  	
	}

	public void insertItem(Monomial[] m, int i){
		for (Monomial item : m){
			item.setParentPoly(this);
			this.insert(item, i);
		}
	}

	public void addItem(Monomial[] m){
		for (Monomial item : m){
			item.setParentPoly(this);
			this.add(item);
		}
	}

	public void addItem(Monomial m){
		m.setParentPoly(this);
		this.add(m);
	}

	public void insertProduct(Product p, int i){		
		p.setParentPoly(this);
		p.refresh();
		this.insert(p, i);
	}

	public void insertItem(Polynomial p, int position) {
		List<Monomial> list = p.getChildrenMonomials();		

		for(int k = list.size() - 1; k >= 0; k--){
			this.insertItem(new Monomial(list.get(k), true), position);
		}
		p.destroy();
	}

	public void insertItem(Polynomial p, int position, boolean addPoly, boolean continuousFraction, int denominator){
		SubPolynomial sp = new SubPolynomial(p, denominator, addPoly, continuousFraction);
		sp.setParentPoly(this);
		sp.getContent().refresh();
		this.insert(sp, position);
	}

	public void insertItem(Polynomial p, int position, boolean addPoly){
		insertItem(p, position, addPoly, false, 1);
	}

	public void addItem(Polynomial p, boolean addPoly){
		insertItem(p, this.getWidgetCount(), addPoly, false, 1);
	}

	public void removeMonomial(Monomial m){
		this.remove(m);
		m = null;
	}

	public void removeMonomial(List<Monomial> list) {
		for(Monomial item : list){
			if(this.getWidgetIndex(item) == -1 && item.getParentPoly().getParentSubPoly() != null){
				Polynomial parent = item.getParentPoly();
				parent.getParentSubPoly().removeFromParent();
				parent.destroy();
			}
			else this.removeMonomial(item);
		}
	}

	public void replaceMonomial(Monomial oldMon, Monomial newMon){
		int pos = this.getWidgetIndex(oldMon);
		this.removeMonomial(oldMon);
		this.insertItem(newMon, pos);
	}

	/**	Ridisegna il polinomio. Toglie il segno "+" dal primo monomio dell'espressione, se è positivo. Chiamato dopo ogni drop e in svariate altre occasioni.
	 *  Viene chiamato un casino di volte, in ogni 'addMonomial', per cui in caso di lentezza si dovrebbe rivedere quando applicarlo
	 */
	public void refresh(){
		int count = this.getWidgetCount();
		if(count == 1 && this.getParent() instanceof SubPolynomial && this.getWidget(0) instanceof Monomial){
			Monomial first = (Monomial)this.getWidget(0);
			first.showPlusSign(true);	// Per i SubPoly con un solo addendo mostra il segno tra parentesi
		}
		else{
			for(MathItem m : this.getChildrenItems()){
				m.refresh();			
			}
		}
		this.removeZeroes();
	}    	



	private void removeZeroes() {
		if(this.getWidgetCount() > 1 || (this.getParentSubPoly() != null && this.getParentSubPoly().getWidgetCount() == 1)){
			
			Iterator<Widget> it = this.getChildren().iterator();
			while(it.hasNext()){				
				Widget item = it.next();				
				if(item instanceof Monomial && ((Monomial)item).getCoefficient().value() == 0) {
					it.remove();
				}
			}
		}
	}

	/**	Svuota iterativamente il polinomio avendo cura di deregistrare il dropController
	 * 
	 */
	public void destroy() {
		getMainDragController().unregisterDropController(this.dropController);		

		Iterator<Widget> i = this.getChildren().iterator();

		while(i.hasNext()){
			Widget item = i.next();
			if(item instanceof Monomial){
				i.remove();
			}
			else if(item instanceof Symbol){
				i.remove();
			}
			else if(item instanceof MathItem){
				((MathItem) item).destroy();
			}			
		}
		this.removeFromParent();
	}	

	/* (non-Javadoc)
	 * @see org.luxferrari.algebrapp.client.Selectable#select()
	 */
	public void select(){
		if(selected) return;

		selectedWidgets.deselectOffspring(this);
		selectedWidgets.deselectAncestors(this);

		this.selected = true;
		this.getParent().removeStyleName("unselected");
		this.getParent().addStyleName("selected");
		selectedWidgets.setSelected(this);		

		NodeList<Element> list = this.getParent().getElement().getElementsByTagName("sub");
		Element index = list.getItem(list.getLength()-1);
		index.setAttribute("visibility", "visible");
		selectedWidgets.refreshSelectionIndexes();


	}

	public void deselect(){
		if(!selected) return;
		this.selected = false;
		this.getParent().removeStyleName("selected");
		this.getParent().addStyleName("unselected");		
		selectedWidgets.removeSelected(this);
		NodeList<Element> list = this.getParent().getElement().getElementsByTagName("sub");
		Element index = list.getItem(list.getLength()-1);
		index.setAttribute("visibility", "hidden");
		index.setInnerHTML("");
		selectedWidgets.refreshSelectionIndexes();
	}

	public boolean isSelected(){
		return selected;
	}


	public void refreshIndex(String s){
		NodeList<Element> list = this.getParent().getElement().getElementsByTagName("sub");
		Element index = list.getItem(list.getLength()-1);
		index.setInnerHTML(s);
	}

	public Boolean isReduced(){

		int l = this.getTotalLength();

		if (l == 1) { return true;}

		Monomial[] m = new Monomial[l];
		for(int k = 0; k < l; k++){	    
			if(!(this.getWidget(k) instanceof Monomial)) return false;
			m[k] = (Monomial)this.getWidget(k);
		}

		for(int j = 0; j < l-1; j++){
			for(int k = j + 1; k < l; k++){
				if(m[j].isSimilar(m[k])){
					return false;
				}
			}
		}

		return true;
	}

	public Boolean isOnlyAProduct(){
		if(this.getWidgetCount() == 1 && this.getWidget(0) instanceof Product) return true;
		else return false;
	}

	/*public Map<double, List<String>> contentMap(){
		HashMap<double, List<String>> result = new HashMap<double, List<String>>();
		for(Widget item : this.getChildren()){
			if(item instanceof Monomial){
				result.put(((Monomial) item).getCoefficient().value(), Arrays.asList(((Monomial) item).getLiterals()));
			}				
		}
		return result;
	}*/

	public boolean isEqualTo(Polynomial p) {
		int count = this.getWidgetCount();
		if(p == null) return false;
		if(count != p.getWidgetCount()) return false;

		List<Monomial> list = p.getChildrenMonomials();

		for(Monomial m : getChildrenMonomials()){
			if(!m.isContainedIn(list)) return false;
		}
		return true;
	}

	public boolean isContainedIn(List<Polynomial> list){
		boolean result = false;
		for(Polynomial item : list){
			if(item.isEqualTo(this)){
				result = true;
				break;
			}
		}
		return result;
	}
	
	

	public List<Monomial> getChildrenMonomials(){
		List<Monomial> result = new ArrayList<Monomial>();
		for(Widget item : this.getChildren()){
			if(item instanceof Monomial){
				result.add((Monomial)item);
			}				
		}
		return result;		
	}

	public List<MathItem> getChildrenItems(){
		List<MathItem> result = new ArrayList<MathItem>();
		for(Widget item :this.getChildren()){
			if(item instanceof MathItem){
				result.add((MathItem)item);
			}				
		}
		return result;		
	}
	
	public List<Product> getChildrenProducts(){
		List<Product> result = new ArrayList<Product>();
		for(Widget item :this.getChildren()){
			if(item instanceof Product){
				result.add((Product)item);
			}				
		}
		return result;		
	}

	public List<Polynomial> getChildrenPolynomials(){
		List<Polynomial> result = new ArrayList<Polynomial>();
		for(Widget item : this.getChildren()){
			if(item instanceof SubPolynomial){
				Polynomial p = ((SubPolynomial)item).getContent();
				result.add(p);
				if(p.getChildrenPolynomials().size() > 0){
					result.addAll(p.getChildrenPolynomials());
				}
			}				
			if(item instanceof Product){
				Polynomial f1 = ((Product)item).getFirstFactorContent();
				result.add(f1);
				if(f1.getChildrenPolynomials().size() > 0){
					result.addAll(f1.getChildrenPolynomials());
				}
				Polynomial f2 = ((Product)item).getSecondFactorContent();
				result.add(f2);
				if(f2.getChildrenPolynomials().size() > 0){
					result.addAll(f2.getChildrenPolynomials());
				}
			}
		}
		return result;		
	}

	public Polynomial getReducedPoly() {
		List<Monomial> list = this.getChildrenMonomials();
		if(list.size() == 1) return this;

		Polynomial result = new Polynomial(false);		
		for(int j = 0; j < list.size(); j++){
			Monomial m = list.get(j);
			if(!hasSimilars(m) && !result.hasSimilars(m)){
				result.addItem(new Monomial(m));
			}
			else if(!result.hasSimilars(m)){
				Fraction c = new Fraction(m.getCoefficientCopy());
				for(int k = j + 1; k < list.size(); k++){
					if(m.isSimilar(list.get(k))) c = c.add(list.get(k).getCoefficientCopy());
				}
				result.addItem(new Monomial(c, m.getLiterals()));
			}
		}

		return result;
	}

	private boolean hasSimilars(Monomial m){
		List<Monomial> list = this.getChildrenMonomials();
		int position = list.indexOf(m);
		for(int k = 0; k < list.size(); k++){
			if(k == position) continue;
			if(m.isSimilar(list.get(k))) {
				return true;
			}
		}
		return false;
	}
}
