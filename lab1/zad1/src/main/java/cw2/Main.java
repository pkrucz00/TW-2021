package cw2;

import cw2.part_a.BinarySemaphore;
import cw2.part_a.CounterWithSemaphores;
import cw2.part_a.DecrementingThread;
import cw2.part_a.IncrementingThread;
import cw2.part_b.BinarySemaphoreOnIfs;
import cw2.part_c.GeneralSemaphore;

public class Main {

    public static void main(String[] args) {
        System.out.println("Part 1");
        testCounter(new CounterWithSemaphores(new BinarySemaphore()));
        System.out.println("\n");


        /*
           BinarySemaphoreOnIfs jest identyczny jak BinarySemaphore, jednak posiada ify zamiast pętli while

           Nie bedzie on działał prawidłowo, ponieważ gdy jeden wątek ma czekać, drugi może ponownie znaleźć się
           w strefie krytycznej nim pierwszy wyjdzie z ifa (np. z powodu wywłaszczenia).
           W ten sposób dwa wątki znajdą się w strefie krytycznej i może dojść do niespodziewanych wyników.

           Czasem jest jeszcze gorzej - trafiamy na deadlock, bo wątek A czeka na B, a B czeka na A.
           Nie udało mi się wymyślić dokładnego przeplotu, który by do takiego scenariusza prowadził,
           ale wygląda na to, że jest to możliwe, bo poniższy program od czasu do czasu się "zwiesza"

           Poniższy przykład zazwyczaj pokazuje, że w istocie mogą wystepować błędy z taką implementcją
         */
        System.out.println("Part 2");
        testCounter(new CounterWithSemaphores(new BinarySemaphoreOnIfs()));
        System.out.println("\n");

        /*
        Semafor binarny to szczególny przypadek semafora ogólonego,
         ponieważ możemy "zasymulować" jego działanie za pomocą
         semafora ogólnego zainicjowanego na 1
         Co prawda w sytuacji gdy np. (przy sem. ogóln. zainicj. na 0)
         dokonamy operacji V dwa razy, a następnie operacji P dwa razy,
         zachowanie obu tych semaforów będzie różne
          (binarny będzie czekał na drugim P, ogólny nie zatrzyma wątku w ogóle),
         jednak nie jest to raczej sytuacja, w której używamy semafora binarnego.

         Reasumując - semafor ogólnego możemy używać zamiast binarnego (chociaż jego
         zachowanie może być nieco różne), ale nie używamy binarnego zamiast ogólnego.
         Pokazano to na poniższym przykładzie.

         */
        System.out.println("Part 3");
        testCounter(new CounterWithSemaphores(new GeneralSemaphore(1)));

    }

    private static void testCounter(CounterWithSemaphores counter) {
        int numberOfIterations = 10_000;

        DecrementingThread decrementingThread = new DecrementingThread(counter, numberOfIterations);
        IncrementingThread incrementingThread = new IncrementingThread(counter, numberOfIterations);

        decrementingThread.start();
        incrementingThread.start();

        try {
            decrementingThread.join();
            incrementingThread.join();

            System.out.println("Number of iterations is " + numberOfIterations);
            System.out.println("Counter value is " + counter.getCounterValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
