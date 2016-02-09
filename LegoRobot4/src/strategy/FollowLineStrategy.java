package strategy;

import main.*;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;


public class FollowLineStrategy extends Strategy{

	//motors constants
	public static final int wheelMotorSpeed = 250;
	public static final int sensorArmMotorSpeed = 400; //800

	//search line constants
	//public static final int sensorArmSearchOffset = 30;
	//public static final int wheelsSearchOffset = 70;
	public static final int wheelsSearchDegree = 230;
	
	//correct position constants
	//public static final int correctionFactor = 2;
	public static final int sensorArmCorrectionDegree = 40;
	public static final int wheelsCorrectionDegree = 130;

	//follow line constants
	public static final int sensorArmFollowOffset = 2; //2
	public static final int sensorArmFollowDegree = 90; //80
	public static final int sensorArmFollowTurnDegree = 10;
	public static final float wheelMotorSpeedReduction = 1.18f; //1.18

	
	
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

		armMotor.setSpeed(sensorArmMotorSpeed);
		
		robot.ev3.getLED().setPattern(2);
		searchLine();
		
		while (true) {
			robot.ev3.getLED().setPattern(1);
			followLine();
			
			leftWheelMotor.setSpeed(wheelMotorSpeed);
			rightWheelMotor.setSpeed(wheelMotorSpeed);
			
			robot.ev3.getLED().setPattern(3);
			if (!correctPosition()) {
				robot.ev3.getLED().setPattern(2);
				if (!searchLine())
					break;
			}
		}
		
		robot.setStatus(Status.BARCODE_FIND);
	}


	protected boolean searchLine() {		
		float[] sample = { 0.0f };

//		direction = 1;
//		int minPos = Math.min(robot.sensorArmMin + 2 * sensorArmFollowDegree, 
//								armMotor.getTachoCount());
//		armMotor.rotateTo(minPos, true);
//		while (armMotor.isMoving()) {
//			colorSensor.getRedMode().fetchSample(sample, 0);
//			if (sample[0] > Constants.lineThreshold) {
//				armMotor.stop();
//				return true;
//			}
//		}
//		
//		direction = -1;
//		int maxPos = Math.max(robot.sensorArmMax - 2 * sensorArmFollowDegree, 
//								armMotor.getTachoCount());
//		armMotor.rotateTo(maxPos, true);
//		while (armMotor.isMoving()) {
//			colorSensor.getRedMode().fetchSample(sample, 0);
//			if (sample[0] > Constants.lineThreshold) {
//				armMotor.stop();
//				return true;
//			}
//		}
//		armMotor.stop();
		
		robot.centerArm();
		colorSensor.getRedMode().fetchSample(sample, 0);
		if (sample[0] > Constants.lineThreshold)
			return true;
		
		
		direction = -1; //!!!
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
		
//		armMotor.rotateTo(robot.sensorArmMin + sensorArmFollowDegree, false);
		
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

		leftWheelMotor.startSynchronization();
		leftWheelMotor.rotate(-wheelsSearchDegree);
		rightWheelMotor.rotate(wheelsSearchDegree);
		leftWheelMotor.endSynchronization();
		
		return false;
	}

	protected boolean correctPosition() {
		float[] sample = { 0.0f };

		if (armMotor.getTachoCount() < robot.sensorArmMin + sensorArmCorrectionDegree ||
				armMotor.getTachoCount() > robot.sensorArmMax - sensorArmCorrectionDegree) {
			leftWheelMotor.rotate(direction * wheelsCorrectionDegree, true);
			rightWheelMotor.rotate(-direction * wheelsCorrectionDegree, true);
			while (rightWheelMotor.isMoving()) {
				colorSensor.getRedMode().fetchSample(sample, 0);
				if (sample[0] > Constants.lineThreshold) {
					leftWheelMotor.stop();
					rightWheelMotor.stop();
					return true;
				}
			}
			leftWheelMotor.rotate(-direction * wheelsCorrectionDegree, true);
			rightWheelMotor.rotate(direction * wheelsCorrectionDegree, false);
		} 
		for (int i = 0; i < 2; ++i) {
			direction = -direction;
			int targetArmPos = (direction > 0) ? 
					robot.sensorArmMin :
					robot.sensorArmMax;
			armMotor.rotateTo(targetArmPos, true);
			while (armMotor.isMoving()) {
				colorSensor.getRedMode().fetchSample(sample, 0);
				if (sample[0] > Constants.lineThreshold) {
					armMotor.stop();
					return true;
				}
			}
		}

//			direction = 1;
//			armMotor.rotateTo(robot.sensorArmMin, true);
//			while (armMotor.isMoving()) {
//				colorSensor.getRedMode().fetchSample(sample, 0);
//				if (sample[0] > Constants.lineThreshold) {
//					armMotor.stop();
//					return true;
//				}
//			}
//			
//			direction = -1;
//			armMotor.rotateTo(robot.sensorArmMax, true);
//			while (armMotor.isMoving()) {
//				colorSensor.getRedMode().fetchSample(sample, 0);
//				if (sample[0] > Constants.lineThreshold) {
//					armMotor.stop();
//					return true;
//				}
//			}
		
		return false;
	}
	
	protected void followLine() {
		float[] sample = { 0.0f };
		int speedOffset;
		

		boolean turn = false;
		int turnDegree = armMotor.getTachoCount();
		int degreeDiff = 0;
		int currentDegree;
		
		while (true) {
			currentDegree = armMotor.getTachoCount();
			degreeDiff = Math.abs(currentDegree - turnDegree);
			colorSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold) { //above white line
				if (degreeDiff > sensorArmFollowTurnDegree)
					turn = false;
			} else { //above black surface
				if (!turn) {
					turn = true;
					direction = -direction;
					turnDegree = armMotor.getTachoCount();
				}
			}

			if (degreeDiff > sensorArmFollowDegree ||
					currentDegree <= robot.sensorArmMin + 10 ||
					currentDegree >= robot.sensorArmMax - 10) {
				leftWheelMotor.stop(true);
				rightWheelMotor.stop(true);
				armMotor.stop(false);
				return;
			}
			
			//armMotor.stop(true);
			armMotor.rotate(-direction * sensorArmFollowOffset, true);
			
			speedOffset = (int)((float)(robot.sensorArmMid - armMotor.getTachoCount())
							* wheelMotorSpeedReduction);

			leftWheelMotor.setSpeed(wheelMotorSpeed + speedOffset);
			rightWheelMotor.setSpeed(wheelMotorSpeed - speedOffset);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
		}
	}
}
