package strategy;

import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.RegulatedMotor;
import main.Robot;
import main.Status;

public class BossStrategy extends Strategy {
	public static final int wheelMotorSpeed = 700;
	
	public static final int wheelMotorSpeedCorrection = 4;
	public static final int wheelForward = 10000;
	
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3GyroSensor gyroSensor;
	
	public BossStrategy(Robot robot) {
		super(robot);
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.gyroSensor = robot.gyroSensor;
	}
	
	public void execute() {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("BossStrategy", 1, 2);

		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		
		crossBossArea();
		
		robot.setStatus(Status.FINISH);
	}
	
	protected void crossBossArea() {
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		//leftWheelMotor.setSpeed(wheelMotorSpeed);
		//rightWheelMotor.setSpeed(wheelMotorSpeed);
		leftWheelMotor.resetTachoCount();
		gyroSensor.reset();
		
		float[] startDirection = { 0.0f };
		gyroSensor.getAngleMode().fetchSample(startDirection, 0);
		startDirection[0] -= 6.0f;
		float[] gyroSample = { 0.0f };
		int speedOffset;
		int tacho = 0;

		while (true) {
			gyroSensor.getAngleMode().fetchSample(gyroSample, 0);

			speedOffset = (int)(gyroSample[0] - startDirection[0])
							* wheelMotorSpeedCorrection;

			tacho = leftWheelMotor.getTachoCount();
			leftWheelMotor.setSpeed(wheelMotorSpeed + speedOffset);
			rightWheelMotor.setSpeed(wheelMotorSpeed - speedOffset);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
		}
		
		//leftWheelMotor.stop();
		//rightWheelMotor.stop();
	}
}
