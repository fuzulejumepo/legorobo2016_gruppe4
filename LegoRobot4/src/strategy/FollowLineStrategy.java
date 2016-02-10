package strategy;

import main.*;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;


public class FollowLineStrategy extends Strategy{

	//motors constants
	public static final int wheelMotorFollowSpeed = 300;
	public static final int wheelMotorCorrectionSpeed = 120;
	public static final int sensorArmMotorSpeed = 500; //800

	//search line constants
	//public static final int sensorArmSearchOffset = 30;
	//public static final int wheelsSearchOffset = 70;
	public static final int wheelsSearchDegree = 200;
	
	//correct position constants
	public static final int sensorArmCorrectionDegree = 10;
	public static final int wheelsCorrectionDegree = 100;

	//follow line constants
	public static final int sensorArmFollowOffset = 3; //2
	//public static final int sensorArmFollowDegree = 74; //80
	public static final int sensorArmFollowTurnDegree = 14;
	public static final int wheelFollowDegree = 300;
	public static final float wheelMotorSpeedReduction = 1.2f; //1.18

	
	
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor colorSensor;
	
	protected int direction = 1;

	
	
	
	public FollowLineStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.colorSensor = robot.colorSensor;
	}
	
	
	
	public void execute() {
		robot.ev3.getTextLCD().drawString("FollowLineStrategy", 2, 2);

		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());

		robot.centerArm();
		armMotor.setSpeed(sensorArmMotorSpeed);
		
		//robot.ev3.getLED().setPattern(2);
		//searchLine();
		
		while (true) {
			leftWheelMotor.setSpeed(wheelMotorCorrectionSpeed);
			rightWheelMotor.setSpeed(wheelMotorCorrectionSpeed);
			
			robot.ev3.getLED().setPattern(3);
			if (!correctPosition()) {
				robot.ev3.getLED().setPattern(2);
				if (!searchLine())
					return;
			}
			
			robot.ev3.getLED().setPattern(1);
			followLine();
		}
		
		//robot.setStatus(Status.BARCODE_FIND);
	}


	protected boolean searchLine() {		
		float[] sample = { 0.0f };
		
		//robot.centerArm();
		//armMotor.stop(false);
		colorSensor.getRedMode().fetchSample(sample, 0);
		if (sample[0] > Constants.lineThreshold)
			return true;
		
		
		armMotor.rotateTo(robot.sensorArmMax, false);
		direction = -1;
		leftWheelMotor.rotate(-wheelsSearchDegree, true);
		rightWheelMotor.rotate(wheelsSearchDegree, true);
		while (rightWheelMotor.isMoving()) {
			colorSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold) {
				leftWheelMotor.stop();
				rightWheelMotor.stop();
				return true;
			}
		}
		
		armMotor.rotateTo(robot.sensorArmMin, false);
		direction = 1;
		leftWheelMotor.rotate(2 * wheelsSearchDegree, true);
		rightWheelMotor.rotate(-2 * wheelsSearchDegree, true);
		while (rightWheelMotor.isMoving()) {
			colorSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold) {
				leftWheelMotor.stop();
				rightWheelMotor.stop();
				return true;
			}
		}

		leftWheelMotor.rotate(-wheelsSearchDegree, true);
		rightWheelMotor.rotate(wheelsSearchDegree, false);
		
		return false;
	}

	
	protected boolean correctPosition() {
		float[] sample = { 0.0f };
		
//		int currentArmPos = armMotor.getTachoCount();

//		if (currentArmPos < robot.sensorArmMin + sensorArmCorrectionDegree ||
//				currentArmPos > robot.sensorArmMax - sensorArmCorrectionDegree) {
//			leftWheelMotor.rotate(direction * wheelsCorrectionDegree, true);
//			rightWheelMotor.rotate(-direction * wheelsCorrectionDegree, true);
//			while (rightWheelMotor.isMoving()) {
//				colorSensor.getRedMode().fetchSample(sample, 0);
//				if (sample[0] > Constants.lineThreshold) {
//					leftWheelMotor.stop();
//					rightWheelMotor.stop();
//					return true;
//				}
//			}
//			leftWheelMotor.rotate(-direction * wheelsCorrectionDegree, true);
//			rightWheelMotor.rotate(direction * wheelsCorrectionDegree, false);
//		} 
		for (int i = 0; i < 2; ++i) {
			direction = -direction;
			int targetArmPos = (direction > 0) ? 
					robot.sensorArmMin :
					robot.sensorArmMax;
			armMotor.rotateTo(targetArmPos, true);
			while (armMotor.isMoving()) {
				colorSensor.getRedMode().fetchSample(sample, 0);
				if (sample[0] > Constants.lineThreshold) {
					armMotor.stop(false);
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	protected void followLine() {
		float[] sample = { 0.0f };
		int speedOffset;
		

		boolean turn = false;
		int lastLineLeftWheelPos = leftWheelMotor.getTachoCount();
		int lastLineRightWheelPos = rightWheelMotor.getTachoCount();
		int leftWheelPosDiff;
		int rightWheelPosDiff;
		int currentArmDegree;
		int turnArmDegree = armMotor.getTachoCount();
		int armDegreeDiff;
		
		while (true) {
			currentArmDegree = armMotor.getTachoCount();
			armDegreeDiff = Math.abs(currentArmDegree - turnArmDegree);
			colorSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold) { //above white line
				lastLineLeftWheelPos = leftWheelMotor.getTachoCount();
				lastLineRightWheelPos = rightWheelMotor.getTachoCount();
				if (armDegreeDiff > sensorArmFollowTurnDegree)
					turn = false;
			} else { //above black surface
				if (!turn) {
					turn = true;
					direction = -direction;
					turnArmDegree = currentArmDegree;
				}
			}
			
			leftWheelPosDiff = Math.abs(leftWheelMotor.getTachoCount() - lastLineLeftWheelPos);
			rightWheelPosDiff = Math.abs(rightWheelMotor.getTachoCount() - lastLineRightWheelPos);
			if (leftWheelPosDiff + rightWheelPosDiff > wheelFollowDegree) {
				leftWheelMotor.stop(true);
				rightWheelMotor.stop(true);
				armMotor.stop(false);
				return;
			}

//			if (degreeDiff > sensorArmFollowDegree ||
//					currentArmDegree <= robot.sensorArmMin + 4 ||
//					currentArmDegree >= robot.sensorArmMax - 4) {
//				leftWheelMotor.stop(true);
//				rightWheelMotor.stop(true);
//				armMotor.stop(false);
//				return;
//			}
			
//			if (currentArmDegree <= robot.sensorArmMin + sensorArmCorrectionDegree)
//				rightWheelMotor.stop(true);
//			if (currentArmDegree >= robot.sensorArmMax - sensorArmCorrectionDegree)
//				leftWheelMotor.stop(true);
			
			
			armMotor.stop(true);
			//armMotor.rotate(-direction * sensorArmFollowOffset, true);
			if (direction < 0)
				armMotor.rotateTo(robot.sensorArmMin, true);
			else
				armMotor.rotateTo(robot.sensorArmMax, true);
			
			speedOffset = (int)((float)(robot.sensorArmMid - currentArmDegree)
								* wheelMotorSpeedReduction);

			leftWheelMotor.setSpeed(wheelMotorFollowSpeed + speedOffset);
			rightWheelMotor.setSpeed(wheelMotorFollowSpeed - speedOffset);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
		}
	}
}
