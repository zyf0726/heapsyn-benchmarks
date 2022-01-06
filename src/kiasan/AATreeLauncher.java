package kiasan;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import heapsyn.algo.HeapTransGraphBuilder;
import heapsyn.algo.Statement;
import heapsyn.algo.TestGenerator;
import heapsyn.algo.WrappedHeap;
import heapsyn.common.settings.JBSEParameters;
import heapsyn.common.settings.Options;
import heapsyn.heap.ObjectH;
import heapsyn.heap.SymbolicHeap;
import heapsyn.heap.SymbolicHeapAsDigraph;
import heapsyn.smtlib.ExistExpr;
import heapsyn.smtlib.SMTSort;
import heapsyn.wrapper.symbolic.SpecFactory;
import heapsyn.wrapper.symbolic.Specification;
import heapsyn.wrapper.symbolic.SymbolicExecutor;
import heapsyn.wrapper.symbolic.SymbolicExecutorWithCachedJBSE;

import static common.Settings.*;

public class AATreeLauncher {
	
	private static final int scope$AATree	= 1;
	private static final int scope$AANode	= 4;
	private static final String hexFilePath = "HEXsettings/kiasan/aatree.jbse";
	private static final String logFilePath = "tmp/kiasan/aatree.txt";
	
	private static Class<?> cls$AATree;
	private static Class<?> cls$AANode;
	
	private static TestGenerator testGenerator;
	
	private static void init() throws ClassNotFoundException {
		cls$AATree = Class.forName("kiasan.aatree.AATree");
		cls$AANode = Class.forName("kiasan.aatree.AATree$AANode");
	}
	
	private static void configure(boolean showOnConsole) {
		Options options = Options.I();
		options.setSolverTmpDir(SOLVER_TMP_DIR);
		JBSEParameters parms = JBSEParameters.I();
		parms.setTargetClassPath(TARGET_CLASS_PATH);
		parms.setTargetSourcePath(TARGET_SOURCE_PATH);
		parms.setShowOnConsole(showOnConsole);
		parms.setSettingsPath(hexFilePath);
		parms.setHeapScope(cls$AATree, scope$AATree);
		parms.setHeapScope(cls$AANode, scope$AANode);
	}
	
	private static List<Method> getMethods() throws NoSuchMethodException {
		List<Method> decMethods = Arrays.asList(cls$AATree.getDeclaredMethods());
		List<Method> pubMethods = Arrays.asList(cls$AATree.getMethods());
		List<Method> methods = decMethods.stream()
				.filter(m -> pubMethods.contains(m))
				.collect(Collectors.toList());
		System.out.println("public methods (kiasan.AATree):");
		for (Method m : methods) {
			System.out.print("  " + m.getName() + "(");
			AnnotatedType[] paraTypes = m.getAnnotatedParameterTypes();
			for (int i = 0; i < paraTypes.length; ++i) {
				System.out.print(paraTypes[i].getType().getTypeName());
				if (i < paraTypes.length - 1)
					System.out.print(", ");
			}
			System.out.println(")");
		}
		return methods;
	}
	
	private static void buildGraph(Collection<Method> methods, boolean simplify)
			throws FileNotFoundException {
		long start = System.currentTimeMillis();
		SymbolicExecutor executor = new SymbolicExecutorWithCachedJBSE(
				name -> !name.startsWith("_"));
		HeapTransGraphBuilder gb = new HeapTransGraphBuilder(executor, methods);
		gb.setHeapScope(cls$AATree, scope$AATree);
		gb.setHeapScope(cls$AANode, scope$AANode);
		SymbolicHeap initHeap = new SymbolicHeapAsDigraph(ExistExpr.ALWAYS_TRUE);
		List<WrappedHeap> heaps = gb.buildGraph(initHeap, simplify);
		HeapTransGraphBuilder.__debugPrintOut(heaps, executor, new PrintStream(logFilePath));
		testGenerator = new TestGenerator(heaps);
		long end = System.currentTimeMillis();
		System.out.println(">> buildGraph: " + (end - start) + "ms\n");
	}
	
	public static void main(String[] args) throws Exception {
		final boolean showOnConsole = true;
		final boolean simplify = false;
		init();
		configure(showOnConsole);
		List<Method> methods = getMethods();
		buildGraph(methods, simplify);
		genTest1();
		genTest2();
	}
	
	private static void genTest1() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH aatree = specFty.mkRefDecl(cls$AATree, "t");
		ObjectH level = specFty.mkVarDecl(SMTSort.INT, "level");
		specFty.addRefSpec("t", "nullNode", "onull", "root", "onull");
		specFty.addRefSpec("onull", "element", "v0", "level", "level",
				"left", "onull", "right", "onull");
		specFty.addVarSpec("(= v0 0)");
		specFty.setAccessible("t");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, aatree, level);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest1: " + (end - start) + "ms\n");
	}
	
	private static void genTest2() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH aatree = specFty.mkRefDecl(cls$AATree, "t");
		ObjectH l0 = specFty.mkVarDecl(SMTSort.INT, "l0");
		ObjectH l1 = specFty.mkVarDecl(SMTSort.INT, "l1");
		ObjectH l2 = specFty.mkVarDecl(SMTSort.INT, "l2");
		ObjectH v0 = specFty.mkVarDecl(SMTSort.INT, "v0");
		ObjectH v1 = specFty.mkVarDecl(SMTSort.INT, "v1");
		ObjectH v2 = specFty.mkVarDecl(SMTSort.INT, "v2");
		specFty.addRefSpec("t", "root", "o0", "nullNode", "onull");
		specFty.addRefSpec("o0", "left", "o1", "right", "o2", "level", "l0", "element", "v0");
		specFty.addRefSpec("o1", "level", "l1", "element", "v1");
		specFty.addRefSpec("o2", "level", "l2", "element", "v2");
		specFty.addRefSpec("onull", "left", "onull", "right", "onull");
		specFty.addVarSpec("(>= (+ v0 v2) 25)");
		specFty.setAccessible("t");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, aatree,
				v0, v1, v2, l0, l1, l2);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest2: " + (end - start) + "ms\n");
	}
	
}
