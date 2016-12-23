/**
 * A binary trie implementation of Huffman codes
 *
 * @author <a href="mailto:bg5087a@american.edu">Ben Goldstein</a>
 * @version 1.0
 */

import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Map;
import java.io.Reader;
import java.io.InputStreamReader;
import java.lang.StringBuilder;

public class HuffmanTrie {

    private HuffmanNode root;

    /**
     * private default constructor so that this class cannot be instantiated except via the
     * <code>buildTrie</code> method or the <code>readBinaryRepresentation</code>
     */
    private HuffmanTrie() {
    }

    /**
     * Compresses or decompresses standard in to standard out according to command line argument
     *
     * @param args specifies compression (c) or decompression (d)
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Provide a single command line argument to say if");
            System.out.println("I should be compressing (c) or decompressing (d).");
            return;
        }
        switch (args[0]) {
            case "c":
                compressStdIn();
                break;
            case "d":
                decompressStdIn();
                break;
            default:
                System.out.println("Invalid argument");
        }
    }

    /**
     * Decompress standard input to standard output
     */
    private static void decompressStdIn() {
        HuffmanTrie trie = readBinaryRepresentation();

        // System.out.println(trie);

        Map<Character, String> map = trie.getCode();

        try{
            while(true){
                System.out.print(trie.readChar());
            }
        } catch(Exception e){
        }  


    }

    /**
     * Compress standard input to standard output
     */
    private static void compressStdIn() {

		Reader reader = new InputStreamReader(System.in);

        StringBuilder builder = new StringBuilder();

		String wholeDoc = "";

    	HashMap<Character, Integer> charNos = new HashMap();
    	char currentChar = 'a';

    	int data = 0;
	    
	    try{
	    	
		    while(data != -1){
                data = reader.read();

		        currentChar = (char) data;
                if(currentChar != '￿'){

    				if (!charNos.containsKey(currentChar)) charNos.put(currentChar, 1);
    				else{
    					int x = (int)charNos.get(currentChar);
    					charNos.put(currentChar, x + 1);
    	    		}
                    builder.append(currentChar);
    	    		//wholeDoc += currentChar;
                }
	    	}
	    } catch (Exception e){
	    	System.out.println("Program failed");
	    }

        wholeDoc = builder.toString();



    	Collection<Integer> charCObj = charNos.values();
    	Set<Character> charaObj = charNos.keySet();

        Integer[] charCountsI = (Integer[]) (charCObj.toArray(new Integer[charCObj.size()]));
        Character[] charactersC = (Character[]) (charaObj.toArray(new Character[charaObj.size()]));

        char[] characters = new char[charactersC.length];
        for(int i = 0; i< characters.length; i++)
            characters[i] = charactersC[i].charValue();

        int[] charCounts = new int[charCountsI.length];
        for(int i = 0; i< characters.length; i++)
            charCounts[i] = charCountsI[i].intValue();


    	HuffmanTrie trie = buildTrie(characters, charCounts);

        // System.out.println(trie.toString());

    	Map<Character, String> map = trie.getCode();

    	trie.writeBinaryRepresentation(map);

        for (int i = 0; i < wholeDoc.length(); i++) {
            trie.writeChar(wholeDoc.charAt(i),map);
        }

        BinaryStdOut.flush();

    }

    /**
     * Constructs a HuffmanTrie based on the characters and counts given
     *
     * @param characters an array of characters found in the given data file, each character should appear at most once
     * @param charCounts an array of counts of characters such that <code>charCounts[i]</code> is the number of
     *                   occurrences of the character <code>characters[i]</code>.
     * @return a new instance of <code>HuffmanTrie</code> with tree structure given by Huffman's algorithm.
     */
    public static HuffmanTrie buildTrie(char[] characters, int[] charCounts) {
        assert characters != null && charCounts != null && characters.length == charCounts.length;

        int numChars = characters.length;
        HuffmanTrie trie = new HuffmanTrie();
        // HuffmanNode[] leaves = new HuffmanNode[numChars];
        PriorityQueue<HuffmanNode> nodeQueue = new PriorityQueue<>();

        for(int i = 0; i<numChars; i++) {
        	HuffmanNode x = new HuffmanNode();
        	x.count = charCounts[i];
        	x.key = characters[i];
        	nodeQueue.add(x);
        }

        if (nodeQueue.size() == 1) {
            HuffmanNode last = nodeQueue.poll();
            trie.root = last;
        } else {
            while (nodeQueue.size() > 2) {
                HuffmanNode x = nodeQueue.poll();
                HuffmanNode y = nodeQueue.poll();
                HuffmanNode aggregate = new HuffmanNode(x, y, x.count+y.count);
                nodeQueue.add(aggregate);
            }

            HuffmanNode a = nodeQueue.poll();
            HuffmanNode b = nodeQueue.poll();
            HuffmanNode last = new HuffmanNode(a, b, a.count+b.count);
            trie.root = last;
        }

        return trie;
    }

    /**
     * Creates an new instance of <code>HuffmanTrie</code> by reading standard input using <code>BinaryStdIn</code>
     *
     * @return a new instance of <code>HuffmanTrie</code>
     */
    public static HuffmanTrie readBinaryRepresentation() {
        HuffmanTrie trie = new HuffmanTrie();
        
        trie.root = new HuffmanNode();

        if (BinaryStdIn.readBoolean()) {
            trie.root = new HuffmanNode();
            trie.root.key = BinaryStdIn.readChar();
        } else {
            HuffmanNode interior = new HuffmanNode();
            trie.root.left = trie.root.readBinaryRepresentation();
            trie.root.right = trie.root.readBinaryRepresentation();
        }

        return trie;
    }


