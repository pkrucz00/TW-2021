import myProductions.AProduction;
import myProductions.BProduction;
import myProductions.CProduction;
import myProductions.IProduction;
import parallelism.BlockRunner;
import utils.MyMatrix;

import java.io.*;

public class Executor extends Thread {

    private final BlockRunner<IProduction<MyMatrix>> runner;
    private final MyMatrix matrix;
    private final String outputFilename;

    private final MyMatrix mMatrix;
    private final MyMatrix nMatrix;
    private final int n;

    public Executor(BlockRunner<IProduction<MyMatrix>> _runner, String inputMatrixFilename, String outputFilename) throws IOException {
        this.runner = _runner;
        this.matrix = new MyMatrix(inputMatrixFilename);
        this.outputFilename = outputFilename;

        n = matrix.getSize();
        mMatrix = new MyMatrix(n, n);
        nMatrix = new MyMatrix(n, n+1);
    }

    @Override
    public void run() {
        System.out.println("Input matrix:\n" + matrix + "\n");
        for (int i = 0; i < n; i++){
            processAProductions(i);
            processBProductions(i);
            processCProductions(i);

            printProgress(i+1);
        }
        matrix.solveTriangularMatrix();
        System.out.println("Matrix after solving:\n" + matrix);
        trySavingFile();
    }

    private void processAProductions(int i) {
        for (int k = i+1; k < n; k++){
            runner.addThread(new AProduction(i, k, matrix, mMatrix));
        }
        runner.startAll();
    }

    private void processBProductions(int i) {
        for (int j = i; j < n+1; j++){
            for (int k = i+1; k < n; k++){
                runner.addThread(new BProduction(i, j, k, matrix, mMatrix, nMatrix));
            }
        }
        runner.startAll();
    }

    private void processCProductions(int i) {
        for (int j = i; j < n+1; j++){
            for (int k = i+1; k < n; k++){
                runner.addThread(new CProduction(i, j, k, matrix, nMatrix));
            }
        }
        runner.startAll();
    }

    private void printProgress(int iteration) {
        System.out.println("State of matrix after " + iteration + " iteration");
        System.out.println(matrix + "\n");

    }

    private void trySavingFile() {
        try {
            saveToFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile() throws FileNotFoundException {
        File file = new File(outputFilename);
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        System.setOut(ps);

        System.out.println(matrix);
    }
}
