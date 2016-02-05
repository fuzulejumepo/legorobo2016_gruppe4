import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;

public class Labyrinth {
	static EV3 ev3 = (EV3) BrickFinder.getLocal();
	static RegulatedMotor leftMotor = Motor.A;
	static RegulatedMotor rightMotor = Motor.B;
	static TextLCD lcd = ev3.getTextLCD();
	static Keys keys = ev3.getKeys();
	static double minDistance = 0.07;
	static double maxDistance = 0.11;
	static EV3TouchSensor touchSensorFront =  new EV3TouchSensor(SensorPort.S2);
	static EV3UltrasonicSensor irSensor = new EV3UltrasonicSensor(SensorPort.S4);
	static SampleProvider ultra = irSensor.getMode("Distance");
	static int curveSlow = 700;
	static int curveFast = 900;
	static int forwardsSpeed = 1000;
	static double totalCurve = 0.25;

	public static void main(String[] args) {
		
		float[] touched = new float[touchSensorFront.sampleSize()];
		float[] distances = new float[irSensor.sampleSize()];
		leftMotor.forward();
		rightMotor.forward();
		while(true){
		while(touched[0] != 1.0){
			touchSensorFront.fetchSample(touched, 0);
			ultra.fetchSample(distances, 0);
			double distance = distances[0];
			lcd.drawString(" " + distance, 3, 3);
			if(minDistance < distance && distance < maxDistance){
				lcd.clear();
				lcd.drawString("forwards", 4, 4);
				moveForward();
			}
			if(distance>totalCurve){
				lcd.clear();
				lcd.drawString("turn around", 4, 4);
				turnRight();
			}
			else if(distance > maxDistance){
				lcd.clear();
				lcd.drawString("right", 4, 4);
				moveRight();
			}
			if(distance < minDistance){
				lcd.clear();
				lcd.drawString("left", 4, 4);
				moveLeft();
			}
		}
		leftMotor.rotate(-350,false);
		touchSensorFront.fetchSample(touched, 0);
//		rightMotor.rotate(-200,false);
	}
//		irSensor.close();
		
		

	}

	private static void turnRight() {
		move(curveFast, 200);
	}

	private static void moveForward() {
		move(forwardsSpeed,forwardsSpeed);
	}

	private static void moveLeft() {
		move(curveSlow, curveFast);
	}

	private static void moveRight() {
		move(curveFast,curveSlow);
	}
	private static void move(int left, int right){
		leftMotor.setSpeed(left);
		rightMotor.setSpeed(right);
		leftMotor.forward();
		rightMotor.forward();
	}

}
