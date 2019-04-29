package com.jiaparts.api;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.jiaparts.api.componet.InterfaceUnit;
import com.jiaparts.api.parser.SyntaxParse;
import com.jiaparts.api.token.Key;
import com.jiaparts.api.token.Separator;
import com.jiaparts.api.util.StringUtils;
import com.jiaparts.api.vistor.ConsoleVistor;
import com.jiaparts.api.vistor.MarkDownVistor;
import com.jiaparts.api.vistor.Vistor;

@SuppressWarnings("all")
public class AppTest {
	

	@Test
	public void testMarkDown(){
		long start = System.currentTimeMillis();
		SourceSearcher searcher = new SourceSearcher();
		String s= searcher.searchAsString("com.jiaparts.oc.service.order.OrderService");		
		SyntaxParse p=new SyntaxParse();
		InterfaceUnit unit = p.parseInterface(s);
		Vistor vistor = new MarkDownVistor();
		vistor.vistor(unit);
		System.err.println(vistor.getResult());
		System.err.println("耗时:"+(System.currentTimeMillis()-start));
	}
	
	@Test
	public void testInitMap(){
		Separator.delimiterPair.values().forEach(System.out::println);
	}
	
	@Test
	public void testSyntax(){
		long start = System.currentTimeMillis();
		SourceSearcher searcher = new SourceSearcher();
		String s= searcher.searchAsString("com.jiaparts.jsb.service.logistics.LogisticsService");		
		SyntaxParse p=new SyntaxParse();
		InterfaceUnit unit = p.parseInterface(s);
		ConsoleVistor vistor = new ConsoleVistor();
		vistor.vistor(unit);
		System.err.println("耗时:"+(System.currentTimeMillis()-start));
	}

