package com.jiaparts.api;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.jiaparts.api.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class SourceSearcher {
	
	private List<String>baseDirs;
	private static Map<String,byte[]>cache=new HashMap<>();
	
	public SourceSearcher(){
		ResourceBundle bundle = ResourceBundle.getBundle("settings");
		String project = bundle.getString("project");
		ResourceBundle pb = ResourceBundle.getBundle(project);
		String dirs = pb.getString("proDirs");
		if(StringUtils.isBlank(dirs)){
			
			throw new RuntimeException("未配置项目路径");
		}
		baseDirs=new ArrayList<>();
		String[] split = dirs.split(",");
		for(String dir:split){
			baseDirs.add(dir+"/src/main/java/");
		}
	}
	
	public JavaSource search(String className){
		if(StringUtils.isBlank(className)){
			return null;
		}
		String fileName=className.replace(".", "/")+".java";
		for(String baseDir:baseDirs){
			String path=baseDir+fileName;
			File f=new File(path);
			if(f.exists()){
				log.debug("找到源文件:{}",path);
				try {
					List<String> allLines = Files.readAllLines(f.toPath());
					JavaSource source=new JavaSource();
					source.setSrcLines(allLines);
					source.setClassName(className);
					return source;
				} catch (IOException e) {
					throw new RuntimeException("读取源文件失败!");
					
				}
				
			}
		}
		return null;
	}
	
	public byte[] searchAsByte(String className){
		if(StringUtils.isBlank(className)){
			return null;
		}
		byte[] b = cache.get(className);
		if(b!=null){
			log.debug(className+"的源文件查找命中缓存");
			return b;
		}
		String fileName=className.replace(".", "/")+".java";
		for(String baseDir:baseDirs){
			String path=baseDir+fileName;
			File f=new File(path);
			if(f.exists()){
				log.debug("找到源文件:{}",path);
				try {
					byte[] bytes = Files.readAllBytes(f.toPath());
					cache.put(className, bytes);
					return bytes;
				} catch (IOException e) {
					throw new RuntimeException("读取源文件失败!");
					
				}
				
			}
		}
		return null;
	}
	
	public String searchAsString(String className){
		byte[] bytes = searchAsByte(className);
		if(bytes==null){
			return null;
		}
		try {
			return new String(bytes,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
