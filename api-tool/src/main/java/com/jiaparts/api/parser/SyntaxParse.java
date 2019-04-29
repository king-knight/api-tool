package com.jiaparts.api.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jiaparts.api.SourceSearcher;
import com.jiaparts.api.StringScanner;
import com.jiaparts.api.componet.BaseUnit;
import com.jiaparts.api.componet.Comment;
import com.jiaparts.api.componet.ComplexFieldUnit;
import com.jiaparts.api.componet.FieldUnit;
import com.jiaparts.api.componet.InterfaceUnit;
import com.jiaparts.api.componet.ListFieldUnit;
import com.jiaparts.api.componet.MethodUnit;
import com.jiaparts.api.componet.PrimaryFieldUnit;
import com.jiaparts.api.token.Key;
import com.jiaparts.api.util.ResourceUtil;
import com.jiaparts.api.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
/**
 * 语法分析器
 * @author yuanw
 *
 */
@Slf4j
public class SyntaxParse {
	private SourceSearcher searcher=new SourceSearcher();
	private final String[]primaryType={"byte","short","int","char","long","float","double","boolean","Byte","Short","Integer","Character","BigDecimal","String","Long"};
	
	private boolean isPrimary(String SimpleClassName){
		if(StringUtils.inArray(SimpleClassName, primaryType)){
			return true;
		}
		return false;
	}
	
