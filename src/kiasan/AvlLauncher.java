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
import heapsyn.wrapper.symbolic.SpecFactory;
import heapsyn.wrapper.symbolic.Specification;
import heapsyn.wrapper.symbolic.SymbolicExecutor;
import heapsyn.wrapper.symbolic.SymbolicExecutorWithCachedJBSE;

import static common.Settings.*;

public class AvlLauncher {
	
	private static final int scope$AvlTree	= 1;
	private static final int scope$AvlNode	= 5;
	private static final String hexFilePath	= "HEXsettings/kiasan/avltree.jbse";
	private static final String logFilePath = "tmp/kiasan/avl.txt";
	
	private static Class<?> cls$AvlTree;
	private static Class<?> cls$AvlNode;
	
	private static TestGenerator testGenerator;
	
	private static void init() throws ClassNotFoundException {
		cls$AvlTree = Class.forName("kiasan.avltree.AvlTree");
		cls$AvlNode = Class.forName("kiasan.avltree.AvlNode");
	}
	
	private static void configure(boolean showOnConsole) {
		Options options = Options.I();
		options.setSolverTmpDir(SOLVER_TMP_DIR);
		JBSEParameters parms = JBSEParameters.I();
		parms.setTargetClassPath(TARGET_CLASS_PATH);
		parms.setTargetSourcePath(TARGET_SOURCE_PATH);
		parms.setShowOnConsole(showOnConsole);
		parms.setSettingsPath(hexFilePath);
		parms.setHeapScope(cls$AvlTree, scope$AvlTree);
		parms.setHeapScope(cls$AvlNode, scope$AvlNode);
	}
	
	private static List<Method> getMethods() throws NoSuchMethodException {
		List<Method> decMethods = Arrays.asList(cls$AvlTree.getDeclaredMethods());
		List<Method> pubMethods = Arrays.asList(cls$AvlTree.getMethods());
		List<Method> methods = decMethods.stream()
				.filter(m -> pubMethods.contains(m))
				.collect(Collectors.toList());
		System.out.println("public methods (kiasan.AvlTree):");
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
		gb.setHeapScope(cls$AvlTree, scope$AvlTree);
		gb.setHeapScope(cls$AvlNode, scope$AvlNode);
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
		genTest1();
		genTest2();
	}
	
	private static void genTest1() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH avlTree = specFty.mkRefDecl(cls$AvlTree, "t");
		specFty.addRefSpec("t", "root", "root");
		specFty.addRefSpec("root", "element", "v2", "left", "root.l", "right", "root.r");
		specFty.addRefSpec("root.l", "element", "v1", "left", "root.l.l", "right", "null");
		specFty.addRefSpec("root.l.l", "element", "v0", "left", "null", "right", "null");
		specFty.addRefSpec("root.r", "element", "v3", "left", "null", "right", "null");
		specFty.setAccessible("t");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, avlTree);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest1: " + (end - start) + "ms\n");
	}
	
	private static void genTest2() {
		long start = System.currentTimeMillis();
		SpecFactory specFty = new SpecFactory();
		ObjectH avlTree = specFty.mkRefDecl(cls$AvlTree, "t");
		specFty.addRefSpec("t", "root", "root");
		specFty.addRefSpec("root", "element", "v1", "left", "root.l", "right", "root.r");
		specFty.addRefSpec("root.l", "element", "v0", "left", "null", "right", "null");
		specFty.addRefSpec("root.r", "element", "v3", "left", "root.r.l", "right", "null");
		specFty.addRefSpec("root.r.l", "element", "v2", "left", "null", "right", "null");
		specFty.setAccessible("t");
		Specification spec = specFty.genSpec();
		
		List<Statement> stmts = testGenerator.generateTestWithSpec(spec, avlTree);
		Statement.printStatements(stmts, System.out);
		long end = System.currentTimeMillis();
		System.out.println(">> genTest2: " + (end - start) + "ms\n");
	}

}
