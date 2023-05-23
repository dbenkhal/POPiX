package Expression;

import java.util.Map;
import Number.*;
import Util.Environment;

public interface Expression {

	public Numbers evaluate(Environment env);

	public void Generate_constraints(Map<String, String> varEnv);

	public void Generate_Constraints_GLPK(Map<String, String> varEnv);

	public void Generate_Constraints_GLPK_Prime(Map<String, String> varEnv);

	public void Generate_Constraints_AC(Map<String, String> varEnv);

	public void Generate_constraints_Backward(Map<String, String> varEnv);

	public String toString();

	public String toStringLab();

	public String toStringRes(Map<String, Integer> env);

	public String toStringFix(Map<String, Integer> env);

	public abstract void sameLabel();

	public int getFixPrec(Map<String, Integer> env);

}