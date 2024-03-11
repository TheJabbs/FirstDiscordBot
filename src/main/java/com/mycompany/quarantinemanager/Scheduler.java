/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quarantinemanager;

/**
 *
 * @author johnn
 */
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private ScheduledExecutorService executor;

    public Scheduler() {
        executor = Executors.newScheduledThreadPool(1);
    }

    public void scheduleTask(Runnable task, long initialDelay, long interval, TimeUnit unit) {
        executor.scheduleAtFixedRate(task, initialDelay, interval, unit);
    }

    public void stop() {
        executor.shutdown();
    }
}

