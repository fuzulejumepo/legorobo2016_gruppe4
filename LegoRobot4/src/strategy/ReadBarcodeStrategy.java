package strategy;

import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import main.Constants;
import main.Robot;
import main.Status;

public class ReadBarcodeStrategy extends Strategy {
		
	public static final int wheelMotorBarRotate = 178;
	public static final int WheelMotorMoveForward = 260;
	
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor colorSensor;
	
	
	public ReadBarcodeStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.colorSensor = robot.colorSensor;
	}

	@Override
	public void execute() {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("ReadBarcodeStrategy", 1, 2);
		
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		robot.colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());
		
		robot.ev3.getLED().setPattern(4);
		robot.centerArm();
		
		int countLines;
		for (countLines = 1; countLines <= 5; ++countLines) {
			if (!readNextBar())
				break;
		}
		
		robot.ev3.getTextLCD().drawString("bars=" + countLines, 2, 5);
		
		leftWheelMotor.rotate(WheelMotorMoveForward, true);
		rightWheelMotor.rotate(WheelMotorMoveForward, false);
		
		robot.ev3.getLED().setPattern(0);
		
		switch (countLines) {
			case 1: 
				robot.setStatus(Status.SWAMP);
				break;
			case 2:
				robot.setStatus(Status.FOLLOW_LINE);
				break;
			case 3:
				robot.setStatus(Status.BRIDGE);
				break;
			case 4:
				robot.setStatus(Status.SEESAW);
				break;
			case 5: 
				robot.setStatus(Status.SUSPENSE);
				break;
			case 6:
				robot.setStatus(Status.RACE);
				break;
			default:
				robot.setStatus(Status.ERROR);
		}
		
	}
	
	protected boolean readNextBar() {
		float[] sample = { 0.0f };
		boolean foundLine = false;
		
		leftWheelMotor.rotate(wheelMotorBarRotate, true);
		rightWheelMotor.rotate(wheelMotorBarRotate, true);
		
		while (rightWheelMotor.isMoving()) {
			colorSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold)
				foundLine = true;
		}
		
		return foundLine;
	}
}
