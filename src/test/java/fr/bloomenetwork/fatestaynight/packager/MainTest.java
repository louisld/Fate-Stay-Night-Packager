package fr.bloomenetwork.fatestaynight.packager;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainTest {

	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	public void testNumberToJapaneseString() {
		assertEquals("二十一", Utils.numberToJapaneseString(21));
	}
	
	@Test
	public void downloadPdf() {
		try {
			GoogleAPI googleAPI = new GoogleAPI();
			String fileId = "1iVJ-8kncVh5Wk7YSFNqye2hvF9r_t6dY7kGpTs0WANo";
			googleAPI.downloadDocx(fileId, "test.docx");
		} catch (Exception e1) {
			System.out.println(e1.toString());
		}
		fail("ALED");
	}
}
