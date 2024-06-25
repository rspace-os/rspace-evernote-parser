package com.researchspace.evernote;

import java.io.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.io.FileUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EvernoteParser {
	
	/**
	 * 
	 * @param enexFile an Evernote 3 enex file
	 * @return parsed file or null if could not be parsed
   */
	public EnExport parse(File enexFile) throws ParserConfigurationException, SAXException, IOException {
		
		try {
			JAXBContext jc = JAXBContext.newInstance(EnExport.class);
	        SAXParserFactory spf = SAXParserFactory.newInstance();
			// from https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html
			spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			spf.setFeature("http://xml.org/sax/features/validation", false);
	      
	        XMLReader xmlReader = spf.newSAXParser().getXMLReader();

			String input = FileUtils.readFileToString(enexFile, "UTF-8");
			// the Cdata must be on the same line else is ignored.
			input = input.replaceAll("\\n", "");
			input = input.replaceAll("\\r", "");
	        InputSource inputSource = new InputSource(new StringReader(input));
	        SAXSource source = new SAXSource(xmlReader, inputSource);
			Unmarshaller unmarshaller = jc.createUnmarshaller();

      return (EnExport) unmarshaller.unmarshal(source);
		} catch (JAXBException e) {
			log.error("Could not parse file into Evernote objects {} : {} - linked exception = {} ", 
					enexFile.getAbsolutePath(), e.getMessage(),e.getLinkedException()!= null?
							e.getLinkedException().getMessage():"");
			return null;
		}
		
	}

}
