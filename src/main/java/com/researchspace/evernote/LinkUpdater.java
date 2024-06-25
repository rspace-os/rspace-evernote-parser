package com.researchspace.evernote;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LinkUpdater {
	static class XMLErrorHandler implements ErrorHandler {

		@Override
		public void warning(SAXParseException exception) {
		}

		@Override
		public void error(SAXParseException exception) {
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {

			if (exception.toString().matches(".+The entity \"\\S+\" was referenced.+")) {
				log.warn("Ignoring HTML entity validation error:  {}", exception.toString());
			} else {
				throw exception;
			}
		}

	}



	private static final String DEFAULT_FONT_SIZE = "font-size: 12pt;line-height: 12pt;";
	private static final String ENML_DOCTYPE = "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">";

	private EnMediaReplacementPolicy policy;
	
	public LinkUpdater(EnMediaReplacementPolicy policy) {
		this.policy = policy;
	}

	/**
	 * Map of md5 checksums to filename extracted from base64 encoded attachments
	 * @return modified ENML string or empty string if could not be modified
	 */
	public String update (Map<String, FileAndOriginalName> hashToName,  String enml ){
		if(StringUtils.isBlank(enml)) {
			return enml;
		}
		// the SAXBuilder is the easiest way to create the JDOM2 objects.
        SAXBuilder jdomBuilder = new SAXBuilder();
       
        jdomBuilder.setDTDHandler(null);
        // this is to ignore HTML entities e.g. &nbsp; in content, that jdom will try to validate as XML  entities.
      
        jdomBuilder.setErrorHandler(new XMLErrorHandler());
        jdomBuilder.setFeature(
        		  "http://apache.org/xml/features/continue-after-fatal-error", true);
        // removing the dtd speeds up processing, it is awfully slow (10s +) otherwise
		enml = enml.replace(ENML_DOCTYPE, "");//.replaceAll("&nbsp;", "&#160;");
		// remove this which causes some processors to choke
		enml = enml.replaceAll("<\\?xml[^>]+>", "");
        // jdomDocument is the JDOM2 Object
        Document jdomDocument;
		try {
			jdomDocument = jdomBuilder.build(new StringReader(enml));
			
	        replaceEnMedia(hashToName, jdomDocument);
	        replaceEnToDo(jdomDocument);
	       
	        jdomDocument.getRootElement().setName("div");
	        jdomDocument.getRootElement().setAttribute("style", DEFAULT_FONT_SIZE);
	        XMLOutputter out = new XMLOutputter();
	        out.setFormat(Format.getPrettyFormat());
	        return  out.outputString(jdomDocument);
		} catch (JDOMException | IOException e) {
			log.warn("Problem building doc: {}", e.getMessage());
			return "";
		}	
	}

	private void replaceEnMedia(Map<String, FileAndOriginalName> hashToName, Document jdomDocument) {
		ElementFilter f = new ElementFilter("en-media");
		IteratorIterable<Element> it =  jdomDocument.getDescendants(f);
		while(it.hasNext()) {
			Element el = it.next();
			String hash = el.getAttributeValue("hash");
			
			if(hashToName.containsKey(hash)) {
				policy.replaceEnMedia(hashToName, el, hash);
			}
		}
	}

	
	
	private void replaceEnToDo( Document jdomDocument) {
		ElementFilter f = new ElementFilter("en-todo");
		IteratorIterable<Element> it =  jdomDocument.getDescendants(f);
		// can't be updated in place
		List<Element> todos = new ArrayList<>();
		while(it.hasNext()) {
			Element el = it.next();
			todos.add(el);		
		}
		for (Element el: todos) {
			el.getParentElement().setText((el.getParentElement().getText() + " - TODO "));
			el.setName("div");
			el.removeAttribute("checked");
		}
		// for e
	}

}
