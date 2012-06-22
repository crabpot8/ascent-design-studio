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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.configurator.RefreshMatrixCore;
import org.ascent.configurator.conf.ExpressionParser;

public class RePackBinPacker extends FFDBinPacker {

	private class Block {
		private Object target_;
		private List sources_;

		public Block(Object target, List sources) {
			super();
			target_ = target;
			sources_ = sources;
		}

		public Block(Object target, Object src) {
			super();
			target_ = target;
			sources_ = new ArrayList(1);
			sources_.add(src);
		}

		public Object getTarget() {
			return target_;
		}

		public void setTarget(Object target) {
			target_ = target;
		}

		public List getSources() {
			return sources_;
		}

		public void setSources(List sources) {
			sources_ = sources;
		}

		public int[] getSize() {
			int[] size = new int[getSourceState(sources_.get(0)).getSize().length];
			for (int i = 0; i < sources_.size(); i++) {
				ItemState st = getSourceState(sources_.get(i));
				for (int j = 0; j < size.length; j++) {
					size[j] += st.getSize()[j];
				}
			}
			return size;
		}

		public boolean largerThan(int[] size2) {
			int[] size = getSize();

			for (int i = 0; i < size.length; i++) {
				if (size2[i] >= size[i])
					return false;
			}

			return true;
		}

		public boolean smallerThanEqual(int[] size2) {
			int[] size = getSize();

			for (int i = 0; i < size.length; i++) {
				if (size2[i] < size[i])
					return false;
			}

			return true;
		}
		
	}

	private int repackingImprovement_ = 0;
	private int itemsRepacked_ = 0;
	private List repackQueue_;
	private Map<Object, List> hostedMap_ = new HashMap<Object, List>();

	@Override
	public void postProcess() {
		repackQueue_ = new ArrayList(getUsedTargets().size());
		repackQueue_.addAll(getUsedTargets());

		for (Object o : getSources()) {
			ItemState st = getSourceState(o);
			for (Object t : st.getCurrentTargets()) {
				List thost = hostedMap_.get(t);
				if (thost == null) {
					thost = new ArrayList();
					hostedMap_.put(t, thost);
				}
				thost.add(o);

			}
		}

		for (Object o : hostedMap_.keySet()) {
			List l = hostedMap_.get(o);
			if (l != null) {
				Collections.sort(l, new FFDSourceComparator());
			}
		}

		updateRepackQueue();

		// Comparator rcomp = new Comparator() {
		//		
		// @Override
		// public int compare(Object o1, Object o2) {
		// SourceState st = getSourceState(o1);
		// SourceState st2 = getSourceState(o2);
		//				
		// if(st.getCurrentTargets().size() == 0 &&
		// st2.getCurrentTargets().size() > 0)
		// return 1;
		// else if(st.getCurrentTargets().size() > 0 &&
		// st2.getCurrentTargets().size() == 0)
		// return -1;
		//				
		// TargetState t = getTargetState(st.getCurrentTargets().get(0));
		// TargetState t2 = getTargetState(st2.getCurrentTargets().get(0));
		//				
		// if(t.getWeight() < t2.getWeight())
		// return -1;
		// else if(t.getWeight() > t2.getWeight())
		// return 1;
		//	
		// if(st.getWeight() > )
		//				
		// return 0;
		// }
		//		
		// }
		//		
		repack();

		super.postProcess();
	}

	public void updateRepackQueue() {
		for (Object o : getUsedTargets())
			updateItemWeight(getTargetState(o));

		Comparator tcomp = new Comparator() {

			public int compare(Object o1, Object o2) {
				double val = getTargetState(o1).getWeight()
						- getTargetState(o2).getWeight();
				if (val < 0)
					return -1;
				else if (val > 0)
					return 1;
				else
					return 0;
			}

		};

		Collections.sort(repackQueue_, tcomp);
	}

	public void repack() {
		int orig = +getUsedTargets().size();

		
		for (int i = 0; i < 30; i++) {
			Block[] swapped = repackItem();
			if (swapped != null) {
				updateRepackQueue();
				consolidateItems(swapped);
				itemsRepacked_++;
			}
		}

		for (Object o : hostedMap_.keySet()) {
			if (hostedMap_.get(o).size() == 0)
				getUsedTargets().remove(o);
		}
		repackingImprovement_ = orig - getUsedTargets().size();

	}

	public void consolidateItems(Block[] swapped) {
		for (int i = repackQueue_.size() - 1; i > 0; i--) {
			Object target = repackQueue_.get(i);
			List hosted = new ArrayList();
			hosted.addAll(hostedMap_.get(target));
			for (Object o : hosted) {
				Block b = new Block(target, o);
				Object target2 = repackQueue_.get(i - 1);
				BinState ts = getTargetState(target2);
				if (b.smallerThanEqual(ts.getSize())) {
					swapTo(o, target, target2);
					updateRepackQueue();
				} else {
					break;
				}
			}
		}
	}

