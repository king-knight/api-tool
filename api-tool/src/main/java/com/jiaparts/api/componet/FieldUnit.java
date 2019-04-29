package com.jiaparts.api.componet;

import com.jiaparts.api.vistor.Vistor;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class FieldUnit extends BaseUnit {
	
	private String fieldType;
	
	private String fieldName;

	@Override
	public void accept(Vistor vistor) {
		vistor.vistor(this);
	}
	
}
