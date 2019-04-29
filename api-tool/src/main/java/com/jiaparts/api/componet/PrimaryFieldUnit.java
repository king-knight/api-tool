package com.jiaparts.api.componet;

import com.jiaparts.api.vistor.Vistor;

public class PrimaryFieldUnit extends FieldUnit {
	
	@Override
	public void accept(Vistor vistor) {
		vistor.vistor(this);
	}

}
