package main;

import strategy.*;

public class Main {

	public static void main(String[] args) {
		final Robot robot = new Robot();

		// robot.ev3.getTextLCD().drawString("FollowLine", 2, 1);
		//
		// Strategy currentStrategy = new FollowLineStrategy(robot);
		
		
		robot.ev3.getTextLCD().drawString("LabyrinthStrategy", 2, 1);
		Strategy currentStrategy = new LabyrinthStrategy(robot);
		currentStrategy.execute();
	}

}
