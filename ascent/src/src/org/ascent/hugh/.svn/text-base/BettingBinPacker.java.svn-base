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

import org.ascent.binpacking.BasicBinPacker;
import org.ascent.binpacking.ClassicBin;
import org.ascent.binpacking.ClassicItem;

public class BettingBinPacker extends BasicBinPacker{
	private ArrayList<Integer> bets_; // pack in this order, change up the rest if not in here
	private ArrayList<ArrayList<ClassicItem>> madeBets_ = new ArrayList();
	private ArrayList<ClassicItem>originalItems_;
	private ArrayList<ClassicItem> madeBet_;
	public BettingBinPacker(List<ClassicItem> items, List<ClassicBin> bins, int bt){
		super(items,bins,bt);
		originalItems_ = new ArrayList(items);
		
		
	}
	
	public State makeState(ArrayList<Integer> bets){
		bets_ = bets;
		packItems();
		State betState = new State(madeBet_);
		int numBins =0;
		for(ClassicBin b : bins_){
			//System.out.println("on bin " + b.getBinName_() +" which hs this many items: " + b.getItems().size());
			if(b.getItems().size()>0){
				numBins++;
			}
		}
		betState.setNumBins(numBins);
		return betState;
		
	}
	
	
	public boolean packItems(){
		boolean success;
		madeBet_= new ArrayList();

		for(Integer bet: bets_){
			ClassicItem item = itemsToPack_.get(bet.intValue());
			success =placeItem(item);
			
			if(success == false){
				System.out.println("Unsuccessful. Failed on " + bet);
				
				return false;
			}
			madeBet_.add(item);
			itemsToPack_.remove(item);
			
		}
		for( ClassicItem item : itemsToPack_){
			success =placeItem(item);
			if(success == false){
				System.out.println("Unsuccessful. Failed on " + item.getName());
				
				return false;
			}
			madeBet_.add(item);
		}
		madeBets_.add(madeBet_);
		itemsToPack_= originalItems_;
		return true;
	}
}
