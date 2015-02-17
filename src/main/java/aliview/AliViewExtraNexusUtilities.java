package aliview;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import utils.nexus.NexusUtilities;
import aliview.alignment.Alignment;
import aliview.sequences.Sequence;

/*
 * 
 * TODO maybe generalize this so it could be in general NexusUtilities
 * 
 */
public class AliViewExtraNexusUtilities {
	private static final String LF = System.getProperty("line.separator");
	public static final int DATATYPE_PROTEIN = 1;
	public static final int DATATYPE_DNA = 7;
	
	public static final void exportAlignmentAsNexus(Writer out, Alignment alignment, boolean simplified, int datatype) throws IOException{
      	//String nexus = "";
  		
		String dataTypeString = "DNA";
		if(datatype == DATATYPE_PROTEIN){
			dataTypeString = "PROTEIN";
		}
		
		//String sequentialParameter = " SEQUENTIAL=YES";
		String sequentialParameter = "";
		
		
      	out.write("#NEXUS" + LF);
      	out.write(LF);
      	out.write("BEGIN DATA;" + LF);
      	out.write("DIMENSIONS  NTAX=" + alignment.getSize() + " NCHAR=" + alignment.getMaximumSequenceLength() + ";" + LF);
      	out.write("FORMAT DATATYPE=" + dataTypeString + sequentialParameter + " GAP=- MISSING=?;" + LF);
      	out.write("MATRIX" + LF);
      	out.write(LF);
      	
      	int longestSequenceNameLen = alignment.getLongestSequenceName();
      	int BLANK_SPACE_SIZE = 3;
      	int MAX_LEN = 99;
      	
  		for(Sequence seq: alignment.getSequences()){
  			if(simplified){
  				String name = NexusUtilities.replaceProblematicChars(seq.getName());
  				
  				int padSize = longestSequenceNameLen + BLANK_SPACE_SIZE;
  				
  				if(padSize > MAX_LEN){
  					name = StringUtils.substring(name, 0, MAX_LEN - BLANK_SPACE_SIZE);
  					padSize = MAX_LEN;
  				}
  				out.write("" + StringUtils.rightPad(name,padSize) + "" + seq.getBasesAsString());
  				out.write(LF);
  			}else{
  				out.write("" + StringUtils.rightPad(seq.getName(),longestSequenceNameLen + BLANK_SPACE_SIZE) + "" +  seq.getBasesAsString());
  				out.write(LF);
  			}
  		}
  		
  		out.write(";" + LF);
  		out.write(LF);
  		out.write("END;" + LF);
  		out.write(LF);
  		
  		if(alignment.getAlignmentMeta().getExcludes() != null){
	  		out.write(NexusUtilities.getExcludesAsNexusBlock(alignment.getAlignmentMeta().getExcludes()));
	  		out.write(LF);
	  		out.write(LF);
  		}
  		
  		if(alignment.getAlignmentMeta().getCodonPositions() != null){
	  		out.write(NexusUtilities.getCodonPosAsNexusBlock(alignment.getAlignmentMeta().getCodonPositions(), 0, alignment.getMaximumSequenceLength()));
	  		out.write(LF);
	  		out.write(LF);
  		}
		
  		if(alignment.getAlignmentMeta().getCharsets() != null){
	  		out.write(NexusUtilities.getCharsetsBlockAsNexus(alignment.getAlignmentMeta().getCharsets()));
	  		out.write(LF);
	  		out.write(LF);
 		}
  		
  		out.flush();
  		out.close();
       }
      
      public static final void exportAlignmentAsNexusCodonpos(Writer out, Alignment alignment, int datatype) throws IOException{
      	//String nexus = "";
    	  String dataTypeString = "DNA";
  		if(datatype == DATATYPE_PROTEIN){
  			dataTypeString = "PROTEIN";
  		}
    	  
  		int length = alignment.getMaximumSequenceLength();
  		
     	// dump pos
     	
     	boolean removeExcluded = true;
     	
     	ArrayList<Integer> allPos0 = alignment.getAllCodonPositions(0,removeExcluded, 0, length - 1);
     	ArrayList<Integer> allPos1 = alignment.getAllCodonPositions(1,removeExcluded, 0, length - 1);
       	ArrayList<Integer> allPos2 = alignment.getAllCodonPositions(2,removeExcluded, 0, length - 1);
       	ArrayList<Integer> allPos3 = alignment.getAllCodonPositions(3,removeExcluded, 0, length - 1);
     	
       	int nChar = allPos0.size() + allPos1.size() + allPos2.size() + allPos3.size();
     	 
      	out.write("#NEXUS" + LF);
      	out.write(LF);
      	out.write("BEGIN DATA;" + LF);
      	out.write("DIMENSIONS  NTAX=" + alignment.getSize() + " NCHAR=" + nChar + ";" + LF);
      	out.write("FORMAT DATATYPE=" + dataTypeString + " INTERLEAVE=YES GAP=- MISSING=?;" + LF);
      	out.write("MATRIX" + LF);
      	out.write(LF);
  		
      	
      	int longestName = alignment.getLongestSequenceName();
      	
  		for(Sequence seq: alignment.getSequences()){
  			
  			// also replace some characters not always understood
  			String seqName = seq.getName();
  			seqName = seqName.replace(' ','_');
  			
  			out.write("" + StringUtils.rightPad(seqName,longestName + 3));
  			out.write(seq.getBasesAtThesePosAsString(allPos0));
  			out.write(seq.getBasesAtThesePosAsString(allPos1));
  			out.write(seq.getBasesAtThesePosAsString(allPos2));
  			out.write(seq.getBasesAtThesePosAsString(allPos3));
  			out.write(LF);
  			
  		}
  		out.write(";" + LF);
  		out.write("END;" + LF);
  		out.write(LF);
  		
  		
  		// TODO this is not safe if one pos should be 0-size
  		// Write charset (pos 1, 2 & 3)
  		out.write("BEGIN SETS;" + LF);
  		
  		int start = 1;
  		int end = 0;
  		if(allPos0.size() > 0){
  	 		end = start + allPos0.size() - 1;
  	 		out.write("charset npos = " + start + "-" + end + ";" + LF);
  	 		start = end + 1;
  	 	}
  		
  		if(allPos1.size() > 0){
  			end = start + allPos1.size() - 1;
  			out.write("charset 1st = " + start + "-" + end + ";" + LF);
  			start = end + 1;
  		}
  		
  		if(allPos2.size() > 0){
  			end = start + allPos2.size() - 1;
  			out.write("charset 2nd = " + start + "-" + end + ";" + LF);
  			start = end + 1;
  		}
  		
  		if(allPos3.size() > 0){
  			end = start + allPos3.size() - 1;
  			out.write("charset 3nd = " + start + "-" + end + ";" + LF);
  			start = end + 1;
  		}

  		
  		out.write("END;" + LF);
  		out.write(LF);
  		
  		// end write charsets
  		
//  		out.write(getExcludesAsNexusBlock(alignment.getExcludes()));
//  		out.write(LF);
//  		
//  		out.write(getCodonPosAsNexusBlock(alignment.getCodonPositions()));
//  		out.write(LF);
  		
  		
  		out.flush();
  		out.close();
  		
       }

}
