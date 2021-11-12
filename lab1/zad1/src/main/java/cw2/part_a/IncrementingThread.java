package cw2.part_a;

public class IncrementingThread extends Thread{

    private final CounterWithSemaphores counter;
    private final int numberOfIterations;

    public IncrementingThread(CounterWithSemaphores counter, int numberOfIterations){
        this.counter = counter;
        this.numberOfIterations = numberOfIterations;
    }

    @Override
    public void run() {
        for (int i = 0; i < numberOfIterations; i++){
            counter.safelyIncrement();
        }
    }
}
