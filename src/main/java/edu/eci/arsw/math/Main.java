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
        double intervalsSize = (double) digitsAmount/n; //1. Calcula cuantos dígitos debe calcular cada hilo
        int finalInterval = (int) intervalsSize; //2. Lo castea a entero para ver el caso en que la división no sea exacta
        if ((intervalsSize - finalInterval) > 0.5) { //3. Se hace esta resta para tratar de hacer una distribución más justa de cuantos dígitos calcula cada uno
            finalInterval++;
        }
        int begin = 0;
        int end = 0;
        for (int i = 0; i<n; i++) {
            begin = i * finalInterval;
            end = begin + finalInterval;
            if (i == n - 1) { //4. Si es el último hilo, mira cuantos digitos faltan por calcular para que no falte ninguno por calcular ni tampoco calcule de más
                end = digitsAmount;
            }
            PiThread thread = new PiThread(begin, end-begin, lock); //5. Para crear un hilo se le envía el begin que se va recalculando en cada iteración, la cantidad de digitos a calcular por hilo se calcula de la resta entre los índices de inicio y final, junto con el monitor para controlar la dormida y despertada de los hilos
            piThreads.add(thread);
            thread.start();
        }

        boolean running = true;
        while (running){
            try {
                Thread.sleep(5000); //6. Se duerme el hilo principal durante 5 segundos para que todos los otros hilos se ejecuten en esos 5 segundos (Cada hilo sabe cuando dormirse) y no imprima el mensaje que espera un enter para continuar
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (Thread.activeCount() == 2){
                running = false;
            } else {

                Scanner scannerEnter = new Scanner(System.in);
                System.out.println("Para seguir buscando los dígitos de pi, presione enter.");
                String letter = scannerEnter.nextLine();
                if (!letter.equals("")) { //7. Verifica que el usuario quiere seguir impriendo dígitos, esto lo hace revisando que el input que ingrese no sea diferente de enter
                    running = false;

                } else {
                    synchronized (lock) {
                        lock.notifyAll(); //8. Si el usuario ingresó enter entonces debe notificar a todos los hilos que estaban dormidos que se despierten para que sigan su ejecución y terminen de calcular los dígitos
                    }
                }
            }
        }
        ArrayList<Byte> resultArray= new ArrayList<Byte>();
        for (PiThread piThread : piThreads) {
            try {
                piThread.join();
                resultArray.addAll(piThread.getDigitsArray()); //9. Para concatenar los dígitos calculados por cada hilo, se recorre en orden el arreglo con los hilos y se agregan a un arreglo en el cual se almacena la respuesta final
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(bytesToHex(toArray(resultArray))); //10. Imprime la respuesta una vez ya todos hayan terminado de calcular los dígitos
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