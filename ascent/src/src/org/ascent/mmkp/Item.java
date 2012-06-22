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

package org.ascent.mmkp;

public class Item {
	private Object item_;
	private int index_;
	private double value_;
	private double netValue_;
	private double netWeight_;
	private MMKPSet set_;

	public Item(MMKPSet set, Object it, int index, double v) {
		item_ = it;
		index_ = index;
		value_ = v;
		set_ = set;
	}

	public Object getItem() {
		return item_;
	}

	public void setItem(Object item) {
		item_ = item;
	}

	public int getIndex() {
		return index_;
	}

	public void setIndex(int index) {
		index_ = index;
	}

	public double getValue() {
		return value_;
	}

	public void setValue(double value) {
		value_ = value;
	}

	public double getNetValue() {
		return netValue_;
	}

	public void setNetValue(double netValue) {
		netValue_ = netValue;
	}

	public double getNetWeight() {
		return netWeight_;
	}

	public void setNetWeight(double netWeight) {
		netWeight_ = netWeight;
	}

	public MMKPSet getSet() {
		return set_;
	}

	public void setSet(MMKPSet set) {
		set_ = set;
	}

	public String toString() {
		return item_.toString();
	}
}
