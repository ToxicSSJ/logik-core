package com.magmaticcode.logikcore.propositional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.magmaticcode.logikcore.propositional.builder.ProofParser;
import com.magmaticcode.logikcore.propositional.bundle.proof.Proof;
import com.magmaticcode.logikcore.propositional.bundle.proof.ProofResult;
import com.magmaticcode.logikcore.propositional.bundle.proof.ProofResult.ResultType;

public class PropositionalTests {

	@Test
	public void conjunctionTest() {
		
		Proof proof = ProofParser.fromString("{(A & C)//(A)//(B)=>(A & B)}");  //1,2,3

    	proof.addLine(ProofParser.fromString("A & B", "2,3 CONJ"));            //4
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
    	
	}
	
	@Test
	public void simplificationTest() {
		
		Proof proof = ProofParser.fromString("{(~A & C) & (D & ~H)=>~A}"); //1

    	proof.addLine(ProofParser.fromString("~A & C", "1 SIMP"));         //2
    	proof.addLine(ProofParser.fromString("~A", "2 SIMP"));             //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void additionTest() {
		
		Proof proof = ProofParser.fromString("{(~A & C) & (D & ~H)=>(~A | B) | ~(H & ~D)}"); //1

    	proof.addLine(ProofParser.fromString("~A & C", "1 SIMP"));                           //2
    	proof.addLine(ProofParser.fromString("~A", "2 SIMP"));                               //3
    	proof.addLine(ProofParser.fromString("~A | B", "3 ADD"));                            //4
    	proof.addLine(ProofParser.fromString("(~A | B) | ~(H & ~D)", "4 ADD"));              //5
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void implicationATest() {
		
		Proof proof = ProofParser.fromString("{(A → B) & N//C → A//D → E=>(((A → B) & (C → A)) & (D → E)) | (Z \u2194 F)}"); //1,2,3

		proof.addLine(ProofParser.fromString("A → B", "1 SIMP"));                                                            //4
    	proof.addLine(ProofParser.fromString("(A → B) & (C → A)", "4,2 CONJ"));                                              //5
    	proof.addLine(ProofParser.fromString("((A → B) & (C → A)) & (D → E)", "5,3 CONJ"));                                  //6
    	proof.addLine(ProofParser.fromString("(((A → B) & (C → A)) & (D → E)) | (Z \u2194 F)", "6 ADD"));                    //7
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void modusPonensTest() {
		
		Proof proof = ProofParser.fromString("{A → B//A=>B}"); //1,2

    	proof.addLine(ProofParser.fromString("B", "1,2 MP"));  //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void modusTollensTest() {
		
		Proof proof = ProofParser.fromString("{A → ~B//B=>~A}"); //1,2

    	proof.addLine(ProofParser.fromString("~A", "1,2 MT"));   //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void disjunctiveSyllogismTest() {
		
		Proof proof = ProofParser.fromString("{A | ~~B//~A=>~~B}"); //1,2

    	proof.addLine(ProofParser.fromString("~~B", "1,2 DS"));     //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void disjunctiveSyllogismBTest() {
		
		Proof proof = ProofParser.fromString("{(A | ~~B) | ~(K & ~D)//~(A | ~~B)=>~(K & ~D)}"); //1,2

    	proof.addLine(ProofParser.fromString("~(K & ~D)", "1,2 DS"));                           //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void hypoteticalSyllogismTest() {
		
		Proof proof = ProofParser.fromString("{(C → K)//(K → H)=>(C → H)}");  //1,2

    	proof.addLine(ProofParser.fromString("(C → H)", "1,2 HS"));           //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void hypoteticalSyllogismBTest() {
		
		Proof proof = ProofParser.fromString("{(Ka → ~Na)//(~(Ka → Ha) → Ka)=>(~(Ka → Ha) → ~Na)}"); //1,2

    	proof.addLine(ProofParser.fromString("(~(Ka → Ha) → ~Na)", "2,1 HS"));                       //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void constructiveDilemaTest() {
		
		Proof proof = ProofParser.fromString("{((A → B) & (C → D))//(A | C)=>(B | D)}");  //1,2

    	proof.addLine(ProofParser.fromString("(B | D)", "1,2 CD"));                       //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void constructiveDilemaBTest() {
		
		Proof proof = ProofParser.fromString("{((~Aa → Bx) & (~Ca → Dx))//(~Aa | ~Ca)=>(Bx | Dx)}"); //1,2

    	proof.addLine(ProofParser.fromString("(Bx | Dx)", "1,2 CD"));                                //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void implicationBTest() {
		
		Proof proof = ProofParser.fromString("{((Ah → Bj) & (Cc → ~Dd))//(Ah | Cc)//~Bj//Uf//Hk//(Dd | (Uf → (~Sf → ~Hk)))=>~~Sf}"); //1,2,3,4,5,6

    	proof.addLine(ProofParser.fromString("(Bj | ~Dd)", "1,2 CD"));                                                               //7
    	proof.addLine(ProofParser.fromString("~Dd", "7,3 DS"));                                                                      //8
    	proof.addLine(ProofParser.fromString("(Uf → (~Sf → ~Hk))", "6,8 DS"));                                                       //9
    	proof.addLine(ProofParser.fromString("(~Sf → ~Hk)", "9,4 MP"));                                                              //10
    	proof.addLine(ProofParser.fromString("~~Sf", "10,5 MT"));                                                                    //11
    	
    	ProofResult result = proof.build(false);
    	
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
    	
	}
	
	@Test
	public void deMorganTest() {
		
		Proof proof = ProofParser.fromString("{~(~A & B)=>(A | ~B)}"); //1

    	proof.addLine(ProofParser.fromString("(A | ~B)", "1 DM"));     //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void deMorganBTest() {
		
		Proof proof = ProofParser.fromString("{~(~A | B)=>(A & ~B)}"); //1

    	proof.addLine(ProofParser.fromString("(A & ~B)", "1 DM"));     //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void deMorganCTest() {
		
		Proof proof = ProofParser.fromString("{(~A & ~(~A & B)) | (C & D)=>(~A & (A | ~B)) | (C & D)}"); //1

    	proof.addLine(ProofParser.fromString("(~A & (A | ~B)) | (C & D)", "1 DM"));                      //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void deMorganDTest() {
		
		Proof proof = ProofParser.fromString("{(~A & ~(~A & (B | Lx))) | (C & D)=>(~A & ~(~A & ~(~B & ~Lx))) | (C & D)}"); //1

    	proof.addLine(ProofParser.fromString("(~A & ~(~A & ~(~B & ~Lx))) | (C & D)", "1 DM"));                             //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void deMorganETest() {
		
		Proof proof = ProofParser.fromString("{(~A & ~(~A & (B | Lx))) | ((∀x)Cx | ~(∀x)Dx)=>(~A & ~(~A & (B | Lx))) | ~(~(∀x)Cx & (∀x)Dx)}"); //1

    	proof.addLine(ProofParser.fromString("(~A & ~(~A & (B | Lx))) | ~(~(∀x)Cx & (∀x)Dx)", "1 DM"));                                        //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void commutativityTest() {
		
		Proof proof = ProofParser.fromString("{(C | D)=>(D | C)}"); //1

    	proof.addLine(ProofParser.fromString("(D | C)", "1 COM"));  //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void commutativityBTest() {
		
		Proof proof = ProofParser.fromString("{(C & (D | H))=>(H | D)}"); //1

    	proof.addLine(ProofParser.fromString("(D | H) & C", "1 COM"));    //2
    	proof.addLine(ProofParser.fromString("(D | H)", "2 SIMP"));       //3
    	proof.addLine(ProofParser.fromString("(H | D)", "3 COM"));        //4
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void commutativityCTest() {
		
		Proof proof = ProofParser.fromString("{(C & ~(D | H))=>~(H | D)}"); //1

    	proof.addLine(ProofParser.fromString("~(D | H) & C", "1 COM"));     //2
    	proof.addLine(ProofParser.fromString("~(D | H)", "2 SIMP"));        //3
    	proof.addLine(ProofParser.fromString("~(H | D)", "3 COM"));         //4
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void associativityATest() {
		
		Proof proof = ProofParser.fromString("{A & (B & C)=>(A & B) & C}"); //1

    	proof.addLine(ProofParser.fromString("(A & B) & C", "1 ASSOC"));    //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void associativityBTest() {
		
		Proof proof = ProofParser.fromString("{A & (~B & C)=>(A & ~B) & C}"); //1

    	proof.addLine(ProofParser.fromString("(A & ~B) & C", "1 ASSOC"));     //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void associativityCTest() {
		
		Proof proof = ProofParser.fromString("{A & (~B & ((C & H) & ~Kx))=>(A & ~B) & ((C & H) & ~Kx)}"); //1

    	proof.addLine(ProofParser.fromString("(A & ~B) & ((C & H) & ~Kx)", "1 ASSOC"));                   //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void distributionATest() {
		
		Proof proof = ProofParser.fromString("{(A & B) | (A & C)=>A & (B | C)}");  //1

    	proof.addLine(ProofParser.fromString("(A & (B | C))", "1 DIST"));          //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void distributionBTest() {
		
		Proof proof = ProofParser.fromString("{(A | B) & (A | C)=>A | (B & C)}");   //1

    	proof.addLine(ProofParser.fromString("A | (B & C)", "1 DIST"));             //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void distributionCTest() {
		
		Proof proof = ProofParser.fromString("{(A & B) | (A & ((Lx | Hx) & (Lx | Nx)))=>B | (Lx | (Hx & Nx))}"); //1

    	proof.addLine(ProofParser.fromString("A & (B | ((Lx | Hx) & (Lx | Nx)))", "1 DIST"));                    //2
    	proof.addLine(ProofParser.fromString("(B | ((Lx | Hx) & (Lx | Nx))) & A", "2 COM"));                     //3
    	proof.addLine(ProofParser.fromString("(B | ((Lx | Hx) & (Lx | Nx)))", "3 SIMP"));                        //4
    	proof.addLine(ProofParser.fromString("B | (Lx | (Hx & Nx))", "4 DIST"));                                 //5
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void doubleNegationATest() {
		
		Proof proof = ProofParser.fromString("{~~A=>A}");    //1

    	proof.addLine(ProofParser.fromString("A", "1 DN"));  //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void doubleNegationBTest() {
		
		Proof proof = ProofParser.fromString("{~~A & ~~B=>~~A & B}"); //1

    	proof.addLine(ProofParser.fromString("~~A & B", "1 DN"));     //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void doubleNegationCTest() {
		
		Proof proof = ProofParser.fromString("{(~~An & ~~Bn) | Dn=>~~(~~An & Bn) | ~~Dn}"); //1

    	proof.addLine(ProofParser.fromString("(~~An & Bn) | Dn", "1 DN"));                  //2
    	proof.addLine(ProofParser.fromString("(~~An & Bn) | ~~Dn", "2 DN"));                //3
    	proof.addLine(ProofParser.fromString("~~(~~An & Bn) | ~~Dn", "3 DN"));              //4
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void transpositionATest() {
		
		Proof proof = ProofParser.fromString("{A → B=>~B → ~A}");     //1

    	proof.addLine(ProofParser.fromString("~B → ~A", "1 TRANS"));  //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void transpositionBTest() {
		
		Proof proof = ProofParser.fromString("{A → B=>(A → B) | (D → H)}");     //1

    	proof.addLine(ProofParser.fromString("~B → ~A", "1 TRANS"));            //2
    	proof.addLine(ProofParser.fromString("A → B", "2 TRANS"));              //3
    	proof.addLine(ProofParser.fromString("(A → B) | (~H → ~D)", "3 ADD"));  //4
    	proof.addLine(ProofParser.fromString("(A → B) | (D → H)", "4 TRANS"));  //5
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void transpositionCTest() {
		
		Proof proof = ProofParser.fromString("{~~(~~Ha → (Fx → ~Sx))=>~~(~~Ha → (Sx → ~Fx))}"); //1

    	proof.addLine(ProofParser.fromString("~~(~~Ha → (Sx → ~Fx))", "1 TRANS"));              //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialImplicationATest() {
		
		Proof proof = ProofParser.fromString("{A → B=>~A | B}");   //1

    	proof.addLine(ProofParser.fromString("~A | B", "1 IMPL")); //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialImplicationBTest() {
		
		Proof proof = ProofParser.fromString("{H | ~(C → B)//~H=>(C & ~B)}");   //1

    	proof.addLine(ProofParser.fromString("~(C → B)", "1,2 DS"));            //2
    	proof.addLine(ProofParser.fromString("~(~C | B)", "3 IMPL"));           //3
    	proof.addLine(ProofParser.fromString("(C & ~B)", "4 DM"));              //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialImplicationCTest() {
		
		Proof proof = ProofParser.fromString("{~~(Nx → ~(C → ~~(Ah → Bh)))=>~~(Nx → ~(C → ~~(Bh | ~Ah)))}");  //1

    	proof.addLine(ProofParser.fromString("~~(Nx → ~(C → ~~(~Ah | Bh)))", "1 IMPL"));                      //2
    	proof.addLine(ProofParser.fromString("~~(Nx → ~(C → ~~(Bh | ~Ah)))", "2 COM"));                       //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialImplicationDTest() {
		
		Proof proof = ProofParser.fromString("{~~(Nx → ~(C → ~~(Ah → Bh)))=>~~(Nx → ~(~C | ~~(Bh | ~Ah)))}"); //1

    	proof.addLine(ProofParser.fromString("~~(Nx → ~(C → ~~(~Ah | Bh)))", "1 IMPL"));                      //2
    	proof.addLine(ProofParser.fromString("~~(Nx → ~(C → ~~(Bh | ~Ah)))", "2 COM"));                       //3
    	proof.addLine(ProofParser.fromString("~~(Nx → ~(~C | ~~(Bh | ~Ah)))", "3 IMPL"));                     //4
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialEquivalenceATest() {
		
		Proof proof = ProofParser.fromString("{(A → B) & (B → A)=>A \u2194 B}"); //1

    	proof.addLine(ProofParser.fromString("A \u2194 B", "1 EQUIV"));          //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialEquivalenceBTest() {
		
		Proof proof = ProofParser.fromString("{A \u2194 B=>(A → B) & (B → A)}"); //1

    	proof.addLine(ProofParser.fromString("(A → B) & (B → A)", "1 EQUIV"));   //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialEquivalenceCTest() {
		
		Proof proof = ProofParser.fromString("{H & ((A \u2194 B) & (Rx \u2194 Sx))=>H & (((A → B) & (B → A)) & ((Rx & Sx) | (~Rx & ~Sx)))}"); //1

    	proof.addLine(ProofParser.fromString("H & (((A → B) & (B → A)) & (Rx \u2194 Sx))", "1 EQUIV"));                                       //2
    	proof.addLine(ProofParser.fromString("H & (((A → B) & (B → A)) & ((Rx & Sx) | (~Rx & ~Sx)))", "2 EQUIV"));                            //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void exportationATest() {
		
		/*Proof proof = ProofParser.fromString("{(H & C) → D=>H → (C → D)}"); //1

    	proof.addLine(ProofParser.fromString("H → (C → D)", "1 EXP"));        //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");*/
		
	}
	
	
	@Test
	public void exportationBTest() {
		
		Proof proof = ProofParser.fromString("{((H & C) → D) & (H → (C → D))=>(H → (C → D)) & ((H & C) → D)}"); //1

    	proof.addLine(ProofParser.fromString("(H → (C → D)) & (H → (C → D))", "1 EXP"));                        //2
    	proof.addLine(ProofParser.fromString("(H → (C → D)) & ((H & C) → D)", "2 EXP"));                        //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void exportationCTest() {
		
		Proof proof = ProofParser.fromString("{~((Rx & Ax) | (C \u2194 Nfxs)) \u2194 ~(((H & ~C) → D) & (H → (~C → D)))=>~((Rx & Ax) | (C \u2194 Nfxs)) \u2194 ~((H → (~C → D)) & ((H & ~C) → D))}"); //1

    	proof.addLine(ProofParser.fromString("~((Rx & Ax) | (C \u2194 Nfxs)) \u2194 ~((H → (~C → D)) & (H → (~C → D)))", "1 EXP"));                                                                   //2
    	proof.addLine(ProofParser.fromString("~((Rx & Ax) | (C \u2194 Nfxs)) \u2194 ~((H → (~C → D)) & ((H & ~C) → D))", "2 EXP"));                                                                   //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void tautologyATest() {
		
		Proof proof = ProofParser.fromString("{A & A=>A}");    //1
    	proof.addLine(ProofParser.fromString("A", "1 TAUT"));  //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void tautologyBTest() {
		
		Proof proof = ProofParser.fromString("{(A & A) & (C & (C & (D & D)))=>(C & (C & D))}");  //1

    	proof.addLine(ProofParser.fromString("(C & (C & (D & D))) & (A & A)", "1 COM"));         //2
    	proof.addLine(ProofParser.fromString("(C & (C & (D & D)))", "2 SIMP"));                  //3
    	proof.addLine(ProofParser.fromString("(C & (C & D))", "3 TAUT"));                        //4
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void tautologyCTest() {
		
		Proof proof = ProofParser.fromString("{(A & A) & (C & (C \u2194 (~Dxsa & ~Dxsa)))=>(A & A) & (C & (C \u2194 ~Dxsa))}");  //1

    	proof.addLine(ProofParser.fromString("(A & A) & (C & (C \u2194 ~Dxsa))", "1 TAUT"));                                     //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void UIATest() {
		
		Proof proof = ProofParser.fromString("{(∀x)Ax=>Aa}");  //1

    	proof.addLine(ProofParser.fromString("Aa", "1 UI"));   //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void UIBTest() {
		
		Proof proof = ProofParser.fromString("{(∀x)Axfg=>Avfg}"); //1

    	proof.addLine(ProofParser.fromString("Avfg", "1 UI"));    //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void UICTest() {
		
		Proof proof = ProofParser.fromString("{(∀x)(∀z)Axzg=>Aazg}"); //1

    	proof.addLine(ProofParser.fromString("(∀z)Aazg", "1 UI"));    //2
    	proof.addLine(ProofParser.fromString("Aazg", "2 UI"));        //3
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}

	@Test
	public void UIDTest() {
		
		Proof proof = ProofParser.fromString("{(∀y)(Ey & (∀x)(∀z)Axzy)=>Aabc}");  //1

    	proof.addLine(ProofParser.fromString("Ec & (∀x)(∀z)Axzc", "1 UI"));       //2
    	proof.addLine(ProofParser.fromString("(∀x)(∀z)Axzc & Ec", "2 COM"));      //3
    	proof.addLine(ProofParser.fromString("(∀x)(∀z)Axzc", "3 SIMP"));          //4
    	proof.addLine(ProofParser.fromString("(∀z)Aazc", "4 UI"));                //5
    	proof.addLine(ProofParser.fromString("Aabc", "5 UI"));                    //6
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void UIETest() {
		
		Proof proof = ProofParser.fromString("{(∀y)((∀y)Ey & (∀x)(∀z)Axzy)=>Aabc}"); //1

    	proof.addLine(ProofParser.fromString("(∀y)Ey & (∀x)(∀z)Axzc", "1 UI"));      //2
    	proof.addLine(ProofParser.fromString("(∀x)(∀z)Axzc & (∀y)Ey", "2 COM"));     //3
    	proof.addLine(ProofParser.fromString("(∀x)(∀z)Axzc", "3 SIMP"));             //4
    	proof.addLine(ProofParser.fromString("(∀z)Aazc", "4 UI"));                   //5
    	proof.addLine(ProofParser.fromString("Aabc", "5 UI"));                       //6
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void EIATest() {
		
		Proof proof = ProofParser.fromString("{(∃x)Ax=>Aa}"); //1

    	proof.addLine(ProofParser.fromString("Aa", "1 EI"));  //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void EIBTest() {
		
		Proof proof = ProofParser.fromString("{(∃y)Ayyy=>Attt}"); //1

    	proof.addLine(ProofParser.fromString("Attt", "1 EI"));    //2
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void EICTest() {
		
		Proof proof = ProofParser.fromString("{Ea & (∃y)Ey=>Eb}");      //1

    	proof.addLine(ProofParser.fromString("(∃y)Ey & Ea", "1 COM"));  //2
    	proof.addLine(ProofParser.fromString("(∃y)Ey", "2 SIMP"));      //3
    	proof.addLine(ProofParser.fromString("Eb", "3 EI"));            //4
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void EIDTest() {
		
		Proof proof = ProofParser.fromString("{(∃z)((∀x)Exaz & (∃x)Exxz)=>Eaak}"); //1

    	proof.addLine(ProofParser.fromString("(∀x)Exak & (∃x)Exxk", "1 EI"));      //2
    	proof.addLine(ProofParser.fromString("(∀x)Exak", "2 SIMP"));               //3
    	proof.addLine(ProofParser.fromString("Eaak", "3 UI"));                     //4
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void EIETest() {
		
		Proof proof = ProofParser.fromString("{(∃x)((∀x)Waxs & (∃x)(∃z)Exxz)=>Ejjk}"); //1

    	proof.addLine(ProofParser.fromString("(∀x)Waxs & (∃x)(∃z)Exxz", "1 EI"));      //2
    	proof.addLine(ProofParser.fromString("(∃x)(∃z)Exxz & (∀x)Waxs", "2 COM"));     //3
    	proof.addLine(ProofParser.fromString("(∃x)(∃z)Exxz", "3 SIMP"));               //4
    	proof.addLine(ProofParser.fromString("(∃z)Ejjz", "4 EI"));                     //5
    	proof.addLine(ProofParser.fromString("Ejjk", "5 EI"));                         //6
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}

    @Test
    public void UGATest() {

        Proof proof = ProofParser.fromString("{Wx & Bx=>(∀x)(Wx & Bx)}"); //1

        proof.addLine(ProofParser.fromString("(∀x)(Wx & Bx)", "1 UG"));   //2

        ProofResult result = proof.build(false);

        if(result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

    }

    @Test
    public void UGBTest() {

        Proof proof = ProofParser.fromString("{Wx & Bx=>(∀x)(Wx & Bx)}"); //1

        proof.addLine(ProofParser.fromString("(∀x)(Wx & Bx)", "1 UG"));   //2

        ProofResult result = proof.build(false);

        if(result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

    }

    @Test
    public void UGCTest() {

        Proof proof = ProofParser.fromString("{Ax=>(∀y)(∀x)(Ax | By)}");    //1

        proof.addLine(ProofParser.fromString("Ax | By", "1 ADD"));          //2
        proof.addLine(ProofParser.fromString("(∀x)(Ax | By)", "2 UG"));     //3
        proof.addLine(ProofParser.fromString("(∀y)(∀x)(Ax | By)", "3 UG")); //4

        ProofResult result = proof.build(false);

        if(result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

    }

    @Test
    public void UGDTest() {

        Proof proof = ProofParser.fromString("{Ax=>(∀x)(Bx | Ax)}");    //1

        proof.addLine(ProofParser.fromString("Ax | Bx", "1 ADD"));      //2
		proof.addLine(ProofParser.fromString("Bx | Ax", "2 COM"));      //3
        proof.addLine(ProofParser.fromString("(∀x)(Bx | Ax)", "3 UG")); //4

        ProofResult result = proof.build( false);

        if(result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

    }

	@Test
	public void UGETest() {

		Proof proof = ProofParser.fromString("{Ax | Bx // ~Ax=>(∀x)Bx}"); //1,2

		proof.addLine(ProofParser.fromString("Bx", "1,2 DS"));   //3
		proof.addLine(ProofParser.fromString("(∀x)Bx", "3 UG")); //4

		ProofResult result = proof.build( false);

		if(result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");

	}

	@Test
	public void UGFTest() {

		Proof proof = ProofParser.fromString("{A → ~(Bx & C) // A & C=>(∀x)~Bx}"); //1,2

		proof.addLine(ProofParser.fromString("A", "2 SIMP"));                      //3
		proof.addLine(ProofParser.fromString("~(Bx & C)", "1,3 MP"));              //4
		proof.addLine(ProofParser.fromString("~Bx | ~C", "4 DM"));                 //5
		proof.addLine(ProofParser.fromString("C & A", "2 COM"));                   //6
		proof.addLine(ProofParser.fromString("C", "6 SIMP"));                      //7
		proof.addLine(ProofParser.fromString("~~C", "7 DN"));                      //8
		proof.addLine(ProofParser.fromString("~C | ~Bx", "5 COM"));                //9
		proof.addLine(ProofParser.fromString("~Bx", "9,8 DS"));                    //10
		proof.addLine(ProofParser.fromString("(∀x)~Bx", "10 UG"));                 //11

		ProofResult result = proof.build(false);

		if(result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");

	}

	@Test
	public void UGGTest() {
		Proof proof = ProofParser.fromString("{(∀x)(Px → Dx)//(∀x)(Dx → Cx)=>(∀x)(Px → cx)}"); //1,2

		proof.addLine(ProofParser.fromString("Py → Dy", "1 UI"));                              //3
		proof.addLine(ProofParser.fromString("Dy → Cy", "2 UI"));                              //4
		proof.addLine(ProofParser.fromString("(Py → Cy)", "3,4 HS"));                          //5
		proof.addLine(ProofParser.fromString("(∀x)(Px → cx)", "5 UG"));                        //6

		ProofResult result = proof.build(true);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");
	}

    @Test
	public void implicationCTest() {
		
		Proof proof = ProofParser.fromString("{(N → A) & (~N → ~A)=>N \u2194 A}"); //1
		
    	proof.addLine(ProofParser.fromString("(N → A) & (A → N)", "1 TRANS"));     //2
    	proof.addLine(ProofParser.fromString("N \u2194 A", "2 EQUIV"));            //3
    	
    	ProofResult result = proof.build(false);
    	
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
    	
	}

	@Test
	public void copiATest() {

        Proof proof = ProofParser.fromString("{(∀x)(Hx → Mx)//Hs=>Ms}"); //1,2

        proof.addLine(ProofParser.fromString("(Hs → Ms)", "1 UI"));      //3
        proof.addLine(ProofParser.fromString("Ms", "3,2 MP"));           //4

        ProofResult result = proof.build(false);

        if (result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

    }

	@Test
	public void EGATest(){
		Proof proof = ProofParser.fromString("{Aa=>((∃x)(Ax))}");  //1

		proof.addLine(ProofParser.fromString("(∃x)(Ax)", "1 EG")); // 2

		ProofResult result = proof.build(true);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");
	}

	@Test
	public void EGBTest(){

		Proof proof = ProofParser.fromString("{Ax=>((∃y)(Ay))}");  //1

		proof.addLine(ProofParser.fromString("(∃y)(Ay)", "1 EG")); // 2

		ProofResult result = proof.build(true);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");
	}
    @Test
    public void EGCTest() {     // Exercise 8.6, Part II, number 3

	    Proof proof = ProofParser.fromString("{(∃x)(Ax & (∀y)(By → Cxy))//(∃x)Ax → Bj=>(∃x)Cxj}"); //1,2

	    proof.addLine(ProofParser.fromString("Aa & (∀y)(By → Cay)", "1 EI"));                      //3
	    proof.addLine(ProofParser.fromString("Aa", "3 SIMP"));                                     //4
	    proof.addLine(ProofParser.fromString("(∃x)(Ax)", "4 EG"));                                 //5
	    proof.addLine(ProofParser.fromString("Bj", "2,5 MP"));                                     //6
	    proof.addLine(ProofParser.fromString("(∀y)(By → Cay) & Aa", "3 COM"));                     //7
	    proof.addLine(ProofParser.fromString("(∀y)(By → Cay)", "7 SIMP"));                         //8
	    proof.addLine(ProofParser.fromString("Bj → Caj", "8 UI"));                                 //9
	    proof.addLine(ProofParser.fromString("Caj", "6,9 MP"));                                    //10
	    proof.addLine(ProofParser.fromString("(∃x)Cxj", "10 EG"));                                 //11

        ProofResult result = proof.build(true);

        if (result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

	}

	@Test
    public void EGDTest() {

    	Proof proof = ProofParser.fromString("{(∀y)(Sy → Wy)//(∀y)Sy=>(∃x)Wx}"); //1,2

    	proof.addLine((ProofParser.fromString("Sy → Wy", "1 UI")));              //3
    	proof.addLine(ProofParser.fromString("Sy", "2 UI"));                     //4
    	proof.addLine(ProofParser.fromString("Wy", "3,4 MP"));                   //5
    	proof.addLine(ProofParser.fromString("(∃x)Wx", "5 EG"));                 //6

		ProofResult result = proof.build(true);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");
	}

	@Test
	public void EGETest()  {

        Proof proof = ProofParser.fromString("{(∀x)(Jx → Kx)//(∃x)(Jx & Lx)=>(∃x)(Lx & Kx)}"); //1,2

        proof.addLine(ProofParser.fromString("Jy & Ly", "2 EI"));                              //3
        proof.addLine(ProofParser.fromString("Jy → Ky", "1 UI"));                              //4
        proof.addLine(ProofParser.fromString("Jy", "3 SIMP"));                                 //5
        proof.addLine(ProofParser.fromString("Ly & Jy", "3 COM"));                             //6
        proof.addLine(ProofParser.fromString("Ly", "6 SIMP"));                                 //7
        proof.addLine(ProofParser.fromString("Ky", "4,5 MP"));                                 //8
        proof.addLine(ProofParser.fromString("Ly & Ky", "7,8 CONJ"));                          //9
        proof.addLine(ProofParser.fromString("(∃x)(Lx & Kx)", "9 EG"));                        //10

        ProofResult result = proof.build(true);

        if (result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

    }

	@Test
    public void EGFTest() {

    	Proof proof = ProofParser.fromString("{(∀x)((Aa | Bx) → Cx) //(∃x)Ax=>(∃x)Cx}"); //1,2

        proof.addLine(ProofParser.fromString("Aa", "2 EI"));                             //3
        proof.addLine(ProofParser.fromString("(Aa | Ba) → Ca", "1 UI"));                 //4
        proof.addLine(ProofParser.fromString("Aa | Ba", "3 ADD"));                       //5
        proof.addLine(ProofParser.fromString("Ca", "4,5 MP"));                           //6
        proof.addLine(ProofParser.fromString("(∃x)Cx", "6 EG"));                         //7

    	ProofResult result = proof.build(true);

    	if (result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType());

	}

	@Test
	public void EGGTest() {

        Proof proof = ProofParser.fromString("{(∀x)((Aa | Bx) → Cx) //(∃x)Bx=>(∃x)Cx}"); //1,2

        proof.addLine(ProofParser.fromString("Ba", "2 EI"));                             //3
        proof.addLine(ProofParser.fromString("Ba | Aa", "3 ADD"));                       //4
        proof.addLine(ProofParser.fromString("Aa | Ba", "4 COM"));                       //5
        proof.addLine(ProofParser.fromString("(Aa | Ba) → Ca", "1 UI"));                 //6
        proof.addLine(ProofParser.fromString("Ca", "6,5 MP"));                           //7
        proof.addLine(ProofParser.fromString("(∃x)Cx", "7 EG"));                         //8

        ProofResult result = proof.build((true));

        if (result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType());
	}

	@Test
	public void EGHTest() {

        Proof proof = ProofParser.fromString("{(∃x)Kx → (∀x)(Lx →  Mx)//Kc & Lc =>Mc}"); //1,2

        proof.addLine(ProofParser.fromString("Kc", "2 SIMP"));                           //3
        proof.addLine(ProofParser.fromString("(∃x)Kx", "3 EG"));                         //4
        proof.addLine(ProofParser.fromString("(∀x)(Lx → Mx)", "1,4 MP"));                //5
        proof.addLine(ProofParser.fromString("Lc & Kc", "2 COM"));                       //6
        proof.addLine(ProofParser.fromString("Lc", "6 SIMP"));                           //7
        proof.addLine(ProofParser.fromString("Lc → Mc", "5 UI"));                        //8
        proof.addLine(ProofParser.fromString("Mc", "8,7 MP"));                           //9

        ProofResult result = proof.build(true);

        if (result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType());

    }



	@Test
	public void randomTest() {

		Proof proof = ProofParser.fromString("{(F & H) → N//F | S//H=>N | S}");    //1,2,3

		proof.addLine(ProofParser.fromString("(F | S) & H", "2,3 CONJ"));          //4
		proof.addLine(ProofParser.fromString("H & (F | S)", "4 COM"));             //5
		proof.addLine(ProofParser.fromString("(H & F) | (H & S)", "5 DIST"));      //6
		proof.addLine(ProofParser.fromString("(H & S) | (H & F)", "6 COM"));       //7
		proof.addLine(ProofParser.fromString("(H & S) | (F & H)", "7 COM"));       //8
		proof.addLine(ProofParser.fromString("~(H & S) → (F & H)", "8 IMPL"));     //9
		proof.addLine(ProofParser.fromString("~(H & S) → N", "9,1 HS"));           //10
		proof.addLine(ProofParser.fromString("(H & S) | N", "10 IMPL"));           //11
		proof.addLine(ProofParser.fromString("N | (H & S)", "11 COM"));            //12
		proof.addLine(ProofParser.fromString("(N | H) & (N | S)", "12 DIST"));     //13
		// [ERROR, la regla al parecer no tiene la opción de A | (B & C) ↔ (A | B) & (A | C) ]
		proof.addLine(ProofParser.fromString("(N | S) & (N | H)", "13 COM"));      //14
		proof.addLine(ProofParser.fromString("N | S", "14 SIMP"));                 //15

		ProofResult result = proof.build(true);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");

	}

	@Test
	public void randomTest2() {

		Proof proof = ProofParser.fromString("{(F & H) → N//F | S//H=>N | S}");    //1,2,3

		proof.addLine(ProofParser.fromString("~(F & H) | N", "1 IMPL"));           //4
		proof.addLine(ProofParser.fromString("(~F | ~H) | N", "4 DM"));            //5
		proof.addLine(ProofParser.fromString("~F | (~H | N)", "5 ASSOC"));         //6
		proof.addLine(ProofParser.fromString("(~H | N) | ~F", "6 COM"));           //7
		proof.addLine(ProofParser.fromString("~H | (N | ~F)", "7 ASSOC"));         //8
		proof.addLine(ProofParser.fromString("H → (N | ~F)", "8 IMPL"));           //9
		proof.addLine(ProofParser.fromString("N | ~F", "9,3 MP"));                 //10
		proof.addLine(ProofParser.fromString("~F | N", "10 COM"));                 //11
		proof.addLine(ProofParser.fromString("F → N", "11 IMPL"));                 //12
		proof.addLine(ProofParser.fromString("S | F", "2 COM"));                   //13
		proof.addLine(ProofParser.fromString("~~S | F", "13 DN"));                 //14
		// [ERROR, no se pude hacer la regla de IMPL, pienso que es porque esta negada la S]
		proof.addLine(ProofParser.fromString("~S → F", "13 IMPL"));                //15
		proof.addLine(ProofParser.fromString("~S → N", "15,12 HS"));               //16
		proof.addLine(ProofParser.fromString("~~S | N", "16 IMPL"));               //17
		// [ERROR, no se puede hacer regla de la IMPL, pienso que es porque esta negada la S]
		proof.addLine(ProofParser.fromString("S | N", "17 DN"));                   //18
		proof.addLine(ProofParser.fromString("N | S", "18 COM"));                  //19

		ProofResult result = proof.build(true);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");

	}

	@Test
	public void randomTest3() {

		Proof proof = ProofParser.fromString("{A ↔ W//~A | ~W//R → A=>~(W | R)}");      //1,2,3

		proof.addLine(ProofParser.fromString("(A & W) | (~A & ~W)", "1 EQUIV"));        //4
		proof.addLine(ProofParser.fromString("~(A & W)", "2 DM"));                      //5
		proof.addLine(ProofParser.fromString("~A & ~W", "4,5 DS"));                     //6
		proof.addLine(ProofParser.fromString("~W & ~A", "6 COM"));                      //7
		proof.addLine(ProofParser.fromString("~W", "7 SIMP"));                          //8
		proof.addLine(ProofParser.fromString("~A", "6 SIMP"));                          //9
		proof.addLine(ProofParser.fromString("~R", "3,9 MT"));                          //10
		proof.addLine(ProofParser.fromString("~W & ~R", "8,10 CONJ"));                  //11
		proof.addLine(ProofParser.fromString("~(W | R)", "11 DM"));                     //12

		ProofResult result = proof.build(false);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");

	}

	@Test
	public void randomTest4(){
    	Proof proof = ProofParser.fromString("{A → B//~A → (C | D)//~B//~C=> D}"); //1,2,3,4

    	proof.addLine(ProofParser.fromString("~A", "1,3 MT"));		               //5
		proof.addLine(ProofParser.fromString("C | D", "2,5 MP"));	               //6
		proof.addLine(ProofParser.fromString("D","6,4 DS"));		               //7

		ProofResult result = proof.build(false);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");

	}

	@Test
	public void randomTest5(){
    	// There is a strange problem.
    	Proof proof = ProofParser.fromString("{F → G//(F | H)//~G//H → (G → H)=>(F → I)}"); //1,2,3,4

    	proof.addLine(ProofParser.fromString("~F", "1,3 MT"));		                        // 5
    	proof.addLine(ProofParser.fromString("H", "2,5 DS"));		                        // 6
		proof.addLine(ProofParser.fromString("G → H", "4,6 MP"));	                        // 7
		proof.addLine(ProofParser.fromString("F → I", "1,7 HS"));	                        // 8

		ProofResult result = proof.build(false);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");

	}
		@Test
	public void parcial1ManuelSierraTuesday2(){

		Proof proof = ProofParser.fromString("{S → (T → U)//U → ~U//(V → S) & (W → T)=>V → ~W}"); //1,2,3

		proof.addLine(ProofParser.fromString("~U | ~U", "2 IMPL"));                               //4
		proof.addLine(ProofParser.fromString("~U", "4 TAUT"));                                    //5
		proof.addLine(ProofParser.fromString("(S & T) → U", "1 EXP"));                            //6
		proof.addLine(ProofParser.fromString("~(S & T)", "6,5 MT"));                              //7
		proof.addLine(ProofParser.fromString("~S | ~T", "7 DM"));                                 //8
		proof.addLine(ProofParser.fromString("(W → T) & (V → S)", "3 COM"));                      //9
		proof.addLine(ProofParser.fromString("W → T", "9 SIMP"));                                 //10
		proof.addLine(ProofParser.fromString("S → ~T ", "8 IMPL"));                               //11
		proof.addLine(ProofParser.fromString("V → S", "3 SIMP"));                                 //12
		proof.addLine(ProofParser.fromString("V → ~T", "12,11 HS"));                              //13
		proof.addLine(ProofParser.fromString("~T → ~W", "10 TRANS"));                             //14
		proof.addLine(ProofParser.fromString("V → ~W", "13,14 HS"));                              //15

		ProofResult result = proof.build(false);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");

	}

	@Test
	public void parcial1ManuelSierraTuesday3(){

		Proof proof = ProofParser.fromString("{(L | M) | (N & O)//(~L & O) & ~(~L & M)=>~L | N}"); //1

		proof.addLine(ProofParser.fromString("~L & O", "2 SIMP"));                                 //3
		proof.addLine(ProofParser.fromString("~L", "3 SIMP"));                                     //4
		proof.addLine(ProofParser.fromString("~(~L & M) & (~L & O)", "2 COM"));                    //5
		proof.addLine(ProofParser.fromString("~(~L & M)", "5 SIMP"));                              //6
		proof.addLine(ProofParser.fromString("(~~L | ~M)", "6 DM"));                               //7
		proof.addLine(ProofParser.fromString("L | ~M", "7 DN"));                                   //8
		proof.addLine(ProofParser.fromString("~M", "8,4 DS"));                                     //9
		proof.addLine(ProofParser.fromString("~L & ~M ", "4,9 CONJ"));                             //10
		proof.addLine(ProofParser.fromString("~(L | M)", "10 DM"));                                //11
		proof.addLine(ProofParser.fromString("N & O", "1,11 DS"));                                 //12
		proof.addLine(ProofParser.fromString("N", "12 SIMP"));                                     //13
		proof.addLine(ProofParser.fromString("~L & N", "4,13 CONJ"));                              //14

		ProofResult result = proof.build(false);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");

	}

	@Test
	public void parcial1RenéAlejandrotuesday(){

		Proof proof = ProofParser.fromString("{~(S & T) → ~(A & (S | T))=>~(T → S) → ~A}");              //1

		proof.addLine(ProofParser.fromString("(A & (S | T)) → (S & T)", "1 TRANS"));                     //2
		proof.addLine(ProofParser.fromString("((A & S) | (A & T)) → (S & T)", "2 DIST"));                //3
		proof.addLine(ProofParser.fromString("~((A & S) | (A & T)) | (S & T)", "3 IMPL"));               //4
		proof.addLine(ProofParser.fromString("(~(A & S) & ~(A & T)) | (S & T)", "4 DM"));                //5
		proof.addLine(ProofParser.fromString("(S & T) | (~(A & S) & ~(A & T))", "5 COM"));               //6
		proof.addLine(ProofParser.fromString("(~(A & S) | (S & T)) & (~(A & T) | (S & T))", "6 DIST"));  //7
		proof.addLine(ProofParser.fromString("~(A & S) | (S & T)", "7 SIMP"));                           //8
		proof.addLine(ProofParser.fromString("(~(A & T) | (S & T)) & (~(A & S) | (S & T))", "7 COM"));   //9
		proof.addLine(ProofParser.fromString("~(A & T) | (S & T)", "9 SIMP"));                           //10
		proof.addLine(ProofParser.fromString("(~A | ~T) | (S & T)", "10 DM"));                           //11
		proof.addLine(ProofParser.fromString("(S | (~A | ~T)) & (T | (~A | ~T))", "11 DIST"));           //12
		proof.addLine(ProofParser.fromString("S | (~A | ~T)", "11 SIMP"));                               //13
		proof.addLine(ProofParser.fromString("(~A | ~T) | S", "12 COM"));                                //14
		proof.addLine(ProofParser.fromString("~(A & T) | S", "13 DM"));                                  //15
		proof.addLine(ProofParser.fromString("(A & T) → S", "14 IMPL"));                                 //16
		proof.addLine(ProofParser.fromString("A → (T → S)", "15 EXP"));                                  //17
		proof.addLine(ProofParser.fromString("~(T → S) → ~A", "16 TRANS"));                              //18

		ProofResult result = proof.build(false);

		if (result.getType() != ResultType.CORRECT)
			fail("El resultado fue " + result.getType() + "!");

	}

	/**
	 //@Test
	 public void parcial1ManuelSierraTuesday(){ // Ya lo termino ahorita

	 Proof proof = ProofParser.fromString("{(S → K) & (U → V)//W → (S | U)=>W → (T | V)}");


	 }
	 */
}
