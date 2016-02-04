package LineFollower;

import Robot.Robot;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;

public class OffLine implements Behavior {
	private Robot robo;
	private EV3ColorSensor lightSensor;
	private DifferentialPilot pilot;
	private boolean suppressed = false;
	static private int direction = 1;

	public OffLine(Robot r) throws Exception {
		robo = r;
		lightSensor = robo.getColorSensor();
		pilot = robo.getPilot();	
	}

	@Override
	public boolean takeControl() {
		//System.out.println("OFFLine ColorID is " + lightSensor.getColorID());
		return lightSensor.getColorID() != Color.WHITE;
	}

	@Override
	public void action() {
		suppressed = false;
		int ang = 20;
		int count =1;
		while( !suppressed ){
			pilot.rotate((ang*direction)*count, true);
			while(!suppressed && pilot.isMoving()) {
				Thread.yield();
			}
			direction *= -1;
			count += 1; 
		}
		direction *= -1;
		pilot.stop();	
	}

	@Override
	public void suppress() {
		suppressed = true;
	}
}
