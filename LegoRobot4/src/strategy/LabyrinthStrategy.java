package strategy;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
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
	protected EV3GyroSensor gyroSensor;
	protected SampleProvider ultra;
	private DifferentialPilot pilot;
	
	static int speed = 500;
	static int factor = 900; // 1150
	float maxSpeed;
	static float maxFactor;

	float distance;

	public LabyrinthStrategy(Robot robot) {
		super(robot);
//		this.pilot = robot.
		this.gyroSensor = robot.gyroSensor;
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
		robot.ev3.getTextLCD().drawString("LabyrinthStrategy", 2, 2);
		maxSpeed= Math.min(leftWheelMotor.getMaxSpeed(), rightWheelMotor.getMaxSpeed()) -100;
		maxFactor = maxSpeed * 2;
		float[] gyroSample = {0.0f};
		float[] colorSamples = { 0.0f };
		float[] touchedRight = new float[touchSensorRight.sampleSize()];
		float[] distances = new float[ultraSensor.sampleSize()];
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());
		colorSensor.getRedMode().fetchSample(colorSamples, 0);
		
		while (colorSamples[0] < Constants.lineThreshold) {
			while (touchedRight[0] != 1.0 && colorSamples[0] < Constants.lineThreshold) {
				touchSensorRight.fetchSample(touchedRight, 0);
				ultra.fetchSample(distances, 0);
				distance = distances[0] - 0.07f;
				if (distance < 0.02 && touchedRight[0]!= 1.0){
					while(distance<0.02 && touchedRight[0]!= 1.0){
					moveCorrected(maxSpeed,maxFactor,distance);
					ultra.fetchSample(distances, 0);
					distance = distances[0] - 0.07f;
					touchSensorRight.fetchSample(touchedRight, 0);
					}
					while(distance>0.4){
						leftWheelMotor.backward();
						rightWheelMotor.backward();
						ultra.fetchSample(distances, 0);
						distance = distances[0] - 0.07f;
						}
				}
				
				// lcd.drawString(" " + distance, 3, 3);
				moveCorrected(speed, factor, distance);
				colorSensor.getRedMode().fetchSample(colorSamples, 0);
			}
			gyroSensor.reset();
			gyroSensor.getAngleMode().fetchSample(gyroSample, 0);
			while (gyroSample[0] < 45.0f&& colorSamples[0] < Constants.lineThreshold){
				leftWheelMotor.rotate(-350, false);
				gyroSensor.getAngleMode().fetchSample(gyroSample, 0);
			}
//			leftWheelMotor.rotate(-350, false);
			touchSensorRight.fetchSample(touchedRight, 0);
			
		}
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		robot.setStatus(Status.BARCODE_FIND);
	}

	private void moveCorrected(float speed, float factor, float distance ) {
		leftWheelMotor.setSpeed((int) (speed + (factor * distance)));
		rightWheelMotor.setSpeed((int) (speed + -(factor * distance)));
		leftWheelMotor.forward();
		rightWheelMotor.forward();
	}
}
