package aliview.sequencelist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import aliview.sequences.Sequence;

public class FilePage {
	
	public File aliFile;
	public int startIndex;
	public int endIndex;
	public long startPointer;
	public long endPointer;
	public int nMaxSeqsToRetrieve;
	public int pageIndex;
	public List<Sequence> seqList;

	public FilePage(int pageIndex, File aliFile, List<Sequence> seqList, int startIndex, int endIndex,
			long startPointer, long endPointer, int nMaxSeqsToRetrieve) {
		this.pageIndex = pageIndex;
		this.aliFile = aliFile;
		this.seqList = seqList;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.startPointer = startPointer;
		this.endPointer = endPointer;
		this.nMaxSeqsToRetrieve = nMaxSeqsToRetrieve;
	}

}
