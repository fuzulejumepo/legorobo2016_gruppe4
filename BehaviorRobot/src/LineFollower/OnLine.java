package LineFollower;

import Robot.Robot;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;

public class OnLine implements Behavior {
	private Robot robo;
	private EV3ColorSensor lightSensor;
	private DifferentialPilot pilot;
	private boolean suppressed = false;
	
	public OnLine(Robot r) throws Exception {
		robo = r;
		lightSensor = robo.getColorSensor();
		pilot = robo.getPilot();
	}

	@Override
	public boolean takeControl() {
		//System.out.println("ONline ColorID is " + lightSensor.getColorID());
		return lightSensor.getColorID() == Color.WHITE;
	}

	@Override
	public void action() {
		suppressed = false;
		pilot.forward();
		while( !suppressed && takeControl()){			
			Thread.yield();
		}			
		pilot.stop();
	}

	@Override
	public void suppress() {
		suppressed = true;
	}
}
