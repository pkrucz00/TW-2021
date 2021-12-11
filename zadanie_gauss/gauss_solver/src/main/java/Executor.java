import parallelism.BlockRunner;
import utils.MyMatrix;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Executor extends Thread {

    private final BlockRunner runner;
    private final MyMatrix matrix;
    private final String outputFilename;

    private final MyMatrix mMatrix;
    private final MyMatrix nMatrix;
    private final int n;

    public Executor(BlockRunner _runner, String inputMatrixFilename, String outputFilename) throws IOException {
        this.runner = _runner;
        this.matrix = new MyMatrix(inputMatrixFilename);
        this.outputFilename = outputFilename;

        n = matrix.getSize();
        mMatrix = new MyMatrix(n);
        nMatrix = new MyMatrix(n);
    }

    @Override
    public void run() {
        for (int i = 0; i < n; i++){
            processAProductions(i);
            processBProductions(i);
            processCProductions(i);

            printProgress(i);
        }
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
        System.out.println(matrix);
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
//        SquareDrawer drawer = new SquareDrawer(matrix, squareSideLength);

//        System.out.println("Initial production");
//        P0 p0 = new P0(new Square());
//        this.runner.addThread(p0);
//        processProductions(drawer);
//
//        for (int coordSum = 1; coordSum < 2*squareSideLength; coordSum++){  //coordSum pokazuje, którą przekątną obecnie tworzymy
//            System.out.println("\nIteration number " + coordSum);
//            addPSProduction(coordSum);
//            addPEProductions(coordSum);
//            addPMProductions(coordSum-1);  // łączymy dolne kwadraty na wcześniejszej przekątnej z ich górnymi kwadratami
//
//            processProductions(drawer);
//        }
//
//        //done
//        System.out.println("\ndone");
//
//    }

//    private void processProductions(SquareDrawer drawer) {
//        List<ICoordProduction<Square>> allProductions = runner.getProductionList();
//        if (allProductions.size() > 0) {
//            runner.startAll();
//
//            updateMatrix(allProductions);
//            drawer.draw();
//        }
//    }
//
//
//    private void addPSProduction(int coordSum) {
//        addPSProduction(0, coordSum);
//    }
//
//    private void addPSProduction(int x, int y){
//        if (y < squareSideLength){
//            Square originSquare = matrix.get(x, y-1);
//            runner.addThread(new PS(originSquare, x, y));
//        }
//    }
//
//    private void addPEProductions(int coordSum) {
//        getCoordsWithGivenSum(coordSum).forEach(this::addPEProduction);
//    }
//
//    private void addPEProduction(Coord coord) {
//        addPEProduction(coord.getX(), coord.getY());
//    }
//
//    private void addPEProduction(int x, int y){
//        if (peProductionConstraintsAreSatisfied(x, y)){
//            Square leftOriginSquare = matrix.get(x - 1, y);
//            runner.addThread(new PE(leftOriginSquare, x, y));
//        }
//    }
//
//    private boolean peProductionConstraintsAreSatisfied(int x, int y) {
//        return x > 0;
//    }
//
//    private void addPMProductions(int coordSum) {
//        getCoordsWithGivenSum(coordSum).forEach(this::addPMProduction);
//    }
//
//    private void addPMProduction(Coord coord) {
//        addPMProduction(coord.getX(), coord.getY());
//    }
//
//    private void addPMProduction(int x, int y){
//        if (pmProductionConstraintsAreSatisfied(x, y)){
//            Square rightLowerSquare = matrix.get(x, y);
//            runner.addThread(new PM(rightLowerSquare, x, y));
//        }
//    }
//
//    private boolean pmProductionConstraintsAreSatisfied(int x, int y) {
//        return x > 0 && y > 0;
//    }
//
//    private void updateMatrix(List<ICoordProduction<Square>> allProductions) {
//        allProductions.forEach(this::addSquareToMatrix);
//    }
//
//    private void addSquareToMatrix(ICoordProduction<Square> coordProduction){
//        matrix.put(coordProduction.getCoord(), coordProduction.getObj());
//    }
//
//    private List<Coord> getCoordsWithGivenSum(int coordSum){
//        return Stream.iterate(0, x -> x <= coordSum, x -> x + 1)
//                .map(x -> new Coord(x, coordSum - x))
//                .filter(coord ->
//                        coord.getX() < squareSideLength && coord.getY() < squareSideLength)
//                .collect(Collectors.toList());
//    }
}
