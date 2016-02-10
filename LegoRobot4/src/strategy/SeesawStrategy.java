package strategy;

import edu.kit.mindstorms.communication.ComModule;
import edu.kit.mindstorms.communication.Communication;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.RegulatedMotor;
import main.Robot;
import main.Status;

public class SeesawStrategy extends Strategy {
	public static final int wheelMotorSpeed = 500;
	
	public static final int wheelMotorSpeedCorrection = 4;
	public static final int wheelForward = 4900;

	
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3GyroSensor gyroSensor;

	
	public SeesawStrategy(Robot robot) {
		super(robot);
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.gyroSensor = robot.gyroSensor;
	}
	
	
	public void execute () {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("ElevatorStrategy", 1, 2);
		
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		
		crossSeesaw();
		
		//leftWheelMotor.rotate(wheelForward, true);
		//rightWheelMotor.rotate(wheelForward, false);
		
		robot.setStatus(Status.FOLLOW_LINE);
	}
	
	protected void crossSeesaw() {
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		//leftWheelMotor.setSpeed(wheelMotorSpeed);
		//rightWheelMotor.setSpeed(wheelMotorSpeed);
		leftWheelMotor.resetTachoCount();
		gyroSensor.reset();
		
		float[] startDirection = { 0.0f };
		gyroSensor.getAngleMode().fetchSample(startDirection, 0);
		float[] gyroSample = { 0.0f };
		int speedOffset;
		int tacho = 0;
		System.out.println(leftWheelMotor.getTachoCount());

		while (tacho < wheelForward) {
			gyroSensor.getAngleMode().fetchSample(gyroSample, 0);

			speedOffset = (int)(gyroSample[0] - startDirection[0])
							* wheelMotorSpeedCorrection;

			tacho = leftWheelMotor.getTachoCount();
			leftWheelMotor.setSpeed(wheelMotorSpeed + speedOffset);
			rightWheelMotor.setSpeed(wheelMotorSpeed - speedOffset);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
		}
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
	}

}
