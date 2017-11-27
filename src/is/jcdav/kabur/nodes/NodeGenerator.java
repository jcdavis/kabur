package is.jcdav.kabur.nodes;

import java.util.Random;

public class NodeGenerator {
    public static Node generate(Random r) {
        return generate(r, 0);
    }

    public static Node generate(Random r, int level) {
        if (r.nextInt(100) < level*5) {
            switch (r.nextInt(3)) {
                case 0: return Node.leftParam();
                case 1: return Node.rightParam();
                case 2: return Node.constant(r.nextInt());
            }
        }
        //Arbitary
        if (r.nextInt(100) < 10) {
            // To make conditionals more meaningful, prefer simpler expressions and always check param
            Node param = r.nextInt(2) == 0? Node.leftParam() : Node.rightParam();
            return Node.randomConditional(r, generate(r, level + 1), generate(r, level + 1), generate(r, level + 3), param);
        } else {
            if (r.nextInt(100) < 20) {
                return Node.randomUnary(r, generate(r, level + 1));
            } else {
                return Node.randomBinary(r, generate(r, level + 1), generate(r, level + 1));
            }
        }
    }
}
