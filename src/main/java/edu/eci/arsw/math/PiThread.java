package edu.eci.arsw.math;

import java.util.ArrayList;
import java.util.Collections;

public class PiThread extends Thread{
    private int start;
    private int count;
    private Boolean lock;

    private ArrayList<Byte> digits;

    private boolean running;
    public PiThread (int start, int count, boolean lock) {
        this.start = start;
        this.count = count;
        this.lock = lock;
        digits = new ArrayList<Byte>();
        running = true;
    }

    public void run () {
        long startTime = System.currentTimeMillis();
        long current;
        for (int i = start;i<count; i++) {
            current = System.currentTimeMillis();
            if (current - startTime >= 5000) {
                System.out.println(Main.bytesToHex(getDigits()));
                synchronized (lock) {
                    try {
                        lock.wait();
                        startTime = System.currentTimeMillis();
                    } catch (InterruptedException e) {

                    }
                }
            } else {
                digits.add(PiDigits.getDigits(i, count)[0]);
            }
        }
        //while (running) {
        //}
    }

    public byte[] getDigits () {
        byte[] results = new byte[digits.size()];
        for (int i=0; i<digits.size();i++) {
            results[i] = digits.get(i);
        }
        return results;
    }

    public ArrayList<Byte> getDigitsArray () {
        return digits;
    }

    public void setRunning (boolean newRunning) {
        running = newRunning;
    }
}
