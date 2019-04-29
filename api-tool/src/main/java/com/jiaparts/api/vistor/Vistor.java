package com.jiaparts.api.vistor;

import com.jiaparts.api.componet.ComplexFieldUnit;
import com.jiaparts.api.componet.FieldUnit;
import com.jiaparts.api.componet.InterfaceUnit;
import com.jiaparts.api.componet.ListFieldUnit;
import com.jiaparts.api.componet.MethodUnit;
import com.jiaparts.api.componet.PrimaryFieldUnit;
import com.jiaparts.api.util.StringUtils;

public interface Vistor {
	void vistor(InterfaceUnit unit);
	void vistor(MethodUnit unit);
	void vistor(FieldUnit unit);
	void vistor(PrimaryFieldUnit unit);
	void vistor(ComplexFieldUnit unit);
	void vistor(ListFieldUnit unit);
	String getResult();
	default String getSpace(int num){
		String s="";
		for(int i=1;i<=num;i++){
			s+=" ";
		}
		return s;
	}
	
	default String fieldNameAsComment(FieldUnit unit){
		String fieldName = unit.getFieldName();
		if(StringUtils.isBlank(fieldName)){
			return "";
		}
		return "\""+fieldName+"\"";
	}
	
}
