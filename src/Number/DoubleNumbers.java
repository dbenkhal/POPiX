package Number;

import Util.Environment;
import java.util.*;
import Expression.AbstractClass;

public class DoubleNumbers extends Numbers {
	double x;
	AbstractClass y;

	public float getFloatValue() {
		return (float) getValue();
	}

	public int getIntValue() {
		return (int) getValue();
	}

	public double getDoubleValue() {
		return (double) getValue();
	}

	public DoubleNumbers(double x) {
		this.x = x;
	}

	public double getValue() {
		return x;

	}

	public int ufp() {
		int res;
		if (x == 0)
			res = 0;
		else
			res = (int) Math.floor(Math.log(Math.abs(x)) / Math.log(2));
		return res;
	}

	public int ufpErr() {
		return ufp() - prec;
	}

	public int ulp() {
		return ufp() - prec + 1;
	}

	public Numbers add(Numbers y) {
		return new DoubleNumbers(this.getValue() + y.getDoubleValue());

	}

	public Numbers subtract(Numbers y) {
		return new DoubleNumbers(this.getValue() - y.getDoubleValue());

	}

	public Numbers multiply(Numbers y) {
		return new DoubleNumbers(this.getValue() * y.getDoubleValue());

	}

	public Numbers divide(Numbers y) {
		return new DoubleNumbers(this.getValue() / y.getDoubleValue());

	}

	public Numbers Rand(Numbers xmin, Numbers xmax) {
		Random alea = new Random();
		return new DoubleNumbers(
				alea.nextDouble() * (xmax.getDoubleValue() - xmin.getDoubleValue()) + xmin.getDoubleValue());

	}

	public Numbers Rand(double xmin, double xmax) {
		Random alea = new Random();
		return new DoubleNumbers(alea.nextDouble() * (xmax - xmin) + xmin);

	}

	public Boolean LESS(Numbers y) {
		return this.getValue() < y.getDoubleValue();

	}

	public Boolean LESSEQUAL(Numbers y) {
		return this.getValue() <= y.getDoubleValue();

	}

	public Boolean GREATER(Numbers y) {
		return this.getValue() > y.getDoubleValue();

	}

	public Boolean GREATEREQUAL(Numbers y) {
		return this.getValue() > y.getDoubleValue();

	}

	public Boolean EQUAL(Numbers y) {
		return this.getValue() == y.getDoubleValue();

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

	public String toStringLab() {
		return x + "|" + lab + "|";
	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get(lab);
		return x + "|" + this.ufp() + "," + val + "|";
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

	public String toStringInt() {
		return (int) x + "";
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

	public String toStringFix(Map<String, Integer> env) {
		int frac;
		Integer val = env.get(AbstractClass.prefix + "lab" + lab);
		frac = val - ufp();
		return "fx_dtox(" + x + "," + val + ")";
	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
	}
}