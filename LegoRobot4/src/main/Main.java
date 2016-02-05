package main;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
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
		
		KeyListener listener = new KeyListener() {
			
			@Override
			public void keyReleased(Key k) {
				// TODO Auto-generated method stub		
			}
			
			@Override
			public void keyPressed(Key k) {
				robot.bumperRightSensor.close();
				robot.colorSensor.close();
				robot.ultraSensor.close();
				System.exit(0);
				
			}
		};
		Button.ENTER.addKeyListener(listener);
		
		// robot.ev3.getTextLCD().drawString("FollowLine", 2, 1);
		//
		// Strategy currentStrategy = new FollowLineStrategy(robot);
		
		
		robot.ev3.getTextLCD().drawString("LabyrinthStrategy", 2, 1);
		Strategy currentStrategy = new LabyrinthStrategy(robot);
		currentStrategy.execute();

		//Strategy currentStrategy = new SuspensionBridgeStrategy(robot);
		//currentStrategy.execute();
		
	}

}
