package parallelism;

import myProductions.IProduction;

import java.util.List;

public interface BlockRunner<T extends IProduction> {

    //starts all threads
    void startAll();

    //adds a thread to poll
    void addThread(T pThread);

    List<T> getProductionList();
}
