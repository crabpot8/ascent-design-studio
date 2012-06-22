 /**************************************************************************
 * Copyright 2009 Jules White                                              *
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


package org.ascent.binpacking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractBinPacker {

	private Map<Bin,List<Item>> binContents_;
	private List<Bin> bins_;
	private List<Item> items_;	
	private List<Item> queue_;
	private FitsOperator fits_;
	
	public Map<Item,Bin> pack(BinPackingProblem problem){
		
		configure(problem);
		
		sortBins(bins_);
		sortItems(items_);
		
		Map<Item,Bin> solution = new HashMap<Item, Bin>(items_.size());
		
		while(!done()){
			
			preIterate();
			
			Item it = nextItem();
			Bin bin = getBin(it);
			
			if(bin != null){
				binContents_.get(bin).add(it);
				solution.put(it,bin);
			}
			else {
				break;
			}
			
			postIterate();
		}
		
		return solution;
	}
	
	public void configure(BinPackingProblem bp){
		binContents_ = new HashMap<Bin, List<Item>>(bins_.size());
		queue_ = new ArrayList<Item>(items_.size());
	}
	
	public Bin getBin(Item it){
		Bin bin = null;
		for(int i = 0; i < bins_.size(); i++){
			bin = bins_.get(i);
			List<Item> inbin = binContents_.get(bin);
			if(inbin == null){
				inbin = new ArrayList<Item>();
				binContents_.put(bin, inbin);
			}
			if(fits_.fits(it, inbin, bin)){
				break;
			}
		}
		return bin;
	}
	
	public Item nextItem(){
		return queue_.remove(0);
	}
	
	public boolean done(){
		return queue_.size() == 0;
	}
	
	public void preIterate(){}
	
	public void postIterate(){}
	
	public abstract void sortBins(List<Bin> bins);
	
	public abstract void sortItems(List<Item> items);
}
