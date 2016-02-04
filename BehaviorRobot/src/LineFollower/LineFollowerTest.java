package LineFollower;

import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import Robot.Robot;

public class LineFollowerTest {
	
	public static void main(String[] args) throws Exception {
		Robot robo = new Robot();
		robo.drawText("Line Follower", 4, 4);
		
		Behavior online = new OnLine(robo);
		Behavior offline = new OffLine(robo);
		Behavior[] behavior_array = {offline, online};
		Arbitrator arb = new Arbitrator(behavior_array);
		robo.getKeys().waitForAnyPress();
		arb.start();
	}
}
