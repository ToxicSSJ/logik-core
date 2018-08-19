package compwrteam.logikcore.propositional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.pwrteam.logikcore.propositional.builder.ProofParser;
import com.pwrteam.logikcore.propositional.bundle.proof.Proof;
import com.pwrteam.logikcore.propositional.bundle.proof.ProofResult;
import com.pwrteam.logikcore.propositional.bundle.proof.ProofResult.ResultType;

public class PropositionalTests {

	@Test
	public void conjunctionTest() {
		
		Proof proof = ProofParser.fromString("{(A & C)//(A)//(B)=>(A & B)}");
    	proof.addLine(ProofParser.fromString("A & B", "2,3 CONJ"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
    	
	}
	
	@Test
	public void simplificationTest() {
		
		Proof proof = ProofParser.fromString("{(~A & C) & (D & ~H)=>~A}");
		
    	proof.addLine(ProofParser.fromString("~A & C", "1 SIMP"));
    	proof.addLine(ProofParser.fromString("~A", "2 SIMP"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void additionTest() {
		
		Proof proof = ProofParser.fromString("{(~A & C) & (D & ~H)=>(~A | B) | ~(H & ~D)}");
		
    	proof.addLine(ProofParser.fromString("~A & C", "1 SIMP"));
    	proof.addLine(ProofParser.fromString("~A", "2 SIMP"));
    	proof.addLine(ProofParser.fromString("~A | B", "3 ADD"));
    	proof.addLine(ProofParser.fromString("(~A | B) | ~(H & ~D)", "4 ADD"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void implicationATest() {
		
		Proof proof = ProofParser.fromString("{(A → B) & N//C → A//D → E=>(((A → B) & (C → A)) & (D → E)) | (Z \u2194 F)}");
		
		proof.addLine(ProofParser.fromString("A → B", "1 SIMP"));
    	proof.addLine(ProofParser.fromString("(A → B) & (C → A)", "4,2 CONJ"));
    	proof.addLine(ProofParser.fromString("((A → B) & (C → A)) & (D → E)", "5,3 CONJ"));
    	proof.addLine(ProofParser.fromString("(((A → B) & (C → A)) & (D → E)) | (Z \u2194 F)", "6 ADD"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void modusPonensTest() {
		
		Proof proof = ProofParser.fromString("{A → B//A=>B}");
    	proof.addLine(ProofParser.fromString("B", "1,2 MP"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void modusTollensTest() {
		
		Proof proof = ProofParser.fromString("{A → ~B//B=>~A}");
    	proof.addLine(ProofParser.fromString("~A", "1,2 MT"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void disjunctiveSyllogismTest() {
		
		Proof proof = ProofParser.fromString("{A | ~~B//~A=>~~B}");
    	proof.addLine(ProofParser.fromString("~~B", "1,2 DS"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void disjunctiveSyllogismBTest() {
		
		Proof proof = ProofParser.fromString("{(A | ~~B) | ~(K & ~D)//~(A | ~~B)=>~(K & ~D)}");
    	proof.addLine(ProofParser.fromString("~(K & ~D)", "1,2 DS"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void hypoteticalSyllogismTest() {
		
		Proof proof = ProofParser.fromString("{(C → K)//(K → H)=>(C → H)}");
    	proof.addLine(ProofParser.fromString("(C → H)", "1,2 HS"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void hypoteticalSyllogismBTest() {
		
		Proof proof = ProofParser.fromString("{(Ka → ~Na)//(~(Ka → Ha) → Ka)=>(~(Ka → Ha) → ~Na)}");
    	proof.addLine(ProofParser.fromString("(~(Ka → Ha) → ~Na)", "2,1 HS"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void constructiveDilemaTest() {
		
		Proof proof = ProofParser.fromString("{((A → B) & (C → D))//(A | C)=>(B | D)}");
    	proof.addLine(ProofParser.fromString("(B | D)", "1,2 CD"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void constructiveDilemaBTest() {
		
		Proof proof = ProofParser.fromString("{((~Aa → Bx) & (~Ca → Dx))//(~Aa | ~Ca)=>(Bx | Dx)}");
    	proof.addLine(ProofParser.fromString("(Bx | Dx)", "1,2 CD"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void implicationBTest() {
		
		Proof proof = ProofParser.fromString("{((Ah → Bj) & (Cc → ~Dd))//(Ah | Cc)//~Bj//Uf//Hk//(Dd | (Uf → (~Sf → ~Hk)))=>~~Sf}");
		
    	proof.addLine(ProofParser.fromString("(Bj | ~Dd)", "1,2 CD"));
    	proof.addLine(ProofParser.fromString("~Dd", "7,3 DS"));
    	proof.addLine(ProofParser.fromString("(Uf → (~Sf → ~Hk))", "6,8 DS"));
    	proof.addLine(ProofParser.fromString("(~Sf → ~Hk)", "9,4 MP"));
    	proof.addLine(ProofParser.fromString("~~Sf", "10,5 MT"));
    	
    	ProofResult result = proof.build(false);
    	
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
    	
	}
	
	@Test
	public void deMorganTest() {
		
		Proof proof = ProofParser.fromString("{~(~A & B)=>(A | ~B)}");
    	proof.addLine(ProofParser.fromString("(A | ~B)", "1 DM"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void deMorganBTest() {
		
		Proof proof = ProofParser.fromString("{~(~A | B)=>(A & ~B)}");
    	proof.addLine(ProofParser.fromString("(A & ~B)", "1 DM"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void deMorganCTest() {
		
		Proof proof = ProofParser.fromString("{(~A & ~(~A & B)) | (C & D)=>(~A & (A | ~B)) | (C & D)}");
    	proof.addLine(ProofParser.fromString("(~A & (A | ~B)) | (C & D)", "1 DM"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void deMorganDTest() {
		
		Proof proof = ProofParser.fromString("{(~A & ~(~A & (B | Lx))) | (C & D)=>(~A & ~(~A & ~(~B & ~Lx))) | (C & D)}");
    	proof.addLine(ProofParser.fromString("(~A & ~(~A & ~(~B & ~Lx))) | (C & D)", "1 DM"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void deMorganETest() {
		
		Proof proof = ProofParser.fromString("{(~A & ~(~A & (B | Lx))) | ((∀x)Cx | ~(∀x)Dx)=>(~A & ~(~A & (B | Lx))) | ~(~(∀x)Cx & (∀x)Dx)}");
    	proof.addLine(ProofParser.fromString("(~A & ~(~A & (B | Lx))) | ~(~(∀x)Cx & (∀x)Dx)", "1 DM"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void commutativityTest() {
		
		Proof proof = ProofParser.fromString("{(C | D)=>(D | C)}");
    	proof.addLine(ProofParser.fromString("(D | C)", "1 COM"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void commutativityBTest() {
		
		Proof proof = ProofParser.fromString("{(C & (D | H))=>(H | D)}");
    	proof.addLine(ProofParser.fromString("(D | H) & C", "1 COM"));
    	proof.addLine(ProofParser.fromString("(D | H)", "2 SIMP"));
    	proof.addLine(ProofParser.fromString("(H | D)", "3 COM"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void commutativityCTest() {
		
		Proof proof = ProofParser.fromString("{(C & ~(D | H))=>~(H | D)}");
    	proof.addLine(ProofParser.fromString("~(D | H) & C", "1 COM"));
    	proof.addLine(ProofParser.fromString("~(D | H)", "2 SIMP"));
    	proof.addLine(ProofParser.fromString("~(H | D)", "3 COM"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void associativityATest() {
		
		Proof proof = ProofParser.fromString("{A & (B & C)=>(A & B) & C}");
    	proof.addLine(ProofParser.fromString("(A & B) & C", "1 ASSOC"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void associativityBTest() {
		
		Proof proof = ProofParser.fromString("{A & (~B & C)=>(A & ~B) & C}");
    	proof.addLine(ProofParser.fromString("(A & ~B) & C", "1 ASSOC"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void associativityCTest() {
		
		Proof proof = ProofParser.fromString("{A & (~B & ((C & H) & ~Kx))=>(A & ~B) & ((C & H) & ~Kx)}");
    	proof.addLine(ProofParser.fromString("(A & ~B) & ((C & H) & ~Kx)", "1 ASSOC"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void distributionATest() {
		
		Proof proof = ProofParser.fromString("{(A & B) | (A & C)=>A & (B | C)}");
    	proof.addLine(ProofParser.fromString("(A & (B | C))", "1 DIST"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void distributionBTest() {
		
		Proof proof = ProofParser.fromString("{(A | B) & (A | C)=>A | (B & C)}");
    	proof.addLine(ProofParser.fromString("A | (B & C)", "1 DIST"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void distributionCTest() {
		
		Proof proof = ProofParser.fromString("{(A & B) | (A & ((Lx | Hx) & (Lx | Nx)))=>B | (Lx | (Hx & Nx))}");
    	proof.addLine(ProofParser.fromString("A & (B | ((Lx | Hx) & (Lx | Nx)))", "1 DIST"));
    	proof.addLine(ProofParser.fromString("(B | ((Lx | Hx) & (Lx | Nx))) & A", "2 COM"));
    	proof.addLine(ProofParser.fromString("(B | ((Lx | Hx) & (Lx | Nx)))", "3 SIMP"));
    	proof.addLine(ProofParser.fromString("B | (Lx | (Hx & Nx))", "4 DIST"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void doubleNegationATest() {
		
		Proof proof = ProofParser.fromString("{~~A=>A}");
    	proof.addLine(ProofParser.fromString("A", "1 DN"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void doubleNegationBTest() {
		
		Proof proof = ProofParser.fromString("{~~A & ~~B=>~~A & B}");
    	proof.addLine(ProofParser.fromString("~~A & B", "1 DN"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void doubleNegationCTest() {
		
		Proof proof = ProofParser.fromString("{(~~An & ~~Bn) | Dn=>~~(~~An & Bn) | ~~Dn}");
    	proof.addLine(ProofParser.fromString("(~~An & Bn) | Dn", "1 DN"));
    	proof.addLine(ProofParser.fromString("(~~An & Bn) | ~~Dn", "2 DN"));
    	proof.addLine(ProofParser.fromString("~~(~~An & Bn) | ~~Dn", "3 DN"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void transpositionATest() {
		
		Proof proof = ProofParser.fromString("{A → B=>~B → ~A}");
    	proof.addLine(ProofParser.fromString("~B → ~A", "1 TRANS"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void transpositionBTest() {
		
		Proof proof = ProofParser.fromString("{A → B=>(A → B) | (D → H)}");
    	proof.addLine(ProofParser.fromString("~B → ~A", "1 TRANS"));
    	proof.addLine(ProofParser.fromString("A → B", "2 TRANS"));
    	proof.addLine(ProofParser.fromString("(A → B) | (~H → ~D)", "3 ADD"));
    	proof.addLine(ProofParser.fromString("(A → B) | (D → H)", "4 TRANS"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void transpositionCTest() {
		
		Proof proof = ProofParser.fromString("{~~(~~Ha → (Fx → ~Sx))=>~~(~~Ha → (Sx → ~Fx))}");
    	proof.addLine(ProofParser.fromString("~~(~~Ha → (Sx → ~Fx))", "1 TRANS"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialImplicationATest() {
		
		Proof proof = ProofParser.fromString("{A → B=>~A | B}");
    	proof.addLine(ProofParser.fromString("~A | B", "1 IMPL"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialImplicationBTest() {
		
		Proof proof = ProofParser.fromString("{H | ~(C → B)//~H=>(C & ~B)}");
    	proof.addLine(ProofParser.fromString("~(C → B)", "1,2 DS"));
    	proof.addLine(ProofParser.fromString("~(~C | B)", "3 IMPL"));
    	proof.addLine(ProofParser.fromString("(C & ~B)", "4 DM"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialImplicationCTest() {
		
		Proof proof = ProofParser.fromString("{~~(Nx → ~(C → ~~(Ah → Bh)))=>~~(Nx → ~(C → ~~(Bh | ~Ah)))}");
    	proof.addLine(ProofParser.fromString("~~(Nx → ~(C → ~~(~Ah | Bh)))", "1 IMPL"));
    	proof.addLine(ProofParser.fromString("~~(Nx → ~(C → ~~(Bh | ~Ah)))", "2 COM"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialImplicationDTest() {
		
		Proof proof = ProofParser.fromString("{~~(Nx → ~(C → ~~(Ah → Bh)))=>~~(Nx → ~(~C | ~~(Bh | ~Ah)))}");
    	proof.addLine(ProofParser.fromString("~~(Nx → ~(C → ~~(~Ah | Bh)))", "1 IMPL"));
    	proof.addLine(ProofParser.fromString("~~(Nx → ~(C → ~~(Bh | ~Ah)))", "2 COM"));
    	proof.addLine(ProofParser.fromString("~~(Nx → ~(~C | ~~(Bh | ~Ah)))", "3 IMPL"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialEquivalenceATest() {
		
		Proof proof = ProofParser.fromString("{(A → B) & (B → A)=>A \u2194 B}");
    	proof.addLine(ProofParser.fromString("A \u2194 B", "1 EQUIV"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialEquivalenceBTest() {
		
		Proof proof = ProofParser.fromString("{A \u2194 B=>(A → B) & (B → A)}");
    	proof.addLine(ProofParser.fromString("(A → B) & (B → A)", "1 EQUIV"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void materialEquivalenceCTest() {
		
		Proof proof = ProofParser.fromString("{H & ((A \u2194 B) & (Rx \u2194 Sx))=>H & (((A → B) & (B → A)) & ((Rx & Sx) | (~Rx & ~Sx)))}");
    	proof.addLine(ProofParser.fromString("H & (((A → B) & (B → A)) & (Rx \u2194 Sx))", "1 EQUIV"));
    	proof.addLine(ProofParser.fromString("H & (((A → B) & (B → A)) & ((Rx & Sx) | (~Rx & ~Sx)))", "2 EQUIV"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void exportationATest() {
		
		/*Proof proof = ProofParser.fromString("{(H & C) → D=>H → (C → D)}");
    	proof.addLine(ProofParser.fromString("H → (C → D)", "1 EXP"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");*/
		
	}
	
	
	@Test
	public void exportationBTest() {
		
		Proof proof = ProofParser.fromString("{((H & C) → D) & (H → (C → D))=>(H → (C → D)) & ((H & C) → D)}");
    	proof.addLine(ProofParser.fromString("(H → (C → D)) & (H → (C → D))", "1 EXP"));
    	proof.addLine(ProofParser.fromString("(H → (C → D)) & ((H & C) → D)", "2 EXP"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void exportationCTest() {
		
		Proof proof = ProofParser.fromString("{~((Rx & Ax) | (C \u2194 Nfxs)) \u2194 ~(((H & ~C) → D) & (H → (~C → D)))=>~((Rx & Ax) | (C \u2194 Nfxs)) \u2194 ~((H → (~C → D)) & ((H & ~C) → D))}");
    	proof.addLine(ProofParser.fromString("~((Rx & Ax) | (C \u2194 Nfxs)) \u2194 ~((H → (~C → D)) & (H → (~C → D)))", "1 EXP"));
    	proof.addLine(ProofParser.fromString("~((Rx & Ax) | (C \u2194 Nfxs)) \u2194 ~((H → (~C → D)) & ((H & ~C) → D))", "2 EXP"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void tautologyATest() {
		
		Proof proof = ProofParser.fromString("{A & A=>A}");
    	proof.addLine(ProofParser.fromString("A", "1 TAUT"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void tautologyBTest() {
		
		Proof proof = ProofParser.fromString("{(A & A) & (C & (C & (D & D)))=>(C & (C & D))}");
    	proof.addLine(ProofParser.fromString("(C & (C & (D & D))) & (A & A)", "1 COM"));
    	proof.addLine(ProofParser.fromString("(C & (C & (D & D)))", "2 SIMP"));
    	proof.addLine(ProofParser.fromString("(C & (C & D))", "3 TAUT"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void tautologyCTest() {
		
		Proof proof = ProofParser.fromString("{(A & A) & (C & (C \u2194 (~Dxsa & ~Dxsa)))=>(A & A) & (C & (C \u2194 ~Dxsa))}");
    	proof.addLine(ProofParser.fromString("(A & A) & (C & (C \u2194 ~Dxsa))", "1 TAUT"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void UIATest() {
		
		Proof proof = ProofParser.fromString("{(∀x)Ax=>Aa}");
    	proof.addLine(ProofParser.fromString("Aa", "1 UI"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void UIBTest() {
		
		Proof proof = ProofParser.fromString("{(∀x)Axfg=>Avfg}");
    	proof.addLine(ProofParser.fromString("Avfg", "1 UI"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void UICTest() {
		
		Proof proof = ProofParser.fromString("{(∀x)(∀z)Axzg=>Aazg}");
    	proof.addLine(ProofParser.fromString("(∀z)Aazg", "1 UI"));
    	proof.addLine(ProofParser.fromString("Aazg", "2 UI"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}

	@Test
	public void UIDTest() {
		
		Proof proof = ProofParser.fromString("{(∀y)(Ey & (∀x)(∀z)Axzy)=>Aabc}");
    	proof.addLine(ProofParser.fromString("Ec & (∀x)(∀z)Axzc", "1 UI"));
    	proof.addLine(ProofParser.fromString("(∀x)(∀z)Axzc & Ec", "2 COM"));
    	proof.addLine(ProofParser.fromString("(∀x)(∀z)Axzc", "3 SIMP"));
    	proof.addLine(ProofParser.fromString("(∀z)Aazc", "4 UI"));
    	proof.addLine(ProofParser.fromString("Aabc", "5 UI"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void UIETest() {
		
		Proof proof = ProofParser.fromString("{(∀y)((∀y)Ey & (∀x)(∀z)Axzy)=>Aabc}");
    	proof.addLine(ProofParser.fromString("(∀y)Ey & (∀x)(∀z)Axzc", "1 UI"));
    	proof.addLine(ProofParser.fromString("(∀x)(∀z)Axzc & (∀y)Ey", "2 COM"));
    	proof.addLine(ProofParser.fromString("(∀x)(∀z)Axzc", "3 SIMP"));
    	proof.addLine(ProofParser.fromString("(∀z)Aazc", "4 UI"));
    	proof.addLine(ProofParser.fromString("Aabc", "5 UI"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void UIFTest() {
		
		Proof proof = ProofParser.fromString("{(∀z)(∀y)((∀y)Eyz & (∀x)(∀z)Axzy)=>Eua}");
    	proof.addLine(ProofParser.fromString("(∀y)((∀y)Eya & (∀x)(∀z)Axzy)", "1 UI"));
    	proof.addLine(ProofParser.fromString("(∀y)Eya & (∀x)(∀z)Axzu", "2 UI"));
    	proof.addLine(ProofParser.fromString("(∀y)Eya", "3 SIMP"));
    	proof.addLine(ProofParser.fromString("Eua", "4 UI"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void EIATest() {
		
		Proof proof = ProofParser.fromString("{(∃x)Ax=>Aa}");
    	proof.addLine(ProofParser.fromString("Aa", "1 EI"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void EIBTest() {
		
		Proof proof = ProofParser.fromString("{(∃y)Ayyy=>Attt}");
    	proof.addLine(ProofParser.fromString("Attt", "1 EI"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void EICTest() {
		
		Proof proof = ProofParser.fromString("{Ea & (∃y)Ey=>Eb}");
    	proof.addLine(ProofParser.fromString("(∃y)Ey & Ea", "1 COM"));
    	proof.addLine(ProofParser.fromString("(∃y)Ey", "2 SIMP"));
    	proof.addLine(ProofParser.fromString("Eb", "3 EI"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void EIDTest() {
		
		Proof proof = ProofParser.fromString("{(∃z)((∀x)Exaz & (∃x)Exxz)=>Eaak}");
    	proof.addLine(ProofParser.fromString("(∀x)Exak & (∃x)Exxk", "1 EI"));
    	proof.addLine(ProofParser.fromString("(∀x)Exak", "2 SIMP"));
    	proof.addLine(ProofParser.fromString("Eaak", "3 UI"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}
	
	@Test
	public void EIETest() {
		
		Proof proof = ProofParser.fromString("{(∃x)((∀x)Waxs & (∃x)(∃z)Exxz)=>Ejjk}");
    	proof.addLine(ProofParser.fromString("(∀x)Waxs & (∃x)(∃z)Exxz", "1 EI"));
    	proof.addLine(ProofParser.fromString("(∃x)(∃z)Exxz & (∀x)Waxs", "2 COM"));
    	proof.addLine(ProofParser.fromString("(∃x)(∃z)Exxz", "3 SIMP"));
    	proof.addLine(ProofParser.fromString("(∃z)Ejjz", "4 EI"));
    	proof.addLine(ProofParser.fromString("Ejjk", "5 EI"));
    	
    	ProofResult result = proof.build(false);
		
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
		
	}

    /*@Test
    public void UGATest() {

        Proof proof = ProofParser.fromString("{Wx & Bx=>(∀x)(Wx & Bx)}");

        proof.addLine(ProofParser.fromString("(∀x)(Wx & Bx)", "1 UG"));

        ProofResult result = proof.build(false);

        if(result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

    }

    @Test
    public void UGBTest() {

        Proof proof = ProofParser.fromString("{Wx & Bx=>(∀x)(Wx & Bx)}");

        proof.addLine(ProofParser.fromString("(∀x)(Wx & Bx)", "1 UG"));

        ProofResult result = proof.build(false);

        if(result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

    }

    @Test
    public void UGCTest() {

        Proof proof = ProofParser.fromString("{Ax=>(∀y)(∀x)(Ax | By)}");

        proof.addLine(ProofParser.fromString("Ax | By", "1 ADD"));
        proof.addLine(ProofParser.fromString("(∀x)(Ax | By)", "2 UG"));
        proof.addLine(ProofParser.fromString("(∀y)(∀x)(Ax | By)", "3 UG"));

        ProofResult result = proof.build(false);

        if(result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

    } */

    @Test
    public void UGDTest() {

        Proof proof = ProofParser.fromString("{Ax=>(∀x)(Bx | (∀x)(Ax))}");

        proof.addLine(ProofParser.fromString("(∀x) Ax", "1 UG"));
        proof.addLine(ProofParser.fromString("(∀x) Ax | Bx", "2 ADD"));
        proof.addLine(ProofParser.fromString("Bx | (∀x)(Ax)", "3 COM"));
        proof.addLine(ProofParser.fromString("(∀x)(Bx | (∀x)(Ax))", "4 UG"));

        ProofResult result = proof.build(true);

        if(result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

    }

    @Test
	public void implicationCTest() {
		
		Proof proof = ProofParser.fromString("{(N → A) & (~N → ~A)=>N \u2194 A}");
		
    	proof.addLine(ProofParser.fromString("(N → A) & (A → N)", "1 TRANS"));
    	proof.addLine(ProofParser.fromString("N \u2194 A", "2 EQUIV"));
    	
    	ProofResult result = proof.build(false);
    	
    	if(result.getType() != ResultType.CORRECT)
    		fail("El resultado fue " + result.getType() + "!");
    	
	}

	@Test
	public void copiATest() {

        Proof proof = ProofParser.fromString("{(∀x)(Hx → Mx)//Hs=>Ms}");

        proof.addLine(ProofParser.fromString("(Hs → Ms)", "1 UI"));
        proof.addLine(ProofParser.fromString("Ms", "3,2 MP"));

        ProofResult result = proof.build(false);

        if (result.getType() != ResultType.CORRECT)
            fail("El resultado fue " + result.getType() + "!");

    }

}
