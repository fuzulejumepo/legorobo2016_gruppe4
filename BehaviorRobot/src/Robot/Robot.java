package Robot;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.PilotProps;

public class Robot {
	private EV3 brick;
	private RegulatedMotor leftMotor;
	private RegulatedMotor rightMotor;
	
	private EV3ColorSensor colorSensor;
	private NXTRegulatedMotor headMotor;
	private EV3TouchSensor touchSensor;
	private EV3IRSensor irSensor;
	
	public Robot() {
		this.brick = (EV3) BrickFinder.getLocal();
		this.leftMotor = Motor.A;
		this.rightMotor = Motor.B;
		this.headMotor = Motor.C;
		
	    this.colorSensor = new EV3ColorSensor(SensorPort.S1);
	    this.touchSensor = new EV3TouchSensor(SensorPort.S2);
	    this.irSensor = new EV3IRSensor(SensorPort.S4);
	}

	public void close() {
		this.leftMotor.close();
		this.rightMotor.close();
		this.headMotor.close();
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

	public EV3ColorSensor getColorSensor() {
		return colorSensor;
	}
	
	public EV3TouchSensor getTouchSensor() {
		return touchSensor;
	}
	
	public EV3IRSensor getIRSensor() {
		return irSensor;
	}
	
	public void drawText(String text, int x, int y) {
		this.brick.getTextLCD().drawString(text, x, y);
	}
	
	public void lcdClear() {
		this.brick.getTextLCD().clear();
	}
	
	public Keys getKeys() {
		return this.brick.getKeys();
	}
	
	public DifferentialPilot getPilot() throws Exception {
		
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "3.5"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "11.0"));    	 	
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
		
    	DifferentialPilot pilot = new DifferentialPilot(wheelDiameter,trackWidth,this.leftMotor,this.rightMotor,reverse);
		pilot.setTravelSpeed(10);
		pilot.setRotateSpeed(45);
		
		return pilot;
	}


}
