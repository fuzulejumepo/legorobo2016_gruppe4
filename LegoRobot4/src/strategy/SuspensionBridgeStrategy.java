package strategy;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import main.Constants;
import main.Robot;

public class SuspensionBridgeStrategy extends Strategy {
	//motors constants
	public static final int wheelMotorLineSpeed = 260;
	public static final int wheelMotorAdjustSpeed = 500;
	public static final int wheelMotorCrossingSpeed = 700;
	
	//follow line constants
	public static final int wheelsSearchDegree = 200;

	//adjust constants
	public static final int moveWheelEnterBridge = 700;
	public static final int moveWheelCorrection = 600;
	public static final int wheelCorrectionFactor = 1800;
	
	public static final int wheelMotorSpeedCorrection = 4;


	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor colorSensor;
	protected EV3UltrasonicSensor ultraSensor;
	protected EV3GyroSensor gyroSensor;


	public SuspensionBridgeStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.colorSensor = robot.colorSensor;
		this.ultraSensor = robot.ultraSensor;
		this.gyroSensor = robot.gyroSensor;
	}

	public void execute() {
		robot.ev3.getTextLCD().drawString("SuspensionBridgeStrategy", 2, 2);
		
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		colorSensor.setCurrentMode(colorSensor.getRedMode().getName());
		
		robot.centerArm();
		armMotor.stop(false);
		
		//robot.centerArm();
		robot.ev3.getLED().setPattern(1);
		followLine();
		robot.ev3.getLED().setPattern(2);
		adjustInFrontOfBridge();
		robot.ev3.getLED().setPattern(8);
		crossBridge();
		robot.ev3.getLED().setPattern(0);
	}
	
	protected void followLine() {
		float[] sample = { 1.0f };
		
		leftWheelMotor.setSpeed(wheelMotorLineSpeed);
		rightWheelMotor.setSpeed(wheelMotorLineSpeed);
		
		int lastDirection = 1;
		
		while (true) {
			leftWheelMotor.forward();
			rightWheelMotor.forward();
			while (sample[0] >= Constants.lineThreshold)
				colorSensor.getRedMode().fetchSample(sample, 0);
			
			leftWheelMotor.rotate(lastDirection * wheelsSearchDegree, true);
			rightWheelMotor.rotate(-lastDirection * wheelsSearchDegree, true);
			while (rightWheelMotor.isMoving() && sample[0] < Constants.lineThreshold)
				colorSensor.getRedMode().fetchSample(sample, 0);
			
			if (sample[0] >= Constants.lineThreshold)
				continue;
			
			lastDirection = -lastDirection;
			
			leftWheelMotor.rotate(lastDirection * 2 * wheelsSearchDegree, true);
			rightWheelMotor.rotate(-lastDirection * 2 * wheelsSearchDegree, true);
			while (rightWheelMotor.isMoving() && sample[0] < Constants.lineThreshold)
				colorSensor.getRedMode().fetchSample(sample, 0);
			//leftWheelMotor.stop(true);
			//rightWheelMotor.stop(false);
			
			if (sample[0] >= Constants.lineThreshold)
				continue;
			
			lastDirection = -lastDirection;

			leftWheelMotor.rotate(lastDirection * wheelsSearchDegree, true);
			rightWheelMotor.rotate(-lastDirection * wheelsSearchDegree, false);
			
			leftWheelMotor.stop();
			rightWheelMotor.stop();
			return;
		}
	}
	
	
	protected void adjustInFrontOfBridge() {
		float[] distances = { 0.0f, 0.0f };
		
		leftWheelMotor.setSpeed(wheelMotorAdjustSpeed);
		rightWheelMotor.setSpeed(wheelMotorAdjustSpeed);
		
		leftWheelMotor.rotate(moveWheelEnterBridge, true);
		rightWheelMotor.rotate(moveWheelEnterBridge, false);
		
		ultraSensor.fetchSample(distances, 0);
		
		leftWheelMotor.rotate(moveWheelCorrection, true);
		rightWheelMotor.rotate(moveWheelCorrection, false);
		
		ultraSensor.fetchSample(distances, 1);
		
		int correction = (int) ((distances[1] - distances[0])
								* wheelCorrectionFactor);
		
		//leftWheelMotor.startSynchronization();
		leftWheelMotor.rotate(correction, true);
		rightWheelMotor.rotate(-correction, false);
		//leftWheelMotor.endSynchronization();
	}
	
	protected void crossBridge() {
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		armMotor.stop();
		leftWheelMotor.setSpeed(wheelMotorCrossingSpeed);
		rightWheelMotor.setSpeed(wheelMotorCrossingSpeed);
		gyroSensor.reset();
		
		float[] startDirection = { 0.0f };
		gyroSensor.getAngleMode().fetchSample(startDirection, 0);
		float[] gyroSample = { 0.0f };
		int speedOffset;
		float[] colorSample = { 0.0f };

		while (colorSample[0] < Constants.lineThreshold) {
			gyroSensor.getAngleMode().fetchSample(gyroSample, 0);

			speedOffset = (int)(gyroSample[0] - startDirection[0])
							* wheelMotorSpeedCorrection;

			leftWheelMotor.setSpeed(wheelMotorCrossingSpeed + speedOffset);
			rightWheelMotor.setSpeed(wheelMotorCrossingSpeed - speedOffset);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
			
			colorSensor.getRedMode().fetchSample(colorSample, 0);
		}
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
	}

}
