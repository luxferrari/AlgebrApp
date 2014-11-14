package org.luxferrari.algebrapp.client;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface AlgebrAppConstants extends Constants{
	
	
	@DefaultStringValue("Questa operazione non è consentita!") 
	String notAllowed();
	
	@DefaultStringValue("Clicca per applicare la proprietà commutativa")
	String tooltipCommutative();
	
	@DefaultStringValue("Clicca per selezionare questo polinomio")
	String tooltipPolynomial();
	
	@DefaultStringValue("Clicca per selezionare questo monomio")
	String tooltipMonomial();

	@DefaultStringValue("AlgebrApp 2.00")
	String AlgebrAppTitle();

	@DefaultStringValue("AlgebrApp 2.00")
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

	@DefaultStringValue("Clicca per visualizzare le statistiche della sessione")
	String statisticsButtonTooltip();		
	
	@DefaultStringValue("Scegli il livello di difficoltà")
	String sliderTitle();
	
	@DefaultStringValue("Corretto.")
	String correct();
	
	@DefaultStringValue("Non corretto.")
	String incorrect();	
	
	@DefaultStringValue("Il calcolo non è terminato")
	String noSuccess();
	
	@DefaultStringValue("Non puoi eseguire il calcolo, gli addendi non sono monomi simili.")
	String notSimilar();
	
	@DefaultStringValue("Gli elementi non sono stati calcolati completamente.")
	String notReduced();
	
	@DefaultStringValue("È richiesto che gli addendi siano consecutivi.")
	String notNeighbours();
	
	@DefaultStringValue("Non puoi farlo, non rispetta le precedenze delle operazioni.")
	String orderOfOperations();
	
	@DefaultStringValue("Nessun elemento selezionato.")
	String noSelection();
	
	@DefaultStringValue("Non puoi eseguire nessuna operazione")
	String incorrectSelection();
	
	@DefaultStringValue("Calcolo da eseguire:")
	String calculateText();
	
	@DefaultStringValue("Clicca il risultato corretto")
	String clickResultText();

	@DefaultStringValue("Usa coefficienti in Z")
	String toggleZTooltip();
	
	@DefaultStringValue("Usa coefficienti in Q")
	String toggleQTooltip();

	@DefaultStringValue("Impostazioni")
	String menuTitle();

	@DefaultStringValue("Scegli i numeri da usare come coefficienti.")
	String textZOnly();

	@DefaultStringValue("Apri la guida")
	String helpButtonTooltip();

	@DefaultStringValue("Come addizionare")
	String headerPageAdd();

	@DefaultStringValue("Come moltiplicare")
	String headerPageMultiply();

	@DefaultStringValue("Come sciogliere parentesi")
	String headerPageSubPoly();

	@DefaultStringValue("Come terminare il calcolo")
	String headerPageEnd();

	@DefaultStringValue("È possibile sommare solamente <b>monomi simili</b>, cioè monomi che hanno la stessa parte letterale. <br/>&nbsp;<br/> È sufficiente sommare tra loro i <b>coefficienti</b> dei monomi, mentre la parte letterale rimane la stessa: si tratta di un'applicazione della <i>proprietà distributiva</i>.<br/>"
			+ "<br/>&nbsp;<br/><i>Esempio:</i>&nbsp;&nbsp; <span class=\"math\" style=\"font-size: 20px; \" >&#150;3xy +5xy = (&#150;3 +5)&middot;xy = +2xy</span><br/>"
			+ "<br/>&nbsp;<br/><i>Fare clic sui monomi scelti per selezionarli, fare poi clic sul tasto \"Calcola\"</i>.")
	String helpPageAddText();

	@DefaultStringValue("Per eseguire una moltiplicazione di due monomi occorre moltiplicare tra loro i <b>coefficienti</b> e la <b>parte letterale</b>.<br/>&nbsp;<br/>"
			+ "Nel caso del prodotto di un monomio con un polinomio, si moltiplica il monomio con <b>ogni membro del polinomio</b> - cioè si applica la <i>proprietà distributiva</i>. Per selezionare il polinomio fare clic su una delle parentesi.<br/>"
			+ "<br/>&nbsp;<br/><i>Esempio 1:</i>&nbsp;&nbsp; <span class=\"math\" style=\"font-size: 20px; \" >3x&middot;(&#150;5xy) = 3&middot;(&#150;5)&middot;x&middot;xy = &#150;15x<sup>2</sup>y</span><br/>"
			+ "<br/>&nbsp;<br/><i>Esempio 2:</i>&nbsp;&nbsp; <span class=\"math\" style=\"font-size: 20px; \" >5x&middot;(3y + x) = 5x&middot;3y + 5x&middot;x = 15xy + 5x<sup>2</sup></span><br/>"
			+ "<br/>&nbsp;<br/><i>Fare clic sui monomi scelti per selezionarli, fare poi clic sul tasto \"Calcola\"</i>.")
	String helpPageMultiplyText();

	@DefaultStringValue("Se di fronte alla parentesi c'è un'<b>addizione</b>, tutti i monomi al suo interno vengono addizionati e pertanto rimangono gli stessi. <br/>&nbsp;<br/>Se di fronte alla parentesi c'è una <b>sottrazione</b>, tutti i monomi al suo interno vengono sottratti e pertanto diventano i loro opposti.<br/>"
			+ "<br/>&nbsp;<br/><i>Esempio 1:</i>&nbsp;&nbsp; <span class=\"math\" style=\"font-size: 20px; \" >+ (2 &#150;3x) = +2 &#150;3x </span><br/>"
			+ "<br/>&nbsp;<br/><i>Esempio 2</i>:&nbsp;&nbsp; <span class=\"math\" style=\"font-size: 20px; \" >&#150; (3x &#150;5) = &#150;3x + 5</span><br/>"
			+ "<br/>&nbsp;<br/><i>Per selezionare l'intero polinomio tra parentesi fare clic sul simbolo di operazione che lo precede.</i>")
	String helpPageSubPolyText();

	@DefaultStringValue("Quando si ritiene di aver effettuato ogni calcolo possibile, fare clic sul <b>tasto verde</b>.")
	String helpPageEndText();

	@DefaultStringValue("Genera espressioni numeriche in notazione semplificata")
	String textSimplified();

	@DefaultStringValue("Usa notazione semplificata")
	String simplifiedTooltip();

	@DefaultStringValue("S&igrave;")
	String yes();

	@DefaultStringValue("No")
	String no();
	
	@DefaultStringValue("Calcoli effettuati")
	String statisticsTitle();

	@DefaultStringValue("È ora possibile copiare il testo cliccando col tasto destro e scegliendo 'copia'.")
	String pastableText();

	@DefaultStringValue("Punteggio: ")
	String score();

	@DefaultStringValue("Invia")
	String sendButtonText();
	
	@DefaultStringValue("Annulla")
	String cancelButtonText();
	
	@DefaultStringValue("Invia il messaggio")
	String sendButtonTooltip();
	
	@DefaultStringValue("Chiudi questa finestra senza inviare")
	String cancelButtonTooltip();
	
	@DefaultStringValue("Indirizzo e-mail (opzionale)")
	String emailAddressLabel();
	
	@DefaultStringValue("Messaggio:")
	String messageLabel();

	@DefaultStringValue("Inserire un email valido, oppure nessuno")
	String invalidEmail();
	
	@DefaultStringValue("Inserire un messaggio")
	String invalidMessage();

	@DefaultStringValue("Il messaggio non è stato inviato, si è verificato un problema")
	String sendFailed();

	@DefaultStringValue("Il messaggio è stato inviato")
	String sendSuccess();

	@DefaultStringValue("Invio messaggio in corso...")
	String pleaseWait();
	
}
