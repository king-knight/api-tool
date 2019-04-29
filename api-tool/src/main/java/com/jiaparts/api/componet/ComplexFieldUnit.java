package com.jiaparts.api.componet;

import java.util.ArrayList;
import java.util.List;

import com.jiaparts.api.util.StringUtils;
import com.jiaparts.api.vistor.Vistor;

import lombok.Getter;
import lombok.Setter;
/**
 * 复合对象
 * @author yuanw
 *
 */
@Getter
@Setter
public class ComplexFieldUnit extends FieldUnit {
	/**
	 * 内部字段
	 */
	private List<FieldUnit>inFields;
	/**
	 * 实际字段替换泛型字段
	 * @param genericName
	 */
	public void replaceGenericField(String genericName,FieldUnit unit){
		if(StringUtils.isBlank(genericName)|| unit==null){
			return;
		}
		int idx=-1;
		for(int i=0;i<inFields.size();i++){
			if(inFields.get(i).getFieldType().equals(genericName)){
				idx=i;
				break;
			}
		}
		if(idx>-1){
			FieldUnit remove = inFields.remove(idx);
			unit.setFieldName(remove.getFieldName());
			unit.setStandComment(remove.getStandComment());
			inFields.add(inFields.size(), unit);
		}
	}
	
	public void addFieldIn(FieldUnit field){
		if(inFields==null){
			inFields=new ArrayList<>();
		}
		inFields.add(field);
	}
	@Override
	public void accept(Vistor vistor) {
		vistor.vistor(this);
	}
}
