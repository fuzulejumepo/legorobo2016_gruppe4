
package strategy;

import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.RegulatedMotor;
import main.Robot;

public class RollingFieldStrategy extends Strategy {

	public static final int wheelMotorSpeed = 700;
	public static final int wheelMotorSpeedCorrection = 40;
	
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3GyroSensor gyroSensor;

	protected float[] startDirection = { 0.0f };

	public RollingFieldStrategy(Robot robot) {
		super(robot);
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.gyroSensor = robot.gyroSensor;
	}

	@Override
	public void execute() {
		
		gyroSensor.reset();
		gyroSensor.getAngleMode().fetchSample(startDirection, 0);
				
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});

		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		
		correctDirection();
		
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
