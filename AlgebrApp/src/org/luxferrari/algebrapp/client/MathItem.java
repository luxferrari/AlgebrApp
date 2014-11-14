package org.luxferrari.algebrapp.client;

public interface MathItem {	
	
	public Polynomial getParentPoly();
	
	public Product getParentProduct();
	
	public void refresh();
	
	public void destroy();
	
}
