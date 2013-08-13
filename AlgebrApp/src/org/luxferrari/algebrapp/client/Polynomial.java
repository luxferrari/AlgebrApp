package org.luxferrari.algebrapp.client;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.getMainDragController;
import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.selectedWidgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.dnd.client.drop.HorizontalPanelDropController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Polynomial extends HorizontalPanel implements Selectable{    

	private HorizontalPanelDropController dropController;
	String name = "";
	boolean selected = false;
	private Boolean isLeftFactor = false;
	private Boolean isRightFactor = false;
	private Product parentProduct = null;


	public Polynomial(boolean makeDropTarget){
		super();
		if(makeDropTarget){
			dropController = new HorizontalPanelDropController(this);
			getMainDragController().registerDropController(this.dropController);	
		}			
	}

	public Polynomial(){
		this(true);
	}

	// Copy constructor

	public Polynomial(Polynomial poly, boolean b){
		this(b);
		Iterator<Widget> i = poly.getChildren().iterator();
		while(i.hasNext()){
			Widget item = i.next();
			if(item instanceof Monomial){
				this.addMonomial(new Monomial((Monomial)item, b));
			}
			if(item instanceof Product){				
				this.add(new Product((Product)item, b));
			}
			if(item instanceof Polynomial){
				this.add(new Polynomial((Polynomial)item, b));
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


	public Product getParentProduct() {
		return parentProduct;
	}

	public void setParentProduct(Product parentProduct) {
		this.parentProduct = parentProduct;
	}

	public int getLength(){ // Numero totale di monomi
		int count = 0;
		int members = this.getWidgetCount();;
		for(int k = 0; k < members; k++){
			if (this.getWidget(k) instanceof Monomial){
				count++;
			}
			else if (this.getWidget(k) instanceof Product){
				count += ((Product)this.getWidget(k)).getLength();
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

	public void insertMonomial(Monomial m, int i){
		m.setParentPoly(this);
		this.insert(m, i);  	
	}

	public void insertMonomial(Monomial[] m, int i){
		for (Monomial item : m){
			item.setParentPoly(this);
			this.insert(item, i);
		}
	}

	public void addMonomial(Monomial[] m){
		for (Monomial item : m){
			item.setParentPoly(this);
			this.add(item);
		}
	}

	public void addMonomial(Monomial m){
		m.setParentPoly(this);
		this.add(m);
	}

	public void insertProduct(Product p, int i){		
		p.setParentPoly(this);
		p.refreshProduct();
		this.insert(p, i);
	}

	public void insertPolynomial(Polynomial p, int position) {
		List<Monomial> list = p.getChildrenMonomials();		
		for(int k = list.size() - 1; k >= 0; k--){
			this.insertMonomial(list.get(k), position);
		}		
	}


	public void removeMonomial(Monomial m){
		this.remove(m);
		m = null;
	}

	public void removeMonomial(List<Monomial> list) {
		for(Monomial item: list){
			this.removeMonomial(item);
		}
	}

	/**	Ridisegna il polinomio. Toglie il segno "+" dal primo monomio dell'espressione, se è positivo. Chiamato dopo ogni drop e in svariate altre occasioni.
	 *  Viene chiamato un casino di volte, in ogni 'addMonomial', per cui in caso di lentezza si dovrebbe rivedere quando applicarlo
	 */
	public void refreshPolynomial(){	
		int count = this.getWidgetCount();
		for(int k = 0; k < count; k++){
			if (k == 0 && this.getWidget(0) instanceof Monomial){
				Monomial first = (Monomial)this.getWidget(0);
				first.showPlusSign = false;
				first.refreshHTML();
			}
			if (this.getWidget(k) instanceof Monomial){
				Monomial m = (Monomial)this.getWidget(k);
				if(k==0) m.showPlusSign = false;
				if (k > 0 && !m.showPlusSign) m.showPlusSign = true;
				m.refreshHTML();		
			} 
			else if (this.getWidget(k) instanceof Product){
				((Product)this.getWidget(k)).refreshProduct();
			}				
		}
	}    	



	/**	Svuota iterativamente il polinomio avendo cura di deregistrare il dropController
	 * 
	 */
	public void disposeOfMembers() {
		getMainDragController().unregisterDropController(this.dropController);		

		Iterator<Widget> i = this.getChildren().iterator();

		while(i.hasNext()){
			Widget item = i.next();
			if(item instanceof Monomial){
				i.remove();
			}
			if(item instanceof Product){
				((Product) item).getFirstFactorContent().disposeOfMembers();
				((Product) item).getSecondFactorContent().disposeOfMembers();
			}
		}		
	}	

	/* (non-Javadoc)
	 * @see org.luxferrari.algebrapp.client.Selectable#select()
	 */
	public void select(){
		this.selected = true;
		this.getParent().setStyleName("selected");
		selectedWidgets.setSelected(this);

		NodeList<Element> list = this.getParent().getElement().getElementsByTagName("sub");
		Element index = list.getItem(list.getLength()-1);
		index.setAttribute("visibility", "visible");
		selectedWidgets.refreshSelectionIndexes();
	}

	public void deselect(){
		this.selected = false;
		this.getParent().setStyleName("unselected");		
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

	public Boolean checkReduced(){

		int l = this.getLength();

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

	public Map<Integer, List<String>> contentMap(){
		HashMap<Integer, List<String>> result = new HashMap<Integer, List<String>>();
		for(Widget item : this.getChildren()){
			if(item instanceof Monomial){
				result.put(((Monomial) item).getCoefficient(), Arrays.asList(((Monomial) item).getLiterals()));
			}				
		}
		return result;
	}

	public boolean isEqualTo(Polynomial p) {
		return this.contentMap().equals(p.contentMap());
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
		for(Widget item :this.getChildren()){
			if(item instanceof Monomial){
				result.add((Monomial)item);
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
		for(Widget item :this.getChildren()){
			if(item instanceof Polynomial){
				result.add((Polynomial)item);
			}				
		}
		return result;		
	}
}
