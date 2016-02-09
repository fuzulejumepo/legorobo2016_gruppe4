package strategy;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import main.Constants;
import main.Robot;
import main.Status;

public class SwampStrategy extends Strategy {
	
	public static final int wheelMotorSpeed = 700;
	public static final int wheelMotorSpeedCorrection = 4;
	
	public static final float swampLineThreshold = 0.6f;
	
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor colorSensor;
	protected EV3UltrasonicSensor ultraSensor;
	protected EV3GyroSensor gyroSensor;


	public SwampStrategy(Robot robot) {
		super(robot);
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.colorSensor = robot.colorSensor;
		this.ultraSensor = robot.ultraSensor;
		this.gyroSensor = robot.gyroSensor;
	}
	
	public void execute() {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("SwampStrategy", 2, 2);
		
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		
		robot.centerArm();
		robot.ev3.getLED().setPattern(7);
		crossSwamp();
		robot.ev3.getLED().setPattern(0);
		
		robot.setStatus(Status.BARCODE_FIND);
	}
	
	protected void crossSwamp() {
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		gyroSensor.reset();
		
		float[] startDirection = { 0.0f };
		gyroSensor.getAngleMode().fetchSample(startDirection, 0);
		float[] gyroSample = { 0.0f };
		int speedOffset;
		float[] colorSample = { 0.0f };

		while (colorSample[0] < swampLineThreshold) {
			gyroSensor.getAngleMode().fetchSample(gyroSample, 0);

			speedOffset = (int)(gyroSample[0] - startDirection[0])
							* wheelMotorSpeedCorrection;

			leftWheelMotor.setSpeed(wheelMotorSpeed + speedOffset);
			rightWheelMotor.setSpeed(wheelMotorSpeed - speedOffset);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
			
			colorSensor.getRedMode().fetchSample(colorSample, 0);
		}
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
	}

}
