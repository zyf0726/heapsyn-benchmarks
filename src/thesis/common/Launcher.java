package thesis.common;

import heapsyn.algo.DynamicGraphBuilder;
import heapsyn.algo.TestGenerator;
import heapsyn.algo.WrappedHeap;
import heapsyn.common.settings.JBSEParameters;
import heapsyn.common.settings.Options;
import heapsyn.heap.SymbolicHeap;
import heapsyn.heap.SymbolicHeapAsDigraph;
import heapsyn.smtlib.ExistExpr;
import heapsyn.wrapper.symbolic.SymbolicExecutor;
import heapsyn.wrapper.symbolic.SymbolicExecutorWithCachedJBSE;
import jbse.apps.run.RunParameters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.Settings.*;

public abstract class Launcher {

    private final List<Method> publicMethods;
    private final int maxSeqLength;
    private final String logFilePath;

    private Map<String, Integer> scopeForJBSE = null;
    private Map<String, Integer> scopeForHeapSyn = null;
    private String[] hexFilePaths = null;

    protected Launcher(String targetClassName, int maxSeqLength, String logFilePath) {
        try {
            System.out.println("Target class: " + targetClassName);
            Class<?> targetClass = Class.forName(targetClassName);
            this.publicMethods = getPublicMethods(targetClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.maxSeqLength = maxSeqLength;
        this.logFilePath = logFilePath;
        System.out.println("Max sequence length: " + this.maxSeqLength);
        System.out.println("Log file path: " + this.logFilePath);
    }

    private static List<Method> getPublicMethods(Class<?> cls) {
        System.out.println("Public methods (" + cls.getName() + "):");
        List<Method> decMethods = Arrays.asList(cls.getDeclaredMethods());
        List<Method> pubMethods = Arrays.asList(cls.getMethods());
        List<Method> methods = decMethods.stream()
                .filter(pubMethods::contains)
                .filter(m -> !m.getName().equals("main"))
                .collect(Collectors.toList());
        for (Method m : methods) {
            StringBuilder message = new StringBuilder();
            message.append("  ").append(m.getName()).append("(");
            AnnotatedType[] paraTypes = m.getAnnotatedParameterTypes();
            for (int i = 0; i < paraTypes.length; ++i) {
                message.append(paraTypes[i].getType().getTypeName());
                if (i < paraTypes.length - 1)
                    message.append(", ");
            }
            message.append(")");
            System.out.println(message);
        }
        List<Class<?>> decClasses = Arrays.asList(cls.getDeclaredClasses());
        List<Class<?>> pubClasses = Arrays.asList(cls.getClasses());
        List<Class<?>> classes = decClasses.stream()
                .filter(pubClasses::contains)
                .collect(Collectors.toList());
        for (Class<?> c : classes) {
            methods.addAll(getPublicMethods(c));
        }
        return methods;
    }

    protected void setScope(String clsName, int jbseScope, int heapsynScope) {
        if (this.scopeForJBSE == null || this.scopeForHeapSyn == null) {
            this.scopeForJBSE = new HashMap<>();
            this.scopeForHeapSyn = new HashMap<>();
        }
        this.scopeForJBSE.put(clsName, jbseScope);
        this.scopeForHeapSyn.put(clsName, heapsynScope);
    }

    private static final String[] COMMON_HEX_FILES = new String[]{
            "HEXsettings/thesis/common.jbse",
    };

    protected void setHexFilePaths(String... hexFilePaths) {
        this.hexFilePaths = Stream.concat(
                Arrays.stream(COMMON_HEX_FILES),
                Arrays.stream(hexFilePaths)
        ).toArray(String[]::new);
    }

    private void configure(boolean verbose) {
        Options options = Options.I();
        options.setSolverTmpDir(SOLVER_TMP_DIR);
        JBSEParameters params = JBSEParameters.I();
        params.setTargetClassPath(TARGET_CLASS_PATH);
        params.setTargetSourcePath(TARGET_SOURCE_PATH);
        params.setShowOnConsole(true);
        if (verbose) {
            params.setFormatMode(RunParameters.StateFormatMode.TEXT);
        }
        if (this.hexFilePaths == null)
            throw new IllegalStateException("hexFilePaths not set");
        System.out.println("HEX setting files: " + Arrays.toString(this.hexFilePaths));
        params.setSettingsPath(this.hexFilePaths);
        if (this.scopeForJBSE == null)
            throw new IllegalStateException("scopeForJBSE not set");
        for (Map.Entry<String, Integer> entry : this.scopeForJBSE.entrySet()) {
            try {
                String clsName = entry.getKey();
                Class<?> cls = Class.forName(clsName);
                int scope = entry.getValue();
                System.out.println("JBSE heap scope: " + clsName + " -> " + scope);
                params.setHeapScope(cls, scope);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected TestGenerator buildGraph(boolean verbose) {
        long start = System.currentTimeMillis();
        configure(verbose);
        SymbolicExecutor executor = new SymbolicExecutorWithCachedJBSE();
        DynamicGraphBuilder gb = new DynamicGraphBuilder(executor, this.publicMethods);
        if (this.scopeForHeapSyn == null)
            throw new IllegalStateException("scopeForHeapSyn not set");
        for (Map.Entry<String, Integer> entry : this.scopeForHeapSyn.entrySet()) {
            try {
                String clsName = entry.getKey();
                Class<?> cls = Class.forName(clsName);
                int scope = entry.getValue();
                System.out.println("HeapSyn heap scope: " + clsName + " -> " + scope);
                gb.setHeapScope(cls, scope);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        SymbolicHeap initHeap = new SymbolicHeapAsDigraph(ExistExpr.ALWAYS_TRUE);
        List<WrappedHeap> heaps = gb.buildGraph(initHeap, this.maxSeqLength);
        try {
            Path logFileParentDir = Paths.get(this.logFilePath).getParent();
            if (logFileParentDir != null) {
                Files.createDirectories(logFileParentDir);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            DynamicGraphBuilder.__debugPrintOut(heaps, executor, new PrintStream(this.logFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        TestGenerator testGen = new TestGenerator(heaps);
        long end = System.currentTimeMillis();
        System.out.println(">> buildGraph (dynamic): " + (end - start) + "ms\n");
        return testGen;
    }

}
