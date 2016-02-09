package strategy;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import main.Constants;
import main.Robot;
import main.Status;

public class LabyrinthStrategy extends Strategy {
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3TouchSensor touchSensorRight;
	protected EV3ColorSensor colorSensor;
	protected EV3UltrasonicSensor ultraSensor;
	protected SampleProvider ultra;
	static int speed = 520;
	static int factor = 1150;

	public LabyrinthStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.touchSensorRight = robot.bumperRightSensor;
		this.colorSensor = robot.colorSensor;
		this.ultraSensor = robot.ultraSensor;
		this.ultra = ultraSensor.getMode("Distance");
	}

	@Override
	public void execute() {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("LabyrinthStrategy", 2, 2);
		
		float[] colorSamples = { 0.0f };
		float[] touchedRight = new float[touchSensorRight.sampleSize()];
		float[] distances = new float[ultraSensor.sampleSize()];
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		
		colorSensor.getRedMode().fetchSample(colorSamples, 0);
		
		while (colorSamples[0] < Constants.lineThreshold) {
			while (touchedRight[0] != 1.0 && colorSamples[0] < Constants.lineThreshold) {
				touchSensorRight.fetchSample(touchedRight, 0);
				ultra.fetchSample(distances, 0);
				double distance = distances[0] - 0.07;
				// lcd.drawString(" " + distance, 3, 3);
				leftWheelMotor.setSpeed((int) (speed + (factor * distance)));
				rightWheelMotor.setSpeed((int) (speed + - (factor * distance)));
				leftWheelMotor.forward();
				rightWheelMotor.forward();
				colorSensor.getRedMode().fetchSample(colorSamples, 0);
			}
			leftWheelMotor.rotate(-350, false);
			touchSensorRight.fetchSample(touchedRight, 0);
		}
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		robot.setStatus(Status.BARCODE_FIND);
	}
}
