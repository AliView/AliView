package aliview.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import aliview.sequences.Sequence;

/*
 * Tests for the phylip format detection and import. These are pure importer
 * tests - no AliViewWindow is opened, so they run headless.
 */
public class PhylipImporterTest {

	private File testFile(String name) {
		URL fileUrl = getClass().getResource("/" + name);
		assertTrue("missing test resource: " + name, fileUrl != null);
		return new File(fileUrl.getFile());
	}

	/*
	 * Regression test for issue #159: a relaxed phylip file whose names are longer
	 * than 10 characters was detected as strict short-name phylip, because column 11
	 * holds a non-whitespace character (it is still inside the name). Reading it as
	 * strict truncated every name to 10 characters, producing duplicate names.
	 */
	@Test
	public void relaxedLongNamesAreNotDetectedAsStrictShortName() {
		PhylipImporter.PhylipHint hint =
				PhylipImporter.getPhylipHint(testFile("phylip_relaxed_longname_sequential.phy"));
		assertFalse("long relaxed names must not be detected as strict short name",
				hint.isStrictShortName);
	}

	@Test
	public void relaxedLongNamesAreImportedInFull() throws Exception {
		File file = testFile("phylip_relaxed_longname_sequential.phy");
		List<Sequence> sequences = new PhylipImporter(new FileReader(file),
				FileFormat.PHYLIP_RELAXED_PADDED_AKA_LONG_NAME_SEQUENTIAL).importSequences();

		assertEquals(3, sequences.size());
		assertEquals("Achelura_yunnanensis_GCA041274885", sequences.get(0).getName());
		assertEquals("Achelura_bifasciata_GCA041274886", sequences.get(1).getName());
		assertEquals("Zygaena_filipendulae_GCA041274887", sequences.get(2).getName());
		for(Sequence sequence : sequences){
			assertEquals(32, sequence.getLength());
		}
	}

	/*
	 * The strict short-name interleaved file that the detection was added for must
	 * keep being detected as strict, so the fix above does not regress it.
	 */
	@Test
	public void strictShortNameInterleavedIsStillDetected() {
		PhylipImporter.PhylipHint hint =
				PhylipImporter.getPhylipHint(testFile("phylip_strict_shortname_interleaved.phy"));
		assertTrue("strict short name file must be detected as strict", hint.isStrictShortName);
		assertTrue("strict short name file must be detected as interleaved", hint.isInterleaved);
	}

	@Test
	public void strictShortNameInterleavedIsImported() throws Exception {
		File file = testFile("phylip_strict_shortname_interleaved.phy");
		List<Sequence> sequences = new PhylipImporter(new FileReader(file),
				FileFormat.PHYLIP_SHORT_NAME_INTERLEAVED).importSequences();

		assertEquals(6, sequences.size());
		assertEquals("Archaeopt", sequences.get(0).getName().trim());
		assertEquals("B.subtilis", sequences.get(5).getName().trim());
		for(Sequence sequence : sequences){
			assertEquals(39, sequence.getLength());
		}
	}
}
