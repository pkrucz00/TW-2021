package com.edu.agh.tw.cw2.refereeless;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FivePhilosophersWithoutReferee {

    public static void main(String[] args) {
        int numberOfMeals = 20;
        int numberOfPhilosophers = 10;
        List<SteelFork> forks = Stream.generate(SteelFork::new)
                .limit(numberOfPhilosophers)
                .collect(Collectors.toList());

        List<Philosopher> philosophers = new ArrayList<>();
        for (int i = 0; i < numberOfPhilosophers; i++) {
            philosophers.add(
                    new Philosopher(i,
                            forks.get(i),
                            forks.get((i + 1) % numberOfPhilosophers),
                            numberOfMeals)
            );
        }

        philosophers.forEach(Thread::start);
    }
}
