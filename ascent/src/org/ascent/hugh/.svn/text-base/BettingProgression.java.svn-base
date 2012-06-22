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


public class BettingProgression {
	String name_;
	//ArrayList<Double> multipliers_;
	ArrayList<Double> winMultipliers_;
	ArrayList<Double> lossMultipliers_;
	//BinaryTree multiTree_ = new BinaryTree();

	int winIndex = -1;
	int lossIndex =-1;
	double startMultiplier = 1.0;
	
	public BettingProgression(){
		
	}
	
	public BettingProgression(String name, ArrayList<Double> winMultipliers,  ArrayList<Double> lossMultipliers){
		name_ = name;
		winMultipliers_ = winMultipliers;
		lossMultipliers_ = lossMultipliers;
		
	}
	
	public double getWin(){
		lossIndex = 0;
		if(winIndex < winMultipliers_.size()-1){
			winIndex++;
		}
		Double wm = winMultipliers_.get(winIndex).doubleValue();
		System.out.println("Passing win multiplier " + wm);
		return wm.doubleValue();
	}
	
	public double getLoss(){
		winIndex = 0;
		if(lossIndex < lossMultipliers_.size()-1){
			lossIndex++;
		}
		System.out.println("Passing lost multiplier " + lossMultipliers_.get(lossIndex).doubleValue() );
		return lossMultipliers_.get(lossIndex).doubleValue();
	}

	public double getStartMultiplier() {
		return startMultiplier;
	}

	public void setStartMultiplier(double startMultiplier) {
		this.startMultiplier = startMultiplier;
	}
		
	
	
}
