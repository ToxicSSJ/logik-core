package com.magmaticcode.logikcore.propositional.exception.formula;

import com.magmaticcode.logikcore.propositional.exception.PropositionalLogicException;

public class VariableException extends PropositionalLogicException {

	private static final long serialVersionUID = 6370514341375087631L;

	private VariableExceptionType type;
	private String description = "Unknow";
	
	public VariableException(VariableExceptionType type) {
		super(type.getMessage());
		
		this.type = type;
		
	}
	
	public VariableException(VariableExceptionType type, String description) {
		super(description);
		
		this.type = type;
		this.description = description;
		
	}
	
	public VariableExceptionType getType() {
		return type;
	}

	public void setType(VariableExceptionType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static enum VariableExceptionType {
		INVALID_CHAR("El caracter es invalido.")
		;
		
		private String message;
		
		VariableExceptionType(String message){
			
			this.message = message;
			
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
	}
	
}
