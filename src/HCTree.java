/*
 * Name: Andrew Mokhtarzadeh
 * PID: A15557088
 */

import java.io.*;
import java.util.Iterator;
import java.util.Stack;
import java.util.PriorityQueue;

/**
 * The Huffman Coding Tree
 *
 * @author Andrew Mokhtarzadeh
 * @since 06/02/20
 */
public class HCTree {
    // alphabet size of extended ASCII
    private static final int NUM_CHARS = 256;
    // number of bits in a byte
    private static final int BYTE_BITS = 8;

    // the root of HCTree
    private HCNode root;
    // the leaves of HCTree that contain all the symbols
    private HCNode[] leaves = new HCNode[NUM_CHARS];

    /**
     * The Huffman Coding Node
     */
    protected class HCNode implements Comparable<HCNode> {

        byte symbol; // the symbol contained in this HCNode
        int freq; // the frequency of this symbol
        HCNode c0, c1, parent; // c0 is the '0' child, c1 is the '1' child

        /**
         * Initialize a HCNode with given parameters
         *
         * @param symbol the symbol contained in this HCNode
         * @param freq   the frequency of this symbol
         */
        HCNode(byte symbol, int freq) {
            this.symbol = symbol;
            this.freq = freq;
        }

        /**
         * Getter for symbol
         *
         * @return the symbol contained in this HCNode
         */
        byte getSymbol() {
            return this.symbol;
        }

        /**
         * Setter for symbol
         *
         * @param symbol the given symbol
         */
        void setSymbol(byte symbol) {
            this.symbol = symbol;
        }

        /**
         * Getter for freq
         *
         * @return the frequency of this symbol
         */
        int getFreq() {
            return this.freq;
        }

        /**
         * Setter for freq
         *
         * @param freq the given frequency
         */
        void setFreq(int freq) {
            this.freq = freq;
        }

        /**
         * Getter for '0' child of this HCNode
         *
         * @return '0' child of this HCNode
         */
        HCNode getC0() {
            return c0;
        }

        /**
         * Setter for '0' child of this HCNode
         *
         * @param c0 the given '0' child HCNode
         */
        void setC0(HCNode c0) {
            this.c0 = c0;
        }

        /**
         * Getter for '1' child of this HCNode
         *
         * @return '1' child of this HCNode
         */
        HCNode getC1() {
            return c1;
        }

        /**
         * Setter for '1' child of this HCNode
         *
         * @param c1 the given '1' child HCNode
         */
        void setC1(HCNode c1) {
            this.c1 = c1;
        }

        /**
         * Getter for parent of this HCNode
         *
         * @return parent of this HCNode
         */
        HCNode getParent() {
            return parent;
        }

        /**
         * Setter for parent of this HCNode
         *
         * @param parent the given parent HCNode
         */
        void setParent(HCNode parent) {
            this.parent = parent;
        }

        /**
         * Check if the HCNode is leaf (has no children)
         *
         * @return if it's leaf, return true. Otherwise, return false.
         */
        boolean isLeaf() {
            if (c0 == null && c1 == null)
                return true;
            return false;
        }

        /**
         * String representation
         *
         * @return string representation
         */
        public String toString() {
            return "Symbol: " + this.symbol + "; Freq: " + this.freq;
        }

        /**
         * Compare two nodes
         *
         * @param o node to compare
         * @return int positive if this node is greater
         */
        public int compareTo(HCNode o) {
            if (this.getFreq() > o.getFreq())
                return 1;
            else if (this.getFreq() < o.getFreq())
                return -1;
            else {
                if (this.getSymbol() > o.getSymbol())
                    return 1;
                else if (this.getSymbol() < o.getSymbol())
                    return -1;
            }
            return 0;
        }
    }

    /**
     * Returns the root node
     *
     * @return root node
     */
    public HCNode getRoot() {
        return root;
    }

    /**
     * Sets the root node
     *
     * @param root node to set
     */
    public void setRoot(HCNode root) {
        this.root = root;
    }