	@Test
	public void testByte() {
		//com.jiaparts.jsb.service.send.SendManageService
		InterfaceUnit unit = new InterfaceUnit();
		SourceSearcher searcher = new SourceSearcher();
		byte[] bs = searcher.searchAsByte("com.jiaparts.jsb.service.send.msg.SDM010Resp");
		String s = new String(bs);
		StringScanner sc = new StringScanner(s);
		String id = "";
		String comment = "";
		Map<String, String> commentTag = new HashMap<>();// 注释中以 @开头的
		List<String> annoTags = new ArrayList<>();// 注解
		String modify = "";
		String fm="";
		
		while (sc.hasNext()) {
			char c = sc.next();
			if (Character.isWhitespace(c)) {
				if (StringUtils.isBlank(id)) {
					continue;
				}
				
				if (Key.PACKAGE.eq(id)) {
					// 获取package
					String pack = sc.scanTo(';').trim();
					unit.setPack(pack);
				} else if (Key.IMPORT.eq(id)) {
					String imp = sc.scanTo(';').trim();
					unit.addImport(imp);
				} else if (StringUtils.isModify(id)) {
					modify += id + " ";
				} else if (Key.CLASS.eq(id) || Key.INTERFACE.eq(id)) {
					// 读取类信息
					String classInfo = sc.scanTo('{');
					System.err.println(classInfo);
					comment = "";
					commentTag.clear();
					annoTags.clear();
					modify = "";
				}  else {
					fm+=id+" ";
				}
				id = "";
			}else if(c=='<'){//泛型
				String gen = sc.scanTo('>');
				System.err.println("泛型:"+gen);
			}else if (c=='(') {//方法
				String param = sc.scanTo(')');
				System.err.println("方法:"+id);
				System.err.println("返参:"+fm);
				System.err.println("入参:"+param);
				//抽象方法与实现方法处理
				if(sc.isNextWith(';')){
					sc.skipTo(';');
				}else if(sc.isNextWith('{')){
					sc.skipTo('}');//后续待完善
				}
				comment = "";
				commentTag.clear();
				annoTags.clear();
				modify = "";
				fm="";
				id="";
			}else if(c=='='){//字段
				sc.scanTo(';');
				System.err.println("字段1:"+fm+id);
				modify = "";
				fm="";
				id="";
			}else if(c==';'){//字段
				System.err.println("字段2:"+fm+id);
				modify = "";
				fm="";
				id="";
			} else if (c == '@') {// 注解
				String annoName = "";// 注解名
				// 处理注解
				boolean flag = false;// 是否遇到左括号
				while (true) {
					char anno = sc.next();
					if (Character.isWhitespace(anno)) {
						break;
					}
					if (anno == '(') {
						// 读取直到')'
						sc.skipTo(')');// 简单处理,实际要排除括号中包含括号的情况( ..( ) )
						flag = true;
						break;
					}
					annoName += anno;
				}
				if (!flag) {// 未遇到左括号继续读取,
					while (true) {
						char ss = sc.next();
						if (Character.isWhitespace(ss)) {
							continue;
						}
						if (ss == '(') {
							sc.skipTo(')');
							break;
						}
						sc.goback();
						break;
					}
				}
				annoTags.add(annoName);
			} else if (c == '/') {// 注释
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
					}
				}
			} else {
				id += c;
			}
		}
		System.err.println(unit.getPack());
		System.err.println(unit.getImports());
	}

	@Test
	public void testIdx() {
		System.err.println("@Description: 保存/编辑托运单   返回参数".substring(1, 13));
	}

	@Test
	public void testReg() {
		String p = "public abstract ResponseMsg<SDM001Resp> sdm001(RequestMsg<SDM001Req> req);";
		System.err.println(p.matches("^[a-zA-Z_].*"));
	}

	@Test
	public void testParser() {
		

	}

	@Test
	public void testListRegex() {
		String regex = "List<\\s*((\\w|.)+)\\s*>";
		String f = "List";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(f);
		while (m.find()) {
			System.err.println(m.group());
			System.err.println(m.group(1));

		}
	}

	@Test
	public void testFieldRegex() {
		String regex = "(?:private|protected|public)?\\s*(static\\s+)?(?:final\\s+)?(\\w+|\\.+)(\\s*<(\\w+|\\.+)\\s*>)?\\s*(\\w+)\\s*(?:=?\\s*\\w+)\\s*;$";
		String f = "private static final long serialVersionUID = 1L;";
		// String f="List<LogisticsGroup> list;";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(f);
		while (m.find()) {
			System.err.println(m.group());
			System.err.println("static:" + m.group(1));
			System.err.println("类型读取:" + m.group(2));
			System.err.println("泛型读取:" + m.group(4));
			System.err.println("变量名读取:" + m.group(5));

		}
	}

	@Test
	public void testCommBeanRegex() {
		String regex = "public\\s+class\\s+(\\w+)(\\s*<(\\w+)>)?\\s+(extends\\s+(\\w+)(\\s*<(\\w+)>)?)?(?:\\s*implements\\s+\\w+)?";
		String classline = "public class QueryResponseMsg<T> extends ResponseMsg<T>";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(classline);
		while (m.find()) {
			System.err.println(m.group(1));
			System.err.println(m.group(2));
			System.err.println(m.group(3));
			System.err.println(m.group(4));
			System.err.println(m.group(5));
			System.err.println(m.group(6));
			System.err.println(m.group(7));

		}
	}

	@Test
	public void testJavaBeanRegex() {
		String regex = "public\\s+class\\s+(\\w+)\\s+(extends\\s+(\\w+)){0,1}(?:\\s+implements\\s+\\w+){0,1}";
		String classline = "public class WayBillReturnSimple extends WayBillSimpleBase implements xxxx";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(classline);
		while (m.find()) {
			System.err.println(m.group(1));
			System.err.println(m.group(2));
			System.err.println(m.group(3));

		}
	}

	@Test
	public void testGenericClassRegex() {
		String regex = "public\\s+class\\s+(\\w+)\\s*<(\\w+)>\\s+(implements|extends)\\s+(\\w+)(?:<T>){0,1}";
		String classline = "public class QueryResponseMsg<T> extends ResponseMsg<T>";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(classline);
		while (m.find()) {
			System.err.println(m.group(1));
			System.err.println(m.group(2));
			System.err.println(m.group(3));
			System.err.println(m.group(4));
		}
	}

	@Test
	public void testRegex() {
		String regex = "(?:public\\s+){0,1}(?:abstract\\s+){0,1}(\\w+)\\s*<\\s*(\\w+)\\s*>\\s*(\\w+)\\s*[(]\\s*(\\w+)<(\\w+)>\\s+\\w+[)]\\s*;$";
		String method = "ResponseMsg<SDM002Resp> sdm002(RequestMsg<SDM002Req> req);";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(method);
		while (m.find()) {
			System.err.println(m.group(1));
			System.err.println(m.group(2));
			System.err.println(m.group(3));
			System.err.println(m.group(4));
			System.err.println(m.group(5));
		}
	}

	@Test
	public void testReplace() {
		System.err.println("* abc*".replaceFirst("[*]", ""));
	}
}
