import javax.xml.namespace.QName;

/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

public class LZWmod {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width
    private static boolean reset = false; 

    public static void compress() { 
        //if the boolean stated we are to reset, we write 'r' on top of file for expand method
        //if we are not reseting we write 'n' on top of file for expand to know what to do
        if(reset) BinaryStdOut.write('r');
        else BinaryStdOut.write('n');

        TSTmod<Integer> st = new TSTmod<Integer>();
        for (int i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
        int code = R+1;  // R is codeword for EOF

        //same as Lab7. 
        StringBuilder current = new StringBuilder();
        char c = BinaryStdIn.readChar();
        current.append(c);
        Integer codeword = st.get(current);
        //while still reading in the file
        while (!BinaryStdIn.isEmpty()) {
            codeword = st.get(current);
            c = BinaryStdIn.readChar();
            current.append(c);

            //if there is not a current codeword in the codebook
            if(!st.contains(current)){
                BinaryStdOut.write(codeword, W);
                // if codeword is less than the max # of codewords allowed 
                //insert the codeword
                if (code < L)    
                    st.put(current, code++);
                //if code is equal to the max # of codewords allowed
                else if(code == L){
                    //if the codeword width is at the max and the file said to reset
                    if(W >= 16 && reset){
                        //reset the codebook to original mins
                        L=512; W=9; code = R+1;
                        st = new TSTmod<Integer>();
                        //refill codebook
                        for (int i = 0; i < R; i++)
                            st.put(new StringBuilder("" + (char) i), i); 
                    }
                    //if codeword is less than the max width, increment the width and double the codebook max
                    //then insert
                    else if(W<16) { 
                        W += 1; 
                        L = L * 2;
                        st.put(current, code++);
                    } 
                }
             current = new StringBuilder();
              //append the last char onto it
             current.append(c);
            }
        }
        //write the codeword of whatever remains
        //in current
        BinaryStdOut.write(st.get(current), W);
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
        String[] st = new String[65536];
        int i; // next available codeword value
        char reset = BinaryStdIn.readChar();

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];
        

        while (true) {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            //if codeword is less than the max codewords allowed 
            //insert
            if (i < L)  
                st[i++] = val + s.charAt(0); 

            //if codeword = max # of codewords allowed 
            if(i == L){
                //if the codeword width is at the max and the compression used reset 
                if(W>=16 && reset == 'r'){
                    //reset codebook
                    st = new String[65536];
                    L = 512; W = 9; 
                    //refill codebook
                    for (i = 0; i < R; i++)
                        st[i] = "" + (char) i;
                    st[i++] = "";
                    //write and insert
                    BinaryStdOut.write(s);
                    codeword = BinaryStdIn.readInt(W);
                    s = st[codeword];
                }
                //if codeword is less than the max width, increment the width and double the codebook max
                else if(W<16) { W ++; L = L * 2; }
            }
            val = s;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        //compression
        if(args[0].equals("-")){ 
            //file said to use reset so set boolean to true for compression method to know
            if(args[1].equals("r")) reset = true; 
            compress(); 
        }
        //expansion. it will know wether to reset or not based on compression method
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }
}