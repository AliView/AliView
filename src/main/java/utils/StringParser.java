package utils;

import java.util.ArrayList;

public class StringParser{
	
	private String input;
	private String delimiter;
	private String[] tokensArray;
	ArrayList<String> tokens = new ArrayList<String>();

	public StringParser(String input, String delimiter){
		this.input = input;
		this.delimiter = delimiter;
		this.tokensArray = input.split(delimiter);
		for(String nextString: tokensArray){
			tokens.add(nextString);
		}
	}
	
	public boolean anyTokenMatches(String compare){
		for(String token: tokens){
			if(token.contains(compare)){
				return true;
			}
		}
		return false;
	}

	public String getTokenIfMatches(String compare) {
		for(String token: tokens){
			if(token.matches(compare)){
				return token;
			}
		}
		return null;
	}

}
