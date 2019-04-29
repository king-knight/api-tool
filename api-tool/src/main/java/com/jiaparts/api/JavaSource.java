package com.jiaparts.api;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JavaSource {
	/**
	 * 按行解析的源码
	 */
	private List<String>srcLines;
	/**
	 * 当前行
	 */
	private int currentSrcIndex=-1;
	
	private String className;
	/**
	 * 是否存在下一行
	 * @return
	 */
	public boolean hasNextLine(){
		return this.currentSrcIndex<(srcLines.size()-1);
	}
	
	public String nextLine(){
		currentSrcIndex++;
		return srcLines.get(currentSrcIndex);
	}
	public void goBack(int step){
		currentSrcIndex-=step;
	}
	
}
