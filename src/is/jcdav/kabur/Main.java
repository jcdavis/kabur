package is.jcdav.kabur;

import is.jcdav.kabur.nodes.Node;
import is.jcdav.kabur.nodes.NodeGenerator;

import java.util.Random;

public class Main {

    public static void main(String[] args) throws Throwable {
        // Roughly what the default Random constructor will generate
        long seed = System.nanoTime() ^ (8682522807148012L * 181783497276652981L);
        System.out.println("Using Seed " + seed);
        Random r = new Random(seed);
        while(true) {
            Node generated = NodeGenerator.generate(r);
            ResultRunner runner = new ResultRunner(r, generated, 5);
            runner.run();
        }
    }
}
