package com.jiaparts.api.componet;

import java.util.ArrayList;
import java.util.List;

import com.jiaparts.api.vistor.Vistor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterfaceUnit extends BaseUnit{
	/**
	 * 方法
	 */
	private List<MethodUnit>methods;
	/**
	 * 接口名
	 */
	private String interfaceName;
	
	public void addMethod(MethodUnit method){
		if(methods==null){
			methods=new ArrayList<>();
		}
		methods.add(method);
	}

	@Override
	public void accept(Vistor vistor) {
		vistor.vistor(this);
	}
}
