
package strategy;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.RegulatedMotor;
import main.Constants;
import main.Robot;

public class RollingFieldStrategy extends Strategy {

	public static final int wheelMotorSpeed = 900;
	public static final int wheelMotorSpeedCorrection = 40;
	
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3GyroSensor gyroSensor;
	protected EV3ColorSensor colorSensor;

	protected float[] startDirection = { 0.0f };
	protected float[] colorSample = { 0.0f };

	public RollingFieldStrategy(Robot robot) {
		super(robot);
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.gyroSensor = robot.gyroSensor;
		this.colorSensor = robot.colorSensor;
	}

	@Override
	public void execute() {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("Rolling Field", 2, 2);
		
		colorSensor.setCurrentMode(colorSensor.getRedMode().getName());
		colorSensor.getRedMode().fetchSample(colorSample, 0);
		
		gyroSensor.reset();
		gyroSensor.getAngleMode().fetchSample(startDirection, 0);
				
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});

		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		
		while (colorSample[0]<Constants.lineThreshold) {
			correctDirection();
			colorSensor.getRedMode().fetchSample(colorSample, 0);
		}
					
		leftWheelMotor.stop();
		rightWheelMotor.stop();
	}
	
	private void correctDirection() {
		float[] sample = { 0.0f };
		int speedOffset;
		
		gyroSensor.getAngleMode().fetchSample(sample, 0);

		speedOffset = (int)(sample[0] - startDirection[0])
						* wheelMotorSpeedCorrection;

		leftWheelMotor.setSpeed(wheelMotorSpeed + speedOffset);
		rightWheelMotor.setSpeed(wheelMotorSpeed - speedOffset);
	}
}
