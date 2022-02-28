package sushi;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import heapsyn.algo.DynamicGraphBuilder;
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

public class DllLauncher {

	private static final int scope$List		= 1;
	private static final int scope$Entry	= 5;
	private static final int scope$Iter		= 1;
	private static final int scope$DesIter	= 1;
	private static final int maxSeqLength	= 6;
	private static final String hexFilePath	= "HEXsettings/sushi/dll-accurate.jbse";
	private static final String logFilePath	= "tmp/sushi/dll.txt";
	
	private static final Predicate<String> fieldFilter = (name ->
			(!name.startsWith("_") || name.equals("_owner"))
			&& !name.equals("modCount") && !name.equals("expectedModCount"));

	private static Class<?> cls$List, cls$Entry;
	private static Class<?> cls$Iter, cls$DesIter;
	
	private static TestGenerator testGenerator;
	
	private static void init() throws ClassNotFoundException {
		cls$List = Class.forName("sushi.dll.LinkedList");
		cls$Entry = Class.forName("sushi.dll.LinkedList$Entry");
		cls$Iter = Class.forName("sushi.dll.LinkedList$ListItr");
		cls$DesIter = Class.forName("sushi.dll.LinkedList$DescendingIterator");
	}
	
	private static void configure(boolean showOnConsole) {
		Options options = Options.I();
		options.setSolverTmpDir(SOLVER_TMP_DIR);
		JBSEParameters parms = JBSEParameters.I();
		parms.setTargetClassPath(TARGET_CLASS_PATH);
		parms.setTargetSourcePath(TARGET_SOURCE_PATH);
		parms.setShowOnConsole(showOnConsole);
		parms.setSettingsPath(hexFilePath);
		parms.setHeapScope(cls$List, scope$List);
		parms.setHeapScope(cls$Entry, scope$Entry);
		parms.setHeapScope(cls$Iter, scope$Iter);
		parms.setHeapScope(cls$DesIter, scope$DesIter);
	}
	
	private static List<Method> getMethods() throws NoSuchMethodException {
		List<Method> decMethods = Arrays.asList(cls$List.getDeclaredMethods());
		List<Method> pubMethods = Arrays.asList(cls$List.getMethods());
		List<Method> methods = decMethods.stream()
				.filter(m -> pubMethods.contains(m))
				.collect(Collectors.toList());
		System.out.println("public methods (sushi.DoublyLinkedList):");
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
	
	private static void buildGraphStatic(Collection<Method> methods, boolean simplify)
			throws FileNotFoundException {
		long start = System.currentTimeMillis();
		SymbolicExecutor executor = new SymbolicExecutorWithCachedJBSE(fieldFilter);
		HeapTransGraphBuilder gb = new HeapTransGraphBuilder(executor, methods);
		gb.setHeapScope(cls$List, scope$List);
		gb.setHeapScope(cls$Entry, scope$Entry);
		gb.setHeapScope(cls$Iter, scope$Iter);
		gb.setHeapScope(cls$DesIter, scope$DesIter);
		SymbolicHeap initHeap = new SymbolicHeapAsDigraph(ExistExpr.ALWAYS_TRUE);
		List<WrappedHeap> heaps = gb.buildGraph(initHeap, simplify);
		HeapTransGraphBuilder.__debugPrintOut(heaps, executor, new PrintStream(logFilePath));
		testGenerator = new TestGenerator(heaps);
		long end = System.currentTimeMillis();
		System.out.println(">> buildGraph (static): " + (end - start) + "ms\n");
	}
	
	private static void buildGraphDynamic(Collection<Method> methods)
			throws FileNotFoundException {
		long start = System.currentTimeMillis();
		SymbolicExecutor executor = new SymbolicExecutorWithCachedJBSE(fieldFilter);
		DynamicGraphBuilder gb = new DynamicGraphBuilder(executor, methods);
		gb.setHeapScope(cls$List, scope$List);
		gb.setHeapScope(cls$Entry, scope$Entry);
		gb.setHeapScope(cls$Iter, scope$Iter);
		gb.setHeapScope(cls$DesIter, scope$DesIter);
		SymbolicHeap initHeap = new SymbolicHeapAsDigraph(ExistExpr.ALWAYS_TRUE);
		List<WrappedHeap> heaps = gb.buildGraph(initHeap, maxSeqLength);
		HeapTransGraphBuilder.__debugPrintOut(heaps, executor, new PrintStream(logFilePath));
		testGenerator = new TestGenerator(heaps);
		long end = System.currentTimeMillis();
		System.out.println(">> buildGraph (dynamic): " + (end - start) + "ms\n");
	}
	
	public static void main(String[] args) throws Exception {
		final boolean showOnConsole = true;
		final boolean simplify = false;
		final boolean useDynamicAlgorithm = true;
		init();
		configure(showOnConsole);
		List<Method> methods = getMethods();
		if (useDynamicAlgorithm) {
			buildGraphDynamic(methods);
		} else {
			buildGraphStatic(methods, simplify);
		}
		genTest1();
		genTest2();
		genTest3();
		genTest(0);
		genTest(2);
	}
	
	private static void genTest1() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH dll = specFty.mkRefDecl(cls$List, "dll");
		ObjectH size = specFty.mkVarDecl(SMTSort.INT, "size");
		specFty.addRefSpec("dll", "header", "o1", "size", "size");
		specFty.addRefSpec("o1", "next", "o2", "element", "null");
		specFty.addRefSpec("o2", "next", "o3", "element", "e1");
		specFty.addRefSpec("o3", "next", "o4", "element", "null");
		specFty.addRefSpec("o4", "next", "o1", "element", "e1");
		specFty.setAccessible("dll");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, dll, size);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest1: " + (end - start) + "ms\n");
	}
	
	private static void genTest2() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH dll = specFty.mkRefDecl(cls$List, "dll");
		ObjectH size = specFty.mkVarDecl(SMTSort.INT, "size");
		specFty.mkRefDecl(cls$Iter, "it");
		specFty.addRefSpec("dll", "header", "o1", "size", "size");
		specFty.addRefSpec("o1", "previous", "o0");
		specFty.addRefSpec("o0", "next", "o1");
		specFty.addRefSpec("it", "next", "o0");
		specFty.addVarSpec("(> size 2)");
		specFty.setAccessible("dll", "it");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, dll, size);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest2: " + (end - start) + "ms\n");
	}
	
	private static void genTest3() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH dll = specFty.mkRefDecl(cls$List, "dll");
		ObjectH size = specFty.mkVarDecl(SMTSort.INT, "size");
		specFty.mkRefDecl(cls$DesIter, "dit");
		specFty.addRefSpec("dll", "header", "o1", "size", "size");
		specFty.addRefSpec("o1", "previous", "o0");
		specFty.addRefSpec("o0", "next", "o1");
		specFty.addRefSpec("dit", "itr", "it");
		specFty.addRefSpec("it", "next", "o1");
		specFty.addVarSpec("(> size 2)");
		specFty.setAccessible("dll", "dit");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, dll, size);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest3: " + (end - start) + "ms\n");
	}
	
	private static void genTest(int expcSize) {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH dll = specFty.mkRefDecl(cls$List, "dll");
		ObjectH size = specFty.mkVarDecl(SMTSort.INT, "size");
		specFty.addRefSpec("dll", "size", "size");
		specFty.addVarSpec("(= size " + expcSize + ")");
		specFty.setAccessible("dll");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, dll, size);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest(" + expcSize + "): " + (end - start) + "ms\n");
	}

}
