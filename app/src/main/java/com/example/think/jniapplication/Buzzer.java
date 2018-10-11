package com.example.think.jniapplication;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class Buzzer {

    private final int duration = 1; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final double freqOfTone = 2700; // hz

    private final byte buzzer[] = new byte[2 * numSamples];
    private AudioTrack audioTrack = null;
    void genTone(){

        long start = System.currentTimeMillis();
        // produce freqOfTone sound data
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            buzzer[idx++] = (byte) (val & 0x00ff);
            buzzer[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
        Log.d("PlayBuzzer", "END "+(System.currentTimeMillis() - start));
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, buzzer.length,
                AudioTrack.MODE_STATIC);

        audioTrack.setVolume((float)1.0);
    }

    Buzzer(){
        genTone();
    }

    /* 50 <= millis <= 1000 */
    void playSound(int millis){
        if (millis > 1000)
            millis = 1000;
        if (millis < 50)
            millis = 50;

        if (audioTrack != null && audioTrack.getState() == audioTrack.STATE_INITIALIZED) {
            audioTrack.stop();
        }
        int length = buzzer.length*millis/1000;
        try {
            audioTrack.write(buzzer, 0, length);
            long start = System.currentTimeMillis();
            audioTrack.play();

            /* wait for end of audio */
            while(audioTrack.getPlaybackHeadPosition() < length/2){
            }
            Log.d("PlayBuzzer", "END "+(System.currentTimeMillis() - start));
        } catch(Exception e){
        }
    }

}
