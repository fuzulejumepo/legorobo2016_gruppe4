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
	
	public static final int wheelMotorAdjustAngle = -250;

	public static final int sensorArmMaxDegree = 100;
	public static final int wheelCorrectionFactor = 140;
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

		armMotor.setSpeed(sensorArmMotorSpeed);
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		
		//robot.calibrateArm();
		robot.centerArm();
		
		while (true) {
			robot.ev3.getLED().setPattern(4);
			findBar();
			robot.ev3.getLED().setPattern(5);
			if (adjustAtBar())
				return;
		}
		
		//robot.setStatus(Status.BARCODE_READ);
	}
	
	protected void findBar() {
		leftWheelMotor.resetTachoCount();
		rightWheelMotor.resetTachoCount();
		
		robot.ev3.getTextLCD().drawInt(robot.sensorArmDegree, 3, 6);
		
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		
		float[] sample = { 0.0f };
		while (sample[0] < Constants.lineThreshold) {
			colorSensor.getRedMode().fetchSample(sample, 0);
		}
		
		leftWheelMotor.rotate(wheelMotorSearchAngle, true);
		rightWheelMotor.rotate(wheelMotorSearchAngle, false);
	}
	
	/**
	 * @return
	 */
	protected boolean adjustAtBar() {
		float[] sample = { 0.0f };
		int direction = 1;
		
		int minBarPos = robot.sensorArmMid;
		int maxBarPos = robot.sensorArmMid;
		
		for (int i = 0; i < 3; ++i) {
			minBarPos = robot.sensorArmMid;
			maxBarPos = robot.sensorArmMid;

			armMotor.rotateTo(robot.sensorArmMin, false);
			armMotor.rotateTo(robot.sensorArmMax, true);
			while (armMotor.isMoving()) {
				colorSensor.getRedMode().fetchSample(sample, 0);
				if (sample[0] > Constants.lineThreshold) {
					armMotor.stop(false);
					minBarPos = armMotor.getTachoCount();
				}
			}

			armMotor.rotateTo(robot.sensorArmMax, false);
			armMotor.rotateTo(robot.sensorArmMin, true);
			while (armMotor.isMoving()) {
				colorSensor.getRedMode().fetchSample(sample, 0);
				if (sample[0] > Constants.lineThreshold) {
					armMotor.stop(false);
					maxBarPos = armMotor.getTachoCount();
				}
			}
			
			if (minBarPos < robot.sensorArmMin + sensorArmMaxDegree &&
					maxBarPos > robot.sensorArmMax - sensorArmMaxDegree)
				break;
			
			leftWheelMotor.rotate(direction * i * wheelCorrectionFactor, true);
			rightWheelMotor.rotate(-direction * i * wheelCorrectionFactor, false);
			
			direction = -direction;
		}
		
		robot.centerArm();
		
//		if (minBarPos > robot.sensorArmMin + sensorArmMaxDegree ||
//				maxBarPos < robot.sensorArmMax - sensorArmMaxDegree) {
//			if (robot.sensorArmMax - maxBarPos < minBarPos - robot.sensorArmMin)
//				rightWheelMotor.rotate(wheelMotorAdjustAngle, false);
//			else
//				leftWheelMotor.rotate(wheelMotorAdjustAngle, false);
//			
//			leftWheelMotor.rotate(wheelMotorAdjustAngle, true);
//			rightWheelMotor.rotate(wheelMotorAdjustAngle, false);
//			return false;
//		}
		
//		if (minBarPos > robot.sensorArmMin + sensorArmMaxDegree) {
//			rightWheelMotor.rotate(wheelMotorAdjustAngle, false);
//			leftWheelMotor.rotate(wheelMotorAdjustAngle, true);
//			rightWheelMotor.rotate(wheelMotorAdjustAngle, false);
//			return false;
//		} else if (maxBarPos < robot.sensorArmMax - sensorArmMaxDegree) {
//			leftWheelMotor.rotate(wheelMotorAdjustAngle, false);
//			leftWheelMotor.rotate(wheelMotorAdjustAngle, true);
//			rightWheelMotor.rotate(wheelMotorAdjustAngle, false);
//			return false;
//		}
		
		int correction = (int) (((robot.sensorArmMid - minBarPos) 
								- (maxBarPos - robot.sensorArmMid))
									* wheelAdjustOffset);
		leftWheelMotor.rotate(correction, true);
		rightWheelMotor.rotate(-correction, false);
		
		colorSensor.getRedMode().fetchSample(sample, 0);
		while (sample[0] > Constants.lineThreshold) {
			leftWheelMotor.rotate(16, true);
			rightWheelMotor.rotate(16, false);
			colorSensor.getRedMode().fetchSample(sample, 0);
		}
		
		return true;
	}
}
