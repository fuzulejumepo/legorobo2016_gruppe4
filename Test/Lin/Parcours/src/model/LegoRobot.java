package model;

import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;

public class LegoRobot {
	private EV3 brick;
	private RegulatedMotor leftMotor;
	private RegulatedMotor rightMotor;
	private RegulatedMotor headMotor;
	
	private EV3ColorSensor colorSensor;
	private EV3UltrasonicSensor ultrasonicSensor;
	
	public LegoRobot() {
		this.brick = (EV3) BrickFinder.getLocal();
		this.leftMotor = Motor.A;
		this.rightMotor = Motor.D;
		this.headMotor = Motor.C;
		//this.leftMotor = new EV3MediumRegulatedMotor(MotorPort.A);
		//this.rightMotor = new EV3MediumRegulatedMotor(MotorPort.D);
		//this.headMotor = new EV3MediumRegulatedMotor(MotorPort.C);
		
	    Port s1 = brick.getPort("S1");
	    this.colorSensor = new EV3ColorSensor(s1);
	    this.ultrasonicSensor = new EV3UltrasonicSensor(SensorPort.S4);
	}

	public void close() {
		this.leftMotor.close();
		this.rightMotor.close();
		this.headMotor.close();
		//this.irSensor.close();
		this.colorSensor.close();
	}
	
	public EV3 getBrick() {
		return brick;
	}
	
	public RegulatedMotor getLeftMotor() {
		return leftMotor;
	}

	public RegulatedMotor getRightMotor() {
		return rightMotor;
	}

	public RegulatedMotor getHeadMotor() {
		return headMotor;
	}

	public EV3IRSensor getUltrasonicSensor() {
		return getUltrasonicSensor();
	}

	public EV3ColorSensor getColorSensor() {
		return colorSensor;
	}
	
	public void drawText(String text, int x, int y) {
		this.brick.getTextLCD().drawString(text, x, y);
	}
	
	public void lcdClear() {
		this.brick.getTextLCD().clear();
	}
	
}
