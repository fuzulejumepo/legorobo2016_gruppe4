package main;

public final class Constants {

	//motors
	public static final int wheelMotorSpeed = 300;
	public static final int wheelMotorAcceleration = 6000;
	public static final int sensorArmMotorSpeed = 500;
	public static final int sensorArmMotorAcceleration = 6000;

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
	
}
