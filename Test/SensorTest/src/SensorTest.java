import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;


public class SensorTest {

    static RegulatedMotor armMotor = Motor.C;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		armMotor.resetTachoCount();
	    armMotor.rotateTo(0);
	    armMotor.setSpeed(800);
	    armMotor.setAcceleration(8000);
	    
	    for (int i = 0; i < 30; ++i) {
	    	if (i % 2 == 0)
	    		armMotor.forward();
	    	else
	    		armMotor.backward();
	    	Delay.msDelay(200);
	    }
	}

}
