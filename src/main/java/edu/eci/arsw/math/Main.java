/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author hcadavid
 */
public class Main {

    public static void main(String a[]) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese la cantidad de dígitos de pi que quiere conocer");
        int digitsAmount = scanner.nextInt();
        System.out.println("Ingrese la cantidad de hilos que quiere crear");
        int n = scanner.nextInt();

        Boolean lock = true;
        ArrayList<PiThread> piThreads = new ArrayList<PiThread>();
        double intervalsSize = (double) digitsAmount/n;
        int finalInterval = (int) intervalsSize;
        if ((intervalsSize - finalInterval / n) > 0.5) {
            finalInterval++;
        }
        int begin = 0;
        int end = 0;
        for (int i = 0; i<n; i++) {
            begin = i * finalInterval;
            end = begin + finalInterval;
            if (i == n - 1) {
                end = digitsAmount;
            }
            PiThread thread = new PiThread(begin, end, lock);
            piThreads.add(thread);
            thread.start();
        }

        boolean running = true;
        while (running){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (Thread.activeCount() == 2){
                running = false;
            } else {

                Scanner scannerEnter = new Scanner(System.in);
                System.out.println("Para seguir buscando los dígitos de pi, presione enter.");
                String letter = scannerEnter.nextLine();
                if (!letter.equals("")) {
                    running = false;

                } else {
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            }
        }

        byte[] result = new byte[digitsAmount];
        ArrayList<Byte> resultArray= new ArrayList<Byte>();
        for (PiThread piThread : piThreads) {
            try {
                piThread.join();
                resultArray.addAll(piThread.getDigitsArray());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(bytesToHex(toArray(resultArray)));
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<hexChars.length;i=i+2){
            //sb.append(hexChars[i]);
            sb.append(hexChars[i+1]);            
        }
        return sb.toString();
    }

    public static byte[] toArray(ArrayList<Byte> resultArray) {
        byte[] results = new byte[resultArray.size()];
        for (int i=0; i<resultArray.size();i++) {
            results[i] = resultArray.get(i);
        }
        return results;
    }

}

