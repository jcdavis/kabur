package is.jcdav.kabur;

import is.jcdav.kabur.nodes.Node;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Random;

public class ResultRunner {
    private final Random r;
    private final Node root;
    private final int[] params;
    private volatile int bar;

    public ResultRunner(Random r, Node root, int rparams) {
        this.r = r;
        this.root = root;
        params = new int[rparams + 5];
        params[0] = Integer.MIN_VALUE;
        params[1] = -1;
        params[2] = 0;
        params[3] = 1;
        params[4] = Integer.MAX_VALUE;
        for(int i = 0; i < rparams; i++) {
            params[i + 5] = r.nextInt();
        }
    }

    public static class Result {
        final ArithmeticException e;
        final int result;

        public Result(ArithmeticException e) {
            assert e!= null;
            this.e = e;
            this.result = 0;
        }

        public Result(int result) {
            this.e = null;
            this.result = result;
        }

        public boolean isNumber() {
            return e == null;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Result) {
                Result r = (Result)other;
                return (e != null && r.e != null) ||
                        (e == null && r.e == null && result == r.result);
            }
            return false;
        }

        public static Result getResult(MethodHandle mh, int left, int right) {
            try {
                return new Result((int)mh.invokeExact(left, right));
            } catch (ArithmeticException e) {
                return new Result(e);
            } catch(Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @Override
        public String toString() {
            if (e != null) {
                return "Result(" + e.toString() + ")";
            } else {
                return "Result(" + result + ")";
            }
        }
    }

    private static final Field form;
    private static final Field isCompiled;
    static {
        try {
            form = MethodHandle.class.getDeclaredField("form");
            form.setAccessible(true);
            isCompiled = Class.forName("java.lang.invoke.LambdaForm").getDeclaredField("isCompiled");
            isCompiled.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static boolean isCompiled(MethodHandle mh) {
        try {
            Object lf = form.get(mh);
            return (boolean) isCompiled.get(lf);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        Result[][] results = new Result[params.length][params.length];
        System.out.println("Testing " + root.toString());
        MethodHandle mh = root.compile();
        int goodLeft = 1, goodRight = 1;
        boolean hasGood = false;

        for(int i = 0; i < params.length; i++) {
            for (int j = 0; j < params.length; j++) {
                Result r = Result.getResult(mh, params[i], params[j]);
                results[i][j] = r;
                if (!hasGood && r.isNumber()) {
                    hasGood = true;
                    goodLeft = params[i];
                    goodRight = params[j];
                }
            }
        }

        while (!isCompiled(mh)) {
            for (int i = 0; i < 100; i++) {
                bar += Result.getResult(mh, goodLeft, goodRight).result;
            }
        }

        for(int i = 0; i < params.length; i++) {
            for (int j = 0; j < params.length; j++) {
                Result r = Result.getResult(mh, params[i], params[j]);
                if (!r.equals(results[i][j])) {
                    String output =
                            "--FOUND DIFFERENCE--\n" +
                            "Tree:\n" +
                            root.toString() + "\n" +
                            "left: " + params[i] + ", right: " + params[j] + "\n" +
                            "Interpreted: " + results[i][j].toString() + "\n" +
                            "Compiled: " + r.toString() + "\n";
                    System.out.println(output);
                    System.err.println(output);
                    throw new RuntimeException(output);
                }
            }
        }

    }
}
