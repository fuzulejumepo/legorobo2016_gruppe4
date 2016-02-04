package main;

public final class Constants {

	//motors
	public static final int wheelMotorSpeed = 700;
	public static final int wheelMotorAcceleration = 6000;
	public static final int sensorArmMotorAcceleration = 6000;

	//calibrate arm
	public static final int sensorArmOffsetMax = 130;
	public static final float lineThreshold = 0.2f;
	
	//search line
	public static final int sensorArmReverseOffset = 20;
	
	//follow line
	public static final int sensorArmFollowOffset = 5;
	public static final int sensorArmFollowTries = 400;
	//public static final int wheelMotorFollowOffset = 1;
	public static final float wheelMotorSpeedReduction = 16.0f;

}
