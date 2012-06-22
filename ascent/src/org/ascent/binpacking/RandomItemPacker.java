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

import org.ascent.Util;

public class RandomItemPacker extends FFDBinPacker {

	public RandomItemPacker() {
		super();
	}

	public RandomItemPacker(BinPackingProblem p) {
		super(p);
	}
	
	public Object nextSource() {
		int item = Util.random(0, getQueue().size()-1);
		return getQueue().get(item);
	}
	
	public void sortItems(){}

	public void sortBins(){}
}
