package org.luxferrari.algebrapp.client;

import java.util.Random;

import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;

public class RandomGenerator extends Random{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3697448623555646791L;
	int literalsSet;

	public RandomGenerator() {
		super();
		literalsSet = 1 + this.nextInt(arrLiterals.length - 1);
	}

	@Override
	public int nextInt(int n){
		if(n == 0) return 0;
		else return super.nextInt(n);
	}

	public int getLiteralsSet(){
		if(NO_LETTERS) return 0;
		else return literalsSet;
	}


	public Monomial[] randomMonomials(int monomialsNumber, int order, int coeffRange, int literalsNumber, boolean forProduct){

		Monomial[] m = new Monomial[monomialsNumber];

		for (int k = 0; k < monomialsNumber; k++){	
			String[] literals= new String[order];
			for (int j = 0; j < order; j++){
				// Pick literals
				literals[j] = arrLiterals[getLiteralsSet()][this.nextInt(literalsNumber)];
			}
			m[k] = new Monomial(randomFraction(coeffRange, forProduct), literals, true, SIMPLIFIED);
		}
		return m;
	}

	public void primeFactorsChoices(){	//TODO Enthält zauberZahlen		
		int chosenFactorsNumber = 3 + this.nextInt(3);
		chosenFactors = new int[chosenFactorsNumber];
		chosenFactors[0] = 1;
		chosenFactors[1] = this.nextInt(2) == 0 ? 2 : 3;
		for(int k = 2; k < chosenFactorsNumber; k++){
			chosenFactors[k] = primesArray[this.nextInt(primesArray.length)];
		}
		primeFactorsNumber = 2 + this.nextInt(3);
	}


	public Fraction randomFraction(int coeffRange, boolean forProduct){	//TODO Enthält zauberZahlen		
		int num = 0;
		int den = 1;
		int level = 1;
		double treshold = 1;

		if(levelSlider.getValue() != null) level = levelSlider.getValue();
		if(level < 3) treshold = (double)(level + 1) / 3;
		else treshold = (double)(level - 2) / 8;


		while(num == 0){
			num = (this.nextInt(coeffRange * 2 + 1) - coeffRange);
		}
		if(!Z_ONLY){
			do{
				den = randomDenominator(forProduct);
			}
			while (den > treshold * 125 );
		}
		return new Fraction(num, den);
	}

	public int randomDenominator(boolean forProduct){	
		int den = 1;
		if(forProduct) {
			primeFactorsNumber = primeFactorsNumber > 1 ? primeFactorsNumber - 1 : primeFactorsNumber;
		}
		for (int j = 0; j < primeFactorsNumber; j++){
			int f = chosenFactors[this.nextInt(chosenFactors.length)]; 
			den *= f;
		}
		return den;
	}

	public Product randomProduct(int totalLength, int order, int coeffRange, int literalsNumber, double productFrequency, double subPolyFrequency){
		Product result = null;
		int subOrder1 = order > 1 ? 1 + this.nextInt(order - 1) : 1;
		int subLength1 = totalLength > 1 ? 1 + this.nextInt(totalLength - 1) : 1;
		int subOrder2 = order - subOrder1 > 1 ? order - subOrder1  : 1;
		int subLength2 = totalLength - subLength1 > 1 ? totalLength - subLength1 : 1;

		int subRange = (int) Math.ceil(Math.sqrt(coeffRange));

		Polynomial f1 = randomPolynomial(new double[]{subLength1, subOrder1, subRange, literalsNumber, productFrequency, subPolyFrequency, 1});
		Polynomial f2 = randomPolynomial(new double[]{subLength2, subOrder2, coeffRange - subRange, literalsNumber, productFrequency, subPolyFrequency, 1});

		if(f2.getWidgetCount() == 1 && f2.getFirstMonomial() != null && !f2.getFirstMonomial().isSimplified()){			
			Monomial m = f2.getFirstMonomial();	
			m.hasPlus(true);
		}
		result = new Product(f1, f2);
		return result;
	}

	public Polynomial randomPolynomial(double[] array){

		int totalLength = (int)array[0]; 
		int order = (int)array[1];
		int coeffRange = (int)array[2];
		int literalsNumber = (int)array[3];
		double productFrequency = (double)array[4]; 
		double subPolyFrequency = (double)array[5]; 
		boolean forProduct = array[6]==0?false:true;

		Polynomial result = new Polynomial();
		if (productFrequency > 1) productFrequency = 1;
		int maxProductsNumber = totalLength > 1 ? (int)(productFrequency * (this.nextInt((int)(totalLength / 2 )))) : 0;
		int maxSubPolyNumber = totalLength > 1 ? (int)(subPolyFrequency * (this.nextInt((int)(totalLength / 2)))) : 0;
		int productsNumber = 0;
		int subPolyNumber = 0;
		int length = 0;

		while(totalLength - length > 2 && subPolyNumber < maxSubPolyNumber){
			int subPolyLength = this.nextInt((int)(totalLength - length));
			if(subPolyLength > 0){

				boolean continuousFraction = !Z_ONLY && this.nextInt(2) == 1 ? true : false;				
				boolean hasPlus = rndGenerator.nextInt(2) == 0 ? true : false;
				int denominator = 1;

				if (continuousFraction) {
					Z_ONLY = true;
					do{
						denominator = randomDenominator(forProduct);
					}
					while (denominator > 50 && denominator < 2);
				}

				Polynomial p = randomPolynomial(new double[]{subPolyLength, order, coeffRange, literalsNumber, productFrequency, subPolyFrequency, forProduct?1:0});
				length += p.getTotalLength();
				result.insertItem(p, rndGenerator.nextInt(result.getWidgetCount()), hasPlus, continuousFraction, denominator);

				if (continuousFraction) Z_ONLY = false;

			}			

			subPolyNumber++;	
		}
		while(totalLength - length > 1 && productsNumber < maxProductsNumber){
			int productLength = this.nextInt((int)(totalLength - length));
			Product p = randomProduct(productLength, order, coeffRange, literalsNumber, productFrequency, subPolyFrequency);
			length += p.getTotalLength();
			result.insertProduct(p, rndGenerator.nextInt(result.getWidgetCount()));
			productsNumber++;			
		}
		while  (length < totalLength){
			result.insertItem(randomMonomials(1, order, coeffRange, literalsNumber, forProduct), this.nextInt(result.getWidgetCount()));
			length++;
		}	

		// Consenti solo segno + (cioè non visibile) al primo membro di un polinomio in notazione non semplificata
		if(!SIMPLIFIED) {
			if(result.getWidget(0) instanceof Monomial){		
				Monomial first =  (Monomial)result.getWidget(0);
				if(!first.hasPlus()) first.hasPlus(true);
			}
			if(result.getWidget(0) instanceof SubPolynomial){
				SubPolynomial first = (SubPolynomial)result.getWidget(0);
				first.hasPlus(true);
			}
		}
		return result;
	}	
}
