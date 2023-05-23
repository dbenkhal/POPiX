package Constraint;

public class CstrAnd extends ConstraintsSet {

	ConstraintsSet left;
	ConstraintsSet right;
	ExpressionConstraint exp1, exp2;

	public CstrAnd(ConstraintsSet lt, ConstraintsSet rt) {
		left = lt;
		right = rt;
		System.out.println("No and allowed");
		System.exit(0);

	}

	public CstrAnd(ExpressionConstraint a, ExpressionConstraint b) {
		exp1 = a;
		exp2 = b;
	}

	public String symbol() {
		return "&&";

	}

	public String toString() {
		return exp1.toString() + symbol() + exp2.toString();
	}

	public String print() {
		return ("(and" + exp1.print() + " " + exp2.print() + ")");

	}

	public String getId() {
		return null;
	}

	public ExpressionConstraint getEc() {
		return null;
	}

	public void setEc(ExpressionConstraint ec2) {
		this.ec = ec2;
	}

	public void genLPConst() {

	}

	public void simGenLPConst() {

	}

}
