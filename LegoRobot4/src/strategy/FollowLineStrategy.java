package strategy;

import main.*;
import task.*;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;


public class FollowLineStrategy extends Strategy{
	
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor armSensor;
	
	
	public FollowLineStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.armSensor = robot.colorSensor;
	}
	
	public Status execute() {
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		robot.colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());

		calibrateArm();
		
		for (int i = 0; i < 1000; ++i) {
			robot.ev3.getLED().setPattern(2);
			searchLine();
			robot.ev3.getLED().setPattern(1);
			followLine();
			robot.ev3.getLED().setPattern(0);
			leftWheelMotor.startSynchronization();
			leftWheelMotor.stop();
			rightWheelMotor.stop();
			leftWheelMotor.endSynchronization();
		}
		
		return Status.SUCCESS;
	}
	
	protected void calibrateArm() {
		armMotor.forward();
		while (!armMotor.isStalled());
		int leftMax = armMotor.getTachoCount();
		
		armMotor.backward();
		while (!armMotor.isStalled());
		int rightMax = armMotor.getTachoCount();
		
		int mid = (leftMax + rightMax) / 2;
		//armMotor.rotateTo(mid);
		
		int offset = (leftMax < rightMax) ? Constants.sensorArmOffsetMax : 
											-Constants.sensorArmOffsetMax;
		
		robot.sensorArmMaxLeft = leftMax + offset;
		robot.sensorArmMaxRight = rightMax - offset;
		robot.sensorArmMid = mid;
	}
	
	protected void searchLine() {
		float[] sample = { 0.0f };

		while (true) {
			armMotor.rotateTo(robot.sensorArmMaxLeft, true);
			while (armMotor.isMoving()) {
				armSensor.getRedMode().fetchSample(sample, 0);
				if (sample[0] > Constants.lineThreshold) {
					armMotor.stop(false);
					return;
				}
			}

			armMotor.rotateTo(robot.sensorArmMaxRight, true);
			while (armMotor.isMoving()) {
				armSensor.getRedMode().fetchSample(sample, 0);
				if (sample[0] > Constants.lineThreshold) {
					armMotor.stop(false);
					armMotor.rotate(Constants.sensorArmReverseOffset, false);
					return;
				}
			}
		}
	}
	
	protected void followLine() {
		float[] sample = { 0.0f };
		int followTriesOnLine = 0;
		int followTriesOffLine = 0;
		int speed = Constants.wheelMotorSpeed;
		
//		leftWheelMotor.forward();
//		rightWheelMotor.forward();
		
		while (true) {
			armSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold) { //above white line
				armMotor.rotate(Constants.sensorArmFollowOffset
								+ followTriesOnLine / 50, true);
				followTriesOnLine++;
				followTriesOffLine = 0;
			} else { //above black surface
				armMotor.rotate(-Constants.sensorArmFollowOffset
								- followTriesOffLine / 50, true);
				if (followTriesOffLine > Constants.sensorArmFollowTries)
					return;
				followTriesOnLine = 0;
				followTriesOffLine++;
			}
			
			speed = (int) ((float) Constants.wheelMotorSpeed  
					- Math.abs(armMotor.getTachoCount() - robot.sensorArmMid)
					* Math.abs(armMotor.getTachoCount() - robot.sensorArmMid)
					* Constants.wheelMotorSpeedReduction);

			int armPos = armMotor.getTachoCount();
			
			if (armPos < robot.sensorArmMid) {
				leftWheelMotor.setSpeed(speed);
				rightWheelMotor.setSpeed(Constants.wheelMotorSpeed);
			} else {
				leftWheelMotor.setSpeed(Constants.wheelMotorSpeed);
				rightWheelMotor.setSpeed(speed);
			}
			leftWheelMotor.startSynchronization();
			leftWheelMotor.forward();
			rightWheelMotor.forward();
			leftWheelMotor.endSynchronization();
		}
	}
}
