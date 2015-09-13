package me.shreyasr.cosmicwave;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.javasound.JSMinim;
import hydra.ajm.FSKModem;
import processing.core.PApplet;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CosmicWave extends PApplet {

    ModemQueue modemOutputQueue;
    FSKModem modem;
    AudioInput in;

    @Override public void setup() {
        try {
            Minim minim = new Minim(new JSMinim(new Object()));
            float highFreq = 9000;
            float lowFreq = 3500;
            float bitRate = 400;
            modem = new FSKModem(minim, highFreq, lowFreq, bitRate, highFreq,
                    lowFreq, bitRate);
            in = modem.ain;
            modemOutputQueue = new ModemQueue(modem);
            modemOutputQueue.add("Welcome to zombocom!!!!!");
            new Thread(modemOutputQueue).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    @Override public void draw() {
        if (modem.available() >= 8) {
            byte[] input = modem.readBytes();
            if (Arrays.equals(input, Constants.CLOSING)) {
                System.out.println("\nDONE");
                System.out.println(new String(buffer.toByteArray(), StandardCharsets.US_ASCII));
                buffer.reset();
            } else {
                for (byte b : input) {
                    if (b<32 || b>127) {
                        b = '-';
                    }
                    buffer.write(b);
                }
            }
            System.out.println(new String(buffer.toByteArray(), StandardCharsets.US_ASCII));
        }
    }

    class ModemQueue implements Runnable {

        Queue<byte[]> chunks = new LinkedList<>();
        private FSKModem modem;

        public ModemQueue(FSKModem modem) {
            this.modem = modem;
        }

        public void add(String message) {
            byte[] bytes = message.getBytes(StandardCharsets.US_ASCII);
            for (int i=0;i<bytes.length/8;i++) {
                add(Arrays.copyOfRange(bytes, i * 8, i * 8 + 8));
            }
            add(Constants.CLOSING);
        }

        private void add(byte[] chunk) {
            chunks.offer(chunk);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                while (true) {
                    while (chunks.peek() == null) ;
                    byte[] arr = chunks.poll();
                    modem.write(arr);
                    Thread.sleep(arr.length * 8 / 400 * 1000 + 500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
