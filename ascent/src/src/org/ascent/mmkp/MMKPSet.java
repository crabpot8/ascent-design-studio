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

public class MMKPSet {
	private Item currentItem_;
	private List<Item> items_;

	public MMKPSet(List<Item> items) {
		super();
		items_ = items;
	}

	public MMKPSet(Item currentItem, List<Item> items) {
		super();
		currentItem_ = currentItem;
		items_ = items;
	}

	public Item getCurrentItem() {
		return currentItem_;
	}

	public void setCurrentItem(Item currentItem) {
		currentItem_ = currentItem;
	}

	public List<Item> getItems() {
		return items_;
	}

	public void setItems(List<Item> items) {
		items_ = items;
	}
}
