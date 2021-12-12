package myProductions;

import utils.MyMatrix;

public class CProduction extends AbstractProduction<MyMatrix> {
    private final int i;
    private final int j;
    private final int k;

    private final MyMatrix matrix;
    private final MyMatrix nMatrix;

    public CProduction(int i, int j, int k, MyMatrix matrix, MyMatrix nMatrix) {
        super(matrix);
        this.i = i;
        this.j = j;
        this.k = k;

        this.matrix = matrix;
        this.nMatrix = nMatrix;
    }

    @Override
    public MyMatrix apply(MyMatrix _p) {
        double newValue = matrix.get(k, j) - nMatrix.get(k, j);
        matrix.set(k, j, newValue);
        return getObj();
    }
}
