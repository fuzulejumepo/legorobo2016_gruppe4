package main;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import strategy.*;

public class Main {
	public static void main(String[] args) {
		final Robot robot = new Robot();
		KeyListener listener = new KeyListener() {
			
			@Override
			public void keyReleased(Key k) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(Key k) {
				robot.bumperLeftSensor.close();
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
	}

}
