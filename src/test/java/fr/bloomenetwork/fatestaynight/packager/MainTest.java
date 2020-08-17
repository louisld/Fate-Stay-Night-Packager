package fr.bloomenetwork.fatestaynight.packager;

import static org.junit.jupiter.api.Assertions.*;

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
}
