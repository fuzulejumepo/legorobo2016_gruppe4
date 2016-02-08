package strategy;

import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.RegulatedMotor;
//import lejos.utility.Delay;
import main.Robot;

public class ReadBarcodeStrategy extends Strategy {
	public static final float lineThreshold = 0.2f;
	public static final int tachoCountMax = 200;
	
	protected RegulatedMotor armMotor;
	protected RegulatedMotor leftWheelMotor;
	protected RegulatedMotor rightWheelMotor;
	protected EV3ColorSensor armSensor;
	protected EV3GyroSensor gyroSensor;
	
	protected float[] startDirection = { 0.0f };
	
	public ReadBarcodeStrategy(Robot robot) {
		super(robot);
		this.armMotor = robot.sensorArmMotor;
		this.leftWheelMotor = robot.leftWheelMotor;
		this.rightWheelMotor = robot.rightWheelMotor;
		this.armSensor = robot.colorSensor;
		this.gyroSensor = robot.gyroSensor;
	}

	@Override
	public void execute() {
		robot.ev3.getTextLCD().clear();
		robot.ev3.getTextLCD().drawString("Reading Barcode", 2, 2);
		
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});
		
		robot.colorSensor.setCurrentMode(robot.colorSensor.getRedMode().getName());
		robot.calibrateArm();
		robot.centerArm();
		
		float[] sample1 = { 0.0f };
		float[] sample2 = { 0.0f };
		int counter = 0;
		int sign = 1;
		
		Sound.beepSequenceUp();
		
		gyroSensor.reset();
		gyroSensor.getAngleMode().fetchSample(startDirection, 0);
		
		leftWheelMotor.synchronizeWith(new RegulatedMotor[] {rightWheelMotor});

		leftWheelMotor.startSynchronization();
		leftWheelMotor.forward();
		rightWheelMotor.forward();
		leftWheelMotor.endSynchronization();

		int meanTacho = 0;	
		
		armSensor.getRedMode().fetchSample(sample1, 0);
		armSensor.getRedMode().fetchSample(sample2, 0);
		//System.out.println("	first color : "+sample1[0]+","+sample2[0]);	
		
		if (sample1[0]>lineThreshold ||sample2[0]>lineThreshold )
			sign = 1;
		else
			sign = -1;
			
		while ( meanTacho < tachoCountMax){
			
			counter += 1;
			leftWheelMotor.resetTachoCount();
			rightWheelMotor.resetTachoCount();
			/*
			if (sign == 1)
				System.out.println("	white");
			else
				System.out.println("	black");
			*/
			while ((sign*sample1[0] > sign*lineThreshold || sign*sample2[0] > sign*lineThreshold ) 
					&& leftWheelMotor.getTachoCount() < tachoCountMax) {		
				armSensor.getRedMode().fetchSample(sample1, 0);
				armSensor.getRedMode().fetchSample(sample2, 0);
				//System.out.println("	color : "+sample1[0]+","+sample2[0]);
			}
						
			sign = -sign;
			meanTacho =(leftWheelMotor.getTachoCount()+rightWheelMotor.getTachoCount())/2;	

			//System.out.println("		mean: "+meanTacho);
		}
		
		leftWheelMotor.stop();
		rightWheelMotor.stop();
		counter = counter/2;
		//System.out.println("	Barcode: " + counter);
	}

}
