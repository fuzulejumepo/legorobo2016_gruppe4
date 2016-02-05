package main;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3IRSensor;
import model.LegoRobot;
import model.RobotState;
import strategy.Strategy;

public class SensorWatcher implements Runnable {
	public static final int LINE_MIN = 1;
	public static final int LINE_MAX = 2;
	
	private EV3IRSensor ultrasonicSensor;
	private EV3ColorSensor colorSensor;
	
	private Strategy listener;
	private RobotState state;
	
	public SensorWatcher(LegoRobot robot) {
		this.ultrasonicSensor = robot.getUltrasonicSensor();
		this.colorSensor = robot.getColorSensor();
		this.state = RobotState.INITIAL;
	}
	
	public RobotState getState() {
		return state;
	}
	
	public void setListener(Strategy strategy) {
		this.listener = strategy;
	}

	@Override
	public void run() {
		RobotState newState;
		while(true) {
			newState = getNewState();
			if (newState == this.state) {
				continue;
			}
			else {
				this.state = newState;
				this.listener.notifyOfNewState(newState);
				return;
			}
		}	
	}
	
	private RobotState getNewState() {
		int colorValue = this.getColorValue();
		float ultarsonicValue = this.getUltrasonicValue();
		if (colorValue > LINE_MIN && colorValue < LINE_MAX) {
			return RobotState.LINE;
		}
		else {
			return RobotState.ERROR;
		}
	}
	
	private float getUltrasonicValue() {
		float[] sample = new float[ultrasonicSensor.sampleSize()];
		ultrasonicSensor.getDistanceMode().fetchSample(sample, 0);
		return sample[0];
	}

	private int getColorValue() {
		return colorSensor.getColorID();
	}

}
