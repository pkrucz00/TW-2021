package com.edu.agh.tw.cw2.refereeless;

import java.util.concurrent.Semaphore;

public class SteelFork extends Semaphore {

    public SteelFork(int permits) {
        super(permits);
    }

    public SteelFork() {
        this(1);
    }
}
