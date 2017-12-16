package edu.arizona.biosemantics.client;


import java.util.List;
import java.util.ListIterator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.HtmlEditor;

import edu.arizona.biosemantics.shared.FieldVerifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SemanticTextEditor implements EntryPoint {
	
	protected static final int MIN_HEIGHT = 710;
	protected static final int MIN_WIDTH = 560;
    HtmlEditor htmlEditor = new HtmlEditor();
    
    FramedPanel framedPanel;
    VerticalPanel verticalPanel;
    HorizontalPanel horizontalPanel;
    HorizontalLayoutContainer container;
	RichTextArea areaLeft;
	RichTextArea areaRight;
	RichTextArea.Formatter colorFormatter;

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final ConnectionServiceAsync connectionService = GWT.create(ConnectionService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		this.wordEditorInterface();
		this.initServerSide();
		
        final Button sendWordButton = new Button("Send Word");
        final Button sendSentenceButton = new Button("Send Sentence");

		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendWordButton.addStyleName("sendButton");
		sendSentenceButton.addStyleName("sendButton");


		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("sendButtonContainer").add(sendWordButton);
		RootPanel.get("sendButtonContainer").add(sendSentenceButton);

		// Focus the cursor on the name field when the app loads

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		//dialogVPanel.addStyleName("dialogVPanel");
		//dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		//dialogVPanel.add(textToServerLabel);
		//dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		//dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendWordButton.setEnabled(true);
				sendWordButton.setFocus(true);
				sendSentenceButton.setEnabled(true);
				sendSentenceButton.setFocus(true);
			}
		});
		
		// Create a handler for the sendButton and nameField
		class WordHandler implements ClickHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
	
			public void onClick(ClickEvent event) {
				sendWordToServer();
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendWordToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = htmlEditor.getTextArea().getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendWordButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				connectionService.sendWord(textToServer, new AsyncCallback<List<String>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox.setText("Remote Procedure Call - Failure");
						serverResponseLabel.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Remote Procedure Call");
						serverResponseLabel.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(false);
					}

					@Override
					public void onSuccess(List<String> synonyms) {
						
						//dialogBox.setText("Remote Procedure Call");
						ListIterator<String> itrList = null;
						itrList = synonyms.listIterator();	
						RichTextArea.Formatter formatter1 = htmlEditor.getTextArea().getFormatter();
						areaLeft.setVisible(true);
					    
						RichTextArea.Formatter formatter2 = areaLeft.getFormatter();

						areaLeft.setHTML("");
						formatter2.insertHTML("Synonyms: <br />");
						while(itrList.hasNext()) {
							formatter2.insertHTML("<br>&emsp;&emsp;--->"+"   "+itrList.next()+"<br />\n");
						}	
						dialogBox.setText(areaLeft.getText());
						dialogBox.center();
						closeButton.setFocus(false);						
					}
				});
			}

		}

		// Create a handler for the sendButton and nameField
		class SentenceHandler implements ClickHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			
			public void onClick(ClickEvent event) {
				sendSentenceToServer();
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendSentenceToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				
	            String textFromTextArea = htmlEditor.getTextArea().getText();	
			    if (!FieldVerifier.isValidName(textFromTextArea)) {
			    	    errorLabel.setText("Please enter at least four characters");
					return;
			    }
				// Then, we send the input to the server.
				sendSentenceButton.setEnabled(false);			     
				connectionService.sendSentence(textFromTextArea, new AsyncCallback<List<String>>() {
				
			  	public void onFailure(Throwable caught) {
			  		// Show the RPC error message to the user
					dialogBox.setText("Remote Procedure Call - Failure");
					serverResponseLabel.addStyleName("serverResponseLabelError");
					serverResponseLabel.setHTML(SERVER_ERROR);
					dialogBox.center();
					closeButton.setFocus(true);
				}

				@Override
				public void onSuccess(List<String> synonyms) {
					//dialogBox.setText("Remote Procedure Call");
					ListIterator<String> itrList = null;
					itrList = synonyms.listIterator();						
				     RichTextArea.Formatter formatter1 = htmlEditor.getTextArea().getFormatter();
				  
					areaLeft.setVisible(true);
					RichTextArea.Formatter formatter2 = areaLeft.getFormatter();
					areaLeft.setHTML("");
					formatter2.insertHTML("Synonyms: <br />");
					while(itrList.hasNext()) {
						formatter2.insertHTML("<br>&emsp;&emsp;--->"+"   "+itrList.next()+"<br />\n");
					}	
					dialogBox.center();
					closeButton.setFocus(true);
				  }	  
		        });
			} //sendSentenceToServer
		}

		htmlEditor.getTextArea().addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				
				colorFormatter = htmlEditor.getTextArea().getFormatter();
				colorFormatter.setForeColor("#0000ff");
				colorFormatter.setBackColor("#0ff000");
				colorFormatter.insertHTML("<p>This text contains <sup>superscript</sup> text.</p>");

				int dotIndex = htmlEditor.getTextArea().getText().lastIndexOf(".");
				int semiColonIndex = htmlEditor.getTextArea().getText().lastIndexOf(";");
				if(event.getCharCode() == '.') {
					areaLeft.setText("");
				    if(dotIndex > semiColonIndex) {
				    	    areaLeft.setText(htmlEditor.getTextArea().getText().substring(htmlEditor.getTextArea().getText().lastIndexOf(".") + 1).toString());
				    }else {
					    areaLeft.setText(htmlEditor.getTextArea().getText().substring(htmlEditor.getTextArea().getText().lastIndexOf(";") + 1).toString());
				    }			  
				}else if(event.getCharCode() == ';') {
					if(dotIndex > semiColonIndex) {   
			    	        areaLeft.setText(htmlEditor.getTextArea().getText().substring(htmlEditor.getTextArea().getText().lastIndexOf(".") + 1).toString());
				   } else {
					   areaLeft.setText(htmlEditor.getTextArea().getText().substring(htmlEditor.getTextArea().getText().lastIndexOf(";") + 1).toString());
				   }
				}
			} // onKeyPress
		}); // htmlEditor

		
		// Add a handler to send the name to the server
		WordHandler wordHandler = new WordHandler();
		sendWordButton.addClickHandler(wordHandler);
		
		SentenceHandler sentenceHandler = new SentenceHandler();
		sendSentenceButton.addClickHandler(sentenceHandler);
	}

	/**
	 * The skeleton interface for the word processor
	 */	
	public Widget wordEditorInterface() {
		  
  	    verticalPanel = new VerticalPanel();
	    verticalPanel.setWidth("100%");
	    verticalPanel.setHeight("300%");
	    
	    	areaLeft = new RichTextArea();
	    	areaRight = new RichTextArea();
	    	
		areaLeft.setVisible(false);
		areaRight.setVisible(false);    	
	    	
		htmlEditor.setAllowTextSelection(true);
	    htmlEditor.setEnableColors(true);
	     	         
	    container = new HorizontalLayoutContainer();	
	    container.add(new FieldLabel(areaLeft), new HorizontalLayoutData(350, 300, new Margins(20,-40,0,0))); 
	    container.add(new FieldLabel(htmlEditor), new HorizontalLayoutData(1, 1, new Margins(10,0,-450,0))); 
	    container.add(new FieldLabel(areaRight), new HorizontalLayoutData(450, 300, new Margins(20,0,0,-40))); 

	    verticalPanel.add(container);  
        verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);  
     
        RootPanel.get().add(verticalPanel); 
	  return verticalPanel;
	}

	/**
	 * The RPC call that calls loadMap method
	 * loadMap calls mapLabelsToExactSynonyms method
	 * 
	 * mapLabelsToExactSynonyms:
	 * Place each label(key) into hash map. 
	 * Each label has a list (value) that contains the exact synonyms
	 */	
	private void initServerSide() {
		
		connectionService.loadMap(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			public void onSuccess(String result) {
				areaLeft.setVisible(true);
				RichTextArea.Formatter formatter2 = areaLeft.getFormatter();
				areaLeft.setHTML("");
				formatter2.insertHTML(result);
			}
		});
    }
}
