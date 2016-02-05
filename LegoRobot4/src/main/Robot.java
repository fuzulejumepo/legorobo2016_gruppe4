package main;

import lejos.hardware.ev3.EV3;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;

public class Robot {
	//EV3 device
	public final EV3 ev3 = LocalEV3.get();
		
	//Motors
	public final RegulatedMotor leftWheelMotor = Motor.A;
	public final RegulatedMotor rightWheelMotor = Motor.B;
	public final RegulatedMotor sensorArmMotor = Motor.C;
	
	public final int maxSpeedWheel;
	public final int maxSpeedArm;
	
	public int sensorArmMin;
	public int sensorArmMax;
	public int sensorArmMid;
	
	
	//Sensors
	private static final Port colorSensorPort = SensorPort.S1;
	private static final Port ultraSensorPort = SensorPort.S4;
	private static final Port bumperRightSensorPort = SensorPort.S2;
	private static final Port bumperLeftSensorPort = SensorPort.S3;
	
	
	public final EV3ColorSensor colorSensor = new EV3ColorSensor(colorSensorPort);
	public final EV3UltrasonicSensor ultraSensor = new EV3UltrasonicSensor(ultraSensorPort);
	public final EV3TouchSensor bumperLeftSensor = new EV3TouchSensor(bumperLeftSensorPort);
	public final EV3TouchSensor bumperRightSensor = new EV3TouchSensor(bumperRightSensorPort);

	
	
	
	
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

}