	public void swapTo(Object o, Object otarget, Object ntarget) {
		ItemState ts = getSourceState(o);
		ts.getCurrentTargets().remove(otarget);
		ts.getCurrentTargets().add(ntarget);
		hostedMap_.get(otarget).remove(o);
		hostedMap_.get(ntarget).add(o);
		insert(ts, getTargetState(ntarget));
		remove(ts, getTargetState(otarget));
	}

	public Block[] repackItem() {
		Block block = chooseBlock();

		if (block != null) {
			Object ntarget = block.getTarget();
			Block swap = findSwapBlock(block);

			if (swap != null) {
				Object otarget = swap.getTarget();

				for (Object o : swap.getSources()) {
					swapTo(o, otarget, ntarget);
				}

				for (Object o : block.getSources()) {
					swapTo(o, ntarget, otarget);
				}

				return new Block[] { block, swap };
			}
		}

		return null;
	}

	public Block chooseBlock() {
		if (repackQueue_.size() > 0) {
			Object target = repackQueue_.remove(0);
			List hosted = hostedMap_.get(target);
			if (hosted.size() > 0) {
				Object src = hosted.get(0);
				Block b = new Block(target, src);
				return b;
			}
		}
		return null;
	}

	public Block findSwapBlock(Block block) {
		for (int i = repackQueue_.size() - 1; i > 0; i--) {
			Object target = repackQueue_.get(i);
			
			if(target == block.getTarget())
				return null;
			
			Block b = new Block(target, new ArrayList());
			List hosted = hostedMap_.get(target);
			ItemState ss = getSourceState(block.getSources().get(0));
//			System.out.println("%%%%%%%%%Swap try%%%%%%%%%");
//			SourceState ss = getSourceState(block.getSources().get(0));
//			System.out.println(ss);
//			System.out.println("%%%%%%%%%hosted%%%%%%%%%");
//			for(Object h : hosted){
//				System.out.println(getSourceState(h));
//			}
//			System.out.println("%%%%%%%%%end%%%%%%%%%\n\n\n");
//			
			
			// for (int j = hosted.size() - 1; j >= 0; j--) {
			// Object o = hosted.get(j);

			ArrayList trgs = new ArrayList(1);
			trgs.add(target);
			RefreshMatrixCore core = new RefreshMatrixCore();
			core.setSetsToMap(hosted, trgs);

			BinState ts = getTargetState(target);

			for (Object o : hosted) {
				int[] size = getSourceState(o).getSize();
				for (int j = 0; j < size.length; j++) {
					core.getSourceVariableValues(o).put("" + j, size[j]);
				}
			}
			int[] wsize = getTargetState(target).getSize();
			for (int j = 0; j < wsize.length; j++) {
				core.addTargetIntResourceConstraint(target, "" + j, wsize[j] + ss.getSize()[j]);
			}

			String ofunc = "";
			for (int j = 0; j < wsize.length; j++) {
				ofunc += "Source." + j + ".Sum +";
			}
			ofunc = ofunc.substring(0, ofunc.length() - 1);

			core.setOptimizationFunction(ExpressionParser.getExpression(ofunc));
			core.setMaximizeOptimizationFunction(true);

			Map<Object, List> sol = core.nextMapping();

			if (sol != null) {
				for (Object o : sol.keySet()) {
					if (sol.get(o).size() > 0)
						b.getSources().add(o);
				}

				if (b.getSources().size() > 0 && b.largerThan(block.getSize()))
					return b;
			}
			// for(Object o :)
			// if (!b.getSources().contains(o)) {
			// b.getSources().add(o);
			// addRequired(o, b);
			//
			// if (b.largerThan(block.getSize())) {
			// int[] max = getTargetState(block.getTarget()).getSize();
			// if (b.smallerThanEqual(max))
			// return b;
			// else{
			// b.getSources().remove(o);
			// removeRequired(o, b);
			// }
			// }
			// }
			// }
		}

		return null;
	}

	public void addRequired(Object src, Block b) {
		ItemState st = getSourceState(src);
		for (Object r : getRequired(st)) {
			if (!b.getSources().contains(r)) {
				b.getSources().add(r);
				addRequired(r, b);
			}
		}
	}

	public void removeRequired(Object src, Block b) {
		ItemState st = getSourceState(src);
		for (Object r : getRequired(st)) {
			if (b.getSources().contains(r)) {
				b.getSources().remove(r);
				removeRequired(r, b);
			}
		}
	}

	public int getRepackingImprovement() {
		return repackingImprovement_;
	}

	public void setRepackingImprovement(int repackingImprovement) {
		repackingImprovement_ = repackingImprovement;
	}

	public int getItemsRepacked() {
		return itemsRepacked_;
	}

	public void setItemsRepacked(int itemsRepacked) {
		itemsRepacked_ = itemsRepacked;
	}

	// public void canSwapTo()

	// public boolean excludedBy(SourceState st, List srcs){
	//		
	// }
}
