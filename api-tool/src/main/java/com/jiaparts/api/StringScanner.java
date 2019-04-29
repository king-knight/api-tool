package com.jiaparts.api;

import com.jiaparts.api.token.Separator;

public class StringScanner {
	private String src;
	private int currentIdx = -1;

	public StringScanner(String src) {
		this.src = src;
	}

	public boolean hasNext() {
		return currentIdx < src.length() - 1;
	}

	public char next() {
		return src.charAt(++currentIdx);
	}
	
	public void goback(){
		currentIdx--;
	}
	
	public char getPre() {
		return src.charAt(currentIdx - 1);
	}

	public char getAfter() {
		return src.charAt(currentIdx + 1);
	}

	public String nextLine() {
		String s = "";
		while (currentIdx < src.length() - 1) {
			char ch = src.charAt(++currentIdx);
			if (ch == '\r') {
				break;
			}
			s += ch;
		}
		return s;
	}

	public String scanTo(char c) {
		String s = "";
		while (currentIdx < src.length() - 1) {
			currentIdx++;
			char ch = src.charAt(currentIdx);
			if (ch == c) {
				break;
			}
			if(Separator.inPairEnd(c) && Separator.isPair(ch, c)){//嵌入式递归扫描配对符号
				char end = Separator.getDelimiterPairEnd(ch);
				if(end!='0'){
					s+=ch+scanTo(end);
					continue;
				}
			}
			s += ch;
		}
		return s;
	}

	public void skipTo(char c){
		while (currentIdx < src.length() - 1) {
			char ch = src.charAt(++currentIdx);
			if (ch == c) {
				break;
			}
			if(ch=='"'){//引号开头的跳到引号结尾，排除字符串变量影响结果
				skipTo('"');
			}
			if(Separator.inPairEnd(c)){//嵌入式递归扫描配对符号
				char end = Separator.getDelimiterPairEnd(ch);
				if(end!='0'){
					skipTo(end);
					continue;
				}
			}
		}
	}
	
	public String scanToNotEsCape(char c) {
		String s = "";
		while (currentIdx < src.length()-1) {
			currentIdx++;
			char ch = src.charAt(currentIdx);
			if (ch == c) {
				if (currentIdx - 1 >= 0 && src.charAt(currentIdx - 1) == '\\') {

				} else {
					break;
				}
			}
			s += ch;
		}
		return s;
	}
	
	/**
	 * 预测第一个非空白字符
	 * @param c
	 * @return
	 */
	public boolean isNextWith(char c){
		int idx=currentIdx;
		while(idx<src.length()-1){
			char ch = src.charAt(++idx);
			if(Character.isWhitespace(ch)){
				continue;
			}
			if(ch==c){
				return true;
			}
			break;
		}
		return false;
	}

	public char nextStop(char... chars) {
		int idx=currentIdx;
		while(idx<src.length()-1){
			char ch = src.charAt(++idx);
			if(Character.isWhitespace(ch)){
				continue;
			}
			for(char c :chars ){
				if(ch==c){
					return ch;
				}
			}
		}
		return 0;
		
	}


}
