package Labyrinth;

import Robot.Robot;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;

public class HitWall implements Behavior {
	private Robot robo;
	private DifferentialPilot pilot;
	private EV3TouchSensor touchSensor;
	private boolean suppressed = false;

	public HitWall(Robot r) throws Exception {
		robo = r;
		pilot = robo.getPilot();
		touchSensor = robo.getTouchSensor();
	}

	private boolean isPressed() {
		SampleProvider sp = touchSensor.getTouchMode();
		float [] sample = new float[sp.sampleSize()];
        sp.fetchSample(sample, 0);
        if ((int)sample[0]==1)
        	return true;
        else
        	return false;
	}
	
	@Override
	public boolean takeControl() {
		return isPressed();
	}

	@Override
	public void action() {
		suppressed = false;
		pilot.backward();
		pilot.rotate(90, true);
		while( !suppressed && pilot.isMoving())
	        Thread.yield();
		pilot.stop();	
	}

	@Override
	public void suppress() {
		suppressed = true;
	}

}
