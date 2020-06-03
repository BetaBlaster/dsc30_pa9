/*
 * Name: Andrew Mokhtarzadeh
 * PID: A15557088
 */

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Compress the first given file to the second given file using Huffman coding
 * 
 * @author Andrew Mokhtarzadeh
 * @since 06/02/20
 */
public class Compress {

    private static final int EXP_ARG = 2; // number of expected arguments
    private static final int NUM_CHARS = 256;

    public static void main(String[] args) throws IOException {

        // Check if the number of arguments is correct
        if (args.length != EXP_ARG) {
            System.out.println("Invalid number of arguments.\n"
                + "Usage: ./compress <infile outfile>.\n");
            return;
        }

        // read all the bytes from the given file and make it to a byte array
        byte[] input = Files.readAllBytes(Paths.get(args[0]));

        FileOutputStream file = new FileOutputStream(args[1]);
        DataOutputStream out = new DataOutputStream(file);
        BitOutputStream bitOut = new BitOutputStream(out);

        if (input.length == 0) return;

        // construct HCTree from the file
        int[] freq = new int[NUM_CHARS];
        for (byte b: input)
            freq[b & 0xff]++;
        HCTree huffman = new HCTree();
        huffman.buildTree(freq);

        // write number of bytes to out file
        out.writeInt(input.length);

        // encode HCTree and every byte
        huffman.encodeHCTree(huffman.getRoot(), bitOut);
        for (byte b: input)
            huffman.encode(b, bitOut);


        // There might be several padding bits in the bitOut that we haven't written, so
        // flush it first.
        bitOut.flush();
        out.close();
        file.close();
    }
}
