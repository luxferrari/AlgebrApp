package org.luxferrari.algebrapp.client;

public interface Selectable {
	/** 
	 * Seleziona e inserisce il widget nella lista di selezione	(SelectionList)
	 */
	public void select();
	/**
	 * Deseleziona e rimuove il widget dalla lista di selezione (SelectionList)
	 */
	public void deselect();	
	
	/** Aggiorna il numero del widget, che rappresenta la posizione del widget nella lista di selezione. Esso appare sotto il widget grazie a un astutissima tecnica di tag HTML e regole CSS. Almeno finché non verranno deprecati :-p
	 * @param index Il numero del widget.
	 */
	public void refreshIndex(String index);
	
	public boolean isSelected();
}
