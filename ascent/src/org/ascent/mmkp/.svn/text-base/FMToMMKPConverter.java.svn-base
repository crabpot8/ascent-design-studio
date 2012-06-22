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

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FMToMMKPConverter {

	private Comparator<SparseBitSet> valueComparator_ = new Comparator<SparseBitSet>() {

		public int compare(SparseBitSet o1, SparseBitSet o2) {
			// double ov = getNetValue(o1);
			// double ov2 = getNetValue(o2);
			// if(ov < ov2)
			// return -1;
			// else if(ov > ov2)
			// return 1;
			// else
			// return 0;
			return getValue(o1) - getValue(o2);
		}

	};

	private FeatureMap featureMap_;
	private Filter filter_;
	private int maxCrossDim_ = 20;
	private int elsInMem = 0;
	private int dim_;

	public void convertOptionalsToXORs(Feature f) {
		for (Feature c : f.getRequiredChildren()) {
			convertOptionalsToXORs(c);
		}
		for (Feature c : f.getXorChildren()) {
			convertOptionalsToXORs(c);
		}
		List<Feature> opts = new ArrayList<Feature>(f.getOptionalChildren());
		f.getOptionalChildren().clear();
		for (Feature o : opts) {
			Feature np = new Feature(o.getName() + "_", o.getId());
			np.setTransparent(true);
			np.getXorChildren().add(o);
			np.getXorChildren().add(new NULLFeature());
			np.setParent(f);
			o.setParent(np);
			f.getRequiredChildren().add(np);
			convertOptionalsToXORs(o);
		}
	}

	public boolean isLeaf(Feature f) {
		return f.getXorChildren().size() == 0
				&& f.getRequiredChildren().size() == 0
				&& f.getOptionalChildren().size() == 0;
	}

	public void findSets(Feature f, List<List<SparseBitSet>> sets) {
		if (f.getXorChildren().size() > 0) {
			sets.add(merge(f));
		} else {
			for (Feature c : f.getRequiredChildren()) {
				findSets(c, sets);
			}
		}
	}

	public SparseBitSet newSelection() {
		return new SparseBitSet();
	}

	public void select(Feature f, SparseBitSet sel) {
		if (f.getId() > -1)
			sel.set(f.getId());
		else {
			Feature curr = f.getParent();
			while (curr != null) {
				if (!curr.isTransparent()) {
					sel.set(curr.getId());
					break;
				} else {
					curr = curr.getParent();
				}
			}
		}
	}

	public void deselect(Feature f, SparseBitSet sel) {
		sel.clear(f.getId());
	}

	public List<SparseBitSet> sortedMerge(List<SparseBitSet> trg,
			List<SparseBitSet> mergin, Comparator<SparseBitSet> comp) {
		ArrayList<SparseBitSet> merged = new ArrayList<SparseBitSet>(trg.size()
				+ mergin.size());
		int i = 0;
		int j = 0;
		while (i < trg.size() && j < mergin.size()) {
			int v = comp.compare(trg.get(i), mergin.get(j));
			if (v == i) {
				merged.add(trg.get(i));
				merged.add(mergin.get(j));
				i++;
				j++;
			} else if (v < 0) {
				merged.add(trg.get(i));
				i++;
			} else {
				merged.add(mergin.get(j));
				j++;
			}
		}
		for (int k = i; k < trg.size(); k++) {
			merged.add(trg.get(k));
		}
		for (int k = j; k < mergin.size(); k++) {
			merged.add(mergin.get(k));
		}

		// trg.clear();
		// mergin.clear();

		return merged;
	}

	public List<SparseBitSet> merge(Feature f) {
		if (isLeaf(f)) {
			ArrayList<SparseBitSet> tf = new ArrayList<SparseBitSet>(1);
			SparseBitSet sol = newSelection();
			select(f, sol);
			// sol.setWeight(f.getConsumedResources());
			// sol.setValue(f.getValue());
			// sol.update();
			// sol.setDirty(false);
			tf.add(sol);
			return tf;
		} else if (f.getXorChildren().size() > 0) {
			List<SparseBitSet> tf = new ArrayList<SparseBitSet>();
			for (Feature c : f.getXorChildren()) {
				// This should be replaced with a call to:
				// tf = sortedMerge(tf,merge(c));
				// tf.addAll(merge(c));
				tf = sortedMerge(tf, merge(c), Collections
						.reverseOrder(valueComparator_));
			}
			return tf;
		} else {
			List<SparseBitSet> tf = merge(f.getRequiredChildren().get(0));
			for (int i = 1; i < f.getRequiredChildren().size(); i++) {
				List<SparseBitSet> b = merge(f.getRequiredChildren().get(i));
				if (b.size() > 1) {
					if (tf.size() > 1)
						tf = filteredCrossProduct(tf, b);
					else
						tf = b;
				}
			}
			return tf;
		}
	}

	public String toString(List<List<Feature>> choices) {
		String str = "[";
		for (List<Feature> choice : choices) {
			str += "(";
			for (Feature f : choice) {
				str += f.getName() + " ";
			}
			str += ")";
		}
		return str;
	}

	public List<SparseBitSet> filteredCrossProduct(List<SparseBitSet> a,
			List<SparseBitSet> b) {

		a = filter(a);
		b = filter(b);

		// System.out.println("Sorting:"+a.size());
		// Collections.sort(a,Collections.reverseOrder(valueComparator_));
		// System.out.println("Sorting:"+b.size());
		// Collections.sort(b,Collections.reverseOrder(valueComparator_));

		int asz = Math.min(maxCrossDim_, a.size());
		int bsz = Math.min(maxCrossDim_, b.size());
		ArrayList<SparseBitSet> tf = new ArrayList<SparseBitSet>(asz * bsz);

		// if (maxCrossDim_ < b.size() || maxCrossDim_ < a.size())
		// System.out.println("Pruning......:" + (a.size() - maxCrossDim_)
		// + " " + (b.size() - maxCrossDim_));
		// for(List<Feature> ta : a){
		for (int i = 0; i < asz && i < a.size(); i++) {
			for (int j = 0; j < bsz && j < b.size(); j++) {

				// should do a sorted merge here
				// we want a,b to come in sorted

				SparseBitSet sol = newSelection();
				sol.or(a.get(i));
				sol.or(b.get(j));
				tf.add(sol);
			}
		}

		// Collections.sort(tf,Collections.reverseOrder(valueComparator_));
		// System.out.println("asz:"+a.size());
		// System.out.println("bsz:"+b.size());
		// System.out.println("tfsz:"+tf.size());
		a.clear();
		b.clear();
		return tf;
	}

	public List<SparseBitSet> filter(List<SparseBitSet> set) {
		if (filter_ != null)
			return filter_.filter(set);
		else
			return set;
	}

	public void slice(Feature f, List<Feature> slices) {

		List<Feature> nroots = new ArrayList<Feature>(f.getOptionalChildren());

		for (Feature of : nroots)
			of.setParent(null);

		f.getOptionalChildren().clear();
		for (Feature oc : nroots) {
			slices.add(oc);
		}
		for (Feature c : f.getRequiredChildren())
			slice(c, slices);
		for (Feature c : f.getXorChildren())
			slice(c, slices);
	}

	public void collectTopXors(Feature f, List<Feature> xors) {
		if (f.getXorChildren().size() > 0) {
			xors.add(f);
		} else {
			for (Feature c : f.getRequiredChildren()) {
				collectTopXors(c, xors);
			}
			for (Feature c : f.getOptionalChildren()) {
				collectTopXors(c, xors);
			}
		}
	}

	public int random(int min, int max) {
		double rand = Math.random();
		double range = max - min;
		double delta = range * rand;
		return (int) Math.rint((min + delta));
	}

	public List<Feature> createOptProblem(Feature root, int[] res, int rmin,
			int rmax, int vmin, int vmax, int vtotal, boolean exactfit,
			int deltavmax, int deltavalloc) {
		List<Feature> txors = new ArrayList<Feature>();
		collectTopXors(root, txors);
		double optv = 0;

		int[] rorig = new int[res.length];
		System.arraycopy(res, 0, rorig, 0, res.length);

		List<Feature> ofeats = new ArrayList<Feature>();
		for (Feature xg : txors) {

			int opt = random(0, xg.getXorChildren().size() - 1);
			// int opt = 0;
			while (xg.getXorChildren().get(opt) instanceof NULLFeature)
				opt = random(0, xg.getXorChildren().size() - 1);
			for (int i = 0; i < xg.getXorChildren().size(); i++) {
				Feature f = xg.getXorChildren().get(i);
				f.setConsumedResources(new int[res.length]);

				if (i == opt) {
					ofeats.add(f);
				}
			}
		}

		deltavalloc = (int) Math.rint(((double) deltavalloc) / ofeats.size());

		for (int j = 0; j < ofeats.size(); j++) {
			Feature f = ofeats.get(j);
			if (j != ofeats.size() - 1) {
				int val = random(0, Math.min(vmax, vtotal));
				vtotal -= val;
				if (val >= 0)
					f.setValue(val);
			} else {
				if (vtotal >= 0)
					f.setValue(vtotal);
			}
			for (int i = 0; i < res.length; i++) {
				if (!exactfit) {
					// if (j != ofeats.size() - 1) {
					int v = random(0, Math.min(rmax, res[i]));
					f.getConsumedResources()[i] = v;
					res[i] -= v;
					// } else {
					// f.getConsumedResources()[i] = res[i];
					// }
				} else {
					if (j != ofeats.size() - 1) {
						int v = random(0, Math.min(rmax, res[i]));
						f.getConsumedResources()[i] = v;
						res[i] -= v;
					} else {
						f.getConsumedResources()[i] = res[i];
					}
				}
			}
		}

		for (Feature f : ofeats) {
			if (f.getParent() == null)
				continue;

			for (Feature c : f.getParent().getRequiredChildren()) {
				if (c instanceof NULLFeature)
					continue;
				if (c != f) {
					// int big = random(0, 1);
					// if (big == 1) {
					// updateBigFeatureProps(c, rmin, rmax, random(vtotal,
					// vtotal + deltavalloc), f.getConsumedResources());
					// } else {
					int vup = Math.max(0, f.getValue() - deltavmax);
					updateFeatureProps(c, rmin, rmax, Math.min(vmin, vup), vup,
							rorig);
					// }
				}
			}
			for (Feature c : f.getParent().getXorChildren()) {
				if (c != f) {
					int big = random(0, 1);
					if (big == 1) {
						updateBigFeatureProps(c, rmin, rmax, random(f
								.getValue(), f.getValue() + deltavalloc), f
								.getConsumedResources());
					} else {
						int vup = Math.max(0, f.getValue() - deltavmax);
						updateFeatureProps(c, rmin, rmax, Math.min(vmin, vup),
								vup, rorig);
					}
				}
			}

			distribute(f);
		}

		return ofeats;
	}

	public void distribute(Feature f) {
		f.setTag("tr:" + f.consumedResourcesToString() + " tv:" + f.getValue());
		for (Feature c : f.getRequiredChildren()) {
			if (c instanceof NULLFeature)
				continue;

			for (int i = 0; i < f.getConsumedResources().length; i++) {
				int v = random(0, f.getConsumedResources()[i]);
				f.getConsumedResources()[i] -= v;
				if (c.getConsumedResources() == null) {
					c
							.setConsumedResources(new int[f
									.getConsumedResources().length]);
				}
				c.getConsumedResources()[i] = v;
			}
			int v = random(0, f.getValue());
			f.setValue(f.getValue() - v);
			c.setValue(v);
			distribute(c);
		}
		if (f.getXorChildren().size() > 0) {
			int v = random(0, f.getValue());
			createOptProblem(f, f.getConsumedResources(), 0, 100, 0, f
					.getValue(), v, true, 0, 0);
			f.setConsumedResources(new int[f.getConsumedResources().length]);
			f.setValue(f.getValue() - v);
		}
	}

	public Feature getFeature(int index) {
		return featureMap_.get(index);
	}

	public Set<Feature> extract(SparseBitSet sel) {
		Set<Feature> features = new HashSet<Feature>(sel.size());
		for (Object o : sel.bitset) {
			Integer i = (Integer) o;
			Feature f = getFeature(i);
			features.add(f);
			f.getRequiredTree(features);
		}

		return features;
	}

	public void updateBigFeatureProps(Feature f, int rmin, int rmax, int v,
			int[] res) {
		f.setValue(v);
		for (int j = 0; j < res.length; j++) {
			f.getConsumedResources()[j] = random(res[j], 2 * res[j]);
		}
		distribute(f);
	}

	public void updateFeatureProps(Feature f, int rmin, int rmax, int vmin,
			int vmax, int[] res) {
		f.setValue(random(vmin, Math.max(0, vmax - 1)));
		for (int j = 0; j < res.length; j++) {
			f.getConsumedResources()[j] = random(rmin, rmax);
		}

		distribute(f);
	}

	public static void print(SparseBitSet sol) {
		System.out.print("(");
		System.out.println(sol.toString());
		System.out.print(")\n");
	}

	public static void print(List<SparseBitSet> choice) {
		System.out.println("[");
		for (SparseBitSet sol : choice) {
			print(sol);
		}
		System.out.println("]");
	}

	public Filter getFilter() {
		return filter_;
	}

	public void setFilter(Filter filter) {
		filter_ = filter;
	}

	public FeatureMap getFeatureMap() {
		return featureMap_;
	}

	public void setFeatureMap(FeatureMap featureMap) {
		featureMap_ = featureMap;
	}

	public int[] getWeight(Collection<Feature> features, int len) {
		if (features.size() > 0) {
			int[] res = new int[len];
			for (Feature f : features) {
				if (f.getConsumedResources() != null) {
					for (int i = 0; i < res.length; i++)
						res[i] += f.getConsumedResources()[i];

				}
			}
			return res;
		} else {
			return new int[0];
		}
	}

	public int getValue(Collection<Feature> features) {
		int value = 0;
		for (Feature f : features) {
			value += f.getValue();
		}
		return value;
	}

	public int getValue(SparseBitSet features) {
		int v = features.getValue();
		if (features.isDirty()) {
			v = getValue(extract(features));
			features.setValue(v);
			features.setDirty(false);
		}
		return v;
	}

	public double getNetValue(SparseBitSet features) {
		if (features.isDirty()) {
			int v = getValue(extract(features));
			int[] wght = getWeight(extract(features), 2);
			features.setValue(v);
			features.setWeight(wght);
			features.update();
			features.setDirty(false);
		}
		return features.getNetValue();
	}

	public int[] getWeight(SparseBitSet features, int len) {
		return getWeight(extract(features), len);
	}

	public static void main(String[] args) {
		
		if(args.length < 3){
			System.out.println("USAGE: java ... <total experiments> <total features> <output target>");
			return;
		}
		
		double pct = 0;
		double jpct = 0;
		double avgt = 0;
		double javgt = 0;
		double avgct = 0;
		double jbest = 0;
		double jworst = 1;
		double best = 0;
		double worst = 1;
		PrintStream out = null;
		try {
			if (!args[2].equalsIgnoreCase("system.out"))
				out = new PrintStream(new File(args[2]));
			else
				out = System.out;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		int tconv = 0;
		int totale = Integer.parseInt(args[0]);
		int totalf = Integer.parseInt(args[1]);
		for (int k = 0; k < totale; k++) {
			try {
				FMToMMKPConverter conv = new FMToMMKPConverter();
				conv.dim_ = 2;
				FeatureModelGenerator gen = new FeatureModelGenerator();
				Feature root = gen.createFeatureModel(totalf);
				FeatureMap fmap = new FeatureMap(root);
				conv.setFeatureMap(fmap);
				
				List<Feature> slices = new ArrayList<Feature>();
				conv.slice(root, slices);
				slices.add(0, root);

				int osets = 0;
				int vtotal = 100;
				int rmult = 0;
				int deltav = 10;
				int valloc = (int) Math.rint(((double) (deltav - 1))
						/ slices.size());
				int[] res = { 300, 400 };
				List<SparseBitSet> items = new ArrayList<SparseBitSet>();
				List<List<SparseBitSet>> rsets = null;
				List<List<SparseBitSet>> sets = new ArrayList<List<SparseBitSet>>();
				long totalt = 0;
				for (Feature t : slices) {
					rmult++;
					tconv++;

					long time = System.currentTimeMillis();
					conv.convertOptionalsToXORs(t);
					time = time - System.currentTimeMillis();

					int[] rn = new int[res.length];
					System.arraycopy(res, 0, rn, 0, res.length);
					List opt = conv.createOptProblem(t, rn, 0, 50, 0, 10,
							vtotal, true, deltav, valloc);

					if (opt.size() < 1) {
						rmult--;
						// System.out.println(
						// "need to handle this case better....");
					}

					if (opt.size() > 0)
						osets++;
					
					long start = System.currentTimeMillis();
					ArrayList<List<SparseBitSet>> tsets = new ArrayList<List<SparseBitSet>>();
					conv.findSets(t, tsets);
					for (List<SparseBitSet> set : tsets) {
						for (SparseBitSet sol : set) {
							items.add(sol);
						}
					}
					if (t != root)
						sets.addAll(tsets);
					else
						rsets = tsets;
					time += System.currentTimeMillis() - start;
					totalt += time;
				}

				for (Integer key : fmap.keySet()) {
					Feature f = fmap.get(key);
					if (f.getConsumedResources() == null)
						f.setConsumedResources(new int[res.length]);
				}

				if (sets.size() < 5) {
					k--;
					continue;
				}

				out.println("------------Converstion Stats [" + k
						+ "]--------------------");
				out.println("Conversion Time:" + totalt);

				// root.printTree("", "");

				Set<Feature> mand = new HashSet<Feature>();
				root.getRequiredSubTree(mand);
				mand.add(root);
				int[] overhead = conv.getWeight(mand, res.length);

				// removed b/c it is handled by the way the
				// problem is specd
				// for (int i = 0; i < overhead.length; i++) {
				// res[i] -= overhead[i];
				// }

				// Need to adjust to allow for a null item per set
				Integer[] mmkpsets = new Integer[items.size() + sets.size()];
				for (int i = 0; i < mmkpsets.length; i++)
					mmkpsets[i] = i;

				int[] values = new int[mmkpsets.length];
				int[][] sizes = new int[mmkpsets.length][res.length];
				for (int i = 0; i < mmkpsets.length; i++) {
					if (i < items.size()) {
						int[] size = conv.getWeight(items.get(i), res.length);
						sizes[i] = size;
						values[i] = conv.getValue(items.get(i));
					}
				}

				int[] setmap = new int[mmkpsets.length];
				int setnum = 0;
				int index = 0;
				for (List<SparseBitSet> set : rsets) {
					for (int i = 0; i < set.size(); i++) {
						setmap[index] = setnum;
						index++;
					}
					setnum++;
				}
				for (List<SparseBitSet> set : sets) {
					for (int i = 0; i < set.size(); i++) {
						setmap[index] = setnum;
						index++;
					}
					setnum++;
				}
				for (int i = 0; i < sets.size(); i++) {
					setmap[index + i] = i + 1;
				}

				for (int i = 0; i < res.length; i++)
					res[i] = res[i] * rmult;

				MMKPProblem problem = new MMKPProblem(mmkpsets, setmap, sizes,
						values, res);
				MMKPProblem copy = problem.cloneProblem();
				// System.out.println(problem);

				MMKP solver = new GreedyMMKP(problem);
				List<Item> solution = solver.solve(MMKP.DEFAULT_GOAL);
				if(solution == null)
					solver.setCurrentValue(0);
				double op = (solver.getCurrentValue() / (vtotal * osets));

				if (op > best)
					best = op;
				if (op < worst)
					worst = op;

				pct += op;
				out.println("----------[" + k + "]------------------");
				out.println("Total Features:" + totalf);
				out.println("Total Sets:" + (sets.size() + rsets.size()));
				out.println("Optimal Answer:" + (vtotal * osets));
				out.println("Result:" + solver.getCurrentValue());
				out.println("Solution:" + solution);
				out.println("Opt:" + op);
				out.println("Current Avg Opt:" + (pct / (k + 1)));
				out.println("Worst Opt:" + worst);
				out.println("Best Opt:" + best);

				solver = new JHEU(problem);
				solution = solver.solve(MMKP.DEFAULT_GOAL);
				if(solution == null)
					solver.setCurrentValue(0);
				op = (solver.getCurrentValue() / (vtotal * osets));

				if (op > jbest)
					jbest = op;
				if (op < jworst)
					jworst = op;

				jpct += op;
				out.println("----------J[" + k + "]------------------");
//				out.println("JHEU Total Features:" + totalf);
//				out.println("JHEU Total Sets:" + (sets.size() + rsets.size()));
//				out.println("JHEU Optimal Answer:" + (vtotal * osets));
				out.println("JHEU Result:" + solver.getCurrentValue());
//				out.println("JHEU Solution:" + solution);
				out.println("JHEU Opt:" + op);
//				out.println("JHEU Current Avg Opt:" + (jpct / (k + 1)));
//				out.println("JHEU Worst Opt:" + jworst);
//				out.println("JHEU Best Opt:" + jbest);
				
				solver = new CombinedMMKP(copy);
				solution = solver.solve(MMKP.DEFAULT_GOAL);
				double val = MMKP.DEFAULT_GOAL.getValue(solution);
				op = (val / (vtotal * osets));

				out.println("----------J[" + k + "]------------------");
//				out.println("Hybrid Total Features:" + totalf);
//				out.println("Hybrid Total Sets:" + (sets.size() + rsets.size()));
//				out.println("Hybrid Optimal Answer:" + (vtotal * osets));
				out.println("Hybrid Result:" + val);
				out.println("Hybrid Opt:" + op);
				
				out.println("Optimal Answer:" + (vtotal * osets));
				// if(op > 1){
				// System.out.println(problem);
				// System.out.println("Valid:"+problem.validSolution(solution));
				// }
				// if(op < .75){
				//					
				// for(Feature t : slices){
				// System.out.println("SET------------------------------");
				// t.printTree("", "");
				// }
				// System.out.println(problem);
				// solution = solver.solve(MMKP.DEFAULT_GOAL);
				// }

				// System.out.println("Sample:"+(k+1));
			} catch (Exception e) {
				k--;
				// e.printStackTrace();
			}
		}

		// System.out.println("Avg Opt: " + (pct / totale));
		// System.out.println("Total Sets:"+sets.size());

		// for(List<SparseBitSet> set : sets){
		// print(set);
		// }

		// long start = System.currentTimeMillis();
		// SparseBitSet bitset = new boolean[5000];
		// SparseBitSet bitset2 = new boolean[5000];
		// for (int k = 0; k < 1000000; k++) {
		// for (int i = 0; i < bitset.length; i++) {
		// bitset[i] = bitset[i] && bitset2[i];
		// }
		// }
		//		
		// long end = System.currentTimeMillis();
		// System.out.println("boolean time:"+(end-start));
		//		
		// start = System.currentTimeMillis();
		// HashSet sbitset = new HashSet(100);
		// HashSet sbitset2 = new HashSet(100);
		// for (int k = 0; k < 1000000; k++) {
		// for(Object o : sbitset2){
		// sbitset.add(o);
		// }
		// }
		// end = System.currentTimeMillis();
		// System.out.println("sparse time:"+(end-start));
		// Choice c = conv.getSelectionChoices(root);
		// System.out.println(c);
	}

	public MMKPProblem convertToMMKP(Feature root, int[] res, int dim) {

		dim_ = dim;
		FeatureMap fmap = new FeatureMap(root);
		setFeatureMap(fmap);

		List<Feature> slices = new ArrayList<Feature>();
		slice(root, slices);
		slices.add(0, root);

		int osets = 0;
		int vtotal = 100;
		int rmult = 0;
		int deltav = 10;
		List<SparseBitSet> items = new ArrayList<SparseBitSet>();
		List<List<SparseBitSet>> rsets = null;
		List<List<SparseBitSet>> sets = new ArrayList<List<SparseBitSet>>();
		long totalt = 0;
		for (Feature t : slices) {
			rmult++;

			convertOptionalsToXORs(t);

			int[] rn = new int[res.length];
			System.arraycopy(res, 0, rn, 0, res.length);

			osets++;
			ArrayList<List<SparseBitSet>> tsets = new ArrayList<List<SparseBitSet>>();
			findSets(t, tsets);
			for (List<SparseBitSet> set : tsets) {
				for (SparseBitSet sol : set) {
					items.add(sol);
				}
			}
			if (t != root)
				sets.addAll(tsets);
			else
				rsets = tsets;
		}

		Set<Feature> mand = new HashSet<Feature>();
		root.getRequiredSubTree(mand);
		mand.add(root);
		int[] overhead = getWeight(mand, res.length);

		Integer[] mmkpsets = new Integer[items.size() + sets.size()];
		for (int i = 0; i < mmkpsets.length; i++)
			mmkpsets[i] = i;

		int[] values = new int[mmkpsets.length];
		int[][] sizes = new int[mmkpsets.length][res.length];
		for (int i = 0; i < mmkpsets.length; i++) {
			if (i < items.size()) {
				int[] size = getWeight(items.get(i), res.length);
				sizes[i] = size;
				values[i] = getValue(items.get(i));
			}
		}

		int[] setmap = new int[mmkpsets.length];
		int setnum = 0;
		int index = 0;
		for (List<SparseBitSet> set : rsets) {
			for (int i = 0; i < set.size(); i++) {
				setmap[index] = setnum;
				index++;
			}
			setnum++;
		}
		for (List<SparseBitSet> set : sets) {
			for (int i = 0; i < set.size(); i++) {
				setmap[index] = setnum;
				index++;
			}
			setnum++;
		}
		for (int i = 0; i < sets.size(); i++) {
			setmap[index + i] = i + 1;
		}

		for (int i = 0; i < res.length; i++)
			res[i] = res[i] * rmult;

		MMKPProblem problem = new MMKPProblem(mmkpsets, setmap, sizes, values,
				res);

		return problem;
	}
}
