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

	@DefaultStringValue("AlgebrApp v2 alpha")
	String AlgebrAppTitle();

	@DefaultStringValue("AlgebrApp v2 alpha")
	String AlgebrAppHeader();

	@DefaultStringValue("Opera")
	String operateButtonText();
	
	@DefaultStringValue("Clicca per applicare un'operazione")
	String operateButtonTooltip();

	@DefaultStringValue("Rinnova")
	String refreshButtonText();

	@DefaultStringValue("Genera un'altra espressione")
	String refreshButtonTooltip();
}
