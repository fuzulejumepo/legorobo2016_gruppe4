package task;

import main.*;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class ArmCalibrate implements Runnable {

	private Robot robot;
	private RegulatedMotor armMotor;
	
	public ArmCalibrate(Robot robot) {
		this.robot = robot;
		this.armMotor = robot.sensorArmMotor;
	}
	
	
	@Override
	public void run() {
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
	
}
