package cw2.part_a;

public class BinarySemaphore implements MySemaphore{

    private boolean nowWaitAWhile;

    public synchronized void P(){
        while (nowWaitAWhile){
            waitAndCatchIfNecessary();
        }
        nowWaitAWhile = true;
        notifyAll();
    }

    public synchronized void V(){
        while (!nowWaitAWhile){
            waitAndCatchIfNecessary();
        }
        nowWaitAWhile = false;
        notifyAll();
    }

    private void waitAndCatchIfNecessary() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
