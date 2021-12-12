package myProductions;

import utils.MyMatrix;

public class AProduction extends AbstractProduction<MyMatrix> {
    private final int i;
    private final int k;

    private final MyMatrix matrix;
    private final MyMatrix mMatrix;

    public AProduction(int i, int k, MyMatrix matrix, MyMatrix mMatrix) {
        super(mMatrix);

        this.i = i;
        this.k = k;

        this.matrix = matrix;
        this.mMatrix = mMatrix;
    }


    @Override
    public MyMatrix apply(MyMatrix _p) {
        double newValue = matrix.get(k,i)/matrix.get(i,i);
        mMatrix.set(k, i, newValue);
        return getObj();
    }
}
