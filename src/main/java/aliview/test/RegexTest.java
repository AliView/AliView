package aliview.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {
	
	public static void main(String[] args) {
	//	String test = "wood_ural_888_NC_10102";
		String test = ">S__noctiflora_17907_NC_016728";
		System.out.println(test.matches("[_(\\d)+$]"));
		
		//final Pattern pattern = Pattern.compile("_(.*)$|_\\D+\\d+$");
		final Pattern pattern = Pattern.compile("._\\d+_");
		final Matcher matcher = pattern.matcher(test);
		System.out.println(matcher.find());
		System.out.println(matcher.end());
		System.out.println(matcher.regionEnd());
		
		
	}

}
