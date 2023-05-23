package Number;

import java.util.*;

import Expression.AbstractClass;
import Util.Environment;

public class IntNumbers extends Numbers {

	int x;
	int l;
	public int value;

	public IntNumbers(int va) {
		value = va;

	}

	public IntNumbers(int va, int lab) {
		value = va;
		l = lab;

	}

	public int getValue() {
		return value;
	}

	public float getFloatValue() {
		return (float) getValue();

	}

	public int getIntValue() {
		return getValue();

	}

	public double getDoubleValue() {
		return (double) getValue();

	}

	public int ufp() {
		int res;
		if (Math.abs(x) >= 1.0) {
			res = (int) Math.floor(Math.log(Math.abs(x)) / Math.log(2));
		} else {
			res = (int) Math.floor(Math.log(Math.abs(x)) / Math.log(2));
		}
		return res;
	}

	public int ulp() {
		return ufp() - prec + 1;

	}

	public int ufpErr() {
		return ufp() - prec;
	}

	public Numbers Rand(Numbers xmin, Numbers xmax) {
		Random alea = new Random();
		return new IntNumbers(alea.nextInt() * (xmax.getIntValue() - xmin.getIntValue()) + xmin.getIntValue());

	}

	public Numbers add(Numbers b) {
		return new IntNumbers(this.getValue() + b.getIntValue());

	}

	public Numbers subtract(Numbers b) {
		return new IntNumbers(this.getValue() - b.getIntValue());
	}

	public Numbers multiply(Numbers b) {
		return new IntNumbers(this.getValue() * b.getIntValue());
	}

	public Numbers divide(Numbers b) {
		return new IntNumbers(this.getValue() / b.getIntValue());
	}

	public Boolean LESS(Numbers b) {
		return this.getValue() < b.getIntValue();
	}

	public Boolean LESSEQUAL(Numbers b) {
		return this.getValue() <= b.getIntValue();
	}

	public Boolean GREATER(Numbers b) {
		return this.getValue() > b.getIntValue();
	}

	public Boolean GREATEREQUAL(Numbers b) {
		return this.getValue() >= b.getIntValue();
	}

	public Boolean EQUAL(Numbers b) {
		return this.getValue() == b.getIntValue();
	}

	public Boolean NOTEQUAL(Numbers b) {
		return this.getValue() != b.getIntValue();
	}

	public Numbers evaluate(Environment env) {
		return this;
	}

	public String symbol() {
		return null;
	}

	public String toString() {
		return x + "";
	}

	public String toStringInt() {
		return x + "";
	}

	public String toStringLab() {
		return x + "|" + lab + "|";
	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get("lab" + lab);
		return x + "|" + this.ufp() + "," + val + "|";
	}

	public String toStringFix(Map<String, Integer> env) {
		int frac;
		Integer val = env.get(AbstractClass.prefix + "lab" + lab);
		frac = val - ufp();
		return "fx_itox(" + x + "," + frac + ")";
	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
	}

	public void Generate_constraints(Map<String, String> varEnv) {

	}

	public void Generate_constraints_Backward(Map<String, String> varEnv) {

	}

	public int getufp() {
		return ufp();
	}

	public Intervalle getInterv() {
		return null;
	}

	public int ulpErrCst() {
		int ulperr = val.ufpErr();
		return ulperr;
	}

	public Numbers getElmt(int i) {
		System.out.println("array expected");
		return null;
	}

	public void setElmt(int n, Numbers i) {
		System.out.println("array expected");

	}

	public String toStringOriginal() {
		String s = "mpfr(" + x + "," + precMpfr + ")";
		return s;
	}

	public String toStringPython(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		String s = "mpfr(" + x + "," + val + ")";
		return s;
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