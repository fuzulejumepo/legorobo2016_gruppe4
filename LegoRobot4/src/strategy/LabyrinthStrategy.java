package strategy;

import lejos.hardware.Button;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import main.Robot;

public class LabyrinthStrategy extends Strategy {
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3TouchSensor touchSensorFront = robot.bumperSensor;
	protected EV3UltrasonicSensor ultraSensor = robot.ultraSensor;
	protected SampleProvider ultra = ultraSensor.getMode("Distance");
	static int speed = 500;
	static int factor = 1700;

	public LabyrinthStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
	}

	@Override
	public void execute() {
		float[] touched = new float[touchSensorFront.sampleSize()];
		float[] distances = new float[ultraSensor.sampleSize()];
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		while (true) {
			while (touched[0] != 1.0) {
				touchSensorFront.fetchSample(touched, 0);
				ultra.fetchSample(distances, 0);
				double distance = distances[0] - 0.07;
				// lcd.drawString(" " + distance, 3, 3);
				leftWheelMotor.setSpeed((int) (speed + (factor * distance)));
				rightWheelMotor.setSpeed((int) (speed - (factor * distance)));
				leftWheelMotor.forward();
				rightWheelMotor.forward();
			}
			leftWheelMotor.rotate(-350, false);
			touchSensorFront.fetchSample(touched, 0);
		}
		// irSensor.close();
		// leftMotor.close();
		// rightMotor.close();
		//

	}
}
