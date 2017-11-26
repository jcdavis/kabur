package is.jcdav.kabur;

import is.jcdav.kabur.nodes.Node;
import is.jcdav.kabur.nodes.NodeGenerator;

import java.util.Random;

public class Main {

    public static void main(String[] args) throws Throwable {
        /*Node test = Node.add(Node.leftParam(), Node.negate(Node.constant(3)));
        ResultRunner runner = new ResultRunner(new Random(), test, 5);
        runner.run(100000);*/
        Random r = new Random();
        while(true) {
            Node generated = NodeGenerator.generate(r);
            ResultRunner runner = new ResultRunner(r, generated, 5);
            runner.run(20000);
        }
    }
}
