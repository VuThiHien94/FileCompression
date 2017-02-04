package com.example.vu_hien.demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class DHuffman {
    /* set tree default  */
    public static void setTree() {
        table = new Tree[256];
        list = new Tree[513];
        listTop = 513;
        list[--listTop] = root = NYT = new Tree(-1, 0, 512, null, null, null);
    } /* end of set tree default */

    /* this function decodes given input file (Dynamic Huffman coded) to output file */
    public static boolean decode(String inputFile, String outputFile) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(inputFile));
            out = new BufferedOutputStream(new FileOutputStream(outputFile));
        } catch (Exception e) {return false;}

        try {

            int val;
            Tree iterator = root; // go to root
            int bitNumber = 7;
            int nextByte = in.read();

            if (nextByte == -1) {
                return false;
            }

            while (true) {
                // is this a leaf
                if (iterator.left != null) { // no, then continue traverse
                    if ((1 << bitNumber & (byte) nextByte) == 0) iterator = iterator.left;
                    else iterator = iterator.right;
                    if (bitNumber-- == 0) {
                        bitNumber = 7;
                        nextByte = in.read();
                    } // end of if
                } else { // yes,
                    if (NYT == iterator) { // it is NYT
                        // read next bit for EOF
                        if ((1 << bitNumber & (byte) nextByte) != 0) val = 1;
                        else val = 0;
                        if (bitNumber-- == 0) {
                            bitNumber = 7;
                            nextByte = in.read();
                        } // end of read next bit for EOF
                        if (val == 1) { // if EOF bit equals 1 then break outerWhile;
                            break; /* outerWhile;*/
                        } else {// if not get new char
                            // val = 0; val must be zero to get a new char, but is already zero
                            for (int i = 0; i < 8; i++) {
                                if ((1 << bitNumber & (byte) nextByte) != 0) val |= 1 << i;
                                if (bitNumber-- == 0) {
                                    bitNumber = 7;
                                    nextByte = in.read();
                                } // end of if
                            }
                        }
                    } else { // it is not NYT
                        val = iterator.val;
                    }
                    out.write(val); // write value to disk
                    insert(val);
                    iterator = root; // go to root
                }
            }
            in.close();
            out.close();
            return true;
        } catch (IOException e) {return false;}
    } /* end of public void decode(String inputFile, String outputFile) */

    /* this function encodes (compresses) given file to output file */
    public static boolean encode(String inputFile, String outputFile) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(inputFile));
            out = new BufferedOutputStream(new FileOutputStream(outputFile));
        } catch (FileNotFoundException e) {return false;}

        try {
            Tree temp;
            int nextByte = 0;
            int bitNumber = 7;
            byte data[] = new byte[1000];
            int top;

            while (true) {
                int intRead = in.read();
                if (intRead != -1) { // if not end of file
                    if (table[intRead] != null) { // existing symbol
                        temp = table[intRead];
                        top = 0;
                        while (temp.parent != null) {
                            if (temp.parent.left == temp) data[top++] = 0;
                            else data[top++] = 1;
                            temp = temp.parent;
                        }
                        for (int i = top; i > 0; i--) {
                            if (data[--top] == 1) // write 1
                                nextByte |= 1 << bitNumber;
                            if (bitNumber-- == 0) {
                                bitNumber = 7;
                                out.write(nextByte);
                                nextByte = 0;
                            }
                        }
                    } else { // new symbol
                        // write NYT
                        temp = NYT;
                        top = 0;
                        while (temp.parent != null) {
                            if (temp.parent.left == temp) data[top++] = 0;
                            else data[top++] = 1;
                            temp = temp.parent;
                        }
                        for (int i = top; i > 0; i--) {
                            if (data[--top] == 1) // write 1
                                nextByte |= 1 << bitNumber;
                            if (bitNumber-- == 0) {
                                bitNumber = 7;
                                out.write(nextByte);
                                nextByte = 0;
                            }
                        }
                        // indicate that this is not EOF
                        if (bitNumber-- == 0) { // write 0
                            bitNumber = 7;
                            out.write(nextByte);
                            nextByte = 0;
                        }
                        // write symbol to disk
                        for (int i = 1; i < 256; i *= 2) {
                            if ((intRead & i) != 0) nextByte |= (1 << bitNumber);
                            if (bitNumber-- == 0) {
                                bitNumber = 7;
                                out.write(nextByte);
                                nextByte = 0;
                            } // end of if
                        } // end of for
                    }
                    insert(intRead);
                } else { // if end of file
                    // write NYT
                    temp = NYT;
                    top = 0;
                    while (temp.parent != null) {
                        if (temp.parent.left == temp) data[top++] = 0;
                        else data[top++] = 1;
                        temp = temp.parent;
                    }
                    for (int i = top; i > 0; i--) {
                        if (data[--top] == 1) // write 1
                            nextByte |= 1 << bitNumber;
                        if (bitNumber-- == 0) {
                            bitNumber = 7;
                            out.write(nextByte);
                            nextByte = 0;
                        }
                    }
                    // indicate that this is EOF
                    nextByte |= 1 << bitNumber; // write 1
                    out.write(nextByte);
                    break;
                }
            }
            in.close();
            out.close();
            return true;
        } catch (IOException e) {return false;}
    } /* end of public void encode(String inputFile, String outputFile) */

    /* this function inserts or increases weight of given value */
    private static void insert(int val) {

        Tree t = table[val];
        if (table[val] == null) {// new value
            Tree temp = NYT;
            Tree retVal = new Tree(val, 1, NYT.order - 1, null, null, temp);
            list[--listTop] = retVal;
            NYT = new Tree(-1, 0, NYT.order - 2, null, null, temp);
            list[--listTop] = NYT;
            temp.left = NYT;
            temp.right = retVal;
            temp.weight++;
            table[val] = retVal;
            if (table[val].parent == root) {
                return;
            }
            t = table[val].parent.parent;
        }

        while (t != root) { // stops at the root
            Tree temp = t;

            int i = t.order + 1; // +1 passes its self
            for (; (list[i].weight == t.weight) && (i < 512 /* 513 - 1 passes root*/); i++);
            i--;

            if ((list[i].order > temp.order) && (list[i] != t.parent)) {
                temp = list[i];
                Tree temp2 = list[temp.order];
                list[temp.order] = list[t.order];
                list[t.order] = temp2;
                if (t.parent.left == t) t.parent.left = temp;
                else t.parent.right = temp;
                if (temp.parent.left == temp) temp.parent.left = t;
                else temp.parent.right = t;
                temp2 = temp.parent;
                temp.parent = t.parent;
                t.parent = temp2;
                int order = t.order;
                t.order = temp.order;
                temp.order = order;
            }
            t.weight++;
            t = t.parent;
        }
        t.weight++;
    } /* end of private void insert(int val) */

    private static Tree root;
    private static Tree NYT; // Not Yet Transferred
    private static Tree table[]; // fast look up for leaves
    private static Tree list[]; // fast look up for all nodes
    private static int listTop; // top of the list

    private static class Tree {

        Tree() {
            val = 0;
            weight = 0;
            order = 0;
            left = null;
            right = null;
            parent = null;
        } // end of default constructor

        Tree(int value, int wei, int num, Tree l, Tree r, Tree p) {
            val = value;
            weight = wei;
            order = num;
            left = l;
            right = r;
            parent = p;
        } // end of constructor Tree(int value, int wei, int num, Tree l, Tree r, Tree p)

        int val;      // 8-bit character contained in tree node
        int weight;   // number of times val has occured in file so far
        int order;    // ordering system to track weights
        Tree left;
        Tree right;
        Tree parent;
    }

}
