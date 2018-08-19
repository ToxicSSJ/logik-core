package com.magmaticcode.logikcore.propositional.bundle.proof;

public class ProofResult {
	
	private ResultType type;
	private String[] args;
	
	public ProofResult(ResultType type, String...args) {
		
		this.setType(type);
		this.setArgs(args);
		
	}
	
	public ResultType getType() {
		return type;
	}

	public void setType(ResultType type) {
		this.type = type;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public static enum ResultType {
		
		BAD_RULE,
		INCORRECT_SYNTAX,
		CORRECT,
		
		;
		
	}
	
}
