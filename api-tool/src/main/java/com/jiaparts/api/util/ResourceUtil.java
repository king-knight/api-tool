package com.jiaparts.api.util;

import java.util.ResourceBundle;

public class ResourceUtil {
	
	public static String getString(String key){
		ResourceBundle bundle = ResourceBundle.getBundle("settings");
		String val = bundle.getString(key);
		if(StringUtils.isBlank(val)){
			String pb = bundle.getString("project");
			ResourceBundle pr = ResourceBundle.getBundle(pb);
			val=pr.getString(key);
		}
		return val;
	}
	
}
