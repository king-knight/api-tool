package com.jiaparts.api.componet;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
	private String standComment;
	private String dcomment;
	private Map<String,String>tags;
}
