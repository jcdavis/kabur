package is.jcdav.kabur;

import is.jcdav.kabur.nodes.Node;
import is.jcdav.kabur.nodes.NodeGenerator;

import java.util.Random;

public class Main {

    public static void main(String[] args) throws Throwable {
        Random r = new Random();
        while(true) {
            Node generated = NodeGenerator.generate(r);
            ResultRunner runner = new ResultRunner(r, generated, 5);
            runner.run();
        }
    }
}
