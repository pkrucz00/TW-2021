package com.edu.agh.tw.cw2.referee;

import java.util.concurrent.Semaphore;

public class Referee extends Semaphore {

    public Referee(int permits){
        super(permits);
    }

    public Referee(){
        super(4);
    }
}
