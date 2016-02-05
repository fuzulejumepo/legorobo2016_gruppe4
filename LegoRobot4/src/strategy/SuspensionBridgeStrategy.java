package strategy;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.RegulatedMotor;
import main.Robot;

public class SuspensionBridgeStrategy extends Strategy {
	//motors constants
	public static final int wheelMotorSpeed = 500;
	public static final int sensorArmMotorSpeed = 500;

	//up down constants
	public static final int moveWheelDegree = 1000;
	
	public static final int wheelMotorSpeedCorrection = 4;
	
	protected float[] startDirection = { 0.0f };



	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor armSensor;
	protected EV3GyroSensor gyroSensor;


	public SuspensionBridgeStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.armSensor = robot.colorSensor;
		this.gyroSensor = robot.gyroSensor;
	}

	public void execute() {
		robot.ev3.getTextLCD().drawString("BridgeStrategy", 2, 2);
		
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		armSensor.setCurrentMode(armSensor.getColorIDMode().getName());
		
		robot.calibrateArm();
		robot.centerArm();
		robot.ev3.getLED().setPattern(2);
		//moveUp();
		robot.ev3.getLED().setPattern(1);
		crossBridge();
		robot.ev3.getLED().setPattern(0);
		leftWheelMotor.startSynchronization();
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		leftWheelMotor.endSynchronization();
	}
	
	protected void moveUp() {
		armMotor.rotateTo(robot.sensorArmMid, false);
		
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		
		boolean done = false;
		int newLeftWheelPos;
		int newRightWheelPos;
		int lastLeftWheelPos = leftWheelMotor.getTachoCount();
		int lastRightWheelPos = rightWheelMotor.getTachoCount();
		while (!done) {
			newLeftWheelPos = leftWheelMotor.getTachoCount();
			newRightWheelPos = rightWheelMotor.getTachoCount();
			if (armSensor.getColorID() >= 0) {
				lastLeftWheelPos = newLeftWheelPos;
				lastRightWheelPos = newRightWheelPos;
			}
			if (Math.abs(newLeftWheelPos - lastLeftWheelPos) > moveWheelDegree &&
					Math.abs(newRightWheelPos - lastRightWheelPos) > moveWheelDegree)
				done = true;
		}
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
	}
	
	protected void crossBridge() {
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		armMotor.stop();
		gyroSensor.reset();
		
		gyroSensor.getAngleMode().fetchSample(startDirection, 0);
		float[] sample = { 0.0f };
		int speedOffset;

		while (true) {
			gyroSensor.getAngleMode().fetchSample(sample, 0);

			speedOffset = (int)(sample[0] - startDirection[0])
							* wheelMotorSpeedCorrection;

			leftWheelMotor.setSpeed(wheelMotorSpeed + speedOffset);
			rightWheelMotor.setSpeed(wheelMotorSpeed - speedOffset);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
		}
	}

}
