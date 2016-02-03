package task;

import main.*;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;

public class ArmSearchLine implements Runnable {
	
	private RegulatedMotor armMotor;
	private EV3ColorSensor armSensor;
	private int armMaxLeft;
	private int armMaxRight;
	
	public ArmSearchLine(Robot robot) {
		this.armMotor = robot.sensorArmMotor;
		this.armSensor = robot.colorSensor;
		this.armMaxLeft = robot.sensorArmMaxLeft;
		this.armMaxRight = robot.sensorArmMaxRight;
	}
	
	@Override
	public void run() {
		while (true) {
			armMotor.rotateTo(armMaxLeft, true);
			while (armMotor.isMoving()) {
				if (armSensor.getColorID() == Color.WHITE) {
					armMotor.stop(false);
					return;
				}
			}

			armMotor.rotateTo(armMaxRight, true);
			while (armMotor.isMoving()) {
				if (armSensor.getColorID() == Color.WHITE) {
					armMotor.stop(false);
					armMotor.rotate(Constants.sensorArmReverseOffset, false);
					return;
				}
			}
		}
	}
}
