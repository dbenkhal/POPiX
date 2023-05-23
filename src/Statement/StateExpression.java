package Statement;

import java.util.*;
import Util.Environment;

public interface StateExpression {

	public abstract Environment evaluate(Environment env);

	public Map<String, String> Generate_constraints(Map<String, String> varEnv);

	public Map<String, String> Generate_constraints_Backward(Map<String, String> varEnv);

	public abstract String toString(int indent);

	public abstract String toStringLab(int indent);

	public abstract String toStringRes(int indent, Map<String, Integer> env);

	public abstract String toStringOriginal(int tab);

	public abstract String toStringPython(int tab, Map<String, Integer> env);

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv);

	public Map<String, String> Generate_Constraints_AC(Map<String, String> varEnv);

	public String toStringFix(int indent, Map<String, Integer> env);

	public Map<String, String> Generate_Constraints_GLPK_Prime(Map<String, String> varEnv); // d: true = fwd, false =
	// bckwd

	public abstract void sameLabel();

}
