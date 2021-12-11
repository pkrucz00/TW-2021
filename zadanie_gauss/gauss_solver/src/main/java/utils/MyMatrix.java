package utils;

import java.io.*;
import java.util.Arrays;

/**
 * @author macwozni
 */
public class MyMatrix {
    int size;
    double[][] matrix;

    public MyMatrix(int size){
        this.size = size;

        matrix = new double[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                matrix[i][j] = 0.0;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public MyMatrix(String inputMatrixFilename) throws IOException{
        BufferedReader in = getBufferedReader(inputMatrixFilename);
        parseInput(in);
    }

    private void parseInput(BufferedReader in) throws IOException {
        // in first line there is an integer with matrix size
        String s = in.readLine();

        // parse integer
        size = Integer.parseInt(s);
        //create data structures for reading matrix and RHS vector
        matrix = new double[size][size+1];

        // read matrix line by line
        for (int i = 0; i < size; i++) {
            // read line
            s = in.readLine();
            // split line along space signs
            String[] sp = s.split(" ");
            // parse each string to double
            for (int j = 0; j < size; j++) {
                matrix[i][j] = Double.parseDouble(sp[j]);
            }
        }
        // read RHS vecor line
        s = in.readLine();
        // split line along space signs
        String[] sp = s.split(" ");
        // parse each string to double
        for (int j = 0; j < size; j++) {
            matrix[j][size] = Double.parseDouble(sp[j]);
        }
    }

    @Override
    public String toString() {
        String lhs_string = Arrays.stream(matrix)
                .map(arr -> Arrays.stream(arr).limit(size).toArray())
                .map(Arrays::toString)
                .map(str -> str
                        .replace(",", "")
                        .replace("[", "")
                        .replace("]", "\n"))
                .reduce("", String::concat);

        String rhs_string = Arrays.stream(matrix)
                .mapToDouble(arr -> arr[size])
                .mapToObj(Double::toString)
                .map(str -> str.concat(" "))
                .reduce("", String::concat);

        return size + "\n" + lhs_string + rhs_string;
    }

    private BufferedReader getBufferedReader(String inputMatrixFilename) throws FileNotFoundException {
        File fil = new File(inputMatrixFilename);
        FileReader inputFil = new FileReader(fil);
        return new BufferedReader(inputFil);
    }

    /**
     * @param a first variable for comparisson
     * @param b second variable for comparisson
     * @param epsilon machine precission for floating point
     * @return true if equals or within bounds of epsilon precission
     */
    static boolean compare(double a, double b, double epsilon) {
        double c = Math.abs(a - b);
        return c < epsilon;
    }
}
