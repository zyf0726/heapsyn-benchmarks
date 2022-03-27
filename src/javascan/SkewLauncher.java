package javascan;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import heapsyn.algo.DynamicGraphBuilder;
import heapsyn.algo.StaticGraphBuilder;
import heapsyn.algo.Statement;
import heapsyn.algo.TestGenerator;
import heapsyn.algo.WrappedHeap;
import heapsyn.common.settings.JBSEParameters;
import heapsyn.common.settings.Options;
import heapsyn.heap.ObjectH;
import heapsyn.heap.SymbolicHeap;
import heapsyn.heap.SymbolicHeapAsDigraph;
import heapsyn.smtlib.ExistExpr;
import heapsyn.wrapper.symbolic.SpecFactory;
import heapsyn.wrapper.symbolic.Specification;
import heapsyn.wrapper.symbolic.SymbolicExecutor;
import heapsyn.wrapper.symbolic.SymbolicExecutorWithCachedJBSE;

import static common.Settings.*;

public class SkewLauncher {
	
	private static final int scope$Heap		= 1;
	private static final int scope$Node		= 6;
	private static final int maxSeqLength	= 20;
	private static final String hexFilePath	= "HEXsettings/javascan/skew.jbse";
	private static final String logFilePath = "tmp/javascan/skew.txt";
	
	private static Class<?> cls$Heap;
	private static Class<?> cls$Node;
	
	private static TestGenerator testGenerator;
	
	private static void init() throws ClassNotFoundException {
		cls$Heap = Class.forName("javascan.skewheap.SkewHeap");
		cls$Node = Class.forName("javascan.skewheap.SkewHeap$SkewNode");
	}
	
	private static void configure(boolean showOnConsole) {
		Options options = Options.I();
		options.setSolverTmpDir(SOLVER_TMP_DIR);
		JBSEParameters parms = JBSEParameters.I();
		parms.setTargetClassPath(TARGET_CLASS_PATH);
		parms.setTargetSourcePath(TARGET_SOURCE_PATH);
		parms.setShowOnConsole(showOnConsole);
		parms.setSettingsPath(hexFilePath);
		parms.setHeapScope(cls$Heap, scope$Heap);
		parms.setHeapScope(cls$Node, scope$Node);
	}
	
	private static List<Method> getMethods() throws NoSuchMethodException {
		List<Method> decMethods = Arrays.asList(cls$Heap.getDeclaredMethods());
		List<Method> pubMethods = Arrays.asList(cls$Heap.getMethods());
		List<Method> methods = decMethods.stream()
				.filter(m -> pubMethods.contains(m))
				.collect(Collectors.toList());
		System.out.println("public methods (javascan.SkewHeap):");
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
		SymbolicExecutor executor = new SymbolicExecutorWithCachedJBSE();
		StaticGraphBuilder gb = new StaticGraphBuilder(executor, methods);
		gb.setHeapScope(cls$Heap, scope$Heap);
		gb.setHeapScope(cls$Node, scope$Node);
		SymbolicHeap initHeap = new SymbolicHeapAsDigraph(ExistExpr.ALWAYS_TRUE);
		List<WrappedHeap> heaps = gb.buildGraph(initHeap, simplify);
		StaticGraphBuilder.__debugPrintOut(heaps, executor, new PrintStream(logFilePath));
		testGenerator = new TestGenerator(heaps);
		long end = System.currentTimeMillis();
		System.out.println(">> buildGraph (static): " + (end - start) + "ms\n");
	}
	
	private static void buildGraphDynamic(Collection<Method> methods)
			throws FileNotFoundException {
		long start = System.currentTimeMillis();
		SymbolicExecutor executor = new SymbolicExecutorWithCachedJBSE();
		DynamicGraphBuilder gb = new DynamicGraphBuilder(executor, methods);
		gb.setHeapScope(cls$Heap, scope$Heap);
		gb.setHeapScope(cls$Node, scope$Node);
		SymbolicHeap initHeap = new SymbolicHeapAsDigraph(ExistExpr.ALWAYS_TRUE);
		List<WrappedHeap> heaps = gb.buildGraph(initHeap, maxSeqLength);
		DynamicGraphBuilder.__debugPrintOut(heaps, executor, new PrintStream(logFilePath));
		testGenerator = new TestGenerator(heaps);
		long end = System.currentTimeMillis();
		System.out.println(">> buildGraph (dynamic): " + (end - start) + "ms\n");
	}
	
	public static void main(String[] args) throws Exception {
		final boolean showOnConsole = true;
		final boolean simplify = true;
		final boolean useDynamicAlgorithm = true;
		init();
		configure(showOnConsole);
		List<Method> methods = getMethods();
		if (useDynamicAlgorithm) {
			buildGraphDynamic(methods);
		} else {
			buildGraphStatic(methods, simplify);
		}
		genTest0();
		genTest6();
	}
	
	private static void genTest0() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH heap = specFty.mkRefDecl(cls$Heap, "h");
		specFty.addRefSpec("h", "root", "null");
		specFty.setAccessible("h");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, heap);
		Statement.printStatements(stmts, "@TestEmpty", System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest0: " + (end - start) + "ms\n");
	}
	
	private static void genTest6() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH heap = specFty.mkRefDecl(cls$Heap, "h");
		specFty.addRefSpec("h", "root", "o0");
		specFty.addRefSpec("o0", "left", "o1", "right", "o2");
		specFty.addRefSpec("o1", "left", "o3", "right", "o4");
		specFty.addRefSpec("o3", "left", "o5", "right", "null");
		specFty.addRefSpec("o2", "left", "null", "right", "null");
		specFty.addRefSpec("o4", "element", "v2");
		specFty.addRefSpec("o5", "element", "v5");
		specFty.addVarSpec("(= v2 v5)");
		specFty.setAccessible("h");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, heap);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest6: " + (end - start) + "ms\n");
	}

}
