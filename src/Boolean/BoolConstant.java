package Boolean;

import java.util.*;
import Constraint.*;
import Number.*;
import Util.Environment;

public class BoolConstant extends AbstractClassB {

	Boolean valeur;
	Boolean res;

	public BoolConstant(Boolean val) {
		valeur = val;

	}

	public Boolean evaluate(Environment env) {
		return valeur;

	}

	public String symbol() {
		return "" + valeur;
	}

	public String toString() {
		return "" + valeur;
	}

	public String toStringInt() {
		return "" + valeur;
	}

	public String toStringLab() {
		return valeur + "|" + lab + "| ";
	}

	public String toStringRes(Map<String, Integer> env) {
		return valeur.toString();
	}

	public String toStringFix(Map<String, Integer> env) {
		return valeur.toString();
	}

	public Map<String, String> Generate_constraints(Map<String, String> varEnv) {
		return varEnv;

	}

	public Map<String, String> Generate_constraints_Backward(Map<String, String> varEnv) {
		return varEnv;

	}

	public Intervalle getInterv() {
		return null;
	}

	public String toStringOriginal() {
		String s = valeur.toString();
		return s;
	}

	public String toStringPython(Map<String, Integer> env) {
		String val = valeur.toString();
		return val;
	}

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv) {
		return varEnv;
	}

	public Map<String, String> Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		return varEnv;

	}

	public void sameLabel() {
	}

	public Map<String, String> Generate_Constraints_AC(Map<String, String> varEnv) {
		return varEnv;

	}

}