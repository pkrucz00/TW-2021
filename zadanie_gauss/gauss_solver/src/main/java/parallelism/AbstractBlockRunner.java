package parallelism;

import myProductions.IProduction;
import utils.MyMatrix;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractBlockRunner<T extends IProduction<MyMatrix>> implements BlockRunner<T> {

    private final AbstractQueue<T> list = new ConcurrentLinkedQueue<T>();

    //starts all threads
    @Override
    public void startAll() {
        Iterator<T> iter = list.iterator();
        while (iter.hasNext()) {
            T p = iter.next();
            runOne(p);
        }
        wakeAll();
        iter = list.iterator();
        while (iter.hasNext()) {
            try {
                T p = iter.next();
                p.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(AbstractBlockRunner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        list.clear();
    }

    @Override
    public List<T> getProductionList() {
        return new ArrayList<>(list);
    }

    //adds a thread to poll
    @Override
    public void addThread(T _pThread) {
        list.add(_pThread);
    }

    //starts one thread
    abstract void runOne(T _pOne);

    //wakes all threads
    abstract void wakeAll();
}
