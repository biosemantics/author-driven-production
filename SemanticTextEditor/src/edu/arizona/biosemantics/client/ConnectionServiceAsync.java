package edu.arizona.biosemantics.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ConnectionServiceAsync {

	void sendSentence(String sentence, AsyncCallback<List<String>> callback) throws IllegalArgumentException;;
	void sendWord(String token, AsyncCallback<List<String>> callback) throws IllegalArgumentException;;
	void loadMap(AsyncCallback<String> callback);;
}
