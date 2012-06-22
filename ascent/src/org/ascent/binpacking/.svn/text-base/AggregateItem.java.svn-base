 /**************************************************************************
 * Copyright 2008 Jules White                                              *
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

public class AggregateItem extends Item{

	private int[] size_;
	private int value_;
	private List<Item> parts_ = new ArrayList<Item>();
	
	public AggregateItem(String name){
		super(name,new int[0]);
	}
	
	public AggregateItem(String name, List<Item> parts){
		super(name,new int[0]);
		for(Item i : parts)
			addPart(i);
	}

	public List<Item> getParts() {
		return parts_;
	}

	public void addPart(Item part){
		parts_.add(part);
		if(size_ == null)
			size_ = part.getSize();
		else {
			for(int i = 0; i < size_.length; i++)
				size_[i] += part.getSize()[i];
		}
		for(Item d : part.getExclusions())
			getExclusions().add(d);
	}
}
