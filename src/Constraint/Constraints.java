package Constraint;

import java.util.Map;

public class Constraints extends ConstraintsSet {

	public Constraints() {
	}

	public Constraints(String i, ExpressionConstraint e, String s) {
		id = i;
		ec = e;
		symbol = s;

	}

	public String getId() {
		return id;

	}

	public ExpressionConstraint getEc() {
		return ec;

	}

	public String symbol() {
		return symbol;

	}

	public String toString() {
		return getId() + symbol() + getEc();

	}

	public String print() {

		return ("(assert (" + symbol() + " " + id + " " + ec.print() + "))");

	}

	public void setEc(ExpressionConstraint ec2) {
		ec = ec2;
	}

	public void genLPConst() {
		if (symbol.compareTo("<=") == 0) {
			ExpressionConstraint ecc;
			ecc = new CstrLte(new CstrId(id), ec);
			ecc.genLPConst();
		} else if (symbol.compareTo(">=") == 0) {
			ExpressionConstraint ecc;
			ecc = new CstrGeq(new CstrId(id), ec);
			ecc.genLPConst();
		} else if (symbol.compareTo("=") == 0) {
			ExpressionConstraint ecc;
			ecc = new CstrGeq(new CstrId(id), ec);
			ecc.genLPConst();
			ExpressionConstraint ecc2;
			ecc2 = new CstrLte(new CstrId(id), ec);
			ecc2.genLPConst();
		} else {
			System.out.println("Papillon rouge dans genLPCOnst Classe Constraints:" + symbol.toString() + ".");
			System.exit(0);
		}

	}

	public void simGenLPConst() {
		if (symbol.compareTo("<=") == 0) {
			ExpressionConstraint ecc;
			ecc = new CstrLte(new CstrId(id), ec);
			ecc.simGenLPConst();
		} else if (symbol.compareTo(">=") == 0) {
			ExpressionConstraint ecc;
			ecc = new CstrGeq(new CstrId(id), ec);
			ecc.simGenLPConst();
		} else if (symbol.compareTo("=") == 0) {
			ExpressionConstraint ecc;
			ecc = new CstrGeq(new CstrId(id), ec);
			ecc.simGenLPConst();
		} else {
			System.out.println("Papillon rouge dans genLPCOnst Classe Constraints:" + symbol);
			System.exit(0);
		}

	}

	public boolean isEqual(Map<String, Integer> GLPKSolEnv) {
		int valId, valEc;
		valId = GLPKSolEnv.get(id); // x
		valEc = ec.evaluate(GLPKSolEnv); // f(x)
		if (valId == valEc)
			return valId == valEc;
		else {
			return false;
		}
	}

	public void changePolicy(Map<String, Integer> GLPKSolEnv) {
		int valId, valEc;
		valId = GLPKSolEnv.get(id); // x
		valEc = ec.evaluate(GLPKSolEnv); // f(x)
		if (valId != valEc) {
			ec.changePolicy(GLPKSolEnv, valEc);
		}
	}

}
