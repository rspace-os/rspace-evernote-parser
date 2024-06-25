package com.researchspace.evernote;

import java.util.Map;

import org.jdom2.Element;
/**
 * Strategy for how to handle replacement of en-media links with HTML.
 *
 */
public interface EnMediaReplacementPolicy {
	 
	/**
	  * Update live Element tree with replacement for Enmedia element with HTML
	  * @param hashToFile Map of mediaHash to extracted filename
	  * @param mediaElement An en-media element
	  * @param binaryContentHash Hash of current en-media element
	  */
	 void replaceEnMedia(Map<String, FileAndOriginalName> hashToFile, Element mediaElement, final String binaryContentHash);
	 
	 /**
	  * No-op implementation leaves replacement policy unchanged
	  */
	 EnMediaReplacementPolicy NULL_POLICY = (a,b,c)->{};

}
