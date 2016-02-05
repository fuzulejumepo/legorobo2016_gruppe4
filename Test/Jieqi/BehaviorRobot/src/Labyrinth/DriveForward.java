package Labyrinth;
import Robot.Robot;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;

public class DriveForward implements Behavior {
	private Robot robo;
	private DifferentialPilot pilot;
	private boolean suppressed = false;

	public DriveForward(Robot r) throws Exception {
		robo = r;
		pilot = robo.getPilot();
	}

	@Override
	public boolean takeControl() {
		return true;
	}

	@Override
	public void action() {
		suppressed = false;
		pilot.forward();
		while( !suppressed )
	        Thread.yield();
		pilot.stop();
	}

	@Override
	public void suppress() {
		suppressed = true;
	}

}
