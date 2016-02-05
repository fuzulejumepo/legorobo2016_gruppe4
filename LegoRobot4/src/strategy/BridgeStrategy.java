package strategy;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import main.Robot;

public class BridgeStrategy extends Strategy {
	
	//motors constants
	public static final int wheelMotorSpeed = 500;
	public static final int sensorArmMotorSpeed = 500;
	
	//find edge constants
	public static final int findEdgeWheelDegree = 30;
	
	//follow edge constants
	public static final int followEdgeWheelDegree = 50;
	
	
	
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor armSensor;
	
	
	public BridgeStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.armSensor = robot.colorSensor;
	}
	
	public void execute() {
		robot.ev3.getTextLCD().drawString("BridgeStrategy", 2, 2);
		
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		armSensor.setCurrentMode(armSensor.getColorIDMode().getName());
		
		armMotor.backward();
		robot.ev3.getLED().setPattern(2);
		findEdge();
		robot.ev3.getLED().setPattern(1);
		followEdge();
		robot.ev3.getLED().setPattern(0);
		leftWheelMotor.startSynchronization();
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		leftWheelMotor.endSynchronization();
	}
	
	protected void findEdge() {
		leftWheelMotor.setSpeed(wheelMotorSpeed + findEdgeWheelDegree);
		rightWheelMotor.setSpeed(wheelMotorSpeed - findEdgeWheelDegree);
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		
		while (armSensor.getColorID() >= 0);
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
	}
	
	protected void followEdge() {
		int direction;
		
		while (true) {
			direction = (armSensor.getColorID() < 0) ? -1 : 1;
			
			leftWheelMotor.setSpeed(wheelMotorSpeed + direction * followEdgeWheelDegree);
			rightWheelMotor.setSpeed(wheelMotorSpeed - direction * followEdgeWheelDegree);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
		}
	}

}
