package strategy;

import main.*;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class FindBarcodeStrategy extends Strategy {
	//motors constants
	public static final int wheelMotorSpeed = 300;
	public static final int sensorArmMotorSpeed = 500;
	
	public static final int wheelMotorSearchAngle = 60;
	
	public static final int wheelMotorAdjustAngle = -400;

	
	public static final float wheelAdjustOffset = 0.8f;

	
	
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor colorSensor;
	
	
	public FindBarcodeStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.colorSensor = robot.colorSensor;
	}
	
	public void execute() {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("FindBarcodeStrategy", 2, 2);

		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());

		//robot.calibrateArm();
		robot.centerArm();
		
		//move back a little bit to find the first line
		moveBack();
		
		for (int i = 0; i < 2; ++i) {
			robot.ev3.getLED().setPattern(3);
			findBar();
			if (adjustAtBar())
				robot.ev3.getLED().setPattern(4);
			else
				robot.ev3.getLED().setPattern(5);
			Delay.msDelay(1500);
		}
		
		robot.setStatus(Status.BARCODE_READ);
	}
	
	protected void findBar() {
		armMotor.setSpeed(sensorArmMotorSpeed);
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		
		leftWheelMotor.resetTachoCount();
		rightWheelMotor.resetTachoCount();
		
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		
		float[] sample = { 0.0f };
		while (sample[0] < Constants.lineThreshold && leftWheelMotor.getTachoCount() < 500) {
			colorSensor.getRedMode().fetchSample(sample, 0);
		}
		
		leftWheelMotor.rotate(wheelMotorSearchAngle, true);
		rightWheelMotor.rotate(wheelMotorSearchAngle, false);
	}
	
	protected boolean adjustAtBar() {
		float[] sample = { 0.0f };
		
		int minBarPos = robot.sensorArmMin - 100;
		int maxBarPos = robot.sensorArmMax + 100;
		
		armMotor.rotateTo(robot.sensorArmMin, false);
		armMotor.rotate(robot.sensorArmDegree / 3, true);
		while (armMotor.isMoving()) {
			colorSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold) {
				armMotor.stop(false);
				minBarPos = armMotor.getTachoCount();
			}
		}
		
		armMotor.rotateTo(robot.sensorArmMax, false);
		armMotor.rotate(-robot.sensorArmDegree / 3, true);
		while (armMotor.isMoving()) {
			colorSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold) {
				armMotor.stop(false);
				maxBarPos = armMotor.getTachoCount();
			}
		}
		
		robot.centerArm();
		
		if (minBarPos < robot.sensorArmMin) {
			leftWheelMotor.rotate(wheelMotorAdjustAngle, false);
			leftWheelMotor.rotate(-2 * wheelMotorSearchAngle, true);
			rightWheelMotor.rotate(-2 * wheelMotorSearchAngle, false);
			return false;
		} else if (maxBarPos > robot.sensorArmMax) {
			rightWheelMotor.rotate(wheelMotorAdjustAngle, false);
			leftWheelMotor.rotate(-2 * wheelMotorSearchAngle, true);
			rightWheelMotor.rotate(-2 * wheelMotorSearchAngle, false);
			return false;
		}
		
		int correction = (int) (((robot.sensorArmMid - minBarPos) 
								- (maxBarPos - robot.sensorArmMid))
									* wheelAdjustOffset);
		leftWheelMotor.rotate(correction, true);
		rightWheelMotor.rotate(-correction, false);
		
		return true;
	}
	
	protected void moveBack() {
		leftWheelMotor.backward();
		rightWheelMotor.backward();
		
		Delay.msDelay(700);
	}
}
