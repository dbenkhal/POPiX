package Boolean;

import java.util.Map;
import Util.Environment;

public interface BoolExpression {

	public abstract Boolean evaluate(Environment env);

	public Map<String, String> Generate_constraints(Map<String, String> varEnv);

	public Map<String, String> Generate_constraints_Backward(Map<String, String> varEnv);

	public Map<String, String> Generate_Constraints_AC(Map<String, String> varEnv);

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv);

	public Map<String, String> Generate_Constraints_GLPK_Prime(Map<String, String> varEnv);

	public abstract String toStringLab();

	public abstract String toStringRes(Map<String, Integer> env);

	public abstract String toStringFix(Map<String, Integer> env);

}
