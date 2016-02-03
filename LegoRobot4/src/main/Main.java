package main;

import strategy.*;
import task.*;

import lejos.hardware.ev3.EV3;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;


public class Main {

	public static void main(String[] args) {
		final Robot robot = new Robot();
		
		Strategy currentStrategy = new FollowLineStrategy(robot);
		currentStrategy.execute();
	}

}
