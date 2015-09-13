package me.shreyasr.cosmicwave;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.javasound.JSMinim;
import hydra.ajm.FSKModem;
import processing.core.PApplet;

import java.io.ByteArrayOutputStream;

public class CosmicWave extends PApplet {

    FSKModem modem;
    AudioInput in;

    @Override public void setup() {
        try {
            Minim minim = new Minim(new JSMinim(new Object()));
            float highFreq = 9000;
            float lowFreq = 2500;
            float bitRate = 400;
            modem = new FSKModem(minim, highFreq, lowFreq, bitRate, highFreq,
                    lowFreq, bitRate);
            in = modem.ain;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int count = 1;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    @Override public void draw() {
        try {
            byte[] input = modem.readBytes();
            if (input != null) {
                for (byte b : input) {
                    if (b == '!' && buffer.size() > 0) {
                        System.out.println("\nDONE");
                        System.out.println(new String(buffer.toByteArray(), "ASCII"));
                        buffer.reset();
                    } else {
                        if (!Character.isLetterOrDigit(b)) {
                            b = ' ';
                        }
                        buffer.write(b);
                        System.out.println(new String(buffer.toByteArray(), "ASCII") + " | ");
                    }
                }
            }
            if (count++%100==0) {
                modem.write("Welcome ");
                modem.write(new byte[]{ 0,0,0,0 });
                modem.write("to zombo");
                modem.write(new byte[]{ 0,0,0,0 });
                modem.write("com!!!!!");
//                modem.write("Welcome to Zombocom!");
//                modem.write("Welcome to Zombocom!");
//                modem.write("Welcome to Zombocom!");
//                modem.write("Welcome to Zombocom!");
//                modem.write(Constants.TEST_DATA);
//                modem.write(Constants.TEST_DATA);
//                modem.write(Constants.TEST_DATA);
//                modem.write(Constants.TEST_DATA);
            }

            background(0);
            stroke(255);

            // draw the waveforms so we can see what we are monitoring
            for(int i = 0; i < in.bufferSize() - 1; i++) {
                line(i, 50 + in.left.get(i)*50, i+1, 50 + in.left.get(i+1)*50);
                line(i, 150 + in.right.get(i)*50, i+1, 150 + in.right.get(i+1)*50);
            }

            String monitoringState = in.isMonitoring() ? "enabled" : "disabled";
            text("Input monitoring is currently " + monitoringState + ".", 5, 15);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
