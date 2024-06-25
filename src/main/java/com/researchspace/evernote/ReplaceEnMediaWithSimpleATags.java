package com.researchspace.evernote;

import java.util.Map;

import org.jdom2.Element;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ReplaceEnMediaWithSimpleATags implements EnMediaReplacementPolicy {

	/**
	 * Replaces en-media XML elements with <a> elements that contain marker attribute'data-en="true"' for identification
	 * The href attribute values are absolute path of the files.
	 */
	@Override
	public void replaceEnMedia(Map<String, FileAndOriginalName> hashToFile, Element mediaElement, String binaryContentHash) {
		log.info("found file {}", hashToFile.get(binaryContentHash));
		Element replacement = new Element("a");
		replacement.setAttribute("href", hashToFile.get(binaryContentHash).getFile().getAbsolutePath());
		replacement.setAttribute("data-en","true");
		replacement.setText(hashToFile.get(binaryContentHash).getOriginalName());
		replaceWithNewElement( mediaElement, replacement);	
	}
	
	private void replaceWithNewElement( Element el, Element replacement) {
		Element parent = el.getParentElement();
		parent.getChildren().set(parent.getChildren().indexOf(el), replacement);
	}
}
