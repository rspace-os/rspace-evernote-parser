package com.researchspace.evernote;

import static java.nio.file.Files.createTempDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class EnexParserTest {
	File basicExportFile = new File("src/test/resources/basic.enex");
	File fileWithResource = new File("src/test/resources/with-resources.enex");
	EvernoteParser parser = new EvernoteParser();

	@Test
	void parsesNotebook() throws SAXException, IOException, ParserConfigurationException {
		EnExport evernote = parser.parse(fileWithResource);
	   assertNotNull(evernote.getApplication());
	   Map<String, FileAndOriginalName> digestToName = evernote.writeResourcesToFile(
				createTempDirectory("en").toFile());
	   assertEquals(4, digestToName.keySet().size());
	}

	@Test
	void parsesBasicNote() throws IOException, ParserConfigurationException, SAXException {
		EnExport evernote = parser.parse(basicExportFile);
		assertNotNull(evernote);
		Note note = evernote.getNotes().get(0);
		assertEquals("Test Note for Export", note.getTitle());
		var digestToName = evernote.writeResourcesToFile(
				createTempDirectory("en").toFile());
		LinkUpdater updater = new LinkUpdater(new ReplaceEnMediaWithSimpleATags());
		String updatedContentHTML = updater.update(digestToName, note.getContent());
		assertTrue(updatedContentHTML.length() > 100);
	}

	@Test
	void parsesNoteWithResourcesAndReplacesLinks() throws SAXException, IOException, ParserConfigurationException {
		EnExport evernote = parser.parse(fileWithResource);
		assertNotNull(evernote);
		assertEquals("6.x", evernote.getVersion());
		assertEquals(5, evernote.getNotes().size());

		assertEquals("Harry Potter", evernote.getNotes().get(0).getNodeAttributes().getAuthor());
		assertEquals(1, evernote.getNotes().get(0).getResources().size());
		var digestToName = evernote.writeResourcesToFile(
				createTempDirectory("en").toFile());
		Note noteWithAttachments = evernote.getNotes().get(0);

		String content = noteWithAttachments.getContent();
		final int initialALinkCount = Jsoup.parse(content).getElementsByTag("a").size();
		assertEquals(1, Jsoup.parse(content).getElementsByTag("en-media").size() );
		
		LinkUpdater updater = new LinkUpdater( new ReplaceEnMediaWithSimpleATags());
		String updatedContentHTML = updater.update(digestToName, content);
		Document jsoup = Jsoup.parse(updatedContentHTML);
		assertEquals(initialALinkCount + 1, jsoup.getElementsByTag("a").size());
		assertEquals(0, jsoup.getElementsByTag("en-media").size() );
		
		assertTrue(jsoup.getElementsByTag("a").stream().filter(el->el.hasAttr("data-en"))
				.allMatch(el->new File(el.attr("href")).exists()));
	}
}
