package myProductions;


import parallelism.MyLock;

public interface IProduction<P> {

     P apply(P _p);

     void join() throws InterruptedException;

     void start();

     void injectRefs(MyLock _lock);

     P getObj();
}
