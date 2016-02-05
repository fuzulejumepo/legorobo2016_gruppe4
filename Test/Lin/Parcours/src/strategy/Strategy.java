package strategy;

import model.RobotState;

public abstract class Strategy {
	protected RobotState state;
	
	public abstract Strategy apply();
	public abstract String toString();
	
	public void notifyOfNewState(RobotState state) {
		this.state = state;
	}
	
	protected Strategy changeStrategy() {
		switch (this.state) {
			case LINE:
				return new FollowLineStrategy();
			case WALL:
			default:
				return null;
		}
	}
	
}
