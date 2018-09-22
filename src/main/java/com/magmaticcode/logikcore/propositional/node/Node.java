/**
 * @author  Abraham Lora.
 * @author  Daniel Gomez.
 */

package com.magmaticcode.logikcore.propositional.node;

import java.util.UUID;

import com.magmaticcode.logikcore.propositional.builder.FormulaBuilder;
import com.magmaticcode.logikcore.propositional.formula.Action;
import com.magmaticcode.logikcore.propositional.formula.Constant;
import com.magmaticcode.logikcore.propositional.formula.Formula;
import com.magmaticcode.logikcore.propositional.formula.Variable;

   /**
    * Esta clase abstracta categoriza y crea un ID propio
    * a las premisas ingresadas en el trascurso del programa.
	*
	*/

public abstract class Node {

	public Node master;   // Axioma.
	public UUID uniqueID; // ID por el cual se va a identificar el axioma.


    /**
     * Este método genera un número random
     * y se lo delega a UUID de la clase.
     *
     */
	public Node() {
		this.uniqueID = UUID.randomUUID();
	}

	/**
     * Método setter del uniqueID,
     * el cual permite cambiar el ID de un axioma.
     *
     * @param uniqueID
     *
     */
	public Node(UUID uniqueID) {
		this.uniqueID = uniqueID;
	}

	/**
		* @return uniqueID (ID)
		*/
	public UUID getUniqueID() {
		return uniqueID;
	}

	/**
		* Método setter de la variable master(el axioma),
		* este método te permite cambiarle el
		* valor al Axioma con otro Axioma.
		*
		* @param node
		* @return node
		*
		*/
	public Node setMaster(Node node) {
		master = node;
		return this;
	}

	/**
		* @return master (axioma).
		*/
	public Node getMaster() {
		return master;
	}

	/**
		* Este método convierte los axiomas tipo nodos
		* a un formula de la clase Formula.
		*
		* @return Formula
		*/
	public Formula toFormula() {
		return isFormula() ? (Formula) this : FormulaBuilder.getBuilder().newFormula(this, uniqueID);
	}

	/**
	 * Este método retorna false
	 * dependiendo si el axioma lo acompaña
	 * un constante (a-z)
	 *
	 * @return boolean
	 *
	 */
	public boolean isConstant() {
		return false;
	}

	/**
	 * @return constant (a-z)
	 */
	public Constant getConstant() {
		return isConstant() ? (Constant) this : null;
	}

	/**
	 * Este método  retorna false o true
	 * si la el axioma está catalogada
	 * en la clase Constan.
	 *
	 * @return boolean
	 */
	public boolean isVariable() {
		return false;
	}

	/**
	 * Este método revisa si el objeto
	 * posee una varible.
	 *
	 * @return constant (constante)
	 */
	public Variable getVariable() {
		return isVariable() ? (Variable) this : null;
	}

	/**
	 * Este método retorna true o false
	 * si en la varible es o no de tipo
	 * formula.
	 *
	 * @return boolean
	 */
	public boolean isFormula() {
		return false;
	}

	/**
	 * Este método retorna la varible
	 * contenida en la clase Nodo.
	 *
	 * @return Formula
	 */
	public Formula getFormula() {
		return isFormula() ? (Formula) this : null;
	}

	/**
	 * Este método revisa si la varible tipo
     * Nodo posee un operador en la primera
     * posición (0) de cadena.
     *
	 * @return Action
	 */
	public boolean isAction() {
		
		if(isFormula())
			if(getFormula().isAtomic())
				return getFormula().getNode(0).isAction();
		
		return false;
	}


	/**
     * @return Action (operador).
     */
	public Action getAction() {
		return isAction() ? (Action) this : ActionType.UNKNOW.getAction();
	}

	/**
     * Este método clona el Nodo
     *
     * @return Node
     */
	public abstract Node clone();

	/**
     * Este método retorna true o false
     * si el Nodo posee operador.
     *
     * @return boolean.
     */
	public abstract boolean workWithAttaches();

	/**
     * Este método retorna true o false
     * si el Nodo esta acotado o posse
     * un paréntesis.
     *
     * @return boolean
     */
	public abstract boolean workWithParenthisis();

