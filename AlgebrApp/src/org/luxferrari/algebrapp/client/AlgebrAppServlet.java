package org.luxferrari.algebrapp.client;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service")
public interface AlgebrAppServlet extends RemoteService {

  void sendMail(String text) throws Exception;
  
}