package com.arman.jfx.util;

import java.awt.AWTException;
import java.awt.Robot;

public class KeepAwakeThread extends Thread {

	public void run() {
		try {
			Robot r = new Robot();
			while (true) {
				r.delay(1000 * 60);
				r.keyPress(123);
			}
		} catch (AWTException e) {
			System.out.println("Never mind!");
		}
	}
	
}