	/**
     * Este método retorna true o false
     * si el Node posee un operador, el
     * cual se puede simplificar o expandir.
     *
     * @return boolean
     */
	public abstract boolean canConvert();

	/**
     * Este método recibe un objeto tipo Object
     * y crea un objeto de tipo Nodo con la finlidad
     * de comparar los componetes entre Nodos, como su
     * ID - variables - constantes - operadores - Fomula.
     *
     * @param object
     * @return boolean
     */
	@Override
	public boolean equals(Object object) {
		
		if(object instanceof Node) {
			
			Node node = (Node) object;
			
			if(node.getUniqueID().equals(uniqueID))
				return true;
			
			if(node.isVariable() && isVariable())
				return getVariable().equals(node.getVariable());
			
			if(node.isConstant() && isConstant())
				return getConstant().equals(node.getConstant());
			
			if(node.isAction() && isAction())
				return getAction().equals(node.getAction());
			
			if(node.isFormula() && isFormula())
				return getFormula().equals(node.getFormula());
			
		}
		
		return false;
		
	}

	/**
     * En este método estan enlistados todos los tipos
     * de operadores logicas con el fin de construir un
     * Nodo y caracterizarlo con un enumerador. Por lo
     * que puede retornar los objetos de un Nodo como
     * la Formula, su identificador o su operador lógico.
     */
	public static enum ActionType {
		
		UNKNOW("§", true),
		NO("~", true),
		
		UNIVERSAL_QUANTIFIER("∀", true),
		EXISTENTIAL_QUANTIFIER("∃", true),
		
		AND("&", false),
		OR("|", false),
		SO("→", false),
		BICONDITIONAL("\u2194", false),
		
		;
		
		private String identifier;
		private boolean attached;

		/**
         * Método constructor de la clase ActionType, sus
         * variables son de tipo Nodo.
         *
         * @param identifier,attached
         * @param attached
         */
		ActionType(String identifier, boolean attached){
			
			this.identifier = identifier;
			this.attached = attached;
			
		}

        /**
         * Este metodo retorna un true o un false
         * si el Nodo posee un operador.
         *
         * @return boolean
         */
		public boolean isAttached() {
			return attached;
		}

        /**
         * Este método retorna el identificador del Nodo.
         * @return String
         */
		public String getIdentifier() {
			return identifier;
		}

        /**
         * Este método retorna como un String el identificador
         * numerico del Nodo.
         * @return String
         */
		public String getFormula() {
			return String.valueOf(identifier);
		}

        /**
         * Este método permite obtener el operador del objeto Nodo.
         * @return Action
         */
		public Action getAction() {
			return FormulaBuilder.getBuilder().newAction(this);
		}

        /**
         * Este método verifica si los parametros
         * dados son iguales al parametro de la
         * instancia.
         *
         * @param types
         * @return boolean
         */
		public boolean is(ActionType ... types) {
			
			for(ActionType type : types)
				if(this == type)
					return true;
			
			return false;
			
		}
		
	}

	/**
     * Este método puede retornar las constantes y
     * las varibles de un Nodo.
     */
	public static enum ConstantType {
		
		A,
		B,
		C,
		D,
		E,
		F,
		G,
		H,
		I,
		J,
		K,
		L,
		M,
		N,
		O,
		P,
		Q,
		R,
		S,
		T,
		U,
		V,
		W,
		
		X,
		Y,
		Z;

        /**
         * @return Varible (X-Y-Z).
         */
		public boolean isVar() {
			return this == X || this == Y || this == Z;
		}

        /**
         * @return constant (a-w).
         */
		public boolean isConst() {
			return this != X && this != Y && this != Z;
		}

        /**
         * @return constant (varible).
         */
		public char getIdentifier() {
			return this.name().toLowerCase().charAt(0);
		}

        /**
         * @return Constant (a-z)
         */
		public Constant getConstant() {
			return new Constant(getIdentifier());
		}

        /**
         * @param constant
         * @return type (varible del parámetro).
         *
         */
		public static ConstantType valueOf(char constant) {
			
			for(ConstantType type : ConstantType.values())
				if(type.name().equalsIgnoreCase(String.valueOf(constant)))
					return type;
			
			return null;
			
		}
		
	}
	
}
