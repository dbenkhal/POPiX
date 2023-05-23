package Number;

import java.util.*;

import Expression.AbstractClass;
import Util.Environment;

public class PopArray extends Numbers {
	Vector<Numbers> val;

	public PopArray(int i) {
		val = new Vector<Numbers>(i);

	};

	public PopArray(int i, Numbers d) {
		val = new Vector<Numbers>(i);
		for (int j = 0; j < i; j++) {
			val.add(d);
		}
	};

	public Numbers getElmt(int i) {
		return val.get(i);
	};

	public void setElmt(int i, Numbers x) {
		val.set(i, x);

	};

	public Vector<Numbers> getArray() {
		return val;
	};

	public Numbers evaluate(Environment env) {
		return this;
	}

	public void Generate_constraints(Map<String, String> varEnv) {

	}

	public void Generate_constraints_Backward(Map<String, String> varEnv) {

	}

	public String toStringRes(Map<String, Integer> env) {
		return null;// a completer
	}

	public String toStringFix(Map<String, Integer> env) {
		return null;// a completer
	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
	}

	public float getFloatValue() {
		System.out.println("Error array getFloatValue");
		return 0;

	}

	public int getIntValue() {
		System.out.println("Error array getIntValue");
		return 0;
	}

	public double getDoubleValue() {
		return 0;
	}

	public int ufp() {
		System.out.println("Array has no ufp");
		return 0;

	}

	public int getufp() {
		System.out.println("Array has no ufp");
		return 0;
	}

	public int ufpErr() {
		System.out.println("Array has no ufpErr");
		return 0;
	}

	public int ulp() {
		System.out.println("Array has no ulp");
		return 0;
	}

	public int ulpErrCst() {
		System.out.println("Array has no ulpErrCst");
		return 0;
	}

	public Numbers add(Numbers b) {
		System.out.println("Arrays cannot be added");
		return null;
	}

	public Numbers subtract(Numbers b) {
		System.out.println("Arrays cannot be subtracted");
		return null;
	}

	public Numbers multiply(Numbers b) {
		System.out.println("Arrays cannot be multiplied");
		return null;
	}

	public Numbers divide(Numbers b) {
		System.out.println("Arrays cannot be divided");
		return null;
	}

	public Numbers Rand(Numbers xmin, Numbers xmax) {
		System.out.println("No Random arrays");
		return null;
	}

	public Boolean LESS(Numbers b) {
		System.out.println("No arrays less");
		return null;
	}

	public Boolean LESSEQUAL(Numbers b) {
		System.out.println("no arrays lessequal");
		return null;
	}

	public Boolean GREATER(Numbers b) {
		System.out.println("no arrays greater");
		return null;
	}

	public Boolean GREATEREQUAL(Numbers b) {
		System.out.println("no arrays greaterequal");
		return null;
	}

	public Boolean EQUAL(Numbers b) {
		System.out.println("no arrays equal");
		return null;
	}

	public Boolean NOTEQUAL(Numbers b) {
		System.out.println("no arrays notequal");
		return null;
	}

	public String symbol() {
		System.out.println("no arrays symbol");
		return null;
	}

	public Intervalle getInterv() {
		System.out.println("no arrays getInterv");
		return null;
	}

	public String toString() {
		return "[" + length() + "]";
	}

	public String toStringLab() {
		return val.toString() + "|" + lab + "|";

	}

	public String toStringOriginal() {
		return "[" + length() + "]";
	}

	public String toStringPython(Map<String, Integer> env) {
		return "[" + length() + "]";
	}

	public int length() {
		return val.size();
	}

	public void Generate_Constraints_GLPK(Map<String, String> varEnv) {

	}

	public void Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {

	}

	public void sameLabel() {
	}

	public void Generate_Constraints_AC(Map<String, String> varEnv) {

	}

}
