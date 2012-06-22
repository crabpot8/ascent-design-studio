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

public class SetState {
	private int index_;
	private Object currentItem_;
	private List set_;
	
	public SetState(List set) {
		super();
		set_ = set;
		if(set_.size() > 0)
			currentItem_ = set_.get(0);
	}
	
	public SetState(List set, int index) {
		super();
		set_ = set;
		index_ = index;
		if(set_.size() > 0)
			currentItem_ = set_.get(0);
	}

	public SetState(Object currentItem, List set) {
		super();
		currentItem_ = currentItem;
		set_ = set;
	}

	public Object getCurrentItem() {
		return currentItem_;
	}

	public void setCurrentItem(Object currentItem) {
		currentItem_ = currentItem;
	}

	public List getSet() {
		return set_;
	}

	public void setSet(List set) {
		set_ = set;
	}

	public int getIndex() {
		return index_;
	}

	public void setIndex(int index) {
		index_ = index;
	}

	
}
