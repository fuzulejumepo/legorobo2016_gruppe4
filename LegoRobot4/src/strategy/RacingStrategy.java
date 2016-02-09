package strategy;

import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import main.*;

public class RacingStrategy extends Strategy{
	private static final double DistanceToWall1 = 0.4;
	private static final double DistanceToWall2 = 0.3;
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	// protected EV3TouchSensor touchSensorLeft = robot.bumperLeftSensor;
	// touchedLeft[0] != 1.0 &&
	protected EV3TouchSensor touchSensorRight;
	protected EV3UltrasonicSensor ultraSensor;
	protected SampleProvider ultra;
	protected EV3GyroSensor gyroSensor;
	static int speed = 620;
	static int speedSAVE = 400;
	static int factorSAVE = 600;
	static int factor = 800;
	static int backRotation = 170;
	static float maxSpeed;
	

	public RacingStrategy(Robot robot) {
		super(robot);

		this.touchSensorRight = robot.bumperRightSensor;
		this.ultraSensor = robot.ultraSensor;
		this.ultra = ultraSensor.getMode("Distance");
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.gyroSensor=robot.gyroSensor;

	}

	@Override
	public void execute() {
		maxSpeed =Math.min(leftWheelMotor.getMaxSpeed(), rightWheelMotor.getMaxSpeed()) -100.0f;
		// float[] touchedLeft = new float[touchSensorLeft.sampleSize()];
		float[] touchedRight = new float[touchSensorRight.sampleSize()];
		float[] distances = new float[ultraSensor.sampleSize()];
		float[] gyroDirection = { 0.0f };
		ultra.fetchSample(distances, 0);
		leftWheelMotor.setSpeed((int) leftWheelMotor.getMaxSpeed());
		leftWheelMotor.forward();
		rightWheelMotor.setSpeed((int) rightWheelMotor.getMaxSpeed());
		rightWheelMotor.forward();
		robot.ev3.getTextLCD().drawString("Initilaized", 2, 2);
		leftWheelMotor.startSynchronization();
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});

		
		/*
		 * Run the first straight on part
		 */
		while (distances[0] < 0.7) {
			ultra.fetchSample(distances, 0);
			double distance = distances[0] - DistanceToWall1;
			robot.ev3.getTextLCD().drawString("current " + distance, 3, 3);
			leftWheelMotor.setSpeed((int) (maxSpeed + (factor * distance)));
			rightWheelMotor.setSpeed((int) (maxSpeed -(factor * distance)));
			leftWheelMotor.forward();
			rightWheelMotor.forward();
			robot.ev3.getTextLCD().drawString("Running", 2, 2);
		}
		
		gyroSensor.getAngleMode().fetchSample(gyroDirection, 0);
		robot.ev3.getTextLCD().drawString("start " + gyroDirection[0], 3, 3);
		float gyroStart = gyroDirection[0];

		/*
		 * turn around
		 */
		
		leftWheelMotor.resetTachoCount();
		while(Math.abs(leftWheelMotor.getTachoCount()) < Math.abs(10)){
			leftWheelMotor.forward();
			rightWheelMotor.forward();
		}
		robot.ev3.getTextLCD().clear(); 
		robot.ev3.getTextLCD().drawString("Turn", 2, 2);

		while (Math.abs(gyroDirection[0]) < (Math.abs(gyroStart - 90.0f))){
			leftWheelMotor.rotate(10,true);
			rightWheelMotor.rotate(80,false);
			gyroSensor.getAngleMode().fetchSample(gyroDirection, 0);
			robot.ev3.getTextLCD().clear();
			robot.ev3.getTextLCD().drawString("current " + gyroDirection[0], 3, 3);
		}
		gyroSensor.reset();
		gyroSensor.getAngleMode().fetchSample(gyroDirection, 0);
		float gyroInit = gyroDirection[0];
		touchSensorRight.fetchSample(touchedRight, 0);
		robot.ev3.getTextLCD().drawString("value " + gyroDirection[0], 3, 3);
	
		while (touchedRight[0 ]!= 1.0) {
			touchSensorRight.fetchSample(touchedRight, 0);
			gyroSensor.getAngleMode().fetchSample(gyroDirection, 0);
			float gyroDiff = gyroInit - gyroDirection[0];
			robot.ev3.getTextLCD().drawString("current " + gyroDiff, 3, 3);
			leftWheelMotor.setSpeed((int) (leftWheelMotor.getMaxSpeed()-100 - (factor * gyroDiff)));
			rightWheelMotor.setSpeed((int) (rightWheelMotor.getMaxSpeed()-100 +(factor * gyroDiff)));
			leftWheelMotor.forward();
			rightWheelMotor.forward();
			robot.ev3.getTextLCD().drawString("Forwards", 2, 2);
		}
//		while (distances[0] > 0.2) {
//			ultra.fetchSample(distances, 0);
//			leftWheelMotor.setSpeed(speed);
//			rightWheelMotor.setSpeed(speed);
//			leftWheelMotor.backward();
//			rightWheelMotor.backward();
//		}
}
}
