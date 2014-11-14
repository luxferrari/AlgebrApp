package org.luxferrari.algebrapp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AlgebrAppServletAsync {

	void sendMail(String text, AsyncCallback<Void> callback);

}
