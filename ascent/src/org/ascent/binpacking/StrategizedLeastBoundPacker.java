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

public class StrategizedLeastBoundPacker extends LeastBoundPacker {

	private BinSelector binSelectionStrategy_;
	private ItemSelector itemSelectionStrategy_;

	public StrategizedLeastBoundPacker(BinSelector binstrat) {
		super();
		binSelectionStrategy_ = binstrat;
	}

	public StrategizedLeastBoundPacker(BinSelector binstrat, BinPackingProblem p) {
		super(p);
		binSelectionStrategy_ = binstrat;
	}

	public Object selectTarget(ItemState ss, List valid) {

		List potentials = new ArrayList();
		List potentialbinstates = new ArrayList();
		for (Object t : valid) {
			BinState ts = getTargetState(t);
			if (willFit(ss, ts)) {
				potentials.add(t);
				potentialbinstates.add(ts);
			}
		}

		return binSelectionStrategy_.selectBin(this, ss.item_, potentials, ss, potentialbinstates);
	}
	
	

	@Override
	public Object nextSource() {
		if(itemSelectionStrategy_ == null)
			return super.nextSource();
		else
			return itemSelectionStrategy_.selectItem(this, getQueue(), getTargets());
	}

	public BinSelector getBinSelectionStrategy() {
		return binSelectionStrategy_;
	}

	public void setBinSelectionStrategy(BinSelector strategy) {
		binSelectionStrategy_ = strategy;
	}

	public ItemSelector getItemSelectionStrategy() {
		return itemSelectionStrategy_;
	}

	public void setItemSelectionStrategy(ItemSelector itemSelectionStrategy) {
		itemSelectionStrategy_ = itemSelectionStrategy;
	}

}
