package org.luxferrari.algebrapp.client;

import com.google.gwt.i18n.client.Messages;

public interface AlgebrAppMessages extends Messages{
	
	@DefaultMessage("<b>Hai completato il calcolo con {0} errori</b> <br/><i>Premi il tasto azzurro per continuare</i>")
	String calculationCompleted(String errorNumber);
	
	@DefaultMessage("<b>Hai completato il calcolo con 1 errore</b> <br/><i>Premi il tasto azzurro per continuare</i> ")
	String calculationCompletedWithOneError();

}
