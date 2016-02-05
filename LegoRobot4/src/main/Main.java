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
			public void keyReleased(Key k) {}
			@Override
			public void keyPressed(Key k) {
				robot.bumperRightSensor.close();
				robot.colorSensor.close();
				robot.ultraSensor.close();
				System.exit(0);
				
			}
		};
		Button.ENTER.addKeyListener(listener);

		
		Strategy currentStrategy = new LabyrinthStrategy(robot);
		currentStrategy.execute();
		
	}

}
