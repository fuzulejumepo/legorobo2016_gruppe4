package main;

import lejos.hardware.ev3.EV3;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;

public final class Constants {
	//EV3 device
	public static final EV3 ev3 = LocalEV3.get();
	
	//Motors
	public static final RegulatedMotor leftWheelMotor = Motor.A;
	public static final RegulatedMotor rightWheelMotor = Motor.D;
	public static final RegulatedMotor sensorArmMotor = Motor.C;
	
	//Sensors
	private static final Port colorSensorPort = SensorPort.S1;
	
	public static final EV3ColorSensor colorSensor = new EV3ColorSensor(colorSensorPort);

}
