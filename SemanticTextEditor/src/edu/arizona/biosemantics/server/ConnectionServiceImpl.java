package edu.arizona.biosemantics.server;

import edu.arizona.biosemantics.client.ConnectionService;
import edu.arizona.biosemantics.shared.FieldVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ConnectionServiceImpl extends RemoteServiceServlet implements ConnectionService {
  	
	OWLAccessorImpl accessor = new OWLAccessorImpl("https://raw.githubusercontent.com/pato-ontology/pato/master/pato.owl");
	public List<String> sendWord(String token) throws IllegalArgumentException {
	    
	    if(accessor.isClassLabel(token)) {
	        return accessor.getExactSynonymsfromMap(token);    	        
	    }else {
	        List<String> list =  Arrays.asList("There is no Synonym"); 
	        return list;       
	    }	    
	}
	public boolean callMappingLabelsToExactSynonyms() throws IllegalArgumentException {
		
		if(accessor.mapLabelsToExactSynonyms()){
		    System.out.println("Loading of map is successful!");
		    return true;
		}else {
            System.out.println("Loading of map is failed!");	
            return false;
		}
	}
	
	@Override
	public List<String> sendSentence(String list) {
		ArrayList<String> tokenizedTextFromTextArea = tokenizeSentence(list);
	    for (int counter = 0; counter < tokenizedTextFromTextArea.size(); counter++) { 	
	          System.out.println(tokenizedTextFromTextArea.get(counter)); 
	    }
		return tokenizedTextFromTextArea;	
	}

	private ArrayList<String> tokenizeSentence(String sentence) {
		String token = null;
	    StringTokenizer stringTokenizer = 
	             new StringTokenizer(sentence,",  .");  
	     ArrayList<String> tokens = new ArrayList<String>();
	      
	      while (stringTokenizer.hasMoreElements()) {
	    	    token = stringTokenizer.nextElement().toString();
	        //System.out.println(token);
	        tokens.add(token);            
	      }
	    return tokens;
	 }
		
	
}
