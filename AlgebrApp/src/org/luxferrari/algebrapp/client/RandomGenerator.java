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
		literalsSet = this.nextInt(arrLiterals.length);
	}
	
	@Override
	public int nextInt(int n){
		if(n == 0) return 0;
		else return super.nextInt(n);
	}

	public int getLiteralsSet(){
		return literalsSet;
	}


	public Monomial[] randomMonomials(int monomialsNumber, int order, int coeffRange, int literalsNumber){

		Monomial[] m = new Monomial[monomialsNumber];

		int coefficient;

		for (int k = 0; k < monomialsNumber; k++){	

			coefficient = 0;
			String[] literals= new String[order];

			for (int j=0; j<order; j++){
				// Pick literals
				literals[j] = arrLiterals[getLiteralsSet()][this.nextInt(literalsNumber)];
			}

			while(coefficient==0){
				// generate coefficient
				coefficient = (this.nextInt(coeffRange*2+1)-coeffRange);
			}

			m[k] = new Monomial(coefficient, literals);
		}

		return m;
	}

	public Product randomProduct(int totalLength, int order, int coeffRange, int literalsNumber, float productFrequency){
		Product result = null;
		int subOrder1 = order > 1 ? 1 + this.nextInt(order - 1) : 1;
		int subLength1 = totalLength > 1 ? 1 + this.nextInt(totalLength - 1) : 1;
		int subOrder2 = order - subOrder1 > 1 ? order - subOrder1  : 1;
		int subLength2 = totalLength - subLength1 > 1 ? totalLength - subLength1 : 1;
		
		int subRange = (int) Math.ceil(Math.sqrt(coeffRange));

		Polynomial f1 = randomPolynomial(subLength1, subOrder1, subRange, literalsNumber, productFrequency);
		Polynomial f2 = randomPolynomial(subLength2, subOrder2, coeffRange - subRange, literalsNumber, productFrequency);
				
		result = new Product(f1, f2);
		return result;
	}

	public Polynomial randomPolynomial(int totalLength, int order, int coeffRange, int literalsNumber, float productFrequency){
		Polynomial result = new Polynomial();
		if (productFrequency > 1) productFrequency = 1;
		int maxProductsNumber = (int)(productFrequency * this.nextInt((int)(totalLength / 2)));
		int productsNumber = 0;
		int length = 0;

		while(length < totalLength && productsNumber < maxProductsNumber){
			int productLength = this.nextInt((int)(totalLength / maxProductsNumber));
			Product p = randomProduct(productLength, order, coeffRange, literalsNumber, productFrequency);
			length += p.getLength();
			result.insertProduct(p, 0);
			productsNumber++;			
		}
		while(length < totalLength){
			result.insertMonomial(randomMonomials(1, order, coeffRange, literalsNumber), this.nextInt(result.getWidgetCount()));
			length++;
		}

		return result;
	}
}
