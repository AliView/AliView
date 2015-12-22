package utils.nexus;

import java.util.ArrayList;

import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

public class NexusParser {
	private static final Logger logger = Logger.getLogger(NexusParser.class);
	
	private String input;
	private ArrayList<String> tokens;
	private int currentPos = 0;

	public NexusParser(String input){
		this.input = input;
	}
	
	public void split(String regex, boolean discardEmptyTokens){
		String splitted[] = input.split(regex);
		tokens = new ArrayList<String>();
		for(String s : splitted){
			if(s.equals("") && discardEmptyTokens){
				
			}else{
				tokens.add(s);
			}
		}
	}
	
	public int countTokens(){
		if(tokens != null){
			return tokens.size();
		}
		else{
			return 0;
		}
	}
	
	public boolean hasMoreTokens(){
		if(currentPos + 1 < countTokens()){
			return true;
		}else{
			return false;
		}
	}
	
	public void next(){
		currentPos ++;
	}
	
	public boolean isNextTokensRange(){
		
//		logger.info(getTokenAt(currentPos));
//		logger.info(isTokenInteger(currentPos));
//		logger.info(getTokenAt(currentPos + 1));
//		logger.info(matchesToken(currentPos + 1,"\\-"));
//		logger.info(getTokenAt(currentPos + 2));
//		logger.info(isTokenInteger(currentPos + 2));
		
		if(isTokenInteger(currentPos) && matchesToken(currentPos + 1,"\\-") && isTokenInteger(currentPos + 2)){
			return true;
		}
		else{
			return false;
		}
	}
	
	private boolean matchesToken(int pos, String matchString) {
		String token = getTokenAt(pos);
		if(token == null){
			return false;
		}
		else{
			//logger.info(matchString);
			return token.matches(matchString); 
		}
	}

	private boolean isTokenInteger(int pos) {	
		return matchesToken(pos,"-?\\d+(\\.\\d+)?(\\\\\\d+)?"); //match a number with optional '-' and decimal. and optional \3
	}
	
	public boolean isNextTokenNumeric() {
		return isTokenInteger(currentPos);
	}

	private String getTokenAt(int pos) {
		if(pos < countTokens()){
			return tokens.get(pos);
		}
		else{
			return null;
		}
	}

	public boolean isNextTokensIntRange() {
		return isNextTokensRange();
	}

	public NexusRange getNexusRange(int positionVal){
		String firstVal = getTokenAt(currentPos);
		String secondVal = getTokenAt(currentPos + 2);
		
		// Get steps val from last integer (if there is one) e.g. 1223\3
		String[] splitted = secondVal.split("\\\\");	
		secondVal = splitted[0];
		String steps = "1";
		if(splitted.length > 1){
			steps = splitted[1];
		}
		
		NexusRange range = new NexusRange(Integer.parseInt(firstVal), Integer.parseInt(secondVal), Integer.parseInt(steps), positionVal);
		
		// Spola fram
		currentPos += 3;
		
		return range;
	}

	public IntRange getIntegerAsRange() {
		String firstVal = getTokenAt(currentPos);
		IntRange range = new IntRange(Integer.parseInt(firstVal), Integer.parseInt(firstVal));
		
		// Spola fram
		currentPos += 1;
		
		return range;
	}
	
	public String getToken() {
		return  getTokenAt(currentPos);
	}
	

	public void debug() {
//		logger.info("tokenCount=" + countTokens());
		for(String token: tokens){
//			logger.info("token='" + token + "'");
		}
	}

	public String getLine() {
		// TODO Auto-generated method stub
		return null;
	}


}
