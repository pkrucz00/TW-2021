package myProductions;

import utils.MyMatrix;

public class BProduction extends AbstractProduction<MyMatrix> {

    private final int i;
    private final int j;
    private final int k;

    private final MyMatrix matrix;
    private final MyMatrix mMatrix;
    private final MyMatrix nMatrix;


    public BProduction(int i, int j, int k, MyMatrix matrix, MyMatrix mMatrix, MyMatrix nMatrix) {
        super(nMatrix);

        this.i = i;
        this.j = j;
        this.k = k;

        this.matrix = matrix;
        this.mMatrix = mMatrix;
        this.nMatrix = nMatrix;
    }

    @Override
    public MyMatrix apply(MyMatrix _p) {
        double newValue = matrix.get(i, j)*mMatrix.get(k, i);
        nMatrix.set(k, j, newValue);
        return getObj();
    }
}
