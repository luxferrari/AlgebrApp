package org.luxferrari.algebrapp.client;

import com.google.gwt.i18n.client.Messages;

public interface AlgebrAppMessages extends Messages{
	
	@DefaultMessage("Hai completato il calcolo con {0} errori.")
	String calculationCompleted(String errorNumber);
	
	@DefaultMessage("Hai completato il calcolo con 1 errore.")
	String calculationCompletedWithOneError();

}
