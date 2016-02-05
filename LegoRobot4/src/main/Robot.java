package main;

import lejos.hardware.ev3.EV3;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.RegulatedMotor;

public class Robot {
	//EV3 device
	public final EV3 ev3 = LocalEV3.get();
		
	//Motors
	public final RegulatedMotor leftWheelMotor = Motor.A;
	public final RegulatedMotor rightWheelMotor = Motor.B;
	public final RegulatedMotor sensorArmMotor = Motor.C;
	
	public static final int sensorArmPositionOffset = 110;

	public final int maxSpeedWheel;
	public final int maxSpeedArm;
	
	public int sensorArmMin;
	public int sensorArmMax;
	public int sensorArmMid;
	
	
	//Sensors
	private static final Port colorSensorPort = SensorPort.S1;
	private static final Port gyroSensorPort = SensorPort.S3;
	
	public final EV3ColorSensor colorSensor = new EV3ColorSensor(colorSensorPort);
	public final EV3GyroSensor gyroSensor = new EV3GyroSensor(gyroSensorPort);

	
	
	
	
	public Robot() {
		maxSpeedWheel = Math.max((int) leftWheelMotor.getMaxSpeed(),
								(int) rightWheelMotor.getMaxSpeed());
		leftWheelMotor.setSpeed(Constants.stdWheelMotorSpeed);
		rightWheelMotor.setSpeed(Constants.stdWheelMotorSpeed);
		leftWheelMotor.setAcceleration(Constants.stdWheelMotorAcceleration);
		rightWheelMotor.setAcceleration(Constants.stdWheelMotorAcceleration);
		
		maxSpeedArm = (int) sensorArmMotor.getMaxSpeed();
		sensorArmMotor.setSpeed(Constants.stdSensorArmMotorSpeed);
		sensorArmMotor.setAcceleration(Constants.stdSensorArmMotorAcceleration);

		colorSensor.setFloodlight(true);
	}
	
	
	public void calibrateArm() {
		sensorArmMotor.forward();
		while (!sensorArmMotor.isStalled());
		int leftMax = sensorArmMotor.getTachoCount();
		
		sensorArmMotor.backward();
		while (!sensorArmMotor.isStalled());
		int rightMax = sensorArmMotor.getTachoCount();
		
		int mid = (leftMax + rightMax) / 2;
		//armMotor.rotateTo(mid);
		
		sensorArmMin = ((leftMax < rightMax) ? leftMax : rightMax);
		sensorArmMin += sensorArmPositionOffset;
		sensorArmMax = ((leftMax > rightMax) ? leftMax : rightMax);
		sensorArmMin -= sensorArmPositionOffset;
		sensorArmMid = mid;
	}
	
	public void centerArm() {
		sensorArmMotor.rotateTo(sensorArmMid, false);
	}

}
