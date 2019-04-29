package com.jiaparts.api.vistor;

import java.util.List;
import java.util.Map;

import com.jiaparts.api.componet.BaseUnit;
import com.jiaparts.api.componet.ComplexFieldUnit;
import com.jiaparts.api.componet.FieldUnit;
import com.jiaparts.api.componet.InterfaceUnit;
import com.jiaparts.api.componet.ListFieldUnit;
import com.jiaparts.api.componet.MethodUnit;
import com.jiaparts.api.componet.PrimaryFieldUnit;
import com.jiaparts.api.util.StringUtils;
public class MarkDownVistor implements Vistor {
	private StringBuilder result=new StringBuilder();
	private void newLine(){
		result.append("\r\n");
	}
	private String getComment(BaseUnit unit){
		String comment=unit.getStandComment();
		if(StringUtils.isNotBlank(comment)){
			comment="//"+comment;
		}else{
			comment="";
		}
		return comment;
	}
	
	private String getMethodComment(BaseUnit unit){
		String comment=unit.getStandComment();
		if(StringUtils.isBlank(comment)){
			Map<String, String> tags = unit.getTags();
			comment = tags.get("Description");
		}
		if(StringUtils.isNotBlank(comment)){
		}else{
			comment="";
		}
		return comment;
	}
	@Override
	public void vistor(InterfaceUnit unit) {
		String interfaceName = unit.getInterfaceName();
		unit.setLevel(2);
		result.append("##")
		.append(interfaceName)
		.append(" ")
		.append(unit.getStandComment());
		newLine();
		for(MethodUnit method :unit.getMethods()){
			method.setLevel(unit.getLevel()+1);
			method.accept(this);
		}
	}

	@Override
	public void vistor(MethodUnit unit) {
		result.append("###")
		.append(getMethodComment(unit))
		.append("(").append(unit.getMethodName()).append(")");
		newLine();
		result.append("####请求参数:");
		newLine();
		result.append("````");
		newLine();
		ComplexFieldUnit in = unit.getIn();
		in.setLevel(unit.getLevel()+1);
		in.accept(this);
		result.append("````");
		newLine();
		result.append("####返回参数:");
		newLine();
		result.append("````");
		newLine();
		ComplexFieldUnit out = unit.getOut();
		out.setLevel(unit.getLevel()+1);
		out.accept(this);
		result.append("````");
		newLine();
	}

	@Override
	public void vistor(FieldUnit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vistor(PrimaryFieldUnit unit) {
		result.append(getSpace((unit.getLevel()-3)*2))
		.append(fieldNameAsComment(unit))
		.append(" : ")
		.append(unit.getFieldType())
		.append(getComment(unit));
		newLine();

	}

	@Override
	public void vistor(ComplexFieldUnit unit) {
		String fieldName = fieldNameAsComment(unit);
		String comment = getComment(unit);
		result.append(getSpace((unit.getLevel()-3)*2))
		.append(fieldName)
		.append("{")
		.append(comment);
		newLine();
		List<FieldUnit> inFields = unit.getInFields();
		if(inFields!=null){
			for(FieldUnit field:inFields){
				field.setLevel(unit.getLevel()+1);
				field.accept(this);
			}
		}
		result.append(getSpace((unit.getLevel()-3)*2)+"}");
		newLine();

	}

	@Override
	public void vistor(ListFieldUnit unit) {
		String comment = getComment(unit);
		result.append(getSpace((unit.getLevel()-3)*2))
		.append(fieldNameAsComment(unit))
		.append("[").append(comment);
		newLine();
		FieldUnit genericField = unit.getGenericField();
		genericField.setLevel(unit.getLevel()+1);
		genericField.accept(this);
		result.append(getSpace((unit.getLevel()-3)*2))
		.append("]");
		newLine();
	}

	@Override
	public String getResult() {
		String resp = result.toString();
		return resp;
	}

}
