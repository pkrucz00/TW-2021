package com.edu.agh.tw.cw2.refereeless;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Philosopher extends Thread{
    private final int number;
    private final SteelFork leftFork;
    private final SteelFork rightFork;
    private final int numberOfMeals;

    public Philosopher(int number, SteelFork leftFork, SteelFork rightFork, int numberOfMeals) {
        this.number = number;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.numberOfMeals = numberOfMeals;
    }

    @Override
    public void run() {
        for (int i = 0; i < numberOfMeals; i++) {
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
        leftFork.acquire();
//        printMessage("has lifted the left fork");
        rightFork.acquire();
//        printMessage("has lifted the right fork and started eating");
        long stop = System.currentTimeMillis();
        printTime(stop - start);
        sleep(500);
//        printMessage("has stopped eating");
        leftFork.release();
//        printMessage("has put down the left fork");
        rightFork.release();
//        printMessage("has put down the right fork");
    }

    private void printMessage(String message){
        System.out.println("Philosopher number " + number + " " + message);
    }

    private void printTime(long timeElapsed){
        System.out.println("Philosopher: #" + number + "# time: #" + timeElapsed + "# [ms]");
    }
}
