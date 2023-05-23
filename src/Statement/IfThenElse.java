package Statement;

import Util.Environment;
import java.util.*;
import Boolean.AbstractClassB;
import Constraint.*;
import Expression.AbstractClass;
import Number.FloatNumbers;

public class IfThenElse extends AbstractClassS {

	public AbstractClassB cond;
	public StatementList thenBlock;
	public StatementList elseBlock;
	ArrayList<Constraints> if_then_else_list = new ArrayList<Constraints>();
	ArrayList<Constraints> if_then_else_list_AC = new ArrayList<Constraints>();

	public IfThenElse(AbstractClassB condd, StatementList thenBlockk, StatementList elseBlockk) {
		cond = condd;
		thenBlock = thenBlockk;
		elseBlock = elseBlockk;
		lab = AbstractClass.newLab();
	}

	public IfThenElse(AbstractClassB condd, StatementList thenBlockk) {
		cond = condd;
		thenBlock = thenBlockk;
		elseBlock = new StatementList(new Assign(new FloatNumbers(0), ""));
		lab = AbstractClass.newLab();
	}

	public AbstractClassB condition() {
		return cond;

	}

	public StatementList thenBlock() {
		return thenBlock;

	}

	public StatementList elseBlock() {
		return elseBlock;
	}

	public Environment evaluate(Environment env) {
		StatementList s2;
		if (condition().evaluate(env) == true) {
			s2 = thenBlock();
		} else {
			s2 = elseBlock();
		}
		env = s2.evaluate(env);
		return env;

	}

	public String toString(int indent) {
		indent = 1;
		String s1 = condition().toString();
		String s2 = thenBlock().toString(indent + 1);
		String s3 = elseBlock().toString(indent + 2);
		return ("if (" + s1 + ") \n {" + s2 + " }\n else {\n" + s3 + "}");

	}

	public String toStringLab(int indent) {
		indent = 1;
		String s1 = condition().toStringLab();
		String s2 = thenBlock().toStringLab(indent + 1);
		String s3 = elseBlock().toStringLab(indent + 2);
		return ("if (" + s1 + " |" + lab + " |" + ") \n {" + s2 + " |" + lab + " |" + " }\n else {\n" + s3 + " |" + lab
				+ " |" + "}");
	}

	public String toStringRes(int indent, Map<String, Integer> env) {
		indent = 1;
		String s1 = condition().toString();
		String s2 = thenBlock().toStringRes(indent + 2, env);
		String s3 = elseBlock().toStringRes(indent + 2, env);
		return (spaces(indent) + "if (" + s1 + ") \n {" + s2 + " }\n else {\n" + s3 + "}");
	}

	public String toStringFix(int indent, Map<String, Integer> env) {
		return "if(" + cond + "){" + thenBlock.toStringFix(indent, env) + "} else {"
				+ elseBlock.toStringFix(indent, env) + "};";

	}

	public String symbol() {
		return null;

	}

	public Map<String, String> Generate_constraints(Map<String, String> varEnv) {
		Map<String, String> varEnv2 = cond.Generate_constraints(varEnv);
		Map<String, String> varEnvThen = thenBlock.Generate_constraints(varEnv2);
		Map<String, String> varEnvElse = elseBlock.Generate_constraints(varEnv2);
		Set<String> varSet = varEnv2.keySet();
		Iterator<String> it = varSet.iterator();
		while (it.hasNext()) {
			String id = it.next();
			String id_then = "accf_lab" + varEnvThen.get(id);
			String id_else = "accf_lab" + varEnvElse.get(id);
			String id_endif = "accf_lab" + varEnvThen.get(id) + "_" + varEnvElse.get(id);
			Constraints thenCstr = new Constraints(id_endif, new CstrId(id_then), ">=");
			if_then_else_list.add(thenCstr);
			Constraints elseCstr = new Constraints(id_endif, new CstrId(id_else), ">=");
			if_then_else_list.add(elseCstr);
		}
		ConstraintsSet.allCst.addAll(if_then_else_list);

		return varEnv;

	}

	public Map<String, String> Generate_constraints_Backward(Map<String, String> varEnv) {
		Map<String, String> varEnv2 = cond.Generate_constraints_Backward(varEnv);
		Map<String, String> varEnvThen = thenBlock.Generate_constraints_Backward(varEnv2);
		Map<String, String> varEnvElse = elseBlock.Generate_constraints_Backward(varEnv2);
		Set<String> varSet = varEnv2.keySet();
		Iterator<String> it = varSet.iterator();
		while (it.hasNext()) {
			String id = it.next();
			String id_then = "accb_lab" + varEnvThen.get(id);
			String id_else = "accb_lab" + varEnvElse.get(id);
			String id_endif = "accb_lab" + varEnvThen.get(id) + "_" + varEnvElse.get(id);
			Constraints thenCstr = new Constraints(id_endif, new CstrId(id_then), "<=");
			if_then_else_list.add(thenCstr);
			Constraints elseCstr = new Constraints(id_endif, new CstrId(id_else), "<=");
			if_then_else_list.add(elseCstr);
		}
		ConstraintsSet.allCst.addAll(if_then_else_list);
		return varEnv;

	}

