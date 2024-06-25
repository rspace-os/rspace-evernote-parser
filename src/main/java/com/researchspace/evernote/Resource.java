package com.researchspace.evernote;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Resource {
	@XmlElement
	private String data;
	
	@XmlTransient
	public byte [] getDataAsBytes () {
		if(!StringUtils.isBlank(data)) {
			return Base64.decodeBase64(data);
		} else {
			return new byte [0];
		}
		
	}
	
	@XmlElement
	private String mime;
	@XmlElement
	private int width;
	@XmlElement
	private int height;
	@XmlElement
	private int duration;
	@XmlElement
	private String recognition;
	@XmlElement(name="resource-attributes")
	private ResourceAttributes resourceAttributes_nullable;
	
	/*
	 * Package scoped to avoid usage by client code.
	 */
	ResourceAttributes getResourceAttributes_nullable() {
		return resourceAttributes_nullable;
	}
	/**
	 * This XML element is optional
   */
	public Optional<ResourceAttributes> getResourceAttributes() {
		return Optional.ofNullable(resourceAttributes_nullable);
	}
	
	@XmlElement(name="alternate-data")
	private String alternateData;
			  

}
