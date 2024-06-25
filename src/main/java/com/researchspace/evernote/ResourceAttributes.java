package com.researchspace.evernote;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceAttributes {
	
	@XmlElement(name="subject-date")
	private String subjectDate;
	@XmlElement
	private double latitude;
	
	@XmlElement
	private double longitude;
	
	@XmlElement
	private double altitude;
	
	@XmlElement
	private String author;
	
	@XmlElement
	private String source;
	
	@XmlElement(name="source-url")
	private String sourceUrl;
	
	@XmlElement(name="source-application")
	private String sourceApplication;
	
	@XmlElement(name="reminder-order")
	private int reminderOrder;
	
	@XmlElement(name="reminder-time")
	private String reminderTime;
	
	@XmlElement(name="reminder-done-time")
	private String reminderDoneTime;
	
	@XmlElement(name="place-name")
	private String placeName;
	
	
	@XmlElement(name="content-class")
	private String contentClass;
	
	@XmlElement(name="application-data")
	private List<String> applicationData;
	
	@XmlElement(name="camera-make")
	private String cameraMake;
	
	
	@XmlElement(name="camera-model")
	private String cameraModel;
	
	@XmlElement(name="reco-type")
	private String recoType;
	
	@XmlElement(name="file-name")
	private String fileName;
	
	@XmlElement
	private Boolean attachment;
	
	@XmlElement
	private String timestamp;


}
