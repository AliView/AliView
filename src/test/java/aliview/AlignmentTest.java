package aliview;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import aliview.alignment.Alignment;
import aliview.gui.AliViewJMenuBarFactory;

public class AlignmentTest {

	private static final Logger logger = Logger.getLogger(AlignmentTest.class);
	private static Alignment alignment;

	@BeforeClass
	public static void initAlignment() {
		Logger.getRootLogger().setLevel(Level.DEBUG);
		URL fileUrl = new AlignmentTest().getClass().getResource("/woodsia_chloropl_excl_hybrid.fasta");
		File fastaNucleotideFile = new File(fileUrl.getFile());
		AliView.openAlignmentFile(fastaNucleotideFile);
		AliViewWindow aliWin = AliView.getActiveWindow();
		alignment = aliWin.getAlignment();
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
	public void testCountStopCodons() {
		int result = alignment.countStopCodons();
		assertEquals("This alignment should contain 485 stop codons", 485, result);
	}
	
	@Test
	public void testFindAndSelectDuplicates() {
		ArrayList<String> result = alignment.findDuplicateNames();
		assertEquals(0, result.size());
	}
	

}
