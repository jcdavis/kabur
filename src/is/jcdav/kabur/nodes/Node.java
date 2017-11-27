package is.jcdav.kabur.nodes;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Random;

public abstract class Node {
    public abstract MethodHandle compile();

    public static Node constant(int a) {
        return new ConstantNode(a);
    }
    public static Node leftParam() {
        return new LeftParamNode();
    }
    public static Node rightParam() {
        return new RightParamNode();
    }

    public static Node randomUnary(Random r, Node child) {
        if (r.nextInt(2) == 0)
            return new NegateNode(child);
        else
            return new NotNode(child);
    }
    public static Node randomBinary(Random r, Node left, Node right) {
        switch (r.nextInt(10)) {
            case 0: return new AddNode(left, right);
            case 1: return new SubNode(left, right);
            case 2: return new MulNode(left ,right);
            case 3: return new DivNode(left, right);
            case 4: return new AndNode(left ,right);
            case 5: return new OrNode(left, right);
            case 6: return new XorNode(left, right);
            case 7: return new ShlNode(left, right);
            case 8: return new AShrNode(left, right);
            case 9: return new LShrNode(left, right);
            default: throw new RuntimeException("Shouldn't reach");
        }
    }

    public static Node randomConditional(Random r, Node trueNode, Node falseNode, Node condLeft, Node condRight) {
        switch (r.nextInt(6)) {
            case 0: return new EqsNode(trueNode, falseNode, condLeft, condRight);
            case 1: return new NeqNode(trueNode, falseNode, condLeft, condRight);
            case 2: return new LtNode(trueNode, falseNode, condLeft, condRight);
            case 3: return new LteNode(trueNode, falseNode, condLeft, condRight);
            case 4: return new GtNode(trueNode, falseNode, condLeft, condRight);
            case 5: return new GteNode(trueNode, falseNode, condLeft, condRight);
            default: throw new RuntimeException("Shouldn't reach");
        }
    }
}

class ConstantNode extends Node {
    private final int value;

    public ConstantNode(int value) {
        this.value = value;
    }

    @Override
    public MethodHandle compile() {
        return MethodHandles.dropArguments(MethodHandles.constant(int.class, value), 0, int.class, int.class);
    }
    @Override
    public String toString() {
        return "" + value;
    }
}

class LeftParamNode extends Node {
    @Override
    public MethodHandle compile() {
        return MethodHandles.dropArguments(MethodHandles.identity(int.class), 1, int.class);
    }
    @Override
    public String toString() {
        return "left";
    }
}

class RightParamNode extends Node {
    @Override
    public MethodHandle compile() {
        return MethodHandles.dropArguments(MethodHandles.identity(int.class), 0, int.class);
    }
    @Override
    public String toString() {
        return "right";
    }

}

abstract class UnaryNode extends Node {
    protected final Node child;

    protected abstract String implName();

    public UnaryNode(Node child) {
        this.child = child;
    }

