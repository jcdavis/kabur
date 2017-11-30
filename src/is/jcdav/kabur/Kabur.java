package is.jcdav.kabur;

import is.jcdav.kabur.nodes.Node;
import is.jcdav.kabur.nodes.NodeGenerator;

import java.util.Optional;
import java.util.Random;

public class Kabur {
    // Default is roughly what the default Random constructor will generate
    public static final long seed = Optional.ofNullable(System.getProperty("kabur.seed"))
        .map(Long::parseLong)
        .orElseGet(() -> System.nanoTime() ^ (8682522807148012L * 181783497276652981L));

    public static void main(String[] args) throws Throwable {
        System.out.println("Using Seed " + seed);
        Random r = new Random(seed);
        while(true) {
            Node generated = NodeGenerator.generate(r);
            ResultRunner runner = new ResultRunner(r, generated, 5);
            runner.run();
        }
    }
}
