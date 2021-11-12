package com.edu.agh.tw.cw2.referee;

import com.edu.agh.tw.cw2.refereeless.SteelFork;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FivePhilosophersWithReferee {
    public static void main(String[] args){
        int numberOfMeals = 20;
        int numberOfPhilosophers = 10;
        Referee referee = new Referee(numberOfPhilosophers - 1);
        List<SteelFork> forks = Stream.generate(SteelFork::new)
                .limit(numberOfPhilosophers)
                .collect(Collectors.toList());


        List<PhilosopherUnderSupervision> philosophers = new ArrayList<>();
        for (int i = 0; i < numberOfPhilosophers; i++){
            philosophers.add(
                    new PhilosopherUnderSupervision(
                            i,
                            forks.get(i),
                            forks.get( (i+1) % numberOfPhilosophers),
                            referee,
                            numberOfMeals));
        }

        philosophers.forEach(Thread::start);
    }

}
