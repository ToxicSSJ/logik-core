package com.pwrteam.logikcore.propositional.builder.parser;

public enum ParserStatus {

	IN_FORMULA,
	IN_ATTACHED,
	IN_VARIABLE,
	IN_VARIABLE_CONSTANTS,
	IN_ACTION,
	
	IN_CLOSE_BRACKETS,
	IN_OPEN_BRACKETS,
	
	IN_EXISTENTIAL_QUANTIFIER,
	IN_UNIVERSAL_QUANTIFIER,
	
	NULL,
	
	;
	
}
