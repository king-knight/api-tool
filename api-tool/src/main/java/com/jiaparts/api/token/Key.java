package com.jiaparts.api.token;

public enum Key {
	PACKAGE("package"),
	IMPORT("import"),
	CLASS("class"),
	INTERFACE("interface"),
	ENUM("enum"),
	CONST("const"),
	ASSERT("assert"),
	PUBLIC("public"),
	PRIVATE("private"),
	PROTECTED("protected"),
	DEFAULT("default"),
	ABSTRACT("abstract"),
	EXTENDS("extends"),
	IMPLEMENTS("implements"),
	NATIVE("native"),
	VOLATILE("volatile"),
	STATIC("static"),
	TRANSIENT("transient"),
	FINAL("final"),
	IF("if"),
	ELSE("else"),
	NEW("new"),
	SUPER("super"),
	WHILE("while"),
	FOR("for"),
	DO("do"),
	RETURN("return"),
	STRICTFP("strictfp"),
	CONTINUE("continue"),
	BREAK("break"),
	SWITCH("switch"),
	CASE("case"),
	TRY("tyr"),
	CATCH("catch"),
	FINALLY("finally"),
	INSTANCEOF("instanceof"),
	SYNCHRONIZED("synchronized"),
	;
	private String name;
	private Key(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public boolean eq(String id){
		return name.equals(id);
	}
}
