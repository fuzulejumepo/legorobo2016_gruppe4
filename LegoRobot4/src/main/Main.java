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
				robot.close();
				System.exit(0);
				
			}
		};
		Button.ENTER.addKeyListener(listener);
		

		robot.ev3.getLED().setPattern(0);
		robot.calibrateArm();
		robot.centerArm();
		
		Strategy currentStrategy;
		for (int i = 0; i < 100; ++i) {
			currentStrategy = new FollowLineStrategy(robot);
			currentStrategy.execute();
			currentStrategy = new SuspensionBridgeStrategy(robot);
			currentStrategy.execute();
		}
		
		robot.close();
	}

}
