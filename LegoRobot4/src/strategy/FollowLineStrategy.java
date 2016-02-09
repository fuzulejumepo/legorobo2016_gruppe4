package strategy;

import main.*;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;


public class FollowLineStrategy extends Strategy{

	//motors constants
	public static final int wheelMotorSpeed = 240;
	public static final int sensorArmMotorSpeed = 700; //800

	//search line constants
	public static final int sensorArmSearchOffset = 30;
	public static final int wheelsSearchOffset = 70;
	public static final int wheelsSearchDegree = 200;

	//follow line constants
	public static final int sensorArmFollowOffset = 2; //2
	public static final int sensorArmFollowDegree = 80; //80
	public static final int sensorArmFollowTurnDegree = 20;
	public static final float wheelMotorSpeedReduction = 1.18f; //1.18

	
	
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor colorSensor;
	
	
	public FollowLineStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.colorSensor = robot.colorSensor;
	}
	
	public void execute() {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("FollowLineStrategy", 2, 2);

		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());

		//robot.calibrateArm();
		armMotor.setSpeed(sensorArmMotorSpeed);
		
		while (true) {
			robot.ev3.getLED().setPattern(2);
			leftWheelMotor.setSpeed(wheelMotorSpeed);
			rightWheelMotor.setSpeed(wheelMotorSpeed);
			if (!searchLine())
				break;
			
			robot.ev3.getLED().setPattern(1);
			leftWheelMotor.setSpeed(wheelMotorSpeed);
			rightWheelMotor.setSpeed(wheelMotorSpeed);
			followLine();
			leftWheelMotor.stop();
			rightWheelMotor.stop();
		}
		
		robot.setStatus(Status.BARCODE_FIND);
	}


	protected boolean searchLine() {
		float[] sample = { 0.0f };

		//		for (int i = 0; i <= tries; ++i) {
		//			leftWheelMotor.rotate(direction * i * wheelsSearchDegree, true);
		//			rightWheelMotor.rotate(-direction * i * wheelsSearchDegree, false);
		//			direction = -direction
		//		}

		armMotor.rotateTo(robot.sensorArmMin, true);
		while (armMotor.isMoving()) {
			colorSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold) {
				armMotor.stop(false);
				//armMotor.rotate(sensorArmSearchOffset, false);
				return true;
			}
		}
		
		armMotor.rotateTo(robot.sensorArmMax, true);
		while (armMotor.isMoving()) {
			colorSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold) {
				armMotor.stop(false);
				//armMotor.rotate(-sensorArmSearchOffset, false);
				return true;
			}
		}
		armMotor.stop(false);
		
		
		leftWheelMotor.rotate(-wheelsSearchDegree, true);
		rightWheelMotor.rotate(wheelsSearchDegree, true);
		while (leftWheelMotor.isMoving()) {
			colorSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold) {
				leftWheelMotor.stop();
				rightWheelMotor.stop();
				return true;
			}
		}
		
		armMotor.rotateTo(robot.sensorArmMin, false);
		armMotor.stop(false);
		
		leftWheelMotor.rotate(2 * wheelsSearchDegree, true);
		rightWheelMotor.rotate(-2 * wheelsSearchDegree, true);
		while (leftWheelMotor.isMoving()) {
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
	
	protected void followLine() {
		float[] sample = { 0.0f };
		int speedOffset;
		

		boolean turn = true;
		int turnDegree = armMotor.getTachoCount();
		int degreeDiff = 0;
		int direction = -1;
		
		while (true) {
			degreeDiff = Math.abs(armMotor.getTachoCount() - turnDegree);
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
					armMotor.getTachoCount() < robot.sensorArmMin ||
					armMotor.getTachoCount() > robot.sensorArmMax)
				return;
			
			armMotor.rotate(direction * sensorArmFollowOffset, true);
			
			speedOffset = (int)((float)(robot.sensorArmMid - armMotor.getTachoCount())
							* wheelMotorSpeedReduction);

			leftWheelMotor.setSpeed(wheelMotorSpeed + speedOffset);
			rightWheelMotor.setSpeed(wheelMotorSpeed - speedOffset);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
		}
	}
}
