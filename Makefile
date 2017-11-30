
all: compile run

setup:
	mkdir -p out

clean:
	rm -rf out

compile: setup
	javac -d out src/is/jcdav/kabur/*.java src/is/jcdav/kabur/nodes/*.java

JAVA=$(shell which java)
run: compile
	 $(JAVA) \
	-XX:+UnlockDiagnosticVMOptions \
	-XX:CompileCommand=dontinline,*.getResult \
	-XX:-TieredCompilation \
	-XX:-BackgroundCompilation \
	-XX:MaxInlineLevel=100 \
	-XX:CompileThreshold=1000 \
	-Djava.lang.invoke.MethodHandle.DONT_INLINE_THRESHOLD=0 \
	-cp out \
	is.jcdav.kabur.Kabur
	
