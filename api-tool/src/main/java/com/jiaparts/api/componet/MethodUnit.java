package com.jiaparts.api.componet;

import com.jiaparts.api.vistor.Vistor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodUnit extends BaseUnit {
	/**
	 * 方法名
	 */
	private String methodName;
	/**
	 * 入参
	 */
	private ComplexFieldUnit in;
	/**
	 * 返参
	 */
	private ComplexFieldUnit out;
	@Override
	public void accept(Vistor vistor) {
		vistor.vistor(this);
	}
}
