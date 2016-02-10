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

	public static final int sensorArmMaxDegree = 160;
	public static final int wheelCorrectionFactor = 80;
	public static final float wheelAdjustFactor = 0.7f;

	
	
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
		robot.ev3.getTextLCD().drawString("FindBarcodeStrategy", 1, 2);

		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());

		armMotor.setSpeed(sensorArmMotorSpeed);
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);

		robot.centerArm();

		robot.ev3.getLED().setPattern(4);
		findFirstBar();
		robot.ev3.getLED().setPattern(5);
		boolean success = adjustAtBar();
		robot.ev3.getLED().setPattern(0);
		if (success)
			robot.setStatus(Status.BARCODE_READ);
		else
			robot.setStatus(Status.ERROR);
	}
	
	
	protected void findFirstBar() {
		leftWheelMotor.resetTachoCount();
		rightWheelMotor.resetTachoCount();
		
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		
		float[] sample = { 0.0f };
		while (sample[0] < Constants.lineThreshold) {
			colorSensor.getRedMode().fetchSample(sample, 0);
		}
	}
	
	
	protected boolean adjustAtBar() {
		float[] sample = { 0.0f };
		int direction = 1;
		
		int minBarPos = robot.sensorArmMid;
		int maxBarPos = robot.sensorArmMid;
		
		int i;
		for (i = 1; i <= 4; ++i) {
			minBarPos = robot.sensorArmMid;
			maxBarPos = robot.sensorArmMid;

			leftWheelMotor.rotate(wheelMotorSearchAngle, true);
			rightWheelMotor.rotate(wheelMotorSearchAngle, false);
			
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
			
			leftWheelMotor.rotate(-2 * wheelMotorSearchAngle, true);
			rightWheelMotor.rotate(-2 * wheelMotorSearchAngle, false);
			
			leftWheelMotor.rotate(direction * i * wheelCorrectionFactor, true);
			rightWheelMotor.rotate(-direction * i * wheelCorrectionFactor, false);
			
			direction = -direction;
		}
		
		robot.centerArm();
		if (i > 4)
			return false;
		
		
		int correction = (int) (((robot.sensorArmMid - minBarPos) 
								- (maxBarPos - robot.sensorArmMid))
									* wheelAdjustFactor);
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
