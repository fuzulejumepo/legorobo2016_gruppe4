package Labyrinth;
import Robot.Robot;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;

public class KeepDistance implements Behavior {
	private Robot robo;
	private DifferentialPilot pilot;
	private EV3IRSensor irSensor;
	private boolean suppressed = false;
	private int DIS = 10;

	public KeepDistance(DifferentialPilot p, EV3IRSensor ir) {
		pilot = p;
		irSensor = ir;
	}

	public KeepDistance(Robot r) throws Exception {
		robo = r;
		pilot = robo.getPilot();
		irSensor = robo.getIRSensor();
	}

	private float getDistance() {
		SampleProvider sp = irSensor.getDistanceMode();
	    int distance = 255;
	    float [] sample = new float[sp.sampleSize()];
        sp.fetchSample(sample, 0);
        distance = (int)sample[0];
		return distance;
	}
	
	@Override
	public boolean takeControl() {
		return getDistance() != DIS;
	}

	@Override
	public void action() {
		suppressed = false;
		if (getDistance() < DIS) {
			pilot.rotate(10, true);
		} 
		else if (getDistance() > DIS) {
			pilot.rotate(-10, true);
		}
		else {
			
		}
		while( !suppressed && pilot.isMoving())
	        Thread.yield();
		pilot.stop();
	}

	@Override
	public void suppress() {
		suppressed = true;
	}

}