	public InterfaceUnit parseInterface(String source) {
		InterfaceUnit unit = new InterfaceUnit();
		SourceProcessor<InterfaceUnit>processor=new SourceProcessor<InterfaceUnit>(){
			@Override
			public void processInterfaceMethod(String reqParam, Context ctx, InterfaceUnit t) {
				ctx.sc.skipTo(';');
				String methodName = ctx.indentify;
				String resp=ctx.fm.trim();
				String respGeneric = ctx.generic;
				Map<String, String> annoMap = ctx.annoMap;
				//过滤版本
				String version = ResourceUtil.getString("version");
				if(!"*".equals(version)){
					if(!annoMap.containsKey("ApiDoc")){
						log.debug("方法{}版本不匹配将被忽略",methodName);
						return;
					}
					String[] split = annoMap.get("ApiDoc").split(",");
					for(String s:split){
						if(s.trim().startsWith("version")){
							String[] kv = s.split("=");
							String methodVersion=kv[1].trim();
							if(methodVersion.startsWith("ApiDoc.version_")){
								methodVersion=methodVersion.substring("ApiDoc.version_".length()).replace("_", ".");
							}else{
								methodVersion=methodVersion.substring(1, methodVersion.length()-1);
							}
							if(!version.equals(methodVersion)){
								log.debug("方法{}的版本为{}将被忽略",methodName,methodVersion);
								return;
							}
							break;
						}
					}
				}
				//解析返参
				ComplexFieldUnit respUnit= parseWithSimple(resp,respGeneric,unit);
				MethodUnit mu=new MethodUnit();
				unit.addMethod(mu);
				mu.setMethodName(methodName);
				mu.setComment(getComment(ctx));
				mu.setOut(respUnit);
				//解析入参
				StringScanner pc=new StringScanner(reqParam);
				String req = pc.scanTo('<').trim();
				String reqGeneric = pc.scanTo('>').trim();
				ComplexFieldUnit reqUnit = parseWithSimple(req, reqGeneric, unit);
				mu.setIn(reqUnit);
			}

			@Override
			public void processInterface(Context ctx, InterfaceUnit t) {
				// 读取接口信息
				String interfaceInfo = ctx.sc.scanTo('{');
				t.setComment(getComment(ctx));
				t.setInterfaceName(interfaceInfo);
			}
			
		};
		processor.processSourceInfo(source, unit);
		return unit;
	}

	
	public ComplexFieldUnit parseComplexClass(String source) {
		ComplexFieldUnit unit=new ComplexFieldUnit();
		SourceProcessor<ComplexFieldUnit>processor=new SourceProcessor<ComplexFieldUnit>(){
			@Override
			public void processBeanMethod(Context ctx, ComplexFieldUnit t) {
				String methodName = ctx.indentify;
				log.debug("解析到方法:"+methodName);
				ctx.sc.skipTo('}');
			}

			@Override
			public void processField(Context ctx, ComplexFieldUnit t) {
				List<String> annoTags = ctx.annoTags;
				String type = ctx.fm.trim();
				if(StringUtils.isBlank(type)){
					return;
				}
				String fieldName = ctx.indentify.trim();
				String[] split = ctx.fm.trim().split(" ");
				if(split.length==2){
					type=split[0];
					fieldName=split[1];
				}
				String modify = ctx.modify;
				String generic = ctx.generic;
				if(annoTags.contains("JsonIgnore")){
					log.debug("解析到@JsonIgnore注解忽略字段:{} {}",type,fieldName);
					return;
				}
				log.debug("解析到字段:{} {} {}",modify,type,fieldName);
				if(modify.contains("static")||modify.contains("final")){
					log.debug("忽略字段:{} {} {}",modify,type,fieldName);
					return;
				}
				
				FieldUnit field = parseField(type, fieldName, generic, unit);
				Comment comment = getComment(ctx);
				field.setComment(comment);
				t.addFieldIn(field);
			}

			@Override
			public void processClass(Context ctx, ComplexFieldUnit t) {
				// 读取类信息
				String classInfo = ctx.sc.scanTo('{').trim();
				log.debug("解析到类信息:{}",classInfo);
				classInfo+=" ";
				StringScanner sc=new StringScanner(classInfo);
				String className="";
				String generic="";
				String identify="";
				String superName="";
				String superGeneric="";
				while(sc.hasNext()){
					char c = sc.next();
					if(Character.isWhitespace(c)){//空格分隔符
						if(StringUtils.isBlank(identify)){
							continue;
						}
						if(Key.EXTENDS.eq(identify)){
							
						}else if(Key.IMPLEMENTS.eq(identify)){
							break;
						}else{
							if(StringUtils.isBlank(className)){
								className=identify;
							}else{
								superName=identify;
							}
						}
						identify="";
					}else if(c=='<'){//泛型分隔符
						String gen = sc.scanTo('>');
						if(StringUtils.isBlank(generic)){
							generic=gen;
							className=identify;
						}else{
							superGeneric=gen;
							superName=identify;
						}
						identify="";
					}else{
						identify+=c;
					}
				}
				log.debug("类:{},泛型:{},父类:{},父类泛型:{}",className,generic,superName,superGeneric);
				//解析父类
				if(StringUtils.isNotBlank(superName)){
					SyntaxParse parse=new SyntaxParse();
					ComplexFieldUnit superUnit = parse.parseComplexClass(searchNestSource(superName, unit));
					List<FieldUnit> inFields = superUnit.getInFields();
					//将父类字段先设入
					unit.setInFields(inFields);
				}
			}
			
		};
		processor.processSourceInfo(source, unit);
		return unit;
	}

	
	
	
	/**
	 * 解析源文件过程中的上下文
	 * @author yuanw
	 *
	 */
	private class Context {
		public String indentify = "";
		public String comment = "";
		public Map<String, String> commentTag = new LinkedHashMap<>();// 注释中以 @开头的
		public List<String> annoTags = new ArrayList<>();// 注解
		public Map<String,String>annoMap=new HashMap<>();
		public String modify = "";
		public String fm = "";
		public String generic="";
		public StringScanner sc;
		
		
		public void clear() {
			comment = "";
			commentTag=new LinkedHashMap<>();
			annoTags=new ArrayList<>();
			annoMap=new HashMap<>();
			modify = "";
			fm = "";
			indentify = "";
			generic="";
		}
	}
	
