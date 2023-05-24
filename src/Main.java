import Constraint.*;
import Expression.*;
import Number.*;
import java.io.*;
import java.util.*;
import Statement.*;
import Util.Environment;
import org.antlr.v4.runtime.*;
import java.util.concurrent.TimeUnit;

public class Main {

	static String mypath = new String(" change with POPiX Path here");

	private static void copyFileUsingStream(File source, File dest) throws IOException {

		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	public static void main(String[] args) throws Exception {
		long startTimeParsing = System.nanoTime();
		System.out.println("** Welcome to POPiX, Precision OPtimizer for Fixed-Point Ciode Synthesis...");
		System.out.println("** POPiX version 2.0, May 2023...\n");
		File fich = new File(mypath + "popix_src");
		FileInputStream file = new FileInputStream(fich);
		@SuppressWarnings("deprecation")
		ANTLRInputStream input = new ANTLRInputStream(file);
		int costFun = 0; // 0 = total bits, 1 = var assigned
		// *******************************************************
		// parsing
		// *******************************************************
		System.out.println("Starting parsing...");
		GrammaireLexer lexer = new GrammaireLexer(input);
		TokenStream tokenStream = new CommonTokenStream(lexer);
		GrammaireParser parser = new GrammaireParser(tokenStream);
		Environment env = new Environment();
		// AbstractClassS program = parser.statement().result;
		StatementList program = parser.statementList().st;
		System.out.println("Parsing finished.");
		long endTimeParsing = System.nanoTime();
		long timeElapsedParsing = endTimeParsing - startTimeParsing;
		int ctr = 0;
		Long startZ3 = null;
		// *******************************************************
		// Program with labels 
		// *******************************************************
		try {
			int indent = 1;
			String ProgLab = "popix_labs";
			FileWriter test2 = new FileWriter(ProgLab);
			BufferedWriter out2 = new BufferedWriter(test2);
			out2.write(program.toStringLab(indent));
			out2.close();
		} catch (IOException er) {
			;
		}
		// *******************************************************
		// Program Evaluation with 10000 run
		// *******************************************************
		long startTimeEvaluation = System.nanoTime();
		Long startEvaluation = null;
		System.out.println("Starting dynamic analysis...");
		startEvaluation = System.nanoTime();

		// *******************************************************
		// commented part to read files with input values 
		// *******************************************************
//		String TAFFOData = "/home/dbenkhal/Bureau/TAFFO/data/Doppler1";
//		FileReader TAFFOTest = new FileReader(TAFFOData);
//		BufferedReader inTAFFO = new BufferedReader(TAFFOTest);
//		String l = inTAFFO.readLine();
//		String[] TAFFOVarNames = l.split(", ");
//		String[] TAFFOVarVal = new String[TAFFOVarNames.length];
//		int saveLab = AbstractClass.globalLab;
//		AbstractClass.progForAnalysis = false;
//		for (int i = 0; i < 10000; i++) {
//			l = inTAFFO.readLine();
//			TAFFOVarVal = l.split(", ");
//			for (int j = 0; j < TAFFOVarVal.length; j++) {
//				// env.set(TAFFOVarNames[j], new
//				// DoubleNumbers(Double.parseDouble(TAFFOVarVal[j])));
//				// System.out.println(TAFFOVarNames[j]+ " = " +
//				// Double.parseDouble(TAFFOVarVal[j]));
//				Assign stmt = new Assign(new DoubleNumbers(Double.parseDouble(TAFFOVarVal[j])), TAFFOVarNames[j]);
//				program.add(stmt);
//			}
//			program.evaluate(env);
//			for (int j = 0; j < TAFFOVarVal.length; j++) {
//				program.list.remove(0);
//
//			}
//		}
//		inTAFFO.close();
//		AbstractClass.globalLab = saveLab;
//		for (int j = 0; j < TAFFOVarVal.length; j++) {
//			// env.set(TAFFOVarNames[j], new
//			// DoubleNumbers(Double.parseDouble(TAFFOVarVal[j])));
//			System.out.println(TAFFOVarNames[j] + " = " + Double.parseDouble(TAFFOVarVal[j]));
//			Assign stmt = new Assign(new DoubleNumbers(Double.parseDouble(TAFFOVarVal[j])), TAFFOVarNames[j]);
//			program.add(stmt);
//		}
//		System.out.println(program.toStringLab(0));
//		AbstractClass.progForAnalysis = true;
//		program.evaluate(env);
//		double resTheta = 0, resultatFix = 0;
//
//		String TAFFOFloat = "/home/dbenkhal/Bureau/TAFFO/data/Doppler1.float";
//		FileWriter TAFFOFloatData = new FileWriter(TAFFOFloat);
//		for (int i = 0; i < 10000; i++) {
//			TAFFOFloatData.write(AbstractClass.floatTAFFORes.get(i) + "\n");
//			resTheta = resTheta + AbstractClass.floatTAFFORes.get(i);
//		}
//		resultatFix = resTheta / 10000;
//		// System.out.println("Theta final\n"+resultatFix);
//		TAFFOFloatData.close();
		
		long endTimeEvaluation = System.nanoTime();
		long timeElapsedEvaluation = endTimeEvaluation - startTimeEvaluation;

		
		for (int i = 0; i < 10000; i++) {
			program.evaluate(env);
		}
		// *******************************************************
		// Constraints Generation Phase
		// *******************************************************
		int initTotalBits = 0;
		Long startTime = null;
		String str = "0";
		ArrayList<String> accVars = new ArrayList<String>();
		Map<String, String> ve = new HashMap<String, String>();
		startTime = System.currentTimeMillis();

		// ve = program.Generate_constraints(ve);
		// ve = program.Generate_constraints_Backward(ve);
		// ve = program.Generate_Constraints_GLPK(true, ve);
		long startTimeILP = System.nanoTime();
		ve = program.Generate_Constraints_GLPK(ve);
		long endTimeILP = System.nanoTime();
		long timeElapsedILP = endTimeILP - startTimeILP;
		long startTimeIP = 0;
		long endTimeIP = 0;
		long timeElapsedIP = 0;

		if (AbstractClass.iotaMode != 0) {
			startTimeIP = System.nanoTime();
			ve = program.Generate_Constraints_GLPK_Prime(ve);
			endTimeIP = System.nanoTime();
			timeElapsedIP = endTimeIP - startTimeIP;

		}
		;
		// *******************************************************
		// Constraints Solving Phase with GLPK
		// *******************************************************
		long startTimeSolve = System.nanoTime();
		ConstraintsSet cs = new Constraints();
		cs.solveIP();
		try {
			String IpCstr = "constraints";
			FileWriter IpTest = new FileWriter(IpCstr);
			Iterator<ConstraintsSet> it = ConstraintsSet.allCst.iterator();
			BufferedWriter sortie = new BufferedWriter(IpTest);
			while (it.hasNext()) {
				sortie.write(it.next().toString() + "\n");
			}
			sortie.close();
		} catch (IOException er) {

		}

		try {
			int indent = 1;
			String ProgRes = "popix_output";
			FileWriter test2 = new FileWriter(ProgRes);
			BufferedWriter out2 = new BufferedWriter(test2);
			out2.write(program.toStringRes(indent, AbstractClass.resultEnv));
			out2.close();
		} catch (IOException er) {
			;
		}
		long endTimeSolve = System.nanoTime();
		long timeElapsedSolve = endTimeSolve - startTimeSolve;

		// *******************************************************
		//Fixed-point program in C
		// *******************************************************
		long startTimeFixed = System.nanoTime();
		try {
			int indent = 1;
			String ProgRes = "popix_output.c";
			FileWriter test2 = new FileWriter(ProgRes);
			BufferedWriter out2 = new BufferedWriter(test2);
		//Remember to change the path to the fixmath library
			out2.write("#include \"/home/dbenkhal/Documents/fixmath-1.4/include/fixmath.h\"\n");
			out2.write("#include <stdio.h>\n");
			out2.write("#include <math.h>\n");
			out2.write("#include <time.h>\n");
			out2.write("int main(){ \n");
			Iterator<String> itFix = AbstractClass.outputProgVarList.iterator();
			while (itFix.hasNext()) {
				out2.write("volatile int " + itFix.next().toString() + ";\n");
			}
			out2.write("volatile int POPxxx;" + "\n");
			out2.write(program.toStringFix(indent, AbstractClass.resultEnv));
//			out2.write("error += fabs("+env.getValue(AbstractClassS.require_var_name)+"-fx_xtof("
//			+ AbstractClassS.require_var_name +","+AbstractClass.fixFinalPrec+"));\n"); 

//			out2.write("error += fabs(floatResult-fx_xtof(" + AbstractClassS.require_var_name + ","
//					+ AbstractClass.fixFinalPrec + ")/fabs(floatResult));\n");

			out2.write("printf(\"Float : " + AbstractClassS.require_var_name + " = "
					+ env.getValue(AbstractClassS.require_var_name) + "\\n\");\n");
			out2.write("printf(\"Fix   : " + AbstractClassS.require_var_name + "= %f\\n\",fx_xtof("
					+ AbstractClassS.require_var_name + "," + AbstractClass.fixFinalPrec + "));\n");
			out2.write("return 0;\n} \n");
			out2.close();
		} catch (IOException er) {
			;
		}
		long endTimeFixed = System.nanoTime();
		long timeElapsedFixed = endTimeFixed - startTimeFixed;

		long timeElapsedCompile = 0;
		// compile fix code
		try {
//			ProcessBuilder builder = new ProcessBuilder(mypath + "clang\\bin\\z3", "-smt2", mypath + "essai.z3");
//			builder.redirectOutput(new File(mypath + "essai.res"));
//			builder.redirectError(new File(mypath + "essai.res"));
//			Process p = builder.start(); // may throw IOException
//			p.waitFor();
			long startTimeCompile = System.nanoTime();
			ProcessBuilder builder = new ProcessBuilder("clang", "-O3", mypath + "pop_output.c");
			Process p = builder.start(); // may throw IOException
			ProcessBuilder execute = new ProcessBuilder(mypath + "./a.out");
			p.waitFor();
			long endTimeCompile = System.nanoTime();			
		// *******************************************************
		//compile time floating-point codes in C
		// *******************************************************
			long startTimeCompileC = System.nanoTime();
			ProcessBuilder builderC = new ProcessBuilder("clang", "-O3", mypath + "pop_input.c");
			Process pC = builder.start(); // may throw IOException
			p.waitFor();
			long endTimeCompileC = System.nanoTime();
			long timeElapsedC = endTimeCompileC - startTimeCompileC;
			timeElapsedCompile = endTimeCompile - startTimeCompile;
			System.out.println("===================================");
			System.out.println("timeElapsedParsing: " + timeElapsedParsing);
			System.out.println("timeElapsedEvaluation: " + (timeElapsedEvaluation));
			System.out.println("timeElapsedILP: " + timeElapsedILP);
			System.out.println("timeElapsedSolve: " + (timeElapsedSolve));
			System.out.println("Compilation time in milliseconds:: " + timeElapsedC);

		} catch (Exception e) {
			System.out.println("erreur d'execution " + "clang fichierz3.smt2" + e.toString());
		}

	       // *******************************************************
		// MPFR Files
		// *******************************************************

		try {
			String ProgOrig = "popix_float.py";
			FileWriter test3 = new FileWriter(ProgOrig);
			BufferedWriter out3 = new BufferedWriter(test3);
			out3.write("import gmpy2\nfrom gmpy2 import mpfr\nfrom gmpy2 import xmpz\n" + "\n"
					+ program.toStringOriginal(0));
			out3.close();
		} catch (IOException er) {
			;
		}
		try {
			String ProgMpfr = "popix_mpfr.py";
			FileWriter test4 = new FileWriter(ProgMpfr);
			BufferedWriter out4 = new BufferedWriter(test4);
			out4.write("import gmpy2\nfrom gmpy2 import mpfr\nfrom gmpy2 import xmpz\n" + "\n"
					+ program.toStringPython(0, AbstractClass.resultEnv));
			out4.close();
		} catch (IOException er) {

		}
		Long endEvaluation2 = System.nanoTime();
		long evaluationDuration2 = (endEvaluation2 - startEvaluation); // Total execution time in milli seconds
		long TotalPOPiXTime = timeElapsedCompile + evaluationDuration2;
		System.out.println("Total execution time                          : " + evaluationDuration2 + " nanoseconds\n");
		System.exit(0);
		String z3 = mypath + "essai0.z3";
		try {
			FileWriter test = new FileWriter(z3);
			BufferedWriter out = new BufferedWriter(test);
			Iterator<String> it = AbstractClass.z3ConstInt.iterator();
			while (it.hasNext()) {
				String s = it.next();
				out.write("(declare-const " + s + " Int) \n");
				if (s.contains("acc_lab")) {
					accVars.add(s);
				}
			}

			Iterator<String> it2Vars;
			if (costFun == 0) {
				it2Vars = accVars.iterator();
			} else {
				it2Vars = AbstractClass.accVarsAssignedOnly.iterator();
			}
			while (it2Vars.hasNext()) {
				String itn = it2Vars.next();

				if (AbstractClass.arraySize2.containsKey(AbstractClass.acc2Id.get(itn))) {
					int n = AbstractClass.arraySize2.get(AbstractClass.acc2Id.get(itn));
					initTotalBits += AbstractClass.prec * n;
					AbstractClass.arraySize2.put(AbstractClass.acc2Id.get(itn), 0);
					str = (" (+ " + " (* " + itn + " " + n + ") " + str + ")");
				} else {
					initTotalBits += AbstractClass.prec;
					str = (" (+ " + " " + itn + " " + str + ")");
				}
				;

			}

			it = AbstractClass.z3ConstInt.iterator();
			while (it.hasNext()) {
				String st = it.next();
				if (st.contains("acc_")) {
					String st2 = st.substring(4, st.length());
					out.write("(assert (<= 0 accb_" + st2 + ")) \n");
					out.write("(assert (<= accb_" + st2 + " acc_" + st2 + ")) \n");
					out.write("(assert (<= acc_" + st2 + " accf_" + st2 + ")) \n");
				}

			}
			/* nbe positif */
			it = AbstractClass.z3ConstInt.iterator();
			while (it.hasNext()) {
				String st = it.next();
				if (st.contains("nbe_")) {
					String st2 = st.substring(4, st.length());
					out.write("(assert (< 0 nbe_" + st2 + ")) \n");
				}
			}
			Iterator<ConstraintsSet> iterator = Constraints.allCst.iterator();
			while (iterator.hasNext()) {
				ConstraintsSet myCurrentElement = iterator.next(); /* to check */
				out.write(myCurrentElement.print() + "\n");
			}
			out.close();
		} catch (IOException er) {
			;
		}

		/******************* additional cost constraint ***********************/
		Numbers a = new IntNumbers(0);
		Set<String> seKeys = CreateArray.arraySize.keySet();
		Iterator<String> its = seKeys.iterator();
		int maxFinal = 0;
		while (its.hasNext()) {

			String maxSE = its.next();
			if (CreateArray.arraySize.get(maxSE) > maxFinal) {
				maxFinal = CreateArray.arraySize.get(maxSE);
			}

		}
		Numbers b = new IntNumbers(
				AbstractClass.globalLab * AbstractClass.prec + AbstractClassS.nbTab * maxFinal * AbstractClass.prec);
		File srcFile = new File(mypath + "essai0.z3");
		File destFile = new File(mypath + "essai.z3");
		Numbers x = new IntNumbers(0);
		startZ3 = System.currentTimeMillis();
		Boolean z3won = false;

		// *******************************************************
		// dichotomie
		// *******************************************************
		while (Math.abs(a.getIntValue() - b.getIntValue()) > 1) {
			copyFileUsingStream(srcFile, destFile);
			x = a.add(b).divide(new IntNumbers(2));
			System.out.println("Solving constraints total of " + x.getIntValue() + " bits");
			try {
				String filename = "essai.z3";
				FileWriter fw = new FileWriter(filename, true);
				fw.write("(assert (>= " + x.getIntValue() + " " + str + ")) \n");
				fw.write("(check-sat)" + "\n");
				fw.write("(get-model)" + "\n");
				fw.close();
			} catch (IOException ioe) {
				System.err.println("IOException: + ioe.getMessage()");
			}

			try {
				ProcessBuilder builder = new ProcessBuilder(mypath + "Z3-4.1\\bin\\z3", "-smt2", mypath + "essai.z3");
				builder.redirectOutput(new File(mypath + "essai.res"));
				builder.redirectError(new File(mypath + "essai.res"));
				Process p = builder.start(); // may throw IOException
				p.waitFor();
			} catch (Exception e) {
				System.out.println("erreur d'execution " + "z3 fichierz3.smt2" + e.toString());
			}
			String resultat = mypath + "essai.res";
			InputStream in = new FileInputStream(resultat);
			InputStreamReader input2 = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(input2);
			String ligne;
			ligne = br.readLine();
			if (ligne.contains("unsat") == false) {
				b = x;
				z3won = true;
			} else {
				a = x;
			}

			br.close();
		}

		if (!z3won) {
			System.out.println("Z3 failed, exiting POP :-((");
			System.exit(0);
		}
		/********************************************************************/
		copyFileUsingStream(srcFile, destFile);
		x = a.add(new IntNumbers(1));
		System.out.println("Solving constraints total of " + x.getIntValue() + " bits");
		try {
			String filename = "essai.z3";
			FileWriter fw = new FileWriter(filename, true);
			fw.write("(assert (>= " + x.getIntValue() + " " + str + ")) \n");
			fw.write("(check-sat)" + "\n");
			fw.write("(get-model)" + "\n");
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: + ioe.getMessage()");
		}
		try {
			ProcessBuilder builder = new ProcessBuilder(mypath + "Z3-4.1\\bin\\z3", "-smt2", mypath + "essai.z3");
			builder.redirectOutput(new File(mypath + "essai.res"));
			builder.redirectError(new File(mypath + "essai.res"));
			Process p = builder.start(); // may throw IOException
			p.waitFor();
		} catch (Exception e) {
			System.out.println("erreur d'execution " + "z3 fichierz3.smt2" + e.toString());
		}

		long endZ3 = System.currentTimeMillis();
		long durationZ3 = (endZ3 - startZ3); // Total execution time in milli seconds
		System.out.println(" \nExecution time of Z3: " + durationZ3 + " ms");

		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime); // Total execution time in milli seconds
		System.out.println(" Execution time for constraints generation and solving: " + duration + " ms");

		Long endEvaluation = System.currentTimeMillis();
		long evaluationDuration = (endEvaluation - startEvaluation); // Total execution time in milli seconds
		System.out.println(" Execution time including the evaluation of the program: " + evaluationDuration + " ms\n");

		if (costFun == 0) {
			System.out.println("!!! Cost function: ALL CONTROL POINTS\n");
		} else {
			System.out.println("!!! Cost function: ASSIGNED VARIABLES ONLY\n");
		}
		// *******************************************************
		// nombre d 'acc dans fichier res
		// *******************************************************
		try {
			FileReader c = new FileReader(mypath + "essai.res");
			BufferedReader br = new BufferedReader(c);
			String line = br.readLine();
			while (line != null) {
				if (line.contains("acc_lab")) {
					ctr++;
					String[] tab = line.split(":");
				}
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		// *******************************************************
		// Parse Z3
		// *******************************************************
		int start, end, val;
		String line, var;
		BufferedReader reader = new BufferedReader(new FileReader(mypath + "essai.res"));
		int totalbits = 0;
		int fp8 = 0;
		int fp16 = 0;
		int fp32 = 0;
		int fp64 = 0;
		int fp128 = 0;
		int initTotalBits2 = 0;
		Boolean okvar = true;
		String vv = "";
		while ((line = reader.readLine()) != null) {
			start = line.indexOf("(define-fun acc_");
			if (start >= 0) {
				if (costFun == 1) {
					int ss = line.indexOf(' ', 3) + 1;
					String ll = line.substring(ss, line.length());
					int ee = ll.indexOf(' ', 3);
					vv = ll.substring(0, ee);
					if (AbstractClass.accVarsAssignedOnly.contains(vv)) {
						okvar = true;
					} else {
						okvar = false;
					}
				}
				;

				start += 16;
				end = line.lastIndexOf('(') - 1;
				var = line.substring(start + 3, end);
				line = reader.readLine();
				line = line.substring(4, line.length() - 1);
				val = Integer.parseInt(line);
				int sz = 0;
				if (CreateArray.arraySize.containsKey(AbstractClass.acc2Id.get("acc_lab" + var))) {
					sz = CreateArray.arraySize.get(AbstractClass.acc2Id.get("acc_lab" + var));
					CreateArray.arraySize.put(AbstractClass.acc2Id.get("acc_lab" + var), 0);
				} else {
					sz = 1;
				}
				AbstractClass.resultEnv.put(var, val);
				if (okvar) {

					if (AbstractClass.arraySize3.containsKey(AbstractClass.acc2Id.get(vv))) {
						int n = AbstractClass.arraySize3.get(AbstractClass.acc2Id.get(vv));
						initTotalBits2 += AbstractClass.prec * n;
						AbstractClass.arraySize3.put(AbstractClass.acc2Id.get(vv), 0);
						str = (" (+ " + " (* " + vv + " " + n + ") " + str + ")");
					} else {
						initTotalBits2 += AbstractClass.prec;
						str = (" (+ " + " " + vv + " " + str + ")");
					}
					;

					if (val < 8) {
						fp8 += sz;
					} else if (val < 16) {
						fp16 += sz;
					} else if (val < 32) {
						fp32 += sz;
					} else if (val < 64) {
						fp64 += sz;
					} else {
						fp128 += sz;
					}
					;

					totalbits += val * sz;
				} else {
					line = reader.readLine();
					if (line == null)
						break;
				}
			}
		}
		reader.close();

		// *******************************************************
		// affichage resultats
		// *******************************************************
		System.out.println("Number of Z3 variables : " + AbstractClass.z3ConstInt.size());
		System.out.println("Number of Z3 constraints: " + Constraints.allCst.size() + "\n");
		System.out.println("Total number of bits before optimization  : " + initTotalBits2);
		System.out.println("Total number of bits after optimization (1): " + totalbits);
		System.out.println("Total number of bits after optimization (2): " + x.getIntValue());
		System.out.println("Number of declared variables optimized: " + env.size());
		try {
			int indent = 1;
			String ProgRes = "popix_output";
			FileWriter test2 = new FileWriter(ProgRes);
			BufferedWriter out2 = new BufferedWriter(test2);
			out2.write(program.toStringRes(indent, AbstractClass.resultEnv));
			out2.close();
		} catch (IOException er) {

		}
		System.out.println("Number of switches: " + AbstractClass.numberSwitch);
		if (ctr == 0)
			System.out.println("\n\n!!!!! Z3 UNSAT !!!!!!");
		else {
			System.out.println("\n% improvment: " + (100 - (x.getIntValue() * 100 / (initTotalBits2))));
			System.out.println("");
			System.out.println("Number of control points FP8 : " + fp8);
			System.out.println("Number of control points FP16: " + fp16);
			System.out.println("Number of control points FP32: " + fp32);
			System.out.println("Number of control points FP64: " + fp64);
			System.out.println("Number of control points FP128: " + fp128);

		}
		try {
			String IpCstr = "constraints";
			FileWriter IpTest = new FileWriter(IpCstr);
			Iterator<ConstraintsSet> it = ConstraintsSet.allCst.iterator();
			BufferedWriter sortie = new BufferedWriter(IpTest);
			while (it.hasNext()) {
				sortie.write(it.next().toString() + "\n");
			}
			sortie.close();
		} catch (IOException er) {

		}

		/* mpfr files */

		try {
			String ProgOrig = "pop_float.py";
			FileWriter test3 = new FileWriter(ProgOrig);
			BufferedWriter out3 = new BufferedWriter(test3);
			out3.write("import gmpy2\nfrom gmpy2 import mpfr\nfrom gmpy2 import xmpz\n" + "\n"
					+ program.toStringOriginal(0));
			out3.close();
		} catch (IOException er) {
			;
		}
		try {
			String ProgMpfr = "pop_mpfr.py";
			FileWriter test4 = new FileWriter(ProgMpfr);
			BufferedWriter out4 = new BufferedWriter(test4);
			out4.write("import gmpy2\nfrom gmpy2 import mpfr\nfrom gmpy2 import xmpz\n" + "\n"
					+ program.toStringPython(0, AbstractClass.resultEnv));
			out4.close();
		} catch (IOException er) {

		}

	}

}
