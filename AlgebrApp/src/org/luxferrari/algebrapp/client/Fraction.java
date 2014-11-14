package org.luxferrari.algebrapp.client;

public class Fraction {

	private int numerator;
	private int denominator;
	private double value;

	public Fraction(int numerator, int denominator) {
		super();
		this.numerator = numerator;		
		this.denominator = denominator;
		this.value = ((double)numerator) / denominator;
		this.reduce();
	}

	public Fraction(int a){
		this(a,1);
	}
	
	public Fraction(Fraction f){
		this(f.getNumerator(), f.getDenominator());
	}
	
	public int getNumerator() {
		return numerator;
	}
	
	public void setNumerator(int numerator) {
		this.numerator = numerator;
	}
	
	public int getDenominator() {
		return denominator;
	}
	
	public void setDenominator(int denominator) {
		this.denominator = denominator;
	}

	public String toString(){
		if(denominator == 1) return "<span class='integer'>" + Math.abs(numerator) + "</span>";
		else return "<span class='fraction'><span class='top'>" + Math.abs(numerator)+ "</span>"
		+ "<span class='bottom'>" + Math.abs(denominator) + "</span></span>";
	}

	public void reduce(){
		int div = GCD(numerator, denominator);
		numerator /= div;
		denominator /= div;
	}

	public boolean isReduced(){
		return GCD(numerator, denominator) == 1;
	}
	
	public boolean isInteger(){
		return Math.abs(denominator) == 1;
	}
	
	public boolean equals(Fraction f){
		this.reduce();
		f.reduce();
		return this.value == f.value();
	}
	
	public boolean equals(float c){
		return this.value == c;
	}

	public Fraction add(Fraction f) {
		int num = numerator * f.getDenominator() + denominator * f.getNumerator();
		int den = denominator * f.getDenominator();
		Fraction result = new Fraction(num, den);
		result.reduce();
		return result;
	}

	public Fraction add(int a) {
		int num = numerator  + denominator * a;
		int den = denominator;
		Fraction result = new Fraction(num, den);
		result.reduce();
		return result;
	}

	public Fraction multiply(Fraction f) {
		int num = numerator *  f.getNumerator();
		int den = denominator * f.getDenominator();
		Fraction result = new Fraction(num, den);
		result.reduce();
		return result;
	}
	
	public Fraction multiply(int a) {
		int num = numerator *  a;
		int den = denominator;
		Fraction result = new Fraction(num, den);
		result.reduce();
		return result;
	}
	
	public double value(){
		return value;
	}
	
	public double absValue(){
		return Math.abs(value);
	}
	
	private int GCD(int a, int b) {
		if (b==0) return a;
		return GCD(b,a%b);
	}
	
}