	/**
	 * 解析器
	 * @author yuanw
	 *
	 * @param <T>
	 */
	private abstract class SourceProcessor<T extends BaseUnit> {
		
		public ComplexFieldUnit parseWithSimple(String simpleClass,T t){
			String src = searchNestSource(simpleClass, t);
			SyntaxParse parse=new SyntaxParse();
			return parse.parseComplexClass(src);
		}
		
		public ComplexFieldUnit parseWithSimple(String simple,String genericSimple,T t){
			ComplexFieldUnit unit=parseWithSimple(simple,t);
			ComplexFieldUnit generic = parseWithSimple(genericSimple,t);
			unit.replaceGenericField("T", generic);
			return unit;
		}
		
		public String searchNestSource(String simpleClassName,T t) {
			List<String> imports = t.getImports();
			String className="";
			if(imports!=null){
				for(String imp:imports){
					if(imp.endsWith("."+simpleClassName)&&!imp.startsWith("java")){
						className=imp;
					}
				}
			}
			if(StringUtils.isBlank(className)){
				className=t.getPack()+"."+simpleClassName;
			}
			log.debug("开始查找源文件:{}",className);
			return searcher.searchAsString(className);
		}
		public FieldUnit parseField(String type,String fieldName,String generic,T t){
			type=type.trim();
			if (isPrimary(type)) {
				PrimaryFieldUnit unit = new PrimaryFieldUnit();
				unit.setFieldName(fieldName);
				unit.setFieldType(type);
				return unit;
			}
			if (type.equals("List")) {
				ListFieldUnit unit = new ListFieldUnit();
				unit.setFieldName(fieldName);
				unit.setFieldType("List");
				if (StringUtils.isNotBlank(generic)) {
					FieldUnit genericField = parseField(generic, null, null,t);
					unit.setGenericField(genericField);
				}
				return unit;
			}
			String source = searchNestSource(type, t);
			if(StringUtils.isNotBlank(source)){
				SyntaxParse parse=new SyntaxParse();
				ComplexFieldUnit unit = parse.parseComplexClass(source);
				unit.setFieldName(fieldName);
				return unit;
			}else{
				PrimaryFieldUnit unit = new PrimaryFieldUnit();
				unit.setFieldName(fieldName);
				unit.setFieldType(type);
				return unit;
			}
		}
		
		
		public Comment getComment(Context ctx){
			Comment mc=new Comment();
			mc.setStandComment(ctx.comment);
			mc.setTags(ctx.commentTag);
			return mc;
		}

		public void processSourceInfo(String source, T t) {
			StringScanner sc = new StringScanner(source);
			Context ctx = new Context();
			ctx.sc = sc;
			while (sc.hasNext()) {
				char c = sc.next();
				if (Character.isWhitespace(c)) {
					if (StringUtils.isBlank(ctx.indentify)) {
						continue;
					}
					if (Key.PACKAGE.eq(ctx.indentify)) {
						// 获取package
						String pack = sc.scanTo(';').trim();
						t.setPack(pack);
					} else if (Key.IMPORT.eq(ctx.indentify)) {
						String imp = sc.scanTo(';').trim();
						t.addImport(imp);
					} else if (StringUtils.isModify(ctx.indentify)) {
						ctx.modify += ctx.indentify + " ";
					} else if (Key.CLASS.eq(ctx.indentify)) {
						processClass(ctx, t);
						ctx.clear();
					} else if (Key.INTERFACE.eq(ctx.indentify)) {
						processInterface(ctx, t);
						ctx.clear();
					} else {
						ctx.fm += " " + ctx.indentify;
					}
					ctx.indentify = "";

				}else if(c=='<'){//泛型
					String gen = sc.scanTo('>');
					ctx.generic=gen;
					if(StringUtils.isNotBlank(ctx.indentify)){
						ctx.fm += " " + ctx.indentify;
					}
					ctx.indentify="";
				} else if (c == '(') {// 方法
					String reqParam = sc.scanTo(')');
					char stop=sc.nextStop(';','{');
					// 抽象方法与实现方法处理
					if (stop==';') {
						processInterfaceMethod(reqParam,ctx, t);
					} else if (stop=='{') {
						processBeanMethod(ctx, t);
					}else{
						throw new RuntimeException("解析方法出错!");
					}
					ctx.clear();
				} else if (c == '=') {// 字段
					sc.skipTo(';');
					processField(ctx, t);
					ctx.clear();
				} else if (c == ';') {// 字段
					processField(ctx, t);
					ctx.clear();
				} else if (c == '@') {// 注解
					processAnnotation(ctx, t);
				} else if (c == '/') {// 注释
					processComment(ctx, t);
				} else {
					ctx.indentify += c;
				}
			}
		}
		/**
		 * 处理接口
		 * 
		 * @param ctx
		 * @param t
		 */
		public void processInterface(Context ctx, T t) {
			
		}

