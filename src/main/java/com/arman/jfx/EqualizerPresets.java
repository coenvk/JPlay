package com.arman.jfx;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.MediaPlayer;

public class EqualizerPresets {
	
	public static final String[] NAMES = new String[]{"Normal", "Classic", "Club", "Dance", 
		"Fullbass", "Fullbasstreble", "Fulltreble", "Headphones", "Largehall", "Live", 
		"Party", "Pop", "Reggae", "Rock", "Ska", "Soft", "Softrock", "Techno"};
	public static final Float[][] PRESETS = 
	new Float[][]{{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f},
		{-1.11022e-15f, -1.11022e-15f, -1.11022e-15f, -1.11022e-15f, -1.11022e-15f, -1.11022e-15f,
			-7.2f, -7.2f, -7.2f, -9.6f}, {-1.11022e-15f, -1.11022e-15f, 8.0f, 5.6f, 5.6f, 5.6f, 
			3.2f, -1.11022e-15f, -1.11022e-15f, -1.11022e-15f}, {9.6f, 7.2f, 2.4f, -1.11022e-15f, 
			-1.11022e-15f, -5.6f, -7.2f, -7.2f, -1.11022e-15f, -1.11022e-15f}, {-8.0f, 9.6f, 9.6f, 
			5.6f, 1.6f, -4.0f, -8.0f, -10.4f, -11.2f, -11.2f}, {7.2f, 5.6f, -1.11022e-15f, 
			-7.2f, -4.8f, 1.6f, 8.0f, 11.2f, 12.0f, 12.0f}, {-9.6f, -9.6f, -9.6f, -4.0f, 2.4f, 
			11.2f, 16.0f, 16.0f, 16.0f, 16.8f}, {4.8f, 11.2f, 5.6f, -3.2f, -2.4f, 1.6f, 4.8f, 9.6f, 
			12.8f, 14.4f}, {10.4f, 10.4f, 5.6f, 5.6f, -1.11022e-15f, -4.8f, -4.8f, -4.8f, 
			-1.11022e-15f, -1.11022e-15f}, {-4.8f, -1.11022e-15f, 4.0f, 5.6f, 5.6f, 5.6f, 4.0f, 
			2.4f, 2.4f, 2.4f}, {7.2f, 7.2f, -1.11022e-15f, -1.11022e-15f, -1.11022e-15f, 
			-1.11022e-15f, -1.11022e-15f, -1.11022e-15f, 7.2f, 7.2f}, {-1.6f, 4.8f, 7.2f, 8.0f, 
			5.6f, -1.11022e-15f, -2.4f, -2.4f, -1.6f, -1.6f}, {-1.11022e-15f, -1.11022e-15f, 
			-1.11022e-15f, -5.6f, -1.11022e-15f, 6.4f, 6.4f, -1.11022e-15f, -1.11022e-15f, 
			-1.11022e-15f}, {8.0f, 4.8f, -5.6f, -8.0f, -3.2f, 4.0f, 8.8f, 11.2f, 11.2f, 11.2f}, 
		{-2.4f, -4.8f, -4.0f, -1.11022e-15f, 4.0f, 5.6f, 8.8f, 9.6f, 11.2f, 9.6f}, 
		{4.8f, 1.6f, -1.11022e-15f, -2.4f, -1.11022e-15f, 4.0f, 8.0f, 9.6f, 11.2f, 12.0f}, 
		{4.0f, 4.0f, 2.4f, -1.11022e-15f, -4.0f, -5.6f, -3.2f, -1.11022e-15f, 2.4f, 8.8f}, 
		{8.0f, 5.6f, -1.11022e-15f, -5.6f, -4.8f, -1.11022e-15f, 8.0f, 9.6f, 9.6f, 8.8f}};
	
	public static final int AMOUNT_OF_STYLES = NAMES.length;
	public static final int AMOUNT_OF_BANDS = PRESETS[0].length;
	
	public static void next(int index, Slider[] bands, MediaPlayer mp, Label style) {
		int i = index % AMOUNT_OF_STYLES;
		style.setText(NAMES[i]);
		double previousVal, nextVal;
		for (int x = 0; x < AMOUNT_OF_BANDS; x++) {
			mp.getAudioEqualizer().getBands().get(x);
			previousVal = EqualizerBand.MAX_GAIN * 2;
			mp.getAudioEqualizer().getBands().get(x).setGain(PRESETS[i][x]);
			nextVal = mp.getAudioEqualizer().getBands().get(x).getGain();
			bands[x].setValue((int) (Math.abs(nextVal / previousVal) * 100));
		}
	}
	
	public static void previous(int index, Slider[] bands, MediaPlayer mp, Label style) {
		int i = (index < 0) ? (AMOUNT_OF_STYLES - 1) : index;
		style.setText(NAMES[i]);
		double previousVal, nextVal;
		for (int x = 0; x < AMOUNT_OF_BANDS; x++) {
			mp.getAudioEqualizer().getBands().get(x);
			previousVal = EqualizerBand.MAX_GAIN * 2;
			mp.getAudioEqualizer().getBands().get(x).setGain(PRESETS[i][x]);
			nextVal = mp.getAudioEqualizer().getBands().get(x).getGain();
			bands[x].setValue((int) (Math.abs(nextVal / previousVal) * 100));
		}
	}
}
