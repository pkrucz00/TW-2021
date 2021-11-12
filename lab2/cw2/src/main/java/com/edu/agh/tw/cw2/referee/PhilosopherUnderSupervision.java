package com.edu.agh.tw.cw2.referee;

import com.edu.agh.tw.cw2.refereeless.SteelFork;

public class PhilosopherUnderSupervision extends Thread{
    private int number;
    private SteelFork leftFork;
    private SteelFork rightFork;
    private Referee referee;
    private int numberOfMeals;

    public PhilosopherUnderSupervision(int number, SteelFork leftFork, SteelFork rightFork, Referee referee, int numberOfMeals){
        this.number = number;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.referee = referee;
        this.numberOfMeals = numberOfMeals;
    }


    @Override
    public void run() {
        for (int i = 0 ; i < numberOfMeals; i++){
            try {
                think();
                eat(leftFork, rightFork);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void think() throws InterruptedException {
//        printMessage("has started thinking");
        sleep(500);
//        printMessage("has stopped thinking");
    }

    public void eat(SteelFork leftFork, SteelFork rightFork) throws InterruptedException {
        long start = System.currentTimeMillis();
        referee.acquire();
//        printMessage("can eat because referee told him to");
        leftFork.acquire();
//        printMessage("has lifted the left fork");
        rightFork.acquire();
        long stop = System.currentTimeMillis();
        printTime(stop - start);
//        printMessage("has lifted the right fork and started eating");
        sleep(500);
//        printMessage("has stopped eating");
        leftFork.release();
//        printMessage("has put down the left fork");
        rightFork.release();
//        printMessage("has put down the right fork");
        referee.release();
    }

    private void printMessage(String message){
        System.out.println("Philosopher number " + number + " " + message);
    }

    private void printTime(long timeElapsed){
        System.out.println("Philosopher: #" + number + "# time: #" + timeElapsed + "# [ms]");
    }
}
