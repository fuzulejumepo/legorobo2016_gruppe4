package strategy;

import main.*;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;


public class FollowLineStrategy extends Strategy{
	
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
	
	public Status execute() {
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		robot.colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());

		calibrateArm();
		
		for (int i = 0; i < 1000; ++i) {
			leftWheelMotor.setSpeed(Constants.wheelMotorSpeed);
			rightWheelMotor.setSpeed(Constants.wheelMotorSpeed);
			robot.ev3.getLED().setPattern(2);
			searchLine();
			robot.ev3.getLED().setPattern(1);
			followLine();
			robot.ev3.getLED().setPattern(0);
			leftWheelMotor.startSynchronization();
			leftWheelMotor.stop();
			rightWheelMotor.stop();
			leftWheelMotor.endSynchronization();
		}
		
		leftWheelMotor.setSpeed(Constants.wheelMotorSpeed);
		rightWheelMotor.setSpeed(Constants.wheelMotorSpeed);
		
		return Status.SUCCESS;
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
		robot.sensorArmMin += Constants.sensorArmPositionOffset;
		robot.sensorArmMax = ((leftMax > rightMax) ? leftMax : rightMax);
		robot.sensorArmMin -= Constants.sensorArmPositionOffset;
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
				if (sample[0] > Constants.lineThreshold) {
					armMotor.stop(false);
					armMotor.rotate(Constants.sensorArmSearchOffset, false);
					return;
				}
			}

			armMotor.rotateTo(robot.sensorArmMax, true);
			while (armMotor.isMoving()) {
				armSensor.getRedMode().fetchSample(sample, 0);
				if (sample[0] > Constants.lineThreshold) {
					armMotor.stop(false);
					armMotor.rotate(Constants.sensorArmSearchOffset, false);
					return;
				}
			}
			
			leftWheelMotor.startSynchronization();
			leftWheelMotor.rotate(direction * round * Constants.wheelsSearchDegree, false);
			rightWheelMotor.rotateTo(-direction * round * Constants.wheelsSearchDegree, false);
			leftWheelMotor.endSynchronization();
			direction = -direction;
			round++;
		}
	}
	
	protected void followLine() {
		float[] sample = { 0.0f };
		int speed = Constants.wheelMotorSpeed;
		

		boolean turn = true;
		int turnDegree = armMotor.getTachoCount();
		int degreeDiff = 0;
		int direction = -1;
		
		while (true) {
			degreeDiff = Math.abs(armMotor.getTachoCount() - turnDegree);
			armSensor.getRedMode().fetchSample(sample, 0);
			if (sample[0] > Constants.lineThreshold) { //above white line
				if (degreeDiff > Constants.sensorArmFollowTurnDegree)
					turn = false;
			} else { //above black surface
				if (!turn) {
					turn = true;
					direction = -direction;
					turnDegree = armMotor.getTachoCount();
				}
			}
			if (degreeDiff > Constants.sensorArmFollowDegree ||
					armMotor.getTachoCount() < robot.sensorArmMin ||
					armMotor.getTachoCount() > robot.sensorArmMax)
				return;
			
			armMotor.rotate(direction * Constants.sensorArmFollowOffset, true);
			
			speed = (int)((float)(robot.sensorArmMid - armMotor.getTachoCount())
							* Constants.wheelMotorSpeedReduction);

			leftWheelMotor.setSpeed(Constants.wheelMotorSpeed + speed);
			rightWheelMotor.setSpeed(Constants.wheelMotorSpeed - speed);
			leftWheelMotor.forward();
			rightWheelMotor.forward();
		}
	}
}
