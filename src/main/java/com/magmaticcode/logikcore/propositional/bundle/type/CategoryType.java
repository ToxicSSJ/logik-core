package com.magmaticcode.logikcore.propositional.bundle.type;

public enum CategoryType {
	
	IMPLICATION_RULE,
	REPLACEMENT_RULE,
	QUANTIFIER_GENERALIZATION_RULE,
	QUANTIFIER_INSTANTIATION_RULE,
	
	;
	
	public static enum RuleType {
		
		CONJUNCTION(CategoryType.IMPLICATION_RULE, "CONJ"),
		ADDITION(CategoryType.IMPLICATION_RULE, "ADD"),
		SIMPLIFICATION(CategoryType.IMPLICATION_RULE, "SIMP"),
		
		MODUS_PONENDO_PONENS(CategoryType.IMPLICATION_RULE, "MP"),
		MODUS_PONENDO_TOLLENS(CategoryType.IMPLICATION_RULE, "MT"),
		DISJUNCTIVE_SYLLOGISM(CategoryType.IMPLICATION_RULE, "DS"),
		HYPOTETICAL_SYLLOGISM(CategoryType.IMPLICATION_RULE, "HS"),
		CONSTRUCTIVE_DILEMA(CategoryType.IMPLICATION_RULE, "CD"),
		
		TAUTOLOGY(CategoryType.REPLACEMENT_RULE, "TAUT"),
		COMMUTATIVITY(CategoryType.REPLACEMENT_RULE, "COM"),
		TRANSPOSITION(CategoryType.REPLACEMENT_RULE, "TRANS"),
		DISTRIBUTION(CategoryType.REPLACEMENT_RULE, "DIST"),
		ASSOCIATIVITY(CategoryType.REPLACEMENT_RULE, "ASSOC"),
		DOUBLE_NEGATION(CategoryType.REPLACEMENT_RULE, "DN"),
		EXPORTATION(CategoryType.REPLACEMENT_RULE, "EXP"),
		MATERIAL_EQUIVALENCE(CategoryType.REPLACEMENT_RULE, "EQUIV"),
		MATERIAL_IMPLICATION(CategoryType.REPLACEMENT_RULE, "IMPL"),
		DEMORGAN(CategoryType.REPLACEMENT_RULE, "DM"),
		
		UNIVERSAL_INSTANTIATION(CategoryType.QUANTIFIER_INSTANTIATION_RULE, "UI"),
		EXISTENTIAL_INSTANTIATION(CategoryType.QUANTIFIER_INSTANTIATION_RULE, "EI"),
		
		UNIVERSAL_GENERALIZATION(CategoryType.QUANTIFIER_GENERALIZATION_RULE, "UG"),
		EXISTENTIAL_GENERALIZATION(CategoryType.QUANTIFIER_GENERALIZATION_RULE, "EG")
		
		;
		
		private CategoryType category;
		private String[] identifiers;
		
		RuleType(CategoryType category, String...identifiers) {
			
			this.category = category;
			this.identifiers = identifiers;
			
		}
		
		public CategoryType getCategory() {
			return category;
		}
		
		public String[] getIdentifiers() {
			return identifiers;
		}
		
		public boolean is(RuleType...type) {
			
			for(RuleType k : values())
				for(RuleType h : type)
					if(k == h)
						return true;
			
			return false;
			
		}
		
		public static RuleType fromID(String id) {
			
			for(RuleType type : values())
				for(String k : type.getIdentifiers())
					if(k.equalsIgnoreCase(id))
						return type;
			
			return null;
			
		}
		
	}
	
	public static interface Rule {
		
		public CategoryType getCategory();
		
	}
	
}
