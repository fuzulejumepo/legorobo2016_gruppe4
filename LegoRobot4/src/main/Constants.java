package main;

public final class Constants {

	//motors
	public static final int wheelMotorAcceleration = 6000;
	public static final int sensorArmMotorAcceleration = 6000;

	//calibrate arm
	public static final int sensorArmOffsetMax = 130;
	
	//search arm
	public static final int sensorArmReverseOffset = 10;
	
	//follow arm
	public static final int sensorArmFollowOffset = 1;
	public static final int sensorArmFollowTries = 9000;
	
	//follow wheel
	public static final int wheelMotorFollowOffset = 20;
}
