package main;

import strategy.*;



public class Main {

	public static void main(String[] args) {
		final Robot robot = new Robot();
		
//		float[] sample = { 1.0f };
//
//		for (int i = 0; i < 6; ++i) {
//			robot.gyroSensor.getAngleMode().fetchSample(sample, 0);
//			robot.ev3.getTextLCD().drawString("" + sample[0], 2, 2);
//			robot.ev3.getKeys().waitForAnyPress();
//		}

				
		Strategy currentStrategy = new SuspensionBridgeStrategy(robot);
		currentStrategy.execute();
		
	}

}
