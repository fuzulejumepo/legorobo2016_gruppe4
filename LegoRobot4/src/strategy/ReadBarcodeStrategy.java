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
		robot.ev3.getTextLCD().drawString("ReadBarcodeStrategy", 2, 2);
		
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		robot.colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());
		robot.centerArm();
		
		int countLines;
		for (countLines = 0; countLines < 6; ++countLines) {
			if (!readNextBar())
				break;
			Delay.msDelay(2);
		}
		
		
		robot.ev3.getTextLCD().drawInt(countLines, 5, 5);
		
		switch (countLines) {
			case 1: 
				robot.setStatus(Status.SWAMP);
				break;
			case 2:
				robot.setStatus(Status.LINE);
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
				robot.setStatus(Status.FINISH);
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
