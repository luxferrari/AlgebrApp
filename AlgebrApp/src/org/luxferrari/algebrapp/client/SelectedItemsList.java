package org.luxferrari.algebrapp.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.luxferrari.algebrapp.client.Selectable;
import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;

public class SelectedItemsList extends ArrayList<Selectable>{

	public SelectedItemsList() {
		super();
	}

	public SelectedItemsList(SelectedItemsList list) {
		this();
		for(Selectable item : list){
			this.add(item);
		}
	}

	public void setSelected(Selectable s){
		this.add(s);
		if(this.size()>1){
			showOperations(true);
		}
	}

	public boolean isSelected(Selectable s){
		return s.isSelected();
	}

	public void removeSelected(Selectable s){
		this.remove(s);
		if(this.size()<2){
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
		Monomial prev = null;

		for(Selectable item : this){
			if(item == null) continue;

			if(!(item instanceof Monomial)) return errorType.PRECEDENZE; 	// Devono essere selezionati solo monomi

			Monomial m = (Monomial)item;

			// Controlla che siano tutti nello stesso polinomio
			if(parent == null){parent = m.getParentPoly();}
			else{
				if(!parent.equals(m.getParentPoly())) {return errorType.PRECEDENZE;}	// precedenze			
			}

			// Controlla che siano simili
			if(prev == null){prev = m;}
			else{
				if(!prev.isSimilar(m)){return errorType.NON_SIMILI; } // non simili
			}
		}
		return errorType.NONE;
	}

	/** Esegue l'addizione dei monomi selezionati, se possibile
	 * @return	Il monomio risultante dall'addizione
	 */
	public Monomial addition(){

		//if(!this.canPerformAddition()) {return null;} // Sanity check

		int coeff = 0;
		String[] literals = ((Monomial)this.get(0)).getLiterals();	// La parte letterale è uguale per tutti i monomi, sicché prendo quella del primo
		for(Selectable item : this){
			coeff += ((Monomial)item).getCoefficient();
		}
		return new Monomial(coeff, literals);
	}

	public ArrayList<Monomial> additionResultsList(){

		Monomial first = (Monomial)this.get(0);
		int rndLiterals = 0;		
				
		ArrayList<Monomial> result = new ArrayList<Monomial>();
		Monomial solution = this.addition();
		String[] literals = solution.getLiterals();
		
		result.add(solution);		
		Monomial case0 = new Monomial(this.incorrectAddition(0), first.getLiterals(), false); // Casi fissi, errori comuni
		Monomial case1 = new Monomial(this.incorrectAddition(1), first.getLiterals(), false);		
		if(!case0.isContainedIn(result)){result.add(case0);}
		if(!case1.isContainedIn(result)){result.add(case1);}
		
		while(result.size() < SHOWN_ADDITIONS_NUMBER){
			
			if(!first.isScalar()){ 
				rndLiterals = rndGenerator.nextInt(INCORRECT_LITERALS_CASES);
			}
			
			if(rndLiterals > 0){
				Monomial case2 = new Monomial(solution.getCoefficient(),this.incorrectLiterals(literals, literals, rndLiterals));
				if(!case2.isContainedIn(result)){
					result.add(case2);
					continue;
				}
			}
			
			int rndCoeffCase = 2 + rndGenerator.nextInt(INCORRECT_ADDITION_CASES - 2); //casi da 2 a max
			
			Monomial candidate = new Monomial(this.incorrectAddition(rndCoeffCase), this.incorrectLiterals(literals, literals, rndLiterals), false);
			
			if(!candidate.isContainedIn(result)){
				result.add(candidate);				
			}
		}
		
		// Shuffle list
		
		List<Integer> tempList = new ArrayList<Integer>();
	    for(int k = 0; k < result.size(); k++) {
	        tempList.add(k);
	    }		
		return result;
	}
	
	private int incorrectAddition(int c){
		int result = ((Monomial)this.get(0)).getCoefficient();
		for(int k = 1; k < this.size(); k++){			
			result = this.incorrectPairwiseAddition(result, ((Monomial)this.get(k)).getCoefficient(), c);
		}
		return result;
	}

	private int incorrectPairwiseAddition(int c1, int c2, int c){	
		int result = 0;	
		
		switch(c) {		
		case 0:										// Errore di addizione (!)
			int r = rndGenerator.nextInt(2);
			int e = r == 0? -1 : 1;			
			result = c1 + c2 + e;
			break;		
		case 1:										// Inverte secondo segno (!)
			result = c1 - c2;
			break;
		case 2:										// Inverte primo segno
			result = -c1+c2;
			break;
		case 3:										// Inverte segni
			result = -c1 - c2;
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
					if(!prod.equals(m.getParentPoly().getParentProduct())){return null;}	// precedenze			
				}
				if(prod == null){return null;}	// È il caso se il monomio appartiene al polinomio principale, quindi non è in un prodotto
				monList.add(m);

			}
			else if(item instanceof Polynomial){	
				Polynomial p = (Polynomial)item;
				if(prod == null) {prod = p.getParentProduct();
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

		if(counter_1 == firstFactor.getLength() && !foundFirstFactor){foundFirstFactor = true; } 
		if(counter_2 == secondFactor.getLength() && !foundSecondFactor){foundSecondFactor = true;}

		if(foundFirstFactor && foundSecondFactor) {	return prod;} 

		return null;
	}	
	
	public Polynomial getAdditionPolynomial(){
		Polynomial result = new Polynomial(false);
		for(Selectable item : this){
			result.addMonomial(new Monomial((Monomial)item, false));
		}
		result.removeStyleName("dragdrop-handle");
		return result;
	}
}
