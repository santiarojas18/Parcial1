package edu.eci.arsw.math;

import java.util.ArrayList;
import java.util.Collections;

public class PiThread extends Thread{
    private int start;
    private int count;
    private Boolean lock;

    private ArrayList<Byte> digits;

    private boolean running;

    private static int DigitsPerSum = 8;
    private static double Epsilon = 1e-17;

    private byte[] digitsArray;
    public PiThread (int start, int count, boolean lock) {
        this.start = start;
        this.count = count;
        this.lock = lock;
        digits = new ArrayList<Byte>();
        running = true;
        digitsArray = new byte[count];
        System.out.println("ini: "+start+" Count: "+count);
    }

    public void run () {
        if (start < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        if (count < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        double sum = 0;

        long startTime = System.currentTimeMillis(); //1. Obtiene el tiempo en el que el hilo se empieza a ejecutar
        long current;

        for (int i = 0;i<count; i++) { //2. Ciclo para calcular la cantidad de dígitos solicitada (count) desde la posición requerida (start)
            current = System.currentTimeMillis(); //3. Cada vez que se itera se debe pedir el tiempo actual para hacer la resta entre el tiempo de inicio y el actual, con esto, se sabe cuantos segundos se ha transcurrido
            if (current - startTime >= 5000) { //4. Verifica si el tiempo transcurrido es de 5 segundos
                System.out.println(Main.bytesToHex(digitsArray)); //5. Si ya pasó 5 segundos muestra los dígitos que ha calculado antes de pasar a dormirse
                synchronized (lock) {
                    try {
                        lock.wait(); //6. El hilo se duerme a través del monitor compartido, y solo será despertado por el hilo main el cual espera hasta que el usuario ingrese enter
                        startTime = System.currentTimeMillis(); //7. En caso de que el hilo main lo despierte, aquí es donde continua su ejecución, es por esto que se vuelve a preguntar el tiempo de inicio, porque es cuando se reanuda el hilo y se debe volver a contar 5 segundos
                    } catch (InterruptedException e) {
                    }
                }
            }
            if (i % DigitsPerSum == 0) { //8. Hace el cálculo de un dígito
                sum = 4 * PiDigits.sum(1, start)
                        - 2 * PiDigits.sum(4, start)
                        - PiDigits.sum(5, start)
                        - PiDigits.sum(6, start);

                start += DigitsPerSum;
            }

            sum = 16 * (sum - Math.floor(sum));
            digitsArray[i] = (byte) sum; //9. Agrega el dígito calculado a la respuesta
            digits.add((byte) sum);
        }
    }



    public byte[] getDigits () {
        return digitsArray;
    }

    public ArrayList<Byte> getDigitsArray () {
        return digits;
    }

    public void setRunning (boolean newRunning) {
        running = newRunning;
    }

}
