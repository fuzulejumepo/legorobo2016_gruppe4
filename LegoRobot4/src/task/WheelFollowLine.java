package task;

import main.*;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;

public class WheelFollowLine implements Runnable{
	
	private Robot robot;
	private RegulatedMotor leftWheelMotor;
	private RegulatedMotor rightWheelMotor;
	private RegulatedMotor armMotor;
	
	public WheelFollowLine(Robot robot) {
		this.robot = robot;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.armMotor = robot.sensorArmMotor;
	}
	
	
	@Override
	public void run() {
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		
		int armPos;
		while (true) {
			armPos = armMotor.getTachoCount();
			if (armPos < robot.sensorArmMid)
				leftWheelMotor.setSpeed(robot.maxSpeedWheel - 
										Constants.wheelMotorFollowOffset);
			else if (armPos > robot.sensorArmMid)
				rightWheelMotor.setSpeed(robot.maxSpeedWheel - 
										Constants.wheelMotorFollowOffset);
			else {
				leftWheelMotor.setSpeed(robot.maxSpeedWheel);
				rightWheelMotor.setSpeed(robot.maxSpeedWheel);
			}
		}
	}
}
