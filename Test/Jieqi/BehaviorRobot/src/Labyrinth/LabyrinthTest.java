package Labyrinth;

import Robot.Robot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class LabyrinthTest {

	public static void main(String[] args) throws Exception {
		Robot robo = new Robot();
		robo.drawText("Labyrinth Test", 4, 4);

		Behavior b1 = new DriveForward(robo);
		Behavior b2 = new KeepDistance(robo);
		Behavior b3 = new HitWall(robo);
		Behavior[] behavior_array = {b1, b2, b3};
		Arbitrator arb = new Arbitrator(behavior_array);
		robo.getKeys().waitForAnyPress();
		arb.start();
	}

}
