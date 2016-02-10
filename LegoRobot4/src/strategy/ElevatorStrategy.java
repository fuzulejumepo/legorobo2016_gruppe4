package strategy;

import java.io.IOException;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import main.*;
import edu.kit.mindstorms.communication.*;


public class ElevatorStrategy extends Strategy {
	
	//motors constants
	public static final int wheelMotorSpeed = 400;
	
	//enter elevator constants
	public static final float elevatorThreshold = 0.15f;
	public static final float enterElevatorDist = 0.2f;
	public static final int enterCorrectionFactor = 1200;

	
	
	
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor colorSensor;
	protected EV3TouchSensor touchSensor;
	protected EV3UltrasonicSensor ultraSensor;

	
	public ElevatorStrategy(Robot robot) {
		super(robot);
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.colorSensor = robot.colorSensor;
		this.touchSensor = robot.bumperRightSensor;
		this.ultraSensor = robot.ultraSensor;
	}

	public void execute () {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("ElevatorStrategy", 1, 2);
		
		ComModule comModule = Communication.getModule();
		
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		colorSensor.setCurrentMode(colorSensor.getAmbientMode().getName());
		
		waitForElevatorTicket(comModule);
		moveElevatorUp(comModule);
		waitForUp();
		enterElevator();
		moveElevatorDown(comModule);
		waitForDown();
		leaveElevator();
		
		robot.setStatus(Status.FOLLOW_LINE);
	}
	
	protected void waitForUp() {
		float[] colorSample = { 0.0f };
		
		robot.calibrateArm();
		robot.centerArm();

		while (colorSample[0] < elevatorThreshold)
			colorSensor.getAmbientMode().fetchSample(colorSample, 0);
	}
	
	protected void enterElevator() {
		float[] touchSample = { 0.0f };
		float[] distSample = { 0.0f };
		int speedOffset;
		
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		
		while (touchSample[0] < 1.0f) {
			ultraSensor.fetchSample(distSample, 0);
			speedOffset = (int) ((enterElevatorDist - distSample[0])
									* enterCorrectionFactor);
			leftWheelMotor.setSpeed(wheelMotorSpeed + speedOffset);
			rightWheelMotor.setSpeed(wheelMotorSpeed - speedOffset);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
			touchSensor.fetchSample(touchSample, 0);
		}
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
	}
	
	protected void waitForDown() {
		Delay.msDelay(7000);
	}
	
	protected void leaveElevator() {
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		leftWheelMotor.rotate(1200, true);
		rightWheelMotor.rotate(1200, false);
	}
	
	protected void waitForElevatorTicket(ComModule comModule) {
		boolean response = false;
		while (!response) {
			try {
				response = comModule.requestStatus();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Delay.msDelay(100);
		}
	}
	
	protected void moveElevatorUp(ComModule comModule) {
		try {
			comModule.requestElevator();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void moveElevatorDown(ComModule comModule) {
		try {
			comModule.moveElevatorDown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
