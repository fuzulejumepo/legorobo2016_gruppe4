package main;

import lejos.hardware.ev3.EV3;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
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
	
	public final EV3ColorSensor colorSensor = new EV3ColorSensor(colorSensorPort);

	
	
	
	
	public Robot() {
		maxSpeedWheel = Math.max((int) leftWheelMotor.getMaxSpeed(),
								(int) rightWheelMotor.getMaxSpeed());
		leftWheelMotor.setSpeed(Constants.wheelMotorSpeed);
		rightWheelMotor.setSpeed(Constants.wheelMotorSpeed);
		leftWheelMotor.setAcceleration(Constants.wheelMotorAcceleration);
		rightWheelMotor.setAcceleration(Constants.wheelMotorAcceleration);
		
		maxSpeedArm = (int) sensorArmMotor.getMaxSpeed();
		sensorArmMotor.setSpeed(Constants.sensorArmMotorSpeed);
		sensorArmMotor.setAcceleration(Constants.sensorArmMotorAcceleration);

		colorSensor.setFloodlight(true);
	}

}
