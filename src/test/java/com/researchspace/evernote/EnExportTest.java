package com.researchspace.evernote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnExportTest {

	private static final String DISALLOWED_FILE_CHARS = "[^A-Za-z0-9_\\-\\.]";

	@Test
	@DisplayName("Empty url handled gracefully")
	void emptyUrlGeneratesNonDuplicateFilename() {
		EnExport export = new EnExport();
		String generated1 = export.convertUrlToFileName("");
		String generated2 = export.convertUrlToFileName("");
		assertTrue(generated1.endsWith(".dat"));
		assertTrue(generated2.endsWith(".dat"));
    assertNotEquals(generated2, generated1);
	}
	
	@Test
	@DisplayName("URLs converted to safe filenames")
	void urlsConvertedToSafeFileNames() {
		EnExport export = new EnExport();
		// this is converted to URL
		String fileName = export.convertUrlToFileName("https://some-path/path/to/my_file.png?search=abc");
		
		assertTrue(fileName.endsWith("my_file.png"));
		assertFalse(fileName.matches(DISALLOWED_FILE_CHARS));
		assertEquals("path-to-my_file.png", fileName);
		
		// this is not a valid URL
		String fileName2 = export.convertUrlToFileName("?????some file.png");
		assertTrue(fileName2.endsWith("-file.png"));
		assertFalse(fileName2.matches(DISALLOWED_FILE_CHARS));
	}

}