		/**
		 * 处理类
		 * 
		 * @param ctx
		 * @param t
		 */
		public void processClass(Context ctx, T t) {
			
		}

		/**
		 * 处理接口方法
		 * @param reqParam 
		 * @param ctx
		 * @param t
		 */
		public void processInterfaceMethod(String reqParam, Context ctx, T t) {
			
		}

		/**
		 * 处理javabean方法
		 * 
		 * @param ctx
		 * @param t
		 */
		public void processBeanMethod(Context ctx, T t) {
			
		}

		/**
		 * 处理字段
		 * 
		 * @param ctx
		 * @param t
		 */
		public void processField(Context ctx, T t) {

		}

		public void processAnnotation(Context ctx, T t) {
			String annoName = "";// 注解名
			// 处理注解
			boolean flag = false;// 是否遇到左括号
			while (true) {
				char anno = ctx.sc.next();
				if (Character.isWhitespace(anno)) {
					break;
				}
				if (anno == '(') {
					// 读取直到')'
					String c = ctx.sc.scanTo(')');
					ctx.annoMap.put(annoName, c);
					flag = true;
					break;
				}
				annoName += anno;
			}
			if (!flag) {// 未遇到左括号继续读取,
				while (true) {
					char ss = ctx.sc.next();
					if (Character.isWhitespace(ss)) {
						continue;
					}
					if (ss == '(') {
						String c = ctx.sc.scanTo(')');
						ctx.annoMap.put(annoName, c);
						break;
					}
					ctx.sc.goback();
					break;
				}
			}
			ctx.annoTags.add(annoName);
		}

		public void processComment(Context ctx, T t) {
			StringScanner sc = ctx.sc;
			Map<String, String> commentTag = ctx.commentTag;
			String lastTag="";
			char after = sc.getAfter();
			if (after == '/') {// 双斜杠注释
				sc.next();
				String dcomment = sc.scanTo('\r');// 读取到行尾
				System.err.println(dcomment);
			} else if (after == '*') {// 多行注释
				sc.next();
				// 按行读取注释直到 */
				while (true) {
					String line = sc.nextLine().trim();
					if (StringUtils.isBlank(line)) {
						continue;
					}
					if (line.endsWith("*/")) {
						break;
					}
					if (line.startsWith("*")) {
						line = line.substring(1, line.length()).trim();
					}
					if(line.startsWith("@")){
						int idx=line.indexOf(" ");
						if(idx<0){
							lastTag=line.replace(":", "");
							commentTag.put(lastTag, "");
						}else{
							lastTag = line.substring(1, idx).replace(":", "");
							commentTag.put(lastTag, line.substring(idx+1).trim());
						}
					}else if(commentTag.isEmpty()){
						ctx.comment+=line;
					}else{
						String tagContent = commentTag.get(lastTag);
						commentTag.put(lastTag, tagContent+line);
					}
				}
			}
		}
	}

}
