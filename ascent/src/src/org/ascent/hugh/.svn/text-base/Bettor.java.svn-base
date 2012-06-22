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
	
	public Bettor(){
		myPersona_ = new Personality();
		favNums_ = new ArrayList();
		pd_ = new PlayerData();
		bp_ = new BettingProgression();
	}
	
	
	public Bettor(Personality persona, ArrayList<Integer> nums, PlayerData pd, BettingProgression bp){
		myPersona_ = persona;
		favNums_ = nums;
		pd_= pd;
		bp_ = bp;
		bankroll_ = 500;
	}
	
	public Bettor(Personality persona, ArrayList<Integer> nums, PlayerData pd){
		myPersona_ = persona;
		favNums_ = nums;
		pd_ = pd;
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


	public ArrayList<Integer> getFavNums_() {
		return favNums_;
	}


	public void setFavNums_(ArrayList<Integer> favNums_) {
		this.favNums_ = favNums_;
	}


	public Personality getMyPersona_() {
		return myPersona_;
	}


	public void setMyPersona_(Personality myPersona_) {
		this.myPersona_ = myPersona_;
	}


	public PlayerData getPd_() {
		return pd_;
	}


	public void setPd_(PlayerData pd_) {
		this.pd_ = pd_;
	}


	public BettingProgression getBp_() {
		return bp_;
	}


	public void setBp_(BettingProgression bp_) {
		this.bp_ = bp_;
	}
	
	
}
