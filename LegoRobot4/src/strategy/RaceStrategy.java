package strategy;

import lejos.robotics.RegulatedMotor;
import main.Robot;
import main.Status;

public class RaceStrategy extends Strategy {
	public static final int wheelSpeed = 500;
	
	public static final int wheelRotate = 190;
	public static final int wheelForward = 1200;
	
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
		
		leftWheelMotor.setSpeed(wheelSpeed);
		rightWheelMotor.setSpeed(wheelSpeed);
		
		leftWheelMotor.rotate(wheelRotate, true);
		rightWheelMotor.rotate(-wheelRotate, false);
		
		leftWheelMotor.rotate(wheelForward, true);
		rightWheelMotor.rotate(wheelForward, false);
		
		leftWheelMotor.rotate(-wheelRotate, true);
		rightWheelMotor.rotate(wheelRotate, false);
		
		robot.setStatus(Status.LABYRINTH);
	}
}
