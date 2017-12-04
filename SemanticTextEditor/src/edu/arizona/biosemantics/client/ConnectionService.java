package edu.arizona.biosemantics.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;



/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("connect")
public interface ConnectionService extends RemoteService {
	List<String> sendSentence(String sentence);
	List<String> sendWord(String token);
          String loadMap(String load);
}
