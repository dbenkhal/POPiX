package Constraint;

public class CstrOr extends ConstraintsSet {

	ConstraintsSet left;
	ConstraintsSet right;

	public CstrOr(ConstraintsSet lt, ConstraintsSet rt) {
		left = lt;
		right = rt;

	}

	public String symbol() {
		return "||";
	}

	public String toString() {
		return left.toString() + symbol() + right.toString();
	}

	public String print() {
		return ("(assert (or " + left.print() + " " + right.print() + "))");
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