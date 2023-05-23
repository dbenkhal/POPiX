package Constraint;

import java.util.*;
import Expression.*;
import Number.*;

public abstract class ExpressionConstraint {
	ExpressionConstraint left;
	ExpressionConstraint right;
	public String id;
	public int lab;
	static Numbers coef;
	static int v;

	public ExpressionConstraint(String x) {
		id = x;
	}

	public ExpressionConstraint(ExpressionConstraint lt, ExpressionConstraint rt) {
		left = lt;
		right = rt;
	}

	public ExpressionConstraint() {

	}

	public String getId() {
		return id;

	}

	public String print() {
		return "";
	}

	public abstract int evaluate(Map<String, Integer> env);

	public void changePolicy(Map<String, Integer> GLPKSolEnv, int obj) {
	};

	public abstract void genLPConst();

	public abstract boolean isCstrConstant();

	public abstract Numbers getCstrConstant();

	public abstract void simGenLPConst();

	public Numbers getCoef() {
		return coef;
	}

	public int getVarNumber() {
		return v;
	}

	public int lab2VarNum(int lab) {
		int res = AbstractClass.z3ConstInt.size() + lab + 1;
		int mm = AbstractClass.z3ConstInt.size() + AbstractClass.nbVarSupplement + Constraints.allCst.size()
				+ Constraints.globalLabCstr;
		if (res > mm) {
			System.exit(0);
		}
		return res;
	}

	public static int varName2VarNum(String name) {
		int i = 1;
		int mm = AbstractClass.z3ConstInt.size() + AbstractClass.nbVarSupplement + Constraints.allCst.size()
				+ Constraints.globalLabCstr;
		Iterator<String> it = AbstractClass.z3ConstInt.iterator();
		while (it.hasNext() && (name.compareTo(it.next()) != 0))
			i++;
		if (i > mm) {
			System.exit(0);
		}
		return i;
	}

	static public int newLabCstr() {
		return Constraints.globalLabCstr++;

	}

}
