package com.researchspace.evernote;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import lombok.Data;
/**
 * Top-level element of Evernote Enex data structure
 *
 */
@XmlRootElement(name="en-export")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class EnExport {
	
	@XmlAttribute(name="export-date")
	private String exportDate;
	
	@XmlAttribute(name="version")
	private String version;
	
	@XmlAttribute(name="application")
	private String application;
	
	@XmlElement(name="note")
	private List<Note>  notes;
	
	/**
	 * Extracts Base64 data from .enex file and writes to  a temp file for subsequent conversion to pure HTML
	 * <br/> The file name is generated from, in order:
	 * <ol>
	 * <li> The 'filename' resource attribute if it exists 
	 * <li> Sanitised 'sourceUrl' resource attribute if it exists 
	 * <li> If no resource attributes,  will generate a random filename with suffix generated from 'mime' subtype
	 * </ol>
	 * @return A Map where keys are MD5 checksums of the data, and values are mappings of original name to
	 *   File object.
   */
	public Map<String, FileAndOriginalName> writeResourcesToFile(File folder) throws IOException {
		Map<String, FileAndOriginalName> digestToName = new HashMap<>();
		for (Note note : getNotes()) {
			for (Resource resource : note.getResources()) {
				byte[] data = resource.getDataAsBytes();
				String md5 = DigestUtils.md5Hex(data);
				
				Optional<ResourceAttributes> optAttr = resource.getResourceAttributes();
				String originalName = optAttr.map(ResourceAttributes::getFileName).orElse("");
			 
				
				if(StringUtils.isEmpty(originalName)) {
					String fileType = guessTypeFromMimeType(resource.getMime());
					originalName = optAttr.map(ra->convertUrlToFileName(ra.getSourceUrl()))
							.orElse(randomFileName(fileType));
				}
				
				File target = new File(folder, originalName);
				FileUtils.writeByteArrayToFile(target,
						resource.getDataAsBytes());
				digestToName.put(md5, new FileAndOriginalName(originalName, target));
			}
		}
		return digestToName;
	}		

	private String guessTypeFromMimeType(String mime) {
		try {
			MimeType mt = new MimeType(mime);
			// remove any non-alpha chars
			return mt.getSubType().replaceAll("[^a-zA-Z0-9_\\-]", "");
		} catch (MimeTypeParseException e) {
			return "dat"; // catch all
		}
	}

	/*
	 * Tries to generate file name from path element of the URL
	 * If fails, sanitises the input string
	 * If input string is empty, returns generated name
	 * Should never return null or empty
	 */
	String convertUrlToFileName(String urlStr) {
		if (StringUtils.isEmpty(urlStr)) {
			// should be a unique name as files are put in a single temp-folder
			return randomFileName();
		}
		try {
			URL url = new URL(urlStr);
			String path = url.getPath();
			return sanitise(path);
		} catch (MalformedURLException e) {
			return sanitise(urlStr);	
		}
		
	}

	private String randomFileName(String suffix) {
		return "Unknown-filename" + RandomStringUtils.randomAlphanumeric(6) + "." + suffix;
	}
	
	private String randomFileName() {
		return randomFileName("dat");
	}

	private String sanitise(String urlStr) {
		//ensure name doesn't start with '-'
		return urlStr.replaceAll("[^A-Za-z0-9_\\.\\-]", "-").replaceAll("^\\-", "");
	}
	

}
