package cw2.part_a;

public class DecrementingThread extends Thread{

    private final CounterWithSemaphores counter;
    private final int numberOfIterations;

    public DecrementingThread(CounterWithSemaphores counter, int numberOfIterations){
        this.counter = counter;
        this.numberOfIterations = numberOfIterations;
    }

    @Override
    public void run() {
        for (int i = 0; i < numberOfIterations; i++){
            counter.safelyDecrement();
        }
    }
}
