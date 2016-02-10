package strategy;

import lejos.robotics.RegulatedMotor;
import main.Robot;
import main.Status;

public class RaceStrategy extends Strategy {
	public static final int wheelMotorSpeed = 500;
	
	public static final int wheelMotorRotate = 190;
	public static final int wheelMotorForward = 1000;
	
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	
	public RaceStrategy(Robot robot) {
		super(robot);
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
	}
	
	
	public void execute() {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("RaceStrategy", 1, 2);

		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
		
		leftWheelMotor.rotate(wheelMotorRotate, true);
		rightWheelMotor.rotate(-wheelMotorRotate, false);
		
		leftWheelMotor.rotate(wheelMotorForward, true);
		rightWheelMotor.rotate(wheelMotorForward, false);
		
		leftWheelMotor.rotate(-wheelMotorRotate, true);
		rightWheelMotor.rotate(wheelMotorRotate, false);
		
		(new LabyrinthStrategy(robot)).execute();
		(new FindBarcodeStrategy(robot)).execute();
		
		robot.setStatus(Status.BOSS);
	}
}
