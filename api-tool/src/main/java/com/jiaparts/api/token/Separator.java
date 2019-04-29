package com.jiaparts.api.token;

import java.util.HashMap;
import java.util.Map;

public class Separator {
	public static String[]separators={";","(",")","[","]","{","}"," ","<",">"};

	public static Map<String,String> delimiterPair=new HashMap<String,String>(){
		
		private static final long serialVersionUID = 5577548718741795726L;

		{
			put("(",")");
			put("<",">");
			put("{","}");
		}
	};
	
	public static char getDelimiterPairEnd(char c){
		char result='0';
		for(Map.Entry<String, String>entry:delimiterPair.entrySet()){
			if(entry.getKey().equals(String.valueOf(c))){
				result=entry.getValue().charAt(0);
				break;
			}
		}
		return result;
	}
	
	public static boolean inPairEnd(char c){
		for(Map.Entry<String, String>entry:delimiterPair.entrySet()){
			if(entry.getValue().equals(String.valueOf(c))){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isPair(char k,char v){
		String val = delimiterPair.get(String.valueOf(k));
		if(val!=null && val.equals(String.valueOf(v))){
			return true;
		}
		return false;
	}
	
}
