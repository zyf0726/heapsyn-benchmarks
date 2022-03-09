package sir;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
import heapsyn.wrapper.symbolic.SpecFactory;
import heapsyn.wrapper.symbolic.Specification;
import heapsyn.wrapper.symbolic.SymbolicExecutor;
import heapsyn.wrapper.symbolic.SymbolicExecutorWithCachedJBSE;

import static common.Settings.*;

public class SllLauncher {
	
	private static final int scope$List		= 1;
	private static final int scope$Node		= 6;
	private static final int scope$Iter		= 1;
	private static final int maxSeqLength	= 20;
	private static final String hexFilePath	= "HEXsettings/sir/sir-sll.jbse";
	private static final String logFilePath = "tmp/sir/sll.txt";
	
	private static Class<?> cls$List;
	private static Class<?> cls$Node;
	private static Class<?> cls$Iter;
	
	private static TestGenerator testGenerator;
	
	private static void init() throws ClassNotFoundException {
		cls$List = Class.forName("sir.sll.MyLinkedList");
		cls$Node = Class.forName("sir.sll.MyLinkedList$MyListNode");
		cls$Iter = Class.forName("sir.sll.MyLinkedList$MyLinkedListItr");
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
		parms.setHeapScope(cls$Node, scope$Node);
		parms.setHeapScope(cls$Iter, scope$Iter);
	}
	
	private static List<Method> getMethods() throws NoSuchMethodException {
		List<Method> decMethods$List = Arrays.asList(cls$List.getDeclaredMethods());
		List<Method> pubMethods$List = Arrays.asList(cls$List.getMethods());
		List<Method> methods = decMethods$List.stream()
				.filter(m -> pubMethods$List.contains(m))
				.collect(Collectors.toList());
		List<Method> decMethods$Iter = Arrays.asList(cls$Iter.getDeclaredMethods());
		List<Method> pubMethods$Iter = Arrays.asList(cls$Iter.getMethods());
		methods.addAll(decMethods$Iter.stream()
				.filter(m -> pubMethods$Iter.contains(m))
				.collect(Collectors.toList()));
		System.out.println("public methods (sir.MyLinkedList):");
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
		HeapTransGraphBuilder gb = new HeapTransGraphBuilder(executor, methods);
		gb.setHeapScope(cls$List, scope$List);
		gb.setHeapScope(cls$Node, scope$Node);
		gb.setHeapScope(cls$Iter, scope$Iter);
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
		SymbolicExecutor executor = new SymbolicExecutorWithCachedJBSE();
		DynamicGraphBuilder gb = new DynamicGraphBuilder(executor, methods);
		gb.setHeapScope(cls$List, scope$List);
		gb.setHeapScope(cls$Node, scope$Node);
		gb.setHeapScope(cls$Iter, scope$Iter);
		SymbolicHeap initHeap = new SymbolicHeapAsDigraph(ExistExpr.ALWAYS_TRUE);
		List<WrappedHeap> heaps = gb.buildGraph(initHeap, maxSeqLength);
		HeapTransGraphBuilder.__debugPrintOut(heaps, executor, new PrintStream(logFilePath));
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
	}
	

}
