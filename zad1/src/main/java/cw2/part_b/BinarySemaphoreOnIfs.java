package cw2.part_b;

import cw2.part_a.MySemaphore;

public class BinarySemaphoreOnIfs implements MySemaphore {

    private boolean nowWaitAWhile;

    public synchronized void P(){
        if (nowWaitAWhile){
            waitAndCatchIfNecessary();
        }
        nowWaitAWhile = true;
        notifyAll();
    }

    public synchronized void V(){
        if (!nowWaitAWhile){
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
