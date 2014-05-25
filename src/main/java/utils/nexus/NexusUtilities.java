package utils.nexus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;


import utils.RangeUtils;


public class NexusUtilities {

	private static final Logger logger = Logger.getLogger(NexusUtilities.class);
	private static final String LF = System.getProperty("line.separator");


	public static void main(String[] args) {
		try {
			new NexusUtilities().createCharsetsFromNexusFile(new File("/home/anders/projekt/ormbunkar/analys/seqconcat_test/seqconcat_test.nexus"), 10000);
		} catch (NexusAlignmentImportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final boolean updateExcludesFromFile(File alignmentFile, Excludes excludes) throws NexusAlignmentImportException {

		logger.info("look for nexus EXSET block in file " + alignmentFile.toString());

		try {
			String assumptionsBlock = extractBlockFromFile(alignmentFile, "BEGIN ASSUMPTIONS;","END");
			if(assumptionsBlock == null || assumptionsBlock.length() == 0){
				return false;
			}
			String exsetBlock = StringUtils.substringBetween(assumptionsBlock, "EXSET", ";");
			if(exsetBlock == null){
				return false;
			}
			logger.info("Found block");
			String excludeString = StringUtils.substringAfter(exsetBlock, "=");
			if(excludeString == null || excludeString.length() == 0){
				return true;
			}
			ArrayList<NexusRange> allRanges = parseNexusRanges(excludeString);
			for(NexusRange range: allRanges){
				excludes.addRange(range);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new NexusAlignmentImportException("Could not parse NEXUS CHARSET Block");
		}
		return true;
	}

	public static final boolean updateCodonPositionsFromNexusFile(File alignmentFile, CodonPositions codonPositions) throws NexusAlignmentImportException {

		logger.info("look for nexus BEGIN CODONS; block in file " + alignmentFile.toString());


		try {
			String codonsBlock = extractBlockFromFile(alignmentFile, "BEGIN CODONS;", "END;");
			if(codonsBlock == null || codonsBlock.length() == 0){
				return false;
			}

			// TODO there could be different codonposset - I just use first one
			String codonPositionsBlock = StringUtils.substringBetween(codonsBlock, "CODONPOSSET", ";");
			if(codonPositionsBlock == null || codonPositionsBlock.length() == 0){
				return false;
			}
			// add a "," to the end makes it easier to parse (then codon pos can be unordered
			codonPositionsBlock += ",";

			logger.info("Found block");

			Scanner lineTokenizer = new Scanner(codonPositionsBlock).useDelimiter(",");
			logger.info("search" + lineTokenizer.findWithinHorizon("\\?:", codonPositionsBlock.length()));
//			while(lineTokenizer.hasNext()){
//				logger.info(lineTokenizer.next());
//			}
			//

			// Always set position n - 1 because program internally is working with first pos in alignment as 0 (and in codonpos block from 1)

			// TODO with n range does not ends with \3
			String nPos = StringUtils.substringBetween(codonPositionsBlock, "N:",",");
			if(nPos != null){	
				ArrayList<NexusRange> allRanges = parseNexusRanges(nPos);
				for(NexusRange range: allRanges){
					for(int n = range.getMinimumInteger(); n <= range.getMaximumInteger(); n++){
						codonPositions.setPosition(n - 1,0);
					}
				}
				codonPositions.fireUpdated();
			}
			// TODO questionmarkpos is treated as n
			// TODO with n range does not ends with \3
			String questionmarkPos = StringUtils.substringBetween(codonPositionsBlock, "?:",",");
			if(questionmarkPos != null){	
				ArrayList<NexusRange> allRanges = parseNexusRanges(questionmarkPos);
				for(NexusRange range: allRanges){
					for(int n = range.getMinimumInteger(); n <= range.getMaximumInteger(); n++){
						codonPositions.setPosition(n - 1,0);
					}
				}	
			}

			// TODO check that range ends with \3
			String pos1 = StringUtils.substringBetween(codonPositionsBlock, "1:",",");
			if(pos1 != null){	
				ArrayList<NexusRange> allRanges = parseNexusRanges(pos1);
//				logger.info("allRangesSize" + allRanges.size());
				for(NexusRange range: allRanges){
//					logger.info("range" + range.toString());
					for(int n = range.getMinimumInteger(); n <= range.getMaximumInteger(); n=n+3){
						codonPositions.setPosition(n - 1,1);
					}
				}	
			}

			String pos2 = StringUtils.substringBetween(codonPositionsBlock, "2:",",");
			if(pos2 != null){	
				ArrayList<NexusRange> allRanges = parseNexusRanges(pos2);
//				logger.info("allRangesSize" + allRanges.size());
				for(NexusRange range: allRanges){
					for(int n = range.getMinimumInteger(); n <= range.getMaximumInteger(); n=n+3){
						codonPositions.setPosition(n-1,2);
					}
				}	
			}

			String pos3 = StringUtils.substringBetween(codonPositionsBlock, "3:",",");
			if(pos3 != null){	
				ArrayList<NexusRange> allRanges = parseNexusRanges(pos3);
				for(NexusRange range: allRanges){
					for(int n = range.getMinimumInteger(); n <= range.getMaximumInteger(); n=n+3){
						codonPositions.setPosition(n - 1,3);
					}
				}	
			}		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new NexusAlignmentImportException("Could not parse NEXUS CHARSET Block");
		}

//		logger.info(codonPositions.debug());

		return true;
	}


	private static String getNexusRangesAsBlock(List <NexusRange> ranges){
		String rangeBlock = "";
		for(NexusRange range: ranges){

			if(range.getMinimumInteger() == range.getMaximumInteger()){
				rangeBlock += " " + (range.getMinimumInteger() + 1); // Add one because internally we work with array posiiton 0, but in charset first pos is 1 
			}
			else{
				rangeBlock += " " + (range.getMinimumInteger() + 1) + "-" + (range.getMaximumInteger() + 1); // Add one because internally we work with array posiiton 0, but in exset spec first pos is 1 
				if(range.getSteps() != 1){
					rangeBlock += "\\" + range.getSteps(); 
				}
			}
		}

		return rangeBlock;
	}


	public static String getCharsetsBlockWithoutNexus(List<CharSet> charsets) {
		if(charsets == null){
			return "";
		}

		StringBuffer charsetBlock = new StringBuffer();

		for(CharSet aSet: charsets){
			charsetBlock.append("charset " + aSet.getName() + "=" + getNexusRangesAsBlock(aSet.getCharSetAsNexusRanges()) + ";" + LF);
		}

		return charsetBlock.toString();
	}


	public static String getCharsetsBlockAsNexus(List<CharSet> charsets) {
		if(charsets == null){
			return "";
		}
		StringBuffer charsetBlock = new StringBuffer("BEGIN SETS;" + LF);

		charsetBlock.append(getCharsetsBlockWithoutNexus(charsets));

		// end line with a semicolon
		charsetBlock.append("END;");

		return charsetBlock.toString();
	}


	public static final ArrayList<CharSet> createCharsetsFromNexusFile(File alignmentFile, int alignmentWidth) throws NexusAlignmentImportException {

		logger.info("look for nexus BEGIN SETS; block in file " + alignmentFile.toString());

		ArrayList<CharSet> allSets = new ArrayList<CharSet>();

		try {

			String setsBlock = extractBlockFromFile(alignmentFile, "BEGIN SETS;", "END;");	

			if(setsBlock != null && setsBlock.length() > 0){

				logger.info("Found block");

				setsBlock = setsBlock.toUpperCase();

				String [] tokens = setsBlock.split(";");

				for(String token: tokens){
					token = token.trim();
					if(token.length() > 0){
						String[] parts = token.split("=");

						String name = parts[0].replace("CHARSET", ""); // /i (insensitive
						name = name.trim();
						String ranges = parts[1].trim();
						CharSet charSet = new CharSet(name, alignmentWidth);

						ArrayList<NexusRange> allRanges = parseNexusRanges(ranges);

						// TODO also make it possible to read sets that are not
						// continous but for example: 
						// CHARSET 1ST =  1-2356\3;
						// CHARSET 2ND =  2-2354\3;
						//
						// only add ranges if all are next to each other
						boolean areAllContinous = true;
						for(NexusRange range: allRanges){
							if(range.steps != 1){
								areAllContinous = false;
							}
						}
						if(allRanges.size() > 0 && areAllContinous){
							charSet.addRanges(allRanges);
							allSets.add(charSet);
						}

						//charSet.debug();
					}

				}

			}else{
				// No block
			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new NexusAlignmentImportException("Could not parse NEXUS CHARSET Block");
		}

		return allSets;
	}

	public static ArrayList<NexusRange> parseNexusRanges(String input){

		ArrayList<NexusRange> allRanges = new ArrayList<NexusRange>();

		// pad '-' with space to ' - ' to make parsing simpler
		input = input.replaceAll("-", " - ");
		NexusParser parser = new NexusParser(input);	
		parser.split(" ", true);

//		logger.info(parser.countTokens());

		parser.debug();

		while(parser.hasMoreTokens()){
			if(parser.isNextTokensIntRange()){
//				logger.info("TOKENiSiNTRANGE");
				allRanges.add(parser.getNexusRange());
			}
			else if(parser.isNextTokenNumeric()){
//				logger.info("tokenIsNumeric");
				parser.getIntegerAsRange();
			}
			else{
				parser.next();
			}
		}

		for(NexusRange range: allRanges){
//			logger.info("" + range.toString());
		}

		return allRanges;

	}



	//	BEGIN ASSUMPTIONS;
	//	OPTIONS  DEFTYPE=unord PolyTcount=MINSTEPS ;
	//	EXSET * UNTITLED  =  1-613 701-833 946-1045 1124-1256 1313-1628 1651-1654 1756-1880 1952-2038 2133-2358 2498-2750 2913-2921 2940-2944 3007-3123 3164-3169 3239-3321 3343-3348 3396-3503 3556-3561 3580-3659 3735-3848 3894-3897 3928-3933 3951-4037 4108-4555;
	//	END;
	//	
	//	
	//	BEGIN CODONS;
	//		CODONPOSSET * CodonPositions = 
	//			N: 1-76 717-720 946-1045 1124-1256 1313-1628 1651-1654 1756-1880 1952-2038 2133-2358 2498-2506 2659-2740 2940-2944 3007-3123 3239-3321 3396-3503 3580-3659 3735-3848 3894-3897 3951-4037 4108-4112 4152-4555, 
	//			1: 77-716\3 721-943\3 1046-1121\3 1257-1311\3 1630-1648\3 1655-1754\3 1882-1951\3 2041-2131\3 2360-2495\3 2507-2657\3 2742-2937\3 2945-3005\3 3125-3236\3 3322-3394\3 3505-3577\3 3660-3732\3 3849-3891\3 3898-3949\3 4039-4105\3 4113-4149\3, 
	//			2: 78-714\3 722-944\3 1047-1122\3 1258-1312\3 1631-1649\3 1656-1755\3 1883-1949\3 2039-2132\3 2361-2496\3 2508-2658\3 2743-2938\3 2946-3006\3 3126-3237\3 3323-3395\3 3506-3578\3 3661-3733\3 3850-3892\3 3899-3950\3 4040-4106\3 4114-4150\3, 
	//			3: 79-715\3 723-945\3 1048-1123\3 1259-1310\3 1629-1650\3 1657-1753\3 1881-1950\3 2040-2130\3 2359-2497\3 2509-2656\3 2741-2939\3 2947-3004\3 3124-3238\3 3324-3393\3 3504-3579\3 3662-3734\3 3851-3893\3 3900-3948\3 4038-4107\3 4115-4151\3;
	//		CODESET  * UNTITLED = Universal: all ;
	//	END;


	public static final String getExcludesAsNexusBlock(Excludes excludes) {

		ArrayList<NexusRange> allRanges = excludes.getExcludedAsNexusRanges();
		String exsetBlock =  "BEGIN ASSUMPTIONS;" + LF;
		exsetBlock += "EXSET * UNTITLED  = "; 


		exsetBlock += getNexusRangesAsBlock(allRanges);

		// end line with a semicolon
		exsetBlock += ";";
		exsetBlock += LF;
		exsetBlock += "END;";

		return exsetBlock;
	}

	public static final String getPartialCodonPosAsNexusBlock(CodonPositions codonPositions, int startPos, int endPos) {

		String nexusBlock =  "BEGIN CODONS;" + LF;
		nexusBlock += "CODONPOSSET * CodonPositions =" + LF;

		String posN = " N:"; // should not really be necessary to include this
		String pos1 = " 1:";
		String pos2 = " 2:";
		String pos3 = " 3:";

		// add one to all since program internally is working with first pos in alignment as 0
		ArrayList<IntRange> allPos = codonPositions.getAllNonCodingPositionsAsRanges(0, startPos, endPos);
		RangeUtils.sortIntRangeList(allPos);
		for(IntRange range: allPos){
			posN += " " + (range.getMinimumInteger() + 1) + "-" + (range.getMaximumInteger() + 1) + "";
		}

		allPos = codonPositions.getAllCodingPositionsAsRanges(1, startPos, endPos);
		RangeUtils.sortIntRangeList(allPos);
		for(IntRange range: allPos){
			pos1 += " " + (range.getMinimumInteger() + 1) + "-" + (range.getMaximumInteger() + 1) + "\\3";
		}

		allPos = codonPositions.getAllCodingPositionsAsRanges(2, startPos, endPos);
		RangeUtils.sortIntRangeList(allPos);
		for(IntRange range: allPos){
			pos2 += " " + (range.getMinimumInteger() + 1) + "-" + (range.getMaximumInteger() + 1) + "\\3";
		}

		allPos = codonPositions.getAllCodingPositionsAsRanges(3, startPos, endPos);
		RangeUtils.sortIntRangeList(allPos);
		for(IntRange range: allPos){
			pos3 += " " + (range.getMinimumInteger() + 1) + "-" + (range.getMaximumInteger() + 1) + "\\3";
		}


		// nexusBlock += posN + "," + LF;

		nexusBlock += posN + "," + LF;
		nexusBlock += pos1 + "," + LF;
		nexusBlock += pos2 + "," + LF;
		nexusBlock += pos3 + ";" + LF;

		nexusBlock += "CODESET  * UNTITLED = Universal: all ;" + LF;
		nexusBlock += "END;";

		return nexusBlock;
	}


	/*
	 * 
	 * I.e., like this:
 4218C_r1_N= 11-118 175-281 402-510;
 4218C_r1_1= 2-8\3 119-173\3 283-400\3 512-548\3;
 4218C_r1_2= 3-9\3 120-174\3 284-401\3 513-549\3;
 4218C_r1_3= 1-10\3 121-172\3 282-399\3 511-550\3;
 4218C_r1a_N= 561-862;
 4218C_r1a_1= 552-558\3 863-881\3;
	 * 
	 * 
	 * 
	 * 
	 */
	public static final String getPartialCodonPosAsCharsetNexusBlock(String charsetPrefix, CodonPositions codonPositions, int startPos, int endPos) {

		String nexusBlock =  "";


		String posN = "charset " + charsetPrefix + "_N= "; 
		String pos1 = "charset " + charsetPrefix + "_1= "; 
		String pos2 = "charset " + charsetPrefix + "_2= "; 
		String pos3 = "charset " + charsetPrefix + "_3= "; 

		// add one to all since program internally is working with first pos in alignment as 0
		ArrayList<IntRange> allPos = codonPositions.getAllNonCodingPositionsAsRanges(0, startPos, endPos);
		RangeUtils.sortIntRangeList(allPos);
		for(IntRange range: allPos){
			posN += " " + (range.getMinimumInteger() + 1) + "-" + (range.getMaximumInteger() + 1) + "";
		}

		allPos = codonPositions.getAllCodingPositionsAsRanges(1, startPos, endPos);
		RangeUtils.sortIntRangeList(allPos);
		for(IntRange range: allPos){
			pos1 += " " + (range.getMinimumInteger() + 1) + "-" + (range.getMaximumInteger() + 1) + "\\3";
		}

		allPos = codonPositions.getAllCodingPositionsAsRanges(2, startPos, endPos);
		RangeUtils.sortIntRangeList(allPos);
		for(IntRange range: allPos){
			pos2 += " " + (range.getMinimumInteger() + 1) + "-" + (range.getMaximumInteger() + 1) + "\\3";
		}

		allPos = codonPositions.getAllCodingPositionsAsRanges(3, startPos, endPos);
		RangeUtils.sortIntRangeList(allPos);
		for(IntRange range: allPos){
			pos3 += " " + (range.getMinimumInteger() + 1) + "-" + (range.getMaximumInteger() + 1) + "\\3";
		}


		// nexusBlock += posN + "," + LF;

		nexusBlock += posN + "," + LF;
		nexusBlock += pos1 + "," + LF;
		nexusBlock += pos2 + "," + LF;
		nexusBlock += pos3 + ";" + LF;

		return nexusBlock;
	}

	public static final String getCodonPosAsNexusBlock(CodonPositions codonPositions) {
		return getPartialCodonPosAsNexusBlock(codonPositions, 0, codonPositions.getLength() - 1);
	}


	public static String replaceProblematicChars(String text){
		text = text.replace(' ', '_');
		text = text.replace('-', '_');
		text = text.replace('\'', '_');
		text = text.replace( '?', '_');
		text = text.replace( '.', '_');
		text = text.replace( '/', '_');
		text = text.replace( '|', '_');
		text = text.replace( '\"', '_');
		text = text.replace( ',', '_');
		text = text.replace( '&', '_');
		text = text.replace( '\\', '_');

		return text;
	}




	private static String getPersonalNexusBlock(int seqLen){
		StringBuilder block = new StringBuilder();

		block.append("BEGIN MRBAYES;" + LF);
		block.append("charset aligned-WoodsiapgiC-mafft.fasta.nexus = 1-" + seqLen + ";" + LF);
		block.append("Partition ALLDNA = 1:aligned-WoodsiapgiC-mafft.fasta.nexus;" + LF);
		block.append("Set partition = ALLDNA;" + LF);
		block.append("[GTRG]" + LF);
		block.append("["+ LF);
		block.append("Lset applyto=(1) nst=6 rates=gamma;"+ LF);
		block.append("Prset applyto=(1) revmatpr=Dirichlet(1.0,1.0,1.0,1.0,1.0,1.0) statefreqpr=Dirichlet(1.0,1.0,1.0,1.0) shapepr=Uniform(0.1,50.0);"+ LF);
		block.append("]"+ LF);
		block.append("[GTRIG]Lset applyto=(1) nst=6 rates=invgamma;"+ LF);
		block.append("Prset applyto=(1) revmatpr=Dirichlet(1.0,1.0,1.0,1.0,1.0,1.0) statefreqpr=Dirichlet(1.0,1.0,1.0,1.0) shapepr=Uniform(0.1,50.0) pinvarpr=Uniform(0.0,1.0);"+ LF);

		block.append("[SYMG]"+ LF);
		block.append("["+ LF);
		block.append("Lset applyto=(1) nst=6 rates=gamma;"+ LF);
		block.append("Prset applyto=(1) revmatpr=Dirichlet(1.0,1.0,1.0,1.0,1.0,1.0) statefreqpr=Fixed(Equal);"+ LF);
		block.append("]"+ LF);
		block.append("mcmcp nruns=1 ngen=1000000 printfreq=1000 samplefreq=1000 nchains=1 diagnfreq=10000 burninfrac=0.25 stoprule=no stopval=0.002 temp=0.2 checkpoint=yes checkfreq=500000;"+ LF);
		block.append("mcmc;"+ LF);
		block.append("sumt burnin=0.7 nruns=1;"+ LF);
		block.append("END;"+ LF);

		return block.toString();

	}


	public static boolean isNexusFile(File alignmentFile) {
		boolean isNexusFile = false;

		try {
			if(alignmentFile != null && alignmentFile.exists()){
				BufferedReader r = new BufferedReader(new FileReader(alignmentFile));
				String firstLine = r.readLine();
				if(firstLine != null & firstLine.length() > 0 & firstLine.length() < 10){
					if(firstLine.toLowerCase().indexOf("nexus") > 0){
						isNexusFile = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isNexusFile;
	}
	
	public static String extractBlockFromFile(File alignmentFile, String start, String end) {

		StringBuilder block = new StringBuilder();
			try {
				BufferedReader r = new BufferedReader(new FileReader(alignmentFile));
				String line;
				String name = null;
				int nLine = 0;
				boolean startFound = false;
				boolean endFound = false;
				while ((line = r.readLine()) != null) {
					if(StringUtils.containsIgnoreCase(line, start)){
						startFound = true;
						int startPos = line.toUpperCase().indexOf(start.toUpperCase());
						line = line.substring(startPos + start.length());
					}
					if(startFound && StringUtils.containsIgnoreCase(line, end)){
						endFound = true;
						int startPos = line.toUpperCase().indexOf(end.toUpperCase());
						line = line.substring(0,startPos);
					}
					if(startFound){
						block.append(line);
						block.append(LF);
					}
					if(endFound){
						break;
					}
					
				
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return block.toString();
	}

}



