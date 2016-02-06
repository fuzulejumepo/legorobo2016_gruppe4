package strategy;

import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import main.Robot;

public class ReadBarcodeStrategy extends Strategy {
	public static final float lineThreshold = 0.2f;
	public static final int sensorArmSearchOffset = 35;
	public static final int wheelMotorSpeed = 270;
	public static final int wheelMotorSpeedCorrection = 40;
	
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor armSensor;
	protected EV3GyroSensor gyroSensor;
	
	protected float[] startDirection = { 0.0f };
	
	public ReadBarcodeStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.armSensor = robot.colorSensor;
		this.gyroSensor = robot.gyroSensor;
	}

	@Override
	public void execute() {
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		
		robot.colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());
		robot.calibrateArm();
		robot.centerArm();
		
		armMotor.rotate(-sensorArmSearchOffset);
		
		float[] sample = { 0.0f };
		int counter = 0;
		int sign = 1;
		boolean isBarcode = true;
		
		armMotor.rotate(2 * sensorArmSearchOffset, true);
		while (armMotor.isMoving()) {
			armSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] <= lineThreshold) {
				isBarcode = false;
				break;
			}
		}
		armMotor.rotate(-sensorArmSearchOffset);
		
		if (!isBarcode) return;
		
		Sound.beepSequenceUp();
		
		gyroSensor.reset();
		gyroSensor.getAngleMode().fetchSample(startDirection, 0);
		
		leftWheelMotor.forward();
		rightWheelMotor.forward();
			
		while (isBarcode) {
			if (sign > 0) {
				robot.ev3.getLED().setPattern(1);
			}
			else {
				robot.ev3.getLED().setPattern(2);
			}
			
			correctDirection();
			isBarcode = false;
			for (int i = 0; i < 25; i++) {
				armSensor.getRedMode().fetchSample(sample, 0);
				System.out.println(sample[0]);
				if (sign * sample[0] > sign * lineThreshold) {
					isBarcode = true;
				}
			}

			//Delay.msDelay(50);
			sign = -sign;
			counter += 1;
		}
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		System.out.println("Barcode: " + counter);
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
