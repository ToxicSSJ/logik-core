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
		* Método getter del uniqueID,
		* este perimte obtener ID.
		*
		* @return uniqueID
		*
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
		* Método getter de la variable master(el axioma),
		* este método permite obtener al axioma.
		*
		* @return master
		*
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
	 * Este método perimte obtener la(s)
	 * constante(s) que son acotadas por
	 * un axioma.
	 *
	 * @return constant
	 */
	public Constant getConstant() {
		return isConstant() ? (Constant) this : null;
	}

	/**
	 * Este método  retorna false o true
	 * si la el axioma es una es esta catalogada
	 * en la clase Constan.
	 *
	 * @return boolean
	 */
	public boolean isVariable() {
		return false;
	}

	/**
	 * Este método revisa si el objeto
	 * posee una varible, por la que
	 * la retornará (la variable por omisión
	 * es null).
	 *
	 * @return constant
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
     * Nodo es posee un operador en la primera (0)
     * posición de cadena.
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
     * Este método retorna el operador
     * que posee el Nodo, el cual por
     * omisión es UNKNOW (null).
     *
     * @return Action.
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
     * Este método recive un objeto tipo Object
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
     * En este método estan en listas todos los tipos
     * de operadores logicos con el fin de construir un
     * Nodo y caracterizarlo con un enumerador. Por lo
     * que puede retornar los objetos de un Nodo como
     * la Formula, su identificador o su operador logico.
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
         * Este método retorna una viarible sea X, Y o Z.
         * @return Varible
         */
		public boolean isVar() {
			return this == X || this == Y || this == Z;
		}

        /**
         * Este método retorna una constante a - w.
         * @return constant
         */
		public boolean isConst() {
			return this != X && this != Y && this != Z;
		}

        /**
         * Este método retorna el caracter que posee
         * la variable.
         *
         * @return constant
         */
		public char getIdentifier() {
			return this.name().toLowerCase().charAt(0);
		}

        /**
         * Este método retorna una constante con
         * el valor de un identificador.
         *
         * @return Constant.
         */
		public Constant getConstant() {
			return new Constant(getIdentifier());
		}

        /**
         * Este método retorna una varible la cual
         * posee la constante del parametro.
         *
         * @param constant
         * @return type
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
