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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

 
public class JHEU extends GreedyMMKP {

	private SwapData upgrade_;
	private boolean upgrading_;

	private int rSet_;
	private int rCounter_;
	private int rMax_ = 25;
	private int rSMax_ = 25;

	public JHEU(MMKPProblem problem) {
		super(problem);
	}

	private void repackInit() {
		for (MMKPSet s : getSets()) {
			Collections.sort(s.getItems(), Collections
					.reverseOrder(VALUE_COMPARATOR));
		}
	}

	@Override
	public Item choosePermuationItem() {
		Item it = null;

		if (!upgrading_) {
			it = super.choosePermuationItem();
			if (it == null) {
				upgrading_ = true;
				repackInit();
			}
		}

		if (upgrading_) {
			while (rCounter_ < rMax_ && rSet_ < getSets().size()) {

				MMKPSet s = getSets().get(rSet_);
				Item curr = s.getCurrentItem();
				int sindex = Collections.binarySearch(s.getItems(), curr,
						Collections
						.reverseOrder(VALUE_COMPARATOR));

				if(sindex > 0)
					upgrade_ = findUpgrade(curr);

				if (upgrade_ != null) {
					it = upgrade_.getItems().get(0);

					rCounter_++;
					if (rCounter_ == rMax_) {
						rCounter_ = 0;
						rSet_++;
					}

					return it;
				} else {
					rSet_++;
				}
			}
		}

		return it;
	}

	@Override
	public SwapData swapIn(Item item) {
		if (upgrade_ != null) {
			SwapData up = upgrade_;
			upgrade_ = null;
			return up;
		}

		return super.swapIn(item);
	}

	public SwapData findUpgrade(Item item) {
		
		MMKPSet cset = item.getSet();

		for (int k = 0; k < rSMax_; k++) {
			List<Item> swapIn = new ArrayList<Item>(7);
			List<Item> nsol = new ArrayList<Item>(getSolution());
			
			Item alt = cset.getItems().get(k);
			
			swapIn.add(alt);
			nsol.remove(cset.getCurrentItem());
			nsol.add(alt);

			if(alt.getValue() <= item.getValue())
				return null;
			
			double dmax = alt.getValue() - cset.getCurrentItem().getValue();
			int[] residual = new int[getCurrentResources().length];
			System.arraycopy(getCurrentResources(), 0, residual, 0,
					getCurrentResources().length);
			for (int i = 0; i < residual.length; i++) {
				residual[i] -= (getRes(alt.getIndex(),i) - getRes(item.getIndex(),i));
			}
			boolean ok = true;
			for(int i = 0; i < residual.length; i++){
				if(residual[i] < 0){
					ok = false;
					break;
				}
			}
			if(ok){
				List<Item> sol = new ArrayList<Item>(getSolution());
				sol.remove(item);
				sol.add(alt);
				return new SwapData(alt,sol);
			}
			for (MMKPSet s : getSets()) {
				if (s != cset) {
					Item i = findAlternate(s, dmax, residual);
					if (i != null) {
						boolean done = true;
						for (int j = 0; j < residual.length; j++) {
							residual[j] += getRes(s
									.getCurrentItem().getIndex(),j)
									- getRes(i.getIndex(),j);
							if (residual[j] < 0)
								done = false;
						}
						swapIn.add(i);
						nsol.remove(s.getCurrentItem());
						nsol.add(i);
						dmax -= (s.getCurrentItem().getValue() - i.getValue());
						if (done)
							return new SwapData(swapIn, nsol);
					}
				}
			}
		}
		return null;
	}

	public Item findAlternate(MMKPSet s, double dmax, int[] resid) {
		Item curr = s.getCurrentItem();
		int sindex = Collections.binarySearch(s.getItems(), curr,
				Collections
				.reverseOrder(VALUE_COMPARATOR));
		for (int i = sindex; i > 0; i--) {
			Item alt = s.getItems().get(i);
			if (curr.getValue() - alt.getValue() < dmax) {
				boolean goodpick = true;
				boolean delta = false;
				for (int j = 0; j < getResources().length; j++) {
					int v = resid[j]
							- getRes(alt.getIndex(),j)
							+ getRes(curr.getIndex(),j);
					if (v < 0 && v < resid[j]) {
						goodpick = false;
						break;
					}
					if(v > resid[j])
						delta = true;
				}
				if (goodpick && delta)
					return alt;
			} else {
				break;
			}
		}
		for (int i = sindex; i < s.getItems().size() && i > -1; i++) {
			Item alt = s.getItems().get(i);
			if (curr.getValue() - alt.getValue() < dmax) {
				boolean goodpick = true;
				boolean delta = false;
				for (int j = 0; j < getResources().length; j++) {
					int v = resid[j]
							- getRes(alt.getIndex(),j)
							+ getRes(curr.getIndex(),j);
					if (v < 0 && v < resid[j]) {
						goodpick = false;
						break;
					}
					if(v > resid[j])
						delta = true;
				}
				if (goodpick && delta)
					return alt;
			} else {
				break;
			}
		}
		return null;
	}


}
