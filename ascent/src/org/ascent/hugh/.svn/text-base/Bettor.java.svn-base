/**************************************************************************
 * Copyright 2009 Brian Dougherty                                          *
 *                                                                         *
 * Licensed under the Apache License, Version 2.0 (the "License");         *
 * you may not use this file except in compliance with the License.        *
 * You may obtain a copy of the License at                                 *
 *                                                                         *
 * http://www.apache.org/licenses/LICENSE-2.0                              *
 *                                                                         *
 * Unless required by applicable law or agreed to in writing, software     *
 * distributed under the License is distributed on an "AS IS" BASIS,       *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.*
 * See the License for the specific language governing permissions and     *
 * limitations under the License.                                          *
 **************************************************************************/
package org.ascent.hugh;

import java.util.ArrayList;
import java.util.List;

public class Bettor {
	double bankroll_;
	
	
	ArrayList<Integer> favNums_;
	Personality myPersona_;
	PlayerData pd_;
	BettingProgression bp_;
	int favNumRange_;
	public Bettor(){
		myPersona_ = new Personality();
		favNums_ = new ArrayList();
		pd_ = new PlayerData();
		bp_ = new BettingProgression();
		favNumRange_ = 10;
	}
	
	
	public Bettor(Personality persona, ArrayList<Integer> nums, PlayerData pd, BettingProgression bp, int fnr){
		myPersona_ = persona;
		favNums_ = nums;
		pd_= pd;
		bp_ = bp;
		bankroll_ = 500;
		favNumRange_ = fnr;
	}
	
	public Bettor(Personality persona, ArrayList<Integer> nums, PlayerData pd, int fnr){
		myPersona_ = persona;
		favNums_ = nums;
		pd_ = pd;
		favNumRange_ = fnr;
	}

	public String toString(){
		String printString = " Name: "+ pd_.getName() + ", favNums: "+favNums_;
		return printString;
		
	}
	public double getBankroll() {
		return bankroll_;
	}


	public void setBankroll(double bankroll) {
		bankroll_ = bankroll;
	}


	public ArrayList<Integer> getFavNums() {
		return favNums_;
	}


	public void setFavNums(ArrayList<Integer> favNums_) {
		this.favNums_ = favNums_;
	}


	public Personality getMyPersona() {
		return myPersona_;
	}


	public void setMyPersona(Personality myPersona_) {
		this.myPersona_ = myPersona_;
	}


	public PlayerData getPd() {
		return pd_;
	}


	public void setPd(PlayerData pd_) {
		this.pd_ = pd_;
	}


	public BettingProgression getBp() {
		return bp_;
	}


	public void setBp(BettingProgression bp_) {
		this.bp_ = bp_;
	}
	
	public void reBet(Bet lastBet){
		if(lastBet.getStatus() == -1){
			lostBet(lastBet);
		}
		else{
			wonBet(lastBet);
		}
	}
	
	private void lostBet(Bet lastBet){
		ArrayList<Integer> oldFavNums = favNums_;
    	favNums_ = new ArrayList<Integer>();
    	for(Integer num : oldFavNums){
    		double r  = Math.random();
		    double baseLine = r * 10.0;
		    double superst= myPersona_.getSuperstitious_();
		    double spread =  myPersona_.getSpread_();
		    superst= superst + baseLine;
		    System.out.println("Lost the bet. Superstitious is "+superst+" and oldFavNums are " + favNums_);
    		if(superst<10.0){
	    		int lilNum =(int)(Math.round((num * spread)) % favNumRange_);
	    		int safetyCount = 0;
	    		while(oldFavNums.contains(new Integer(lilNum)) && safetyCount < favNumRange_){
	    			lilNum = (lilNum+1) % favNumRange_;
	    			safetyCount++;
	    		}
	    		favNums_.add(new Integer(lilNum));
    		}
    		else{
    			Integer lastFavNum = oldFavNums.get(0);
    			lastFavNum = (lastFavNum + 1) % favNumRange_;
    			favNums_.set(0, lastFavNum);
    		}
    	}
	    
	    System.out.println("New favNums_ = " + favNums_);
	}
	
	
	private void wonBet(Bet lastBet){
		ArrayList<Integer> oldFavNums = favNums_;
    	favNums_ = new ArrayList<Integer>();
    	for(Integer num : oldFavNums){
    		double r  = Math.random();
		    double baseLine = r * 10.0;
		    double tenac = myPersona_.getTenac_();
		    double spread =  myPersona_.getSpread_();
		    tenac = tenac + baseLine;
		    System.out.println("Won the bet. Tenac is "+tenac+" and oldFavNums are " + favNums_);
    		if(tenac<10.0){
	    		int lilNum =(int)(Math.round((num * spread)) % favNumRange_);
	    		int safetyCount = 0;
	    		while(oldFavNums.contains(new Integer(lilNum)) && safetyCount < favNumRange_){
	    			lilNum = (lilNum+1) % favNumRange_;
	    			safetyCount++;
	    		}
	    		favNums_.add(new Integer(lilNum));
    		}
    		else{
    			Integer lastFavNum = oldFavNums.get(0);
    			lastFavNum = (lastFavNum + 1) % favNumRange_;
    			favNums_.set(0, lastFavNum);
    		}
    	}
	    
	    System.out.println("New favNums_ = " + favNums_);
	}
	
	
}
