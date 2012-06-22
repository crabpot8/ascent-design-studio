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

import java.util.List;

public class Solution {
	private MMKPProblem problem_;
	private List<Item> items_;
	private int overFlow_ = -1;
	private int value_ = -1;

	public Solution(List<Item> items, MMKPProblem p) {
		super();
		problem_ = p;
		items_ = items;
	}

	public List<Item> getItems() {
		return items_;
	}

	public void setItems(List<Item> items) {
		items_ = items;
	}

	public int getOverflow() {
		if(items_ == null)
			return 0;
		
		if (overFlow_ == -1) {
			int overflow = 0;
			for (int i = 0; i < problem_.getResources().length; i++) {
				int consumed = 0;
				for (Item item : items_)
					consumed += problem_.getConsumedResources()[item.getIndex()][i];

				int delta = problem_.getResources()[i] - consumed;
				if (delta < 0)
					overflow += -1 * delta;
			}
			overFlow_ = overflow;
		}

		return overFlow_;
	}

	public int getValue() {
		return value_;
	}

	public void setValue(int value) {
		value_ = value;
	}

}

