package task;

import main.*;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class ArmFollowLine implements Runnable {

	private RegulatedMotor armMotor;
	private EV3ColorSensor armSensor;
	
	public ArmFollowLine(Robot robot) {
		this.armMotor = robot.sensorArmMotor;
		this.armSensor = robot.colorSensor;
	}
	
	
	@Override
	public void run() {
		int followTries;
		while (true) {
			while (armSensor.getColorID() == Color.WHITE) {
				armMotor.rotate(Constants.sensorArmFollowOffset, true);
				if (armMotor.isStalled()) return;
			}
			
			followTries = 0;
			while (armSensor.getColorID() != Color.WHITE) {
				armMotor.rotate(-Constants.sensorArmFollowOffset, true);
				if (followTries > Constants.sensorArmFollowTries) return;
				++followTries;
			}
		}
	}
}
