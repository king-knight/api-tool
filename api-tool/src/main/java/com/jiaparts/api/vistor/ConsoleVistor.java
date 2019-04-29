package com.jiaparts.api.vistor;

import java.util.List;

import com.jiaparts.api.componet.ComplexFieldUnit;
import com.jiaparts.api.componet.FieldUnit;
import com.jiaparts.api.componet.InterfaceUnit;
import com.jiaparts.api.componet.ListFieldUnit;
import com.jiaparts.api.componet.MethodUnit;
import com.jiaparts.api.componet.PrimaryFieldUnit;
import com.jiaparts.api.util.StringUtils;

public class ConsoleVistor implements Vistor {
	
	
	@Override
	public void vistor(InterfaceUnit unit) {
		System.err.println(unit.getInterfaceName());
		List<MethodUnit> methods = unit.getMethods();
		unit.setLevel(1);
		for(MethodUnit method :methods){
			method.setLevel(unit.getLevel()+1);
			method.accept(this);
		}
	}

	@Override
	public void vistor(MethodUnit unit) {
		System.err.println("方法:"+unit.getMethodName());
		System.err.println("入参:");
		ComplexFieldUnit in = unit.getIn();
		in.setLevel(unit.getLevel()+1);
		in.accept(this);
		System.err.println("返参:");
		ComplexFieldUnit out = unit.getOut();
		out.setLevel(unit.getLevel()+1);
		out.accept(this);

	}

	@Override
	public void vistor(FieldUnit unit) {
		

	}

	@Override
	public void vistor(PrimaryFieldUnit unit) {
		System.err.println(getSpace((unit.getLevel()-3)*2)+unit.getFieldName()+" : "+unit.getFieldType()+" //"+unit.getStandComment());

	}

	@Override
	public void vistor(ComplexFieldUnit unit) {
		String fieldName = unit.getFieldName();
		if(fieldName==null){
			fieldName="";
		}
		String comment=unit.getStandComment();
		if(StringUtils.isNotBlank(comment)){
			comment="//"+comment;
		}
		System.err.println(getSpace((unit.getLevel()-3)*2)+fieldName+"{"+comment);
		List<FieldUnit> inFields = unit.getInFields();
		if(inFields!=null){
			for(FieldUnit field:inFields){
				field.setLevel(unit.getLevel()+1);
				field.accept(this);
			}
		}
		System.err.println(getSpace((unit.getLevel()-3)*2)+"}");

	}

	@Override
	public void vistor(ListFieldUnit unit) {
		System.err.println(getSpace((unit.getLevel()-3)*2)+unit.getFieldName()+"[//"+unit.getStandComment());
		FieldUnit genericField = unit.getGenericField();
		genericField.setLevel(unit.getLevel()+1);
		genericField.accept(this);
		System.err.println(getSpace((unit.getLevel()-3)*2)+"]");

	}
	@Override
	public String getResult() {
		
		return null;
	}


}
