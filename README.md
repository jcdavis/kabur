# Kabur

Kabur is a dumb AST-based fuzzer designed to test JVM JIT compilers. It generates ASTs of arithmetic and (basic) conditional expressions, and uses `MethodHandle` + `java.lang.invoke` machinery to generate the appropriate bytecode, in which each node is a function that takes 2 `int` parameters and returns an `int` value. This function is then run by the interpreter against a series of known interesting parameters (`MIN_VALUE,-1,0,1,MAX_VALUE`), plus a number of random values. These results (in which an `ArithmeticException` from division by 0 is a valid result) are stored, and then checked against the running the compiled MethodHandle.

Because the usefulness of these checks relies on a number of assumptions about inlining, compilation, and execution being true, Kabur is run with a bunch of particular JVM settings. As such, you probably want to use run `make run`, and if you want to target a specific JVM and/or flags (eg JVMCI) use `make JAVA=.... run`.

Despite being somewhat limited, Kabur has [found one bug](http://mail.openjdk.java.net/pipermail/graal-dev/2017-December/005132.html) as far.
