package com.researchspace.evernote;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Note {
	@XmlElement
	private String title;
	
	@XmlElement
	private String content;
	
	@XmlElement
	private String created;
	
	@XmlElement
	private String updated;
	
	@XmlElement(name="tag")
	private List<String>tags  = new ArrayList<>();
	
	@XmlElement(name="note-attributes")
	private NodeAttributes nodeAttributes;
	
	@XmlElement(name="resource")
	private List<Resource>resources = new ArrayList<>();
	
	

}
