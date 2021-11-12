package default_package.synchronized_version;

import default_package.Counter;

public class DecrementingSynchronizedThread extends Thread{

    private final Counter counter;
    private final int numberOfIterations;

    public DecrementingSynchronizedThread(Counter counter, int numberOfIterations){
        this.counter = counter;
        this.numberOfIterations = numberOfIterations;
    }

    @Override
    public synchronized void run() {
        for (int i = 0; i < numberOfIterations; i++){
            counter.decrement();
        }
    }
}
