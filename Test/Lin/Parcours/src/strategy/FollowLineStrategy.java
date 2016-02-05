package strategy;

import model.RobotState;

public class FollowLineStrategy extends Strategy {

	public FollowLineStrategy() {
		this.state = RobotState.LINE;
	}
	
	@Override
	public Strategy apply() {
		while (this.state == RobotState.LINE) {
			
		}
		return changeStrategy();
	}

	@Override
	public String toString() {
		return "FollowLineStrategy";
	}

}
