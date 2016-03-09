package aliview.settings;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SettingsTest {
	
	private static Settings settings;

	@BeforeClass
	public static void initSettings() {
		settings = new Settings();
	}

	@Before
	public void beforeEachTest() {
		System.out.println("This is executed before each Test");
	}

	@After
	public void afterEachTest() {
		System.out.println("This is exceuted after each Test");
	}

	@Test
	public void testGetCustomFontSize() {
		SettingValue result = settings.getCustomFontSize();
		int size = result.getIntValue();
		assertEquals(14, size);
		
	}
	

}