    /**
     * build a Huffman Coding Tree based off of frequency of symbols
     *
     * @param freq int array where index is the ascii value of characters and value is frequency
     *             character appears in the to-be compressed file
     */
    public void buildTree(int[] freq) {
        PriorityQueue<HCNode> priority = new PriorityQueue<>();
        byte tempB;
        int tempF;
        HCNode currNode;
        // initial populate priority queue
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] > 0) {
                tempB = (byte) i;
                tempF = freq[i];
                currNode = new HCNode(tempB, tempF);
                priority.add(currNode);
                leaves[i] = currNode;
            }
        }
        // building HCTree
        if (priority.size() == 1) {
            HCNode tempC = priority.poll();
            HCNode tempRoot = new HCNode(tempC.getSymbol(), tempC.getFreq());
            this.root = tempRoot;
            tempRoot.setC0(tempC);
            tempRoot.setC1(tempC);
            tempC.setParent(tempRoot);

        } else{
            HCNode tempC0, tempC1, tempParent;
            while (!priority.isEmpty()) {
                tempC0 = priority.poll();
                tempC1 = priority.poll();

                tempParent = new HCNode(tempC0.getSymbol(), tempC0.getFreq() + tempC1.getFreq());

                tempC0.setParent(tempParent);
                tempC1.setParent(tempParent);
                tempParent.setC0(tempC0);
                tempParent.setC1(tempC1);

                if (priority.isEmpty())
                    this.root = tempParent;
                else
                    priority.add(tempParent);
            }
        }
    }

    /**
     * encode a given symbol with the HCTree built
     *
     * @param symbol value being encoded
     * @param out where bits will be written
     * @throws IOException
     */
    public void encode(byte symbol, BitOutputStream out) throws IOException {
        int ascii = symbol & 0xff;
        HCNode currNode = leaves[ascii];
        HCNode parNode = currNode.getParent();
        Stack<Integer> stack = new Stack<>();
        while (parNode != null) {
            if (currNode.equals(parNode.getC0())) stack.push(0);
            else if (currNode.equals(parNode.getC1())) stack.push(1);

            currNode = parNode;
            parNode = currNode.getParent();
        }
        while (!stack.isEmpty())
            out.writeBit(stack.pop());


    }

    /**
     * read input to decode a single symbol with the HCTree built
     *
     * @param in where bits will be read
     * @return btye representation of decoded symbol
     * @throws IOException
     */
    public byte decode(BitInputStream in) throws IOException {
        HCNode currNode = this.root;
        int currBit;
        while (!currNode.isLeaf()) {
            currBit = in.readBit();
            if (currBit == 0)
                currNode = currNode.getC0();
            else if (currBit == 1)
                currNode = currNode.getC1();
        }

        return currNode.getSymbol();
    }

    /**
     * A recursive pre-order traversing to encode HCTree in header
     *
     * @param node current node being encoded
     * @param out where bits will be written
     * @throws IOException
     */
    public void encodeHCTree(HCNode node, BitOutputStream out) throws IOException {
        if (node == null) return;
        if (node.isLeaf()) {
            out.writeBit(1);
            out.writeByte(node.getSymbol());
        } else {
            out.writeBit(0);
        }
        encodeHCTree(node.getC0(), out);
        encodeHCTree(node.getC1(), out);
    }


    /**
     * recursively build the original HCTree from the header of encoded file
     *
     * @param in where bits will be read
     * @return leaf node in base case, branch node otherwise
     * @throws IOException
     */
    public HCNode decodeHCTree(BitInputStream in) throws IOException {
        int currentBit = in.readBit();
        if (currentBit == 1) { // Leaf Node (Base Case)
            return new HCNode(in.readByte(), 0);
        }
        if (currentBit == 0) { // Not Leaf Node
            HCNode left = decodeHCTree(in);
            HCNode right = decodeHCTree(in);
            leaves[left.getSymbol() & 0xff] = left;
            leaves[right.getSymbol() & 0xff] = right;


            HCNode parent = new HCNode(left.getSymbol(), 0);
            parent.setC0(left);
            parent.setC1(right);

            left.setParent(parent);
            right.setParent(parent);

            setRoot(parent);
            return parent;
        }

        return null;
    }

}
