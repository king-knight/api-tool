package com.jiaparts.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;

import com.jiaparts.api.componet.InterfaceUnit;
import com.jiaparts.api.parser.SyntaxParse;
import com.jiaparts.api.vistor.MarkDownVistor;
import com.jiaparts.api.vistor.Vistor;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class MarkDownGenerator {
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		ResourceBundle bundle=ResourceBundle.getBundle("settings");
		String project = bundle.getString("project");
		ResourceBundle pb = ResourceBundle.getBundle(project);
		String[] services = pb.getString("services").split(",");
		String result="";
		SourceSearcher searcher = new SourceSearcher();
		for(String s:services){
			String src = searcher.searchAsString(s);
			SyntaxParse p=new SyntaxParse();
			InterfaceUnit unit = p.parseInterface(src);
			Vistor vistor = new MarkDownVistor();
			vistor.vistor(unit);
			result+=vistor.getResult();
		}
		write(result);
		long end = System.currentTimeMillis();
		System.err.println("耗时:"+(end-start));
	}
	
	
	private static void write(String result){
		ResourceBundle bundle=ResourceBundle.getBundle("settings");
		String project = bundle.getString("project");
		ResourceBundle pb = ResourceBundle.getBundle(project);
		String pandocPath = bundle.getString("pandocPath");
		String mdPath=pandocPath+"/temp.md";
		//生成md文件
		File f=new File(mdPath);
		try {
			OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(f), "utf-8");
			writer.write(result);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String filePath = pb.getString("filePath");
		try {
			filePath=new String(filePath.getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String command="pandoc -f markdown_github -t docx "+ mdPath+" -o "+filePath;
		log.info("开始执行命令:{}",command);
		try {
			 Process p = Runtime.getRuntime().exec(command);
			 p.waitFor();
			 log.info("执行完毕.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
