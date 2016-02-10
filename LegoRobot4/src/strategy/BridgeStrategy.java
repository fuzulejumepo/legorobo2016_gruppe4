package strategy;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import main.Robot;
import main.Status;

public class BridgeStrategy extends Strategy {
	
	//motors constants
	public static final int wheelMotorSpeed = 400;
	public static final int sensorArmMotorSpeed = 500;
	
	//find edge constants
	public static final int findEdgeWheelDegree = 30;
	
	//follow edge constants
	public static final int followEdgeWheelDegree = 50;

	//park constants
	public static final int wheelMotorParkSpeed = 100;
	public static final float parkDist = 0.2f;
	
	
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor colorSensor;
	protected EV3TouchSensor touchSensor;
	protected EV3UltrasonicSensor ultraSensor;
	
	
	public BridgeStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.colorSensor = robot.colorSensor;
		this.touchSensor = robot.bumperRightSensor;
		this.ultraSensor = robot.ultraSensor;
	}
	
	public void execute() {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("BridgeStrategy", 1, 2);
		
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		colorSensor.setCurrentMode(colorSensor.getColorIDMode().getName());
		
		armMotor.backward();
		robot.ev3.getLED().setPattern(2);
		findEdge();
		robot.ev3.getLED().setPattern(1);
		followEdge();
		armMotor.stop();
		robot.ev3.getLED().setPattern(3);
		parkInFrontOfElevator();
		robot.ev3.getLED().setPattern(0);
		
		leftWheelMotor.startSynchronization();
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		leftWheelMotor.endSynchronization();
		
		robot.setStatus(Status.ELEVATOR);
	}
	
	protected void findEdge() {
		leftWheelMotor.setSpeed(wheelMotorSpeed + findEdgeWheelDegree);
		rightWheelMotor.setSpeed(wheelMotorSpeed - findEdgeWheelDegree);
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		
		while (colorSensor.getColorID() >= 0);
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
	}
	
	protected void followEdge() {
		int direction;
		
		float[] touchSample = { 0.0f };
		
		while (touchSample[0] < 1.0f) {
			touchSensor.fetchSample(touchSample, 0);
			
			direction = (colorSensor.getColorID() < 0) ? -1 : 1;
			
			leftWheelMotor.setSpeed(wheelMotorSpeed + direction * followEdgeWheelDegree);
			rightWheelMotor.setSpeed(wheelMotorSpeed - direction * followEdgeWheelDegree);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
		}
		leftWheelMotor.stop();
		rightWheelMotor.stop();
	}
	
	protected void parkInFrontOfElevator() {
		float[] ultraSample = { 1.0f };
		
		robot.centerArm();

		leftWheelMotor.setSpeed(wheelMotorParkSpeed);
		rightWheelMotor.setSpeed(wheelMotorParkSpeed);
		
		leftWheelMotor.backward();
		rightWheelMotor.forward();
		while (ultraSample[0] > parkDist)
			ultraSensor.fetchSample(ultraSample, 0);
		leftWheelMotor.stop();
		rightWheelMotor.stop();
	}

}
