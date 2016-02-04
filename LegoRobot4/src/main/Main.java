package main;

import strategy.*;
import task.*;



public class Main {

	public static void main(String[] args) {
		final Robot robot = new Robot();
		
		//int size = robot.colorSensor.getRedMode().sampleSize();
		//robot.ev3.getTextLCD().drawInt(size, 4, 3);
		
		robot.ev3.getTextLCD().drawString("FollowLine", 2, 1);
		
		Strategy currentStrategy = new FollowLineStrategy(robot);
		currentStrategy.execute();
	}

}