    /**
     * Returns a <code>Map</code> giving Huffman code determined by this trie. Code words are represented by Strings of
     * 0's and 1's.
     *
     * @return <code>Map</code> giving Huffman code determined by this trie
     */
    public Map<Character, String> getCode() {
        Map<Character, String> code = new HashMap<>();
        root.fillInCode(code, "");
        return code;
    }

    /**
     * Reads an encoded character from standard input using <code>BinaryStdIn</code>
     *
     * @return decoded character.
     */
    public char readChar() {
        return root.readChar();
    }

    /**
     * Writes the binary representation of <code>c</code> to standard output using <code>BinaryStdOut</code>
     *
     * @param c character to write
     */
    public void writeChar(char c, Map<Character, String> map) {
        String toWrite = map.get(c);
        for (int i = 0; i < toWrite.length(); i++) {
            char x = toWrite.charAt(i);
            if (x == '0') BinaryStdOut.write(false);
            else if (x == '1') BinaryStdOut.write(true);
            else System.out.println("Map failed");
        }
    }

    /**
     * @return String representation of the trie via its pre-order traversal
     */
    @Override
    public String toString() {
        return this.preOrder(); // TODO: implement
    }

    /**
     * @return String rep of pre-order traversal
     */
    public String preOrder() {
        if (root == null) return "Empty";
        else return root.preOrder();
    }


    /**
     * Write the binary representation of this trie to standard output using <code>BinaryStdOut</code>
     */
    public void writeBinaryRepresentation(Map<Character, String> map) {
      	this.root.writeBinaryRepresentation(map);
    }

    /**
     * Inner class for nodes of the binary trie
     */
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        char key;
        int count;
        HuffmanNode left, right;

        public HuffmanNode(){

        }

        public HuffmanNode(HuffmanNode l, HuffmanNode r, int n){
            left = l;
            right = r;
            count = n;
        }

        /**
         * @return <code>true</code> is <code>this</code> is a leaf (i.e. no children) Otherwise, <code>false</code>
         */
        public boolean isLeaf() {
            if (this.left == null && this.right == null) return true;
            return false;
        }

        /**
        * @return 1 if this node's count value is greater than the other node's, 0 otherwise
        */
        @Override
        public int compareTo(HuffmanNode o) {
            if(this.count > o.count) return 1;
            else return -1;
        }

        @Override
        public String toString() {
            return (String) "(" + key + "," + count + ")";
        }

		
		/**
         * populates <code>Map</code> of codewords recursively
         *
         * @param codeWords <code>Map</code> to populate
         * @param codeSoFar <code>String</code> containing prefix for all codewords under this node.
         */
        void fillInCode(Map<Character, String> codeWords, String codeSoFar) {
            if (this.isLeaf()) {
                codeWords.put(key, codeSoFar);
            } else {
                assert left != null && right != null;
                left.fillInCode(codeWords, codeSoFar + '0');
                right.fillInCode(codeWords, codeSoFar + '1');
            }
        }

        /**
         * Recursive algoritm that implements a trie's writeBinaryRepresentation method. 
         * Encodes bite path down root using <code>BinaryStdIn</code>.
         *
         * A call on a <code>HuffmanNode</code> will print out a representation of 
         * all parts of the trie which are this <code>HuffmanNode</code>'s children.
         */
        public void writeBinaryRepresentation(Map<Character, String> map){
        	if (this.isLeaf()) {
        		BinaryStdOut.write(true);
        		BinaryStdOut.write(this.key);
        	} else {
        		BinaryStdOut.write(false);
        		left.writeBinaryRepresentation(map);
        		right.writeBinaryRepresentation(map);
        	}
        }

        /**
         * Recursive algoritm that implements a trie's readBinaryRepresentation method. 
         * Reads bite path down root using <code>BinaryStdIn</code>.
         * 
         * @return a <code>HuffmanNode</code> whose children are represented in the binary stream.
         */
        public HuffmanNode readBinaryRepresentation() {
            if (BinaryStdIn.readBoolean()){
                HuffmanNode leaf = new HuffmanNode();
                leaf.key = BinaryStdIn.readChar();
                return leaf;
            } else {
                HuffmanNode interior = new HuffmanNode();
                interior.left = readBinaryRepresentation();
                interior.right = readBinaryRepresentation();
                return interior;
            }
        }

        /**
         * Recursive algoritm that implements a trie's readChar. Follows bite path down root using <code>BinaryStdIn</code>.
         *
         * @return decoded character.
         */
        public char readChar() {
            if (this.isLeaf()) return this.key;

            boolean x = BinaryStdIn.readBoolean();
            
            if (x) return this.right.readChar();
            else return this.left.readChar();
        }

        /**
         * Recursive algoritm that implements a trie's toString method.
         *
         * @return string representation of branches below and including this node.
         */
        String preOrder() {
            String str = this.toString() + " ";
            if (left != null) str += left.preOrder();
            if (right != null) str += right.preOrder();
            return str;
        }
    }
}