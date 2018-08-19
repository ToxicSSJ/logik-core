package com.magmaticcode.logikcore.propositional.exception.formula;

import com.magmaticcode.logikcore.propositional.exception.PropositionalLogicException;

public class ConstantException extends PropositionalLogicException {

	private static final long serialVersionUID = 6370514341375087631L;

	private ConstantExceptionType type;
	private String description = "Unknow";
	
	public ConstantException(ConstantExceptionType type) {
		super(type.getMessage());
		
		this.type = type;
		
	}
	
	public ConstantException(ConstantExceptionType type, String description) {
		super(description);
		
		this.type = type;
		this.description = description;
		
	}
	
	public ConstantExceptionType getType() {
		return type;
	}

	public void setType(ConstantExceptionType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static enum ConstantExceptionType {
		INVALID_CHAR("El caracter es invalido.")
		;
		
		private String message;
		
		ConstantExceptionType(String message){
			
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
