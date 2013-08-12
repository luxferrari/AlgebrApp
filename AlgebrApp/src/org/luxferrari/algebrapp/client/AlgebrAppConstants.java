package org.luxferrari.algebrapp.client;

import com.google.gwt.i18n.client.Constants;

public interface AlgebrAppConstants extends Constants{
	
	
	@DefaultStringValue("Questa operazione non è consentita!") String notAllowed();
	
	@DefaultStringValue("Clicca per applicare la proprietà commutativa")
	String tooltipCommutative();
	
	@DefaultStringValue("Clicca per selezionare questo polinomio")
	String tooltipPolynomial();
	
	@DefaultStringValue("Clicca per selezionare questo monomio")
	String tooltipMonomial();

	@DefaultStringValue("AlgebrApp 2.01 beta ")
	String AlgebrAppTitle();

	@DefaultStringValue("AlgebrApp 2.01 beta")
	String AlgebrAppHeader();

	@DefaultStringValue("Opera")
	String operateButtonText();
	
	@DefaultStringValue("Clicca per applicare un'operazione")
	String operateButtonTooltip();

	@DefaultStringValue("Rinnova")
	String refreshButtonText();

	@DefaultStringValue("Genera un'altra espressione")
	String refreshButtonTooltip();
	
	@DefaultStringValue("Clicca quando hai terminato il calcolo")
	String successButtonTooltip();
	
	@DefaultStringValue("Difficoltà")
	String sliderTitle();
	
	@DefaultStringValue("Corretto")
	String correct();
	
	@DefaultStringValue("Non corretto")
	String incorrect();	
	
	@DefaultStringValue("Il calcolo non è terminato")
	String noSuccess();
	
	@DefaultStringValue("Non puoi eseguire il calcolo, gli addendi non sono monomi simili")
	String notSimilar();
	
	@DefaultStringValue("I fattori non sono stati calcolati completamente")
	String notReduced();
	
	@DefaultStringValue("Non puoi eseguire questo calcolo, non rispetta le precedenze")
	String orderOfOperations();
	
	@DefaultStringValue("Nessun elemento selezionato")
	String noSelection();
	
	@DefaultStringValue("Calcolo da eseguire:")
	String calculateText();
	
	@DefaultStringValue("Clicca il risultato corretto")
	String clickResultText();

	
}
