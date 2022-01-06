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

public class TreeMapLauncher {
	
	private static final int scope$TreeMap		= 1;
	private static final int scopeForJBSE$Entry = 4;
	private static final int scopeForHeap$Entry = 5;
	private static final String hexFilePath		= "HEXsettings/kiasan/treemap.jbse";
	private static final String logFilePath 	= "tmp/kiasan/treemap.txt";
	
	private static Class<?> cls$TreeMap;
	private static Class<?> cls$Entry;
	
	private static TestGenerator testGenerator;
	
	private static void init() throws ClassNotFoundException {
		cls$TreeMap = Class.forName("kiasan.redblacktree.TreeMap");
		cls$Entry = Class.forName("kiasan.redblacktree.TreeMap$Entry");
	}
	
	private static void configure(boolean showOnConsole) {
		Options options = Options.I();
		options.setSolverTmpDir(SOLVER_TMP_DIR);
		JBSEParameters parms = JBSEParameters.I();
		parms.setTargetClassPath(TARGET_CLASS_PATH);
		parms.setTargetSourcePath(TARGET_SOURCE_PATH);
		parms.setShowOnConsole(showOnConsole);
		parms.setSettingsPath(hexFilePath);
		parms.setHeapScope(cls$TreeMap, scope$TreeMap);
		parms.setHeapScope(cls$Entry, scopeForJBSE$Entry);
	}
	
	private static List<Method> getMethods() throws NoSuchMethodException {
		List<Method> decMethods = Arrays.asList(cls$TreeMap.getDeclaredMethods());
		List<Method> pubMethods = Arrays.asList(cls$TreeMap.getMethods());
		List<Method> methods = decMethods.stream()
				.filter(m -> pubMethods.contains(m))
				.collect(Collectors.toList());
		System.out.println("public methods (kiasan.TreeMap):");
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
		gb.setHeapScope(cls$TreeMap, scope$TreeMap);
		gb.setHeapScope(cls$Entry, scopeForHeap$Entry);
		SymbolicHeap initHeap = new SymbolicHeapAsDigraph(ExistExpr.ALWAYS_TRUE);
		List<WrappedHeap> heaps = gb.buildGraph(initHeap, simplify);
		HeapTransGraphBuilder.__debugPrintOut(heaps, executor, new PrintStream(logFilePath));
		testGenerator = new TestGenerator(heaps);
		long end = System.currentTimeMillis();
		System.out.println(">> buildGraph: " + (end - start) + "ms\n");
	}
	
	public static void main(String[] args) throws Exception {
		final boolean showOnConsole = true;
		final boolean simplify = true;
		init();
		configure(showOnConsole);
		List<Method> methods = getMethods();
		buildGraph(methods, simplify);
		genTest0();
		genTest4$1();
		genTest4$2();
		genTest5$1();
	}
	
	private static void genTest0() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH treemap = specFty.mkRefDecl(cls$TreeMap, "t");
		ObjectH size = specFty.mkVarDecl(SMTSort.INT, "size");
		specFty.addRefSpec("t", "root", "null", "size", "size");
		specFty.addVarSpec("(>= size 0)");
		specFty.setAccessible("t");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, treemap, size);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest0: " + (end - start) + "ms\n");
	}
	
	private static void genTest4$1() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH treemap = specFty.mkRefDecl(cls$TreeMap, "t");
		ObjectH v1 = specFty.mkRefDecl(Object.class, "v1");
		ObjectH v2 = specFty.mkRefDecl(Object.class, "v2");
		ObjectH v3 = specFty.mkRefDecl(Object.class, "v3");
		specFty.addRefSpec("t", "root", "o1");
		specFty.addRefSpec("o1", "parent", "null", "left", "o2", "right", "o3", "value", "v1");
		specFty.addRefSpec("o2", "parent", "o1", "left", "null", "right", "o4", "value", "null");
		specFty.addRefSpec("o4", "parent", "o2", "left", "null", "right", "null", "value", "v1");
		specFty.addRefSpec("o3", "parent", "o1", "value", "v2");
		specFty.setAccessible("t", "v1", "v2", "v3");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, treemap, v1, v2, v3);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest4$1: " + (end - start) + "ms\n");
	}
	
	private static void genTest4$2() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH treemap = specFty.mkRefDecl(cls$TreeMap, "t");
		ObjectH v1 = specFty.mkRefDecl(Object.class, "v1");
		ObjectH v2 = specFty.mkRefDecl(Object.class, "v2");
		specFty.addRefSpec("t", "root", "o1");
		specFty.addRefSpec("o1", "parent", "null", "left", "o2", "right", "o3", "value", "null");
		specFty.addRefSpec("o2", "parent", "o1", "left", "null", "right", "null", "value", "null");
		specFty.addRefSpec("o3", "parent", "o1", "left", "null", "right", "o4", "value", "v1");
		specFty.addRefSpec("o4", "parent", "o3", "value", "v2");
		specFty.setAccessible("t", "v2");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, treemap, v1, v2);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest4$2: " + (end - start) + "ms\n");
	}
	
	private static void genTest5$1() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH treemap = specFty.mkRefDecl(cls$TreeMap, "t");
		ObjectH v1 = specFty.mkRefDecl(Object.class, "v1");
		ObjectH v3 = specFty.mkRefDecl(Object.class, "v3");
		specFty.addRefSpec("t", "root", "o1");
		specFty.addRefSpec("o1", "parent", "null", "left", "o2", "right", "o3", "value", "v1");
		specFty.addRefSpec("o2", "left", "o4", "right", "o5", "value", "null");
		specFty.addRefSpec("o4", "parent", "o2", "left", "null", "right", "null", "value", "v2");
		specFty.addRefSpec("o5", "parent", "o2", "value", "v1");
		specFty.addRefSpec("o3", "left", "null", "right", "null", "value", "v2");
		specFty.setAccessible("t", "v1", "v3");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, treemap, v1, v3);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest5$1: " + (end - start) + "ms\n");
	}

}
