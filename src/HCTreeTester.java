import static org.junit.Assert.*;

public class HCTreeTester {
    HCTree huff1;

    @org.junit.Before
    public void setUp() throws Exception {
        huff1 = new HCTree();
    }

    public void inorder(HCTree.HCNode node) {
        if (node == null) return;
        inorder(node.getC0());
        System.out.println(node);
        inorder(node.getC1());

    }

    @org.junit.Test
    public void buildTree() {
        int[] freq = new int[256];
        freq[10] = 1;
        freq[97] = 17;
        freq[98] = 8;
        freq[99] = 7;
        freq[100] = 14;
        freq[101] = 9;
        freq[102] = 1;
        huff1.buildTree(freq);

        inorder(huff1.getRoot());

    }
}