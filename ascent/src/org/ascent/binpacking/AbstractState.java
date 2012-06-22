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

import org.ascent.HasSize;

public abstract class AbstractState implements HasSize{
	protected Object item_;
	protected int[] size_;
	protected double weight_ = 0;
	
	public double getWeight() {
		return weight_;
	}

	public void setWeight(double weight) {
		weight_ = weight;
	}

	public int[] getSize() {
		return size_;
	}

	public void setSize(int[] size) {
		size_ = size;
	}
	
	public String toString() {
		String w = "[";
		for (int i = 0; i < size_.length; i++) {
			w += size_[i];
			if (i != size_.length - 1)
				w += ",";
		}
		w += "]";
		return w;
	}

	public Object getItem() {
		return item_;
	}

	public void setItem(Object item) {
		item_ = item;
	}
	
}
