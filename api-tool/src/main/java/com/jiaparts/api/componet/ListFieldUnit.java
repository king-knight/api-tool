package com.jiaparts.api.componet;

import com.jiaparts.api.vistor.Vistor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListFieldUnit extends FieldUnit {
	/**
	 * list泛型类
	 */
	private FieldUnit genericField;
	@Override
	public void accept(Vistor vistor) {
		vistor.vistor(this);
	}
}
