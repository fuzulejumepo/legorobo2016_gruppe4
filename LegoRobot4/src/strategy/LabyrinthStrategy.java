package strategy;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.Delay;
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
	
public static final int wheelMotorAdjustSpeed = 500;
	public static final int moveWheelEnterBridge = 200; //700;
	public static final int moveWheelCorrection = 150; //600;
	public static final int wheelCorrectionFactor = 1800;
	


	static int speed = 400;
	static int factor = 600; // 1150
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
		maxFactor = maxSpeed * 3.5f;
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
				if (distance < 0.02 && touchedRight[0]!= 1.0&& colorSamples[0] < Constants.lineThreshold){
					while(distance<0.02 && touchedRight[0]!= 1.0 && colorSamples[0] < Constants.lineThreshold){
					moveCorrected(maxSpeed,maxFactor,distance);
					ultra.fetchSample(distances, 0);
					distance = distances[0] - 0.07f;
					touchSensorRight.fetchSample(touchedRight, 0);
					}
					while(distance>0.4&& colorSamples[0] < Constants.lineThreshold){
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
			leftWheelMotor.rotate(-350, false);
			touchSensorRight.fetchSample(touchedRight, 0);
			
		}
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		adjustInFrontOfBarcode();
		
		robot.setStatus(Status.BARCODE_FIND);
	}

	private void moveCorrected(float speed, float factor, float distance ) {
		leftWheelMotor.setSpeed((int) (speed + (factor * distance)));
		rightWheelMotor.setSpeed((int) (speed + -(factor * distance)));
		leftWheelMotor.forward();
		rightWheelMotor.forward();
	}
	
	protected void adjustInFrontOfBarcode() {
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
		
		leftWheelMotor.backward();
		rightWheelMotor.backward();
		
		Delay.msDelay(1000);
	}
}
