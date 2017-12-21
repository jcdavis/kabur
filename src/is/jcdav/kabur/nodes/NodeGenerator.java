package is.jcdav.kabur.nodes;

import java.util.Random;

public class NodeGenerator {
    public static Node generate(Random r) {
        return generate(r, 0);
    }

    public static Node generate(Random r, int level) {
        if (r.nextInt(100) < level*5) {
            if (r.nextInt(100) < 60) {
                if (r.nextBoolean()) {
                    switch (r.nextInt(5)) {
                        case 0: return Node.constant(Integer.MIN_VALUE);
                        case 1: return Node.constant(-1);
                        case 2: return Node.constant(0);
                        case 3: return Node.constant(1);
                        case 4: return Node.constant(Integer.MAX_VALUE);
                    }
                } else {
                    return Node.constant(r.nextInt());
                }
            } else if (r.nextBoolean())
                return Node.leftParam();
            else
                return Node.rightParam();
        }
        //These numbers are extremely arbitrary
        if (r.nextInt(100) < 10) {
            // To make conditionals more meaningful, prefer simpler expressions and always check param
            Node param = r.nextBoolean()? Node.leftParam() : Node.rightParam();
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
