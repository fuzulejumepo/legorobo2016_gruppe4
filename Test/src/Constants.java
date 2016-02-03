import lejos.hardware.ev3.*;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.RegulatedMotor;
import lejos.hardware.sensor.*;



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