	public String toStringOriginal(int tab) {
		String s1 = condition().toStringOriginal();
		String s2 = thenBlock().toStringOriginal(tab + 1);
		String s3 = elseBlock().toStringOriginal(tab + 1);
		return ntab(tab) + "if (" + s1 + "):\n" + s2 + "\n" + ntab(tab) + "else:\n" + s3;
	}

	public String toStringPython(int tab, Map<String, Integer> env) {
		String s1 = condition().toStringOriginal();
		String s2 = thenBlock().toStringPython(tab + 1, env);
		String s3 = elseBlock().toStringPython(tab + 1, env);
		return ntab(tab) + "if (" + s1 + "):\n" + s2 + "\n" + ntab(tab) + "else:\n" + s3;
	}

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv) {
		Map<String, String> varEnv2 = cond.Generate_Constraints_GLPK(varEnv);
		Map<String, String> varEnvThen = thenBlock.Generate_Constraints_GLPK(varEnv2);
		Map<String, String> varEnvElse = elseBlock.Generate_Constraints_GLPK(varEnv2);
		Set<String> varSet = varEnv2.keySet();
		Iterator<String> it = varSet.iterator();
		while (it.hasNext()) {
			String id = it.next();
			String id_then = "accb" + "_lab" + varEnvThen.get(id);
			String id_else = "accb" + "_lab" + varEnvElse.get(id);
			String id_endif = "accb" + "_lab" + lab;
			Constraints thenCstr = new Constraints(id_then, new CstrId(id_endif), ">=");
			if_then_else_list.add(thenCstr);
			Constraints elseCstr = new Constraints(id_else, new CstrId(id_endif), ">=");
			if_then_else_list.add(elseCstr);
			varEnv.put(id, lab + "");
		}
		ConstraintsSet.allCst.addAll(if_then_else_list);
		return varEnv;
	}

	public Map<String, String> Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		Map<String, String> varEnv2 = cond.Generate_Constraints_GLPK_Prime(varEnv);
		Map<String, String> varEnvThen = thenBlock.Generate_Constraints_GLPK_Prime(varEnv2);
		Map<String, String> varEnvElse = elseBlock.Generate_Constraints_GLPK_Prime(varEnv2);
		Set<String> varSet = varEnv2.keySet();
		Iterator<String> it = varSet.iterator();
		while (it.hasNext()) {
			String id = it.next();
			String id_then = "acce" + "_lab" + varEnvThen.get(id);
			String id_else = "acce" + "_lab" + varEnvElse.get(id);
			String id_endif = "acce" + "_lab" + lab;
			Constraints thenCstr = new Constraints(id_then, new CstrId(id_endif), ">=");
			if_then_else_list.add(thenCstr);
			Constraints elseCstr = new Constraints(id_else, new CstrId(id_endif), ">=");
			if_then_else_list.add(elseCstr);
			varEnv.put(id, lab + "");
		}
		ConstraintsSet.allCst.addAll(if_then_else_list);
		return varEnv;
	}

	public void sameLabel() {
		if (AbstractClass.sameLab != 0) {
			thenBlock.sameLabel();
			elseBlock.sameLabel();
		}
	}

	public Map<String, String> Generate_Constraints_AC(Map<String, String> varEnv) {
		Map<String, String> varEnv2 = cond.Generate_Constraints_GLPK(varEnv);
		Map<String, String> varEnvThen = thenBlock.Generate_Constraints_GLPK(varEnv2);
		Map<String, String> varEnvElse = elseBlock.Generate_Constraints_GLPK(varEnv2);
		Set<String> varSet = varEnv2.keySet();
		Iterator<String> it = varSet.iterator();
		while (it.hasNext()) {
			String id = it.next();
			String id_then = "ulp" + "_lab" + varEnvThen.get(id);
			String id_else = "ulp" + "_lab" + varEnvElse.get(id);
			String id_endif = "ulp" + "_lab" + lab;
			Constraints thenCstr = new Constraints(id_then, new CstrId(id_endif), ">=");
			if_then_else_list_AC.add(thenCstr);
			Constraints elseCstr = new Constraints(id_else, new CstrId(id_endif), ">=");
			if_then_else_list_AC.add(elseCstr);
			varEnv.put(id, lab + "");
		}
		ConstraintsSet.allCst.addAll(if_then_else_list_AC);
		return varEnv;

	}
}
