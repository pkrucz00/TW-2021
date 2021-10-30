package cw2.part_a;

public class CounterWithSemaphores {
    private Integer counterValue;
    private final MySemaphore semaphore;

    public CounterWithSemaphores(MySemaphore semaphore){
        this.counterValue = 0;
        this.semaphore = semaphore;
    }

    public void safelyIncrement() {
        semaphore.P();
        counterValue++;
        semaphore.V();
    }

    public void safelyDecrement() {
        semaphore.P();
        counterValue--;
        semaphore.V();
    }

    public Integer getCounterValue() {
        return counterValue;
    }
}
