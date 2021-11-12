package default_package;

import default_package.synchronized_version.DecrementingSynchronizedThread;
import default_package.synchronized_version.IncrementingSynchronizedThread;
import default_package.unsynchronized.DecrementingThread;
import default_package.unsynchronized.IncrementingThread;

public class IncrementationTest {

    public static void main(String[] args){
        Counter counter = new Counter();
        int numberOfIterations = 1_000_000;

        DecrementingThread decrementingThread = new DecrementingThread(counter, numberOfIterations);
        IncrementingThread incrementingThread = new IncrementingThread(counter, numberOfIterations);

        decrementingThread.start();
        incrementingThread.start();

        try{
            decrementingThread.join();
            incrementingThread.join();

            System.out.println("Number of iterations is " + numberOfIterations);
            System.out.println("Counter value is " + counter.getCounterValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Counter newCounter = new Counter();

        DecrementingSynchronizedThread decrementingSynchronizedThread =
                new DecrementingSynchronizedThread(newCounter, numberOfIterations);
        IncrementingSynchronizedThread incrementingSynchronizedThread =
                new IncrementingSynchronizedThread(newCounter, numberOfIterations);

        decrementingSynchronizedThread.start();
        incrementingSynchronizedThread.start();

        try{
            decrementingSynchronizedThread.join();
            incrementingSynchronizedThread.join();

            System.out.println("Number of synchronized iterations is " + numberOfIterations);
            System.out.println("Counter value is " + newCounter.getCounterValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        int numberOfThreads = 100;
//        NastyThread nastyThread = new NastyThread();
//        for (int i = 0 ; i < numberOfThreads; i++){
//            nastyThread.start();
//        }
    }
}
