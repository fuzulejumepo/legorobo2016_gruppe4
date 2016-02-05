package strategy;

import main.*;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;


public class FollowLineStrategy extends Strategy{

	//motors
	public static final int wheelMotorSpeed = 300;
	public static final int sensorArmMotorSpeed = 500;

	//calibrate arm
	public static final int sensorArmPositionOffset = 110;
	public static final float lineThreshold = 0.2f;

	//search line
	public static final int sensorArmSearchOffset = 30;
	public static final int wheelsSearchOffset = 100;
	public static final int wheelsSearchDegree = 50;

	//follow line
	public static final int sensorArmFollowOffset = 4;
	public static final int sensorArmFollowDegree = 90;
	public static final int sensorArmFollowTurnDegree = 10;
	public static final float wheelMotorSpeedReduction = 1.18f;

	
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor armSensor;
	
	
	public FollowLineStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.armSensor = robot.colorSensor;
	}
	
	public void execute() {
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		robot.colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());

		calibrateArm();
		
		for (int i = 0; i < 1000; ++i) {
			leftWheelMotor.setSpeed(wheelMotorSpeed);
			rightWheelMotor.setSpeed(wheelMotorSpeed);
			robot.ev3.getLED().setPattern(2);
			searchLine();
			leftWheelMotor.setSpeed(wheelMotorSpeed);
			rightWheelMotor.setSpeed(wheelMotorSpeed);
			robot.ev3.getLED().setPattern(1);
			followLine();
			robot.ev3.getLED().setPattern(0);
			leftWheelMotor.startSynchronization();
			leftWheelMotor.stop();
			rightWheelMotor.stop();
			leftWheelMotor.endSynchronization();
		}
		
		leftWheelMotor.setSpeed(wheelMotorSpeed);
		rightWheelMotor.setSpeed(wheelMotorSpeed);
	}
	
	protected void calibrateArm() {
		armMotor.forward();
		while (!armMotor.isStalled());
		int leftMax = armMotor.getTachoCount();
		
		armMotor.backward();
		while (!armMotor.isStalled());
		int rightMax = armMotor.getTachoCount();
		
		int mid = (leftMax + rightMax) / 2;
		//armMotor.rotateTo(mid);
		
		robot.sensorArmMin = ((leftMax < rightMax) ? leftMax : rightMax);
		robot.sensorArmMin += sensorArmPositionOffset;
		robot.sensorArmMax = ((leftMax > rightMax) ? leftMax : rightMax);
		robot.sensorArmMin -= sensorArmPositionOffset;
		robot.sensorArmMid = mid;
	}
	
	protected void searchLine() {
		float[] sample = { 0.0f };

		int direction = 1;
		int round = 1;
		
		while (true) {
			armMotor.rotateTo(robot.sensorArmMin, true);
			while (armMotor.isMoving()) {
				armSensor.getRedMode().fetchSample(sample, 0);
				if (sample[0] > lineThreshold) {
					armMotor.stop(false);
					armMotor.rotate(sensorArmSearchOffset, false);
					return;
				}
			}

			armMotor.rotateTo(robot.sensorArmMax, true);
			while (armMotor.isMoving()) {
				armSensor.getRedMode().fetchSample(sample, 0);
				if (sample[0] > lineThreshold) {
					armMotor.stop(false);
					armMotor.rotate(sensorArmSearchOffset, false);
					return;
				}
			}
			
			leftWheelMotor.startSynchronization();
			leftWheelMotor.rotate(direction * round * wheelsSearchDegree, false);
			rightWheelMotor.rotateTo(-direction * round * wheelsSearchDegree, false);
			leftWheelMotor.endSynchronization();
			direction = -direction;
			round++;
		}
	}
	
	protected void followLine() {
		float[] sample = { 0.0f };
		int speed = wheelMotorSpeed;
		

		boolean turn = true;
		int turnDegree = armMotor.getTachoCount();
		int degreeDiff = 0;
		int direction = -1;
		
		while (true) {
			degreeDiff = Math.abs(armMotor.getTachoCount() - turnDegree);
			armSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > lineThreshold) { //above white line
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
			
			speed = (int)((float)(robot.sensorArmMid - armMotor.getTachoCount())
							* wheelMotorSpeedReduction);

			leftWheelMotor.setSpeed(wheelMotorSpeed + speed);
			rightWheelMotor.setSpeed(wheelMotorSpeed - speed);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
		}
	}
}
