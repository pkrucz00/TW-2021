package default_package.unsynchronized;

import default_package.Counter;

public class DecrementingThread extends Thread{

    private final Counter counter;
    private final int numberOfIterations;

    public DecrementingThread(Counter counter, int numberOfIterations){
        this.counter = counter;
        this.numberOfIterations = numberOfIterations;
    }

    @Override
    public void run() {
        for (int i = 0; i < numberOfIterations; i++){
            counter.decrement();
        }
    }
}
