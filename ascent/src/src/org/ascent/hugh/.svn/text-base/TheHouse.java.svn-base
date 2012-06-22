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

import org.ascent.binpacking.ClassicItem;

public class TheHouse {
	
	private double pastBest_ = Double.MAX_VALUE;
	private ArrayList<ClassicItem> items_;
	
	public TheHouse(){//ArrayList<ClassicItem> items){
		//items_ = items;
	}
	
	public double spinWheel(ArrayList<Bet> wagers){
		double winnings = 0;
		System.out.println("num wagers is "+ wagers.size());
		for(Bet wager : wagers){
			System.out.println("about to evaluate bet");
			if(evaluateBet(wager.getBetState_())){
				winnings = winnings + wager.getBetAmount_()+payout(wager);
				wager.setPayout(winnings);
				wager.setStatus(1);
				System.out.println("WIIIINNNNNNEEEEERRRR! Bet of " + wager.getBetAmount_()+" paid " + winnings);
			}
			else{
				wager.setStatus(-1);
			}
		}
		return winnings;
		
		
	}
	
	public boolean evaluateBet(State state){
		//ArrayList<ClassicItem> stateItems = state.getStateArray_();
		if(state.getNumBins() < pastBest_){
			pastBest_ = state.getNumBins();
			
			System.out.println("Winning bet with bins reduced to " + pastBest_);
			return true; // we won. yay.
		}
		
		return false;
		
		
	}
	
	public double payout(Bet wager){
		double odds = getOdds(wager.getBetState_());
		return   wager.getBetAmount_()* odds;
		
	}
	
	private double getOdds(State state){
		return 2.0;
	}
	
}
