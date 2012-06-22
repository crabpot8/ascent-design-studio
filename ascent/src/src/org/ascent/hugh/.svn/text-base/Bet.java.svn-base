package org.ascent.hugh;

import org.ascent.hugh.State;

public class Bet  {
	private double betAmount_;
	private State betState_;
	private int status_ =0; // 0 = bet not processed, 1 = bet was a win, -1 = bet was a loss
	private double payout_ = 0;
	public int getStatus() {
		return status_;
	}

	public void setStatus(int status) {
		this.status_ = status;
	}

	public double getPayout() {
		return payout_;
	}

	public void setPayout(double payout) {
		this.payout_ = payout;
	}

	public Bet(){
		
	}
	
	public Bet(State betState){
		betState_ = betState;
	}
	public Bet(State betState, double betAmount){
		betState_ = betState;
		betAmount_ = betAmount;
	}

	public double getBetAmount_() {
		return betAmount_;
	}

	public void setBetAmount_(double betAmount_) {
		this.betAmount_ = betAmount_;
	}

	public State getBetState_() {
		return betState_;
	}

	public void setBetState_(State betState_) {
		this.betState_ = betState_;
	}
	
	
	
}
