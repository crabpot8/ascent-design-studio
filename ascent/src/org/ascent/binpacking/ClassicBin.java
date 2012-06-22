/**************************************************************************
 * Copyright 2008 Brian Dougherty                                          *
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
import java.util.List;

public class ClassicBin {
	private double [] spaceLeft_;
	private String binName_;
	private int binId_;
	private List<ClassicItem> items = new ArrayList();
	
public ClassicBin(){
		
	}
	
	public ClassicBin(int numCR, int binId){
		spaceLeft_ = new double [numCR];
		initializeSpaceLeft_();
		binId_ = binId;
		
	}
	public ClassicBin(int binId, String bn,  double [] sl){
		binName_ = bn;
		//initializeSpaceLeft_();
		spaceLeft_ = sl;
		
		binId_ = binId;
		
	}
	
	private void initializeSpaceLeft_(){
		for( int i = 0; i < spaceLeft_.length; i++){
			spaceLeft_[i] = 0;
		}
	}
	public double[] getSpaceLeft_() {
		return spaceLeft_;
	}

	public void setSpaceLeft_(double[] spaceLeft) {
		this.spaceLeft_ = spaceLeft;
	}
	
	public void addItem(ClassicItem item){
		items.add(item);
	}
	public String getBinName_() {
		return binName_;
	}

	public void setBinName_(String binName_) {
		this.binName_ = binName_;
	}

	public int getBinId_() {
		return binId_;
	}

	public void setBinId_(int binId_) {
		this.binId_ = binId_;
	}

	public List<ClassicItem> getItems() {
		return items;
	}

	public void setItems(List<ClassicItem> items) {
		this.items = items;
	}

	
}