    public MethodHandle compile() {
        try {
            MethodHandle fn = MethodHandles.lookup().findStatic(this.getClass(), implName(), MethodType.methodType(int.class, int.class));
            return MethodHandles.filterReturnValue(child.compile(), fn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return implName() + "(" + child.toString() + ")";
    }
}

class NotNode extends UnaryNode {
    public NotNode(Node child) {
        super(child);
    }
    public static int not(int p) {
        return ~p;
    }
    @Override
    protected String implName() {
        return "not";
    }
}

class NegateNode extends UnaryNode {
    public NegateNode(Node child) {
        super(child);
    }
    public static int negate(int p) {
        return -p;
    }
    @Override
    protected String implName() {
        return "negate";
    }
}

abstract class BinaryNode extends Node {
    protected final Node left;
    protected final Node right;

    protected abstract String implName();

    public BinaryNode(Node left, Node right) {
        this.left = left;
        this.right = right;
    }

    protected MethodType implType() { return MethodType.methodType(int.class, int.class, int.class); }

    public MethodHandle compile() {
        try {
            MethodHandle fn = MethodHandles.lookup().findStatic(this.getClass(), implName(), implType());
            // (leftHandle, left, right) -> fn(leftHandle, rightHandle)
            MethodHandle part = MethodHandles.collectArguments(fn, 1, right.compile());
            return MethodHandles.foldArguments(part, left.compile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return implName() + "(" + left.toString() + "," + right.toString() + ")";
    }
}

class AddNode extends BinaryNode {
    public AddNode(Node left, Node right) {
        super(left, right);
    }

    public static int add(int a, int b) {
        return a+b;
    }

    @Override
    protected String implName() {
        return "add";
    }
}

class SubNode extends BinaryNode {
    public SubNode(Node left, Node right) {
        super(left, right);
    }

    public static int sub(int a, int b) {
        return a-b;
    }
    @Override
    protected String implName() {
        return "sub";
    }
}

class MulNode extends BinaryNode {
    public MulNode(Node left, Node right) {
        super(left, right);
    }

    public static int mul(int a, int b) {
        return a*b;
    }
    @Override
    protected String implName() {
        return "mul";
    }
}

class DivNode extends BinaryNode {
    public DivNode(Node left, Node right) {
        super(left, right);
    }

    public static int div(int a, int b) {
        return a/b;
    }
    @Override
    protected String implName() {
        return "div";
    }
}

class AndNode extends BinaryNode {
    public AndNode(Node left, Node right) {
        super(left, right);
    }

    public static int and(int a, int b) {
        return a&b;
    }
    @Override
    protected String implName() {
        return "and";
    }
}

class OrNode extends BinaryNode {
    public OrNode(Node left, Node right) {
        super(left, right);
    }

    public static int or(int a, int b) {
        return a|b;
    }
    @Override
    protected String implName() {
        return "or";
    }
}

class XorNode extends BinaryNode {
    public XorNode(Node left, Node right) {
        super(left, right);
    }

    public static int xor(int a, int b) {
        return a^b;
    }
    @Override
    protected String implName() {
        return "xor";
    }
}

class ShlNode extends BinaryNode {
    public ShlNode(Node left, Node right) {
        super(left, right);
    }

    public static int shl(int a, int b) {
        return a<<b;
    }
    @Override
    protected String implName() {
        return "shl";
    }
}

class AShrNode extends BinaryNode {
    public AShrNode(Node left, Node right) {
        super(left, right);
    }

    public static int ashr(int a, int b) {
        return a>>b;
    }
    @Override
    protected String implName() {
        return "ashr";
    }
}

class LShrNode extends BinaryNode {
    public LShrNode(Node left, Node right) {
        super(left, right);
    }

    public static int lshr(int a, int b) {
        return a>>>b;
    }
    @Override
    protected String implName() {
        return "lshr";
    }
}

abstract class ConditionalNode extends BinaryNode {
    protected final Node trueNode;
    protected final Node falseNode;


    public ConditionalNode(Node trueNode, Node falseNode, Node condLeft, Node condRight) {
        super(condLeft, condRight);
        this.trueNode = trueNode;
        this.falseNode = falseNode;
    }

    @Override
    protected MethodType implType() { return MethodType.methodType(boolean.class, int.class, int.class); }

    @Override
    public MethodHandle compile() {
        try {
            MethodHandle conditional = super.compile();
            return MethodHandles.guardWithTest(conditional, trueNode.compile(), falseNode.compile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "if(" + super.toString() + "," + trueNode.toString() + "," + falseNode.toString() + ")";
    }
}

class EqsNode extends ConditionalNode {
    public EqsNode(Node trueNode, Node falseNode, Node condLeft, Node condRight) {
        super(trueNode, falseNode, condLeft, condRight);
    }
    public static boolean eqs(int a, int b) { return a == b; }
    @Override
    protected String implName() { return "eqs"; }
}

class NeqNode extends ConditionalNode {
    public NeqNode(Node trueNode, Node falseNode, Node condLeft, Node condRight) {
        super(trueNode, falseNode, condLeft, condRight);
    }
    public static boolean neq(int a, int b) { return a != b; }
    @Override
    protected String implName() { return "neq"; }
}

class LtNode extends ConditionalNode {
    public LtNode(Node trueNode, Node falseNode, Node condLeft, Node condRight) {
        super(trueNode, falseNode, condLeft, condRight);
    }
    public static boolean lt(int a, int b) { return a < b; }
    @Override
    protected String implName() { return "lt"; }
}

class LteNode extends ConditionalNode {
    public LteNode(Node trueNode, Node falseNode, Node condLeft, Node condRight) {
        super(trueNode, falseNode, condLeft, condRight);
    }
    public static boolean lte(int a, int b) { return a <= b; }
    @Override
    protected String implName() { return "lte"; }
}

class GtNode extends ConditionalNode {
    public GtNode(Node trueNode, Node falseNode, Node condLeft, Node condRight) {
        super(trueNode, falseNode, condLeft, condRight);
    }
    public static boolean gt(int a, int b) { return a > b; }
    @Override
    protected String implName() { return "gt"; }
}

class GteNode extends ConditionalNode {
    public GteNode(Node trueNode, Node falseNode, Node condLeft, Node condRight) {
        super(trueNode, falseNode, condLeft, condRight);
    }
    public static boolean gte(int a, int b) { return a >= b; }
    @Override
    protected String implName() { return "gte"; }
}