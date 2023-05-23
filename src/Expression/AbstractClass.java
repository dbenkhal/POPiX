package Expression;

import java.util.*;
import Constraint.ConstraintsSet;
import Number.*;

public abstract class AbstractClass implements Expression {
	public int lab;
	public int p;
	public AbstractClass left;
	public AbstractClass right;
	public Numbers xmin;
	public Numbers xmax;
	public Numbers x;
	public int absCancelPrec;
	public static int uniformMode = 1;
	public static String prefix;
	public static int sameLab = 1; // 0 different lab, !=0 only one lab per expression
	public static int xiMode = 0; // 0 => ILP ; 1 Policy iteration (PI)
	public static int fixedMode = 1; // print ufp in results if fixedMode!=0
	public static int accBCount = 0; // counter on accb
	public int prec_trig = 9;
	public static int costFunction = 0; // 0 -> assigned var ; 1 -> minimize max acc ; 2 -> # of casts ; 3 --> sum op
	// sizes of op ; 4 -> assigned var only
	public static String costFunction1MaxAcc = "MaxAccForCostFunction1";
	public static int nbGLPKVars, nbGLPKRows;
	public static Map<String, Integer> arraySize2;
	public static Map<String, Integer> arraySize3;
	public static ArrayList<String> accVars = new ArrayList<String>();
	public static int nbVarSupplement = 0;
	public static HashSet<String> z3ConstInt = new HashSet<String>();
	public static ArrayList<String> accVarsAssignedOnly = new ArrayList<String>();
	public static ArrayList<ConstraintsSet> clogic = new ArrayList<ConstraintsSet>();
	public static Map<String, Integer> statEnv = new HashMap<String, Integer>();
	public static Map<String, String> acc2Id = new HashMap<String, String>();
	public static Map<String, Integer> resultEnv = new HashMap<String, Integer>();
	public static Set<String> outputProgVarList = new LinkedHashSet<String>();// use for fixed-point code generation
	public static Map<String, Numbers> ufpEnv = new HashMap<String, Numbers>();
	public static ArrayList<String> opLabList = new ArrayList<String>();
	public static boolean progForAnalysis = true;
	static public int globalLab = 0;
	static public int prec = 53;
	static public int precMpfr = 500;
	static public int numberSwitch = 0;
	public static int iotaMode = xiMode; // 0 => xi=1 1=> xi=min(max(...
	public static int fixFinalPrec;
	public static Map<String, Integer> declaredIdPrec = new HashMap<String, Integer>();// for fixed code generation,
																						// prec of the assigned var
	public static Map<String, String> fixVarEnv = new HashMap<String, String>();// maps variableto variable with label
	public static ArrayList<Double> floatTAFFORes = new ArrayList<Double>();

	public static HashSet<String> getZ3ConstInt() {
		return z3ConstInt;
	}

	public AbstractClass(AbstractClass a, AbstractClass b) {
		left = a;
		right = b;
		absCancelPrec = prec;
	}

	public AbstractClass() {
		super();
		absCancelPrec = prec;
	}

	public AbstractClass getLeft() {
		return left;
	}

	public AbstractClass getRight() {
		return right;
	}

	public abstract String symbol();

	public abstract String toStringPython(Map<String, Integer> env);

	public abstract String toStringOriginal();

	public abstract String toStringInt();

	public int getLab() {
		return lab;

	}

	public String toString() {
		return getLeft().toString() + symbol() + getRight().toString();

	}

	static public int newLab() {
		return globalLab++;

	}

	static public void newLabDecrease2() {
		globalLab -= 2;

	}

	static public void newLabDecrease() {
		globalLab -= 1;

	}

	public abstract Intervalle getInterv();

	public String toStringLab() {
		return getLeft().toStringLab() + "|lab_" + lab + "|" + symbol() + "|lab_" + lab + "|" + getRight().toStringLab()
				+ "|lab_" + lab + "|";

	};

}