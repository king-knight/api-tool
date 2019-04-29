package com.jiaparts.api.componet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jiaparts.api.vistor.Vistor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseUnit {
	/**
	 * 包
	 */
	private String pack;
	/**
	 * 导入包
	 */
	private List<String> imports;
	/**
	 * 标准注释
	 */
	private String standComment;
	/**
	 * 双斜线注释
	 */
	private String dcomment;
	/**
	 * 注释中带@注释的
	 */
	private Map<String, String> tags;
	
	private int level;

	public void addImport(String imp){
		if(this.imports==null){
			this.imports=new ArrayList<>();
		}
		imports.add(imp);
	}
	
	public void setComment(Comment comment){
		if(comment==null){
			return;
		}
		this.standComment=comment.getStandComment();
		this.tags=comment.getTags();
	}
	public String getStandComment() {
		if(standComment==null){
			return "";
		}
		return standComment;
	}
	public abstract void accept(Vistor vistor);
}
