package org.luxferrari.algebrapp.client;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.luxferrari.algebrapp.client.AlgebrAppGlobals.errorType;

public class SelectedItemsList extends ArrayList<Selectable>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6452164406591647641L;
	
	public SelectedItemsList() {
		super();
	}

	public SelectedItemsList(SelectedItemsList list) {
		this();
		for(Selectable item : list){
			this.add(item);
		}
	}


	public boolean containsSubPoly(){
		if(this.isEmpty()) return false;
		if(!(this.get(0) instanceof Polynomial)) return false;		
		return (this.size() == 1 && ((Polynomial)this.get(0)).getParentPoly() != null);
	}

	public SubPolynomial getSubPoly(){		
		SubPolynomial subPoly = ((Polynomial)this.get(0)).getParentSubPoly();			
		return subPoly;
	}

	public void setSelected(Selectable s){
		this.add(s);
		if(this.size() > 0){
			showOperations(true);
		}
	}

	public boolean isSelected(Selectable s){
		return s.isSelected();
	}


	public void removeSelected(Selectable s){
		this.remove(s);
		if(this.size() == 0){
			showOperations(false);
		}
	}

	public void clearSelected(){
		int size = this.size();
		Selectable[] list = new Selectable[size];

		for(int k = 0; k < size; k++){
			list[k]=this.get(k);
		}
		for(int k = 0; k < size; k++){
			list[k].deselect();
		}
		this.clear();
	}

	public void refreshSelectionIndexes() {

		for(Selectable item : this){
			String index = Integer.toString(this.indexOf(item) + 1);  // Inizia da 1
			item.refreshIndex(index); 
		}

	}

	private void showOperations(boolean b) {
		operate.setVisible(b);
	}

	/**	Controlla i monomi selezionati possono essere addizionati
	 * @return	true/false
	 */
	public errorType canPerformAddition(){
		Polynomial parent = null;
		Polynomial candidateParent = null;
		Monomial prev = null;
		additionParentPoly = null;

		for(Selectable item : this){
			if(item == null) continue;
			Monomial m = null;

			if(!(item instanceof Monomial)) {		// Nel caso in cui sia selezionato un monomio in un SubPoly che contiene solo lui (o il subpoly stesso)
				if(item instanceof Polynomial && ((Polynomial)item).getParentSubPoly() != null && ((Polynomial)item).getWidgetCount() == 1){
					Polynomial p = (Polynomial)item;				
					candidateParent = p.getParentPoly();
					// Controlla che siano tutti nello stesso polinomio
					if(parent == null){parent = candidateParent;}
					else{
						if(!parent.equals(candidateParent)) {return errorType.ORDER_OF_OPERATIONS;}	// precedenze			
					}
				}
				else return errorType.ORDER_OF_OPERATIONS; 	// Devono essere selezionati solo monomi
			}
			else if(((Monomial)item).getParentPoly().getParentSubPoly() != null && ((Monomial)item).getParentPoly().getWidgetCount() == 1){
				m = (Monomial)item;
				candidateParent = m.getParentPoly().getParentPoly();
				// Controlla che siano tutti nello stesso polinomio
				if(parent == null){parent = candidateParent;}
				else{
					if(!parent.equals(candidateParent)) {return errorType.ORDER_OF_OPERATIONS;}	// precedenze			
				}
			}
			else{
				m = (Monomial)item;
				candidateParent = m.getParentPoly();
				// Controlla che siano tutti nello stesso polinomio
				if(parent == null){parent = candidateParent;}
				else{
					if(!parent.equals(candidateParent)) {return errorType.ORDER_OF_OPERATIONS;}	// precedenze			
				}
			}			

			// Controlla che siano simili
			if(prev == null){prev = m;}
			else{
				if(!prev.isSimilar(m)){return errorType.NOT_SIMILAR; } // non simili
			}
		}
		additionParentPoly = parent;
		
		if(FORCE_NEIGHBOURS){
			ArrayList<Integer> indices = new ArrayList<Integer>();
			for(Selectable item : this){
				if(item instanceof Monomial) indices.add(additionParentPoly.getWidgetIndex((Monomial)item));
				else if(item instanceof Polynomial) indices.add(additionParentPoly.getWidgetIndex(((Polynomial)item).getParentSubPoly()));
			}
			
			Collections.sort(indices);
			for(int k = 1; k < indices.size(); k++){
				if(indices.get(k) - indices.get(k-1) != 1) return errorType.NOT_NEIGHBOURS;
			}
		}		
		
		return errorType.NONE;
	}
	
	
	/** Esegue l'addizione dei monomi selezionati, se possibile
	 * @return	Il monomio risultante dall'addizione
	 */
	public Monomial additionResult(){

		Fraction coeff = new Fraction(0);
		String[] literals = null;

		if(this.get(0) instanceof Monomial){
			literals = ((Monomial)this.get(0)).getLiterals();	// La parte letterale è uguale per tutti i monomi, sicché prendo quella del primo
		}
		else if(this.get(0) instanceof Polynomial){
			literals = ((Polynomial)this.get(0)).getFirstMonomial().getLiterals();	// La parte letterale è uguale per tutti i monomi, sicché prendo quella del primo
		}

		for(Selectable item : this){
			if(item instanceof Monomial) coeff = coeff.add(((Monomial)item).getCoefficient());
			else if (item instanceof Polynomial) coeff = coeff.add( ((Polynomial)item).getFirstMonomial().getCoefficient().multiply(((Polynomial)item).getParentSubPoly().hasPlus()?1:-1));
		}
		return new Monomial(coeff, literals);
	}

	public ArrayList<Monomial> additionResultsList(){

		Monomial first = (Monomial)this.get(0);
		Monomial second = (Monomial)this.get(1);
		int rndLiterals = 0;		

		ArrayList<Monomial> output = new ArrayList<Monomial>();
		Monomial solution = this.additionResult();
		String[] literals = solution.getLiterals();

		output.add(solution);		
		Monomial case0 = new Monomial(this.incorrectAddition(0), first.getLiterals(), false); // Casi fissi, errori comuni
		Monomial case1 = new Monomial(this.incorrectAddition(1), first.getLiterals(), false);		
		if(!case0.isContainedIn(output)){output.add(case0);}
		if(!case1.isContainedIn(output)){output.add(case1);}
		
		// "Sottrazione" della parte letterale, tipo 3x - x = 3 oppure 5x - 2x = 3
		
		if(second.getCoefficient().equals(-1)){
			Monomial case2 = new Monomial(first.getCoefficientCopy(), this.incorrectLiterals(literals, literals, 2), false);			
			if(!case2.isContainedIn(output)){output.add(case2);}
		}
		else if(second.getCoefficient().value() < 0){
			Monomial case2 = new Monomial(solution.getCoefficientCopy(), this.incorrectLiterals(literals, literals, 2), false);			
			if(!case2.isContainedIn(output)){output.add(case2);}
		}

		int failSafe = 0;

		while(output.size() < SHOWN_ADDITIONS_NUMBER){		

			if(!first.isScalar()){ 
				rndLiterals = rndGenerator.nextInt(INCORRECT_LITERALS_CASES);
			}

			if(rndLiterals > 0){
				Monomial case2 = new Monomial(solution.getCoefficientCopy(), this.incorrectLiterals(literals, literals, rndLiterals), false);
				if(!case2.isContainedIn(output)){
					output.add(case2);
					continue;
				}
			}

			int rndCoeffCase = 2 + rndGenerator.nextInt(INCORRECT_ADDITION_CASES - 2); //casi da 2 a max

			Monomial candidate = new Monomial(this.incorrectAddition(rndCoeffCase), this.incorrectLiterals(literals, literals, rndLiterals), false);

			if(!candidate.isContainedIn(output)){
				output.add(candidate);
				continue;
			}

			// Failsafe
			failSafe++;
			if(failSafe > 100 && DEBUG) {
				System.err.println("Candidate: " + candidate.getCoefficient().value() + " Result size: " + output.size());	
			}	
			if(failSafe > 105) break;
		}

		// Shuffle list

		List<Integer> tempList = new ArrayList<Integer>();
		for(int k = 0; k < output.size(); k++) {
			tempList.add(k);
		}		
		return output;
	}

	private Fraction incorrectAddition(int c){
		Fraction result = ((Monomial)this.get(0)).getCoefficientCopy();
		for(int k = 1; k < this.size(); k++){			
			result = this.incorrectPairwiseAddition(result, ((Monomial)this.get(k)).getCoefficientCopy(), c);
		}
		return result;
	}

	private Fraction incorrectPairwiseAddition(Fraction c1, Fraction c2, int c){	
		Fraction result = null;	

		switch(c) {		
		case 0:										// Errore di addizione (!)
			int r = rndGenerator.nextInt(2);
			int e = r == 0? -1 : 1;			
			if(c1.isInteger() && c2.isInteger()) result = c1.add(c2).add(e);
			else result = new Fraction(c1.getNumerator()+c2.getNumerator(), c1.getDenominator()+c2.getDenominator());
			break;		
		case 1:										// Inverte secondo segno (!)
			result = c1.add(c2.multiply(-1));
			break;
		case 2:										// Inverte primo segno
			result = c1.multiply(-1).add(c2);
			break;
		case 3:										// Inverte segni
			if(c1.isInteger() && c2.isInteger()) result = c1.add(c2).multiply(-1);
			else result = new Fraction(c1.getNumerator()+c2.getNumerator(), c1.getDenominator() * c2.getDenominator());
			break;
		}		
		return result;
	}


	private String[] incorrectLiterals(String[] l1, String[] l2, int c){
		String[] result = new String[l1.length];
		switch(c) {
		case 0:
			result = l1;
			break;
		case 1:
			result = joinStrArrays(l1, l2);			// "Somma" la parte letterale (cioè, la moltiplica...)
			break;	
		case 2:
			for(int k = 0; k < l1.length; k++){		// "Sottrae" la parte letterale (cioè, la divide...)
				result[k]="1";
			};
			break;
		}
		return result;		
	}


	/** Controlla che la selezione comprenda (tutti) i fattori di un prodotto
	 * @return	true/false
	 */
	public Product returnProduct(){

		Product prod = null;

		boolean foundFirstFactor = false;	
		boolean foundSecondFactor = false;

		ArrayList<Monomial> monList = new ArrayList<Monomial>();

		// Controlla che tutti i monomi e i polinomi appartengano allo stesso prodotto

		for(Selectable item : this){
			if(item instanceof Monomial){				
				Monomial m = (Monomial)item;
				if(prod == null) {prod = m.getParentPoly().getParentProduct();
				}
				else{
					if(!prod.equals(m.getParentPoly().getParentProduct())){return null; }	// precedenze			
				}
				if(prod == null){return null;}	// È il caso se il monomio appartiene al polinomio principale, quindi non è in un prodotto
				monList.add(m);

			}
			else if(item instanceof Polynomial){	
				Polynomial p = (Polynomial)item;
				if(prod == null) {
					if(p.getParentSubPoly() != null) return null;
					prod = p.getParentProduct();
				}
				else{
					if(!prod.equals(p.getParentProduct())){return null;}	// precedenze
				}

				if(p.equals(prod.getFirstFactorContent())){foundFirstFactor = true;}
				if(p.equals(prod.getSecondFactorContent())){foundSecondFactor = true;}


			}
			else{
				return null;	// Non dovrebbe accadere
			}			
		}

		if(foundFirstFactor && foundSecondFactor) {
			if(monList.size() == 0){return prod;}
			else{return null;}	// Per evitare di validare i fattori selezionati con monomi selezionati all'interno
		}

		int counter_1 = 0;
		int counter_2 = 0;

		Polynomial firstFactor = prod.getFirstFactorContent();
		Polynomial secondFactor = prod.getSecondFactorContent();

		// Controlla che eventuali monomi completino i fattori del prodotto			

		for(Monomial item : monList){
			if(firstFactor.getWidgetIndex(item) > -1) { counter_1++;}
			if(secondFactor.getWidgetIndex(item) > -1) { counter_2++;}
		}

		if(counter_1 == firstFactor.getTotalLength() && !foundFirstFactor){foundFirstFactor = true; } 
		if(counter_2 == secondFactor.getTotalLength() && !foundSecondFactor){foundSecondFactor = true;}

		if(foundFirstFactor && foundSecondFactor) {	return prod;} 

		return null;
	}	

	public Polynomial getAdditionPolynomial(){
		Polynomial result = new Polynomial(false);
		boolean onlySubPoly = false;

		if(!SIMPLIFIED && this.get(0) instanceof Monomial && !((Monomial)this.get(0)).hasPlus()) Collections.swap(this, 0, 1);
		
		for(Selectable item : this){
			if(item instanceof Polynomial && !((Polynomial)item).getParentSubPoly().hasPlus()) onlySubPoly = true;
		}

		for(Selectable item : this){
			if(item instanceof Monomial){
				if(onlySubPoly) {
					Polynomial p = ((Monomial)item).getParentPoly();
					result.addItem(new Polynomial(p, false), p.getParentSubPoly().hasPlus());
				}
				else result.addItem(new Monomial((Monomial)item, false));
			}
			else if(item instanceof Polynomial){
				result.addItem(new Polynomial((Polynomial)item, false), ((Polynomial)item).getParentSubPoly().hasPlus());
			}
		}
		result.removeStyleName("dragdrop-handle");
		return result;
	}

	public void normalizeAddition() { //TODO preservare ordine

		ArrayList<Monomial> monList = new ArrayList<Monomial>();
		ArrayList<Polynomial> polyList = new ArrayList<Polynomial>();
		ArrayList<Integer> position = new ArrayList<Integer>();


		for(Selectable item : this){
			if (item instanceof Polynomial){
				Polynomial p = (Polynomial)item;
				polyList.add(p);

				Monomial m = p.getFirstMonomial();
				if(!p.getParentSubPoly().hasPlus()){
					m = new Monomial(m.getCoefficient().multiply(-1), m.getLiterals());
				}
				else monList.add(m);				
				position.add(this.indexOf(p));
			}
		}		

		for(int k = 0; k < polyList.size(); k++){
			this.remove(polyList.get(k));
			this.add(position.get(k), monList.get(k));
		}




	}

	public void deselectAncestors(Polynomial sp) {
		List<Selectable> toDeselect = new ArrayList<Selectable>();

		for(Selectable item : this){
			if(item instanceof Polynomial){
				Polynomial p = (Polynomial)item;
				if(p.getChildrenPolynomials().contains(sp)){
					toDeselect.add(p);
				}
			}
		}	

		Iterator<Selectable> i = toDeselect.iterator();
		while(i.hasNext()){
			i.next().deselect();
		}
	}

	public void deselectOffspring(Polynomial p){
		List<Polynomial> polyList = p.getChildrenPolynomials();
		polyList.add(p);
		List<Monomial> monList = new ArrayList<Monomial>();
		List<Selectable> toDeselect = new ArrayList<Selectable>();

		for(Polynomial item : polyList){
			monList.addAll(item.getChildrenMonomials());
		}

		for(Selectable item : this){
			if(item instanceof Polynomial){
				if(polyList.contains(item)){
					toDeselect.add(item);
				}				
			}
			if(item instanceof Monomial){
				if(monList.contains(item)){
					toDeselect.add(item);
				}				
			}
		}	

		Iterator<Selectable> i = toDeselect.iterator();
		while(i.hasNext()){
			i.next().deselect();
		}
	}

	public void deselectAncestors(Monomial m) {

		List<Selectable> toDeselect = new ArrayList<Selectable>();

		for(Selectable item : this){
			if(item instanceof Polynomial){
				Polynomial p = (Polynomial)item;
				if(p.getChildrenMonomials().contains(m)){
					toDeselect.add(p);
				}
				else{
					List<Polynomial> plist = p.getChildrenPolynomials();
					for(Polynomial sp : plist){
						if(sp.getChildrenMonomials().contains(m)){
							toDeselect.add(p);
						}
					}
				}
			}
		}

		Iterator<Selectable> i = toDeselect.iterator();
		while(i.hasNext()){
			i.next().deselect();
		}
	}	
}
