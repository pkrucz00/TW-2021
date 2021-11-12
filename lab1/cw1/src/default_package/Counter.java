package default_package;

public class Counter {
    private Integer counterValue;

    public Counter(){
        this.counterValue = 0;
    }

    public synchronized void increment() {
        counterValue++;
    }

    public synchronized void decrement() {
        counterValue--;
    }

    public Integer getCounterValue() {
        return counterValue;
    }
}
