/**************************************************************************
 * Copyright 2009 Jules White                                              *
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ascent.Util;
import org.ascent.binpacking.ValueFunction;
import org.ascent.configurator.RefreshMatrixCore;
import org.ascent.configurator.conf.ExpressionParser;
import org.ascent.configurator.conf.ProblemParser;
import org.ascent.configurator.conf.RefreshProblem;
import org.ascent.probe.Target;
import org.eclipse.gmt.gems.css.parser.Attributes;
import org.eclipse.gmt.gems.css.parser.CSSParser;

public class FMRedux {

	private Comparator<FeatureSelection> valueComparator_ = new Comparator<FeatureSelection>() {

		public int compare(FeatureSelection o1, FeatureSelection o2) {
			return o1.getValue() - o2.getValue();
		}

	};

	private FeatureMap featureMap_;
	private FeatureSelectionFilter filter_;
	private int maxCrossDim_ = 20;

	public void findMandatoryFeatures(Feature f, List<Feature> mand) {
		mand.add(f);
		for (Feature c : f.getRequiredChildren()) {
			findMandatoryFeatures(c, mand);
		}
	}

	public void findSetRoots(Feature f, List<Feature> roots) {
		if (f.getXorChildren().size() > 0 || f.getOptionalChildren().size() > 0) {
			roots.add(f);
		} else {
			for (Feature c : f.getRequiredChildren()) {
				findSetRoots(c, roots);
			}
		}
	}

	public void findSets(Feature f, List<List<FeatureSelection>> sets) {
		if (f.getXorChildren().size() > 0) {
			sets.add(merge(f));
		} else {
			for (Feature c : f.getRequiredChildren()) {
				findSets(c, sets);
			}
		}
	}

	public List<FeatureSelection> merge(Feature f) {
		if (isLeaf(f)) {
			ArrayList<FeatureSelection> tf = new ArrayList<FeatureSelection>(1);
			FeatureSelection sol = newSelection();
			select(f, sol);
			tf.add(sol);
			return tf;
		} else if (f.getXorChildren().size() > 0) {
			List<FeatureSelection> tf = new ArrayList<FeatureSelection>();
			for (Feature c : f.getXorChildren()) {
				tf = sortedMerge(tf, merge(c), Collections
						.reverseOrder(valueComparator_));
			}
			return tf;
		} else {
			List<FeatureSelection> tf = merge(f.getRequiredChildren().get(0));
			for (int i = 1; i < f.getRequiredChildren().size(); i++) {
				List<FeatureSelection> b = merge(f.getRequiredChildren().get(i));
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

	public List<FeatureSelection> sortedMerge(List<FeatureSelection> trg,
			List<FeatureSelection> mergin, Comparator<FeatureSelection> comp) {
		ArrayList<FeatureSelection> merged = new ArrayList<FeatureSelection>(
				trg.size() + mergin.size());
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

		return merged;
	}

	public List<FeatureSelection> filteredCrossProduct(
			List<FeatureSelection> a, List<FeatureSelection> b) {

		a = filter(a);
		b = filter(b);

		int asz = Math.min(maxCrossDim_, a.size());
		int bsz = Math.min(maxCrossDim_, b.size());
		ArrayList<FeatureSelection> tf = new ArrayList<FeatureSelection>(asz
				* bsz);

		for (int i = 0; i < asz && i < a.size(); i++) {
			for (int j = 0; j < bsz && j < b.size(); j++) {

				// should do a sorted merge here
				// we want a,b to come in sorted

				FeatureSelection sol = newSelection();
				sol.or(a.get(i));
				sol.or(b.get(j));
				tf.add(sol);
			}
		}

		a.clear();
		b.clear();
		return tf;
	}

	public void select(Feature f, FeatureSelection sel) {
		if (f.getId() > -1)
			sel.add(f);
		else {
			Feature curr = f.getParent();
			while (curr != null) {
				if (!curr.isTransparent()) {
					sel.add(curr);
					break;
				} else {
					curr = curr.getParent();
				}
			}
		}
	}

	public List<FeatureSelection> filter(List<FeatureSelection> set) {
		if (filter_ != null)
			return filter_.filter(set);
		else
			return set;
	}

	public FeatureSelection newSelection() {
		return new FeatureSelection(featureMap_);
	}

	public boolean isLeaf(Feature f) {
		return f.getXorChildren().size() == 0
				&& f.getRequiredChildren().size() == 0
				&& f.getOptionalChildren().size() == 0;
	}

	public int[] getSize(Collection<Feature> feats) {
		int[] size = null;
		if (feats.size() > 0) {
			size = new int[feats.iterator().next().getConsumedResources().length];
			for (Feature f : feats) {
				for (int i = 0; i < size.length; i++) {
					size[i] += f.getConsumedResources()[i];
				}
			}
		}

		return size;
	}

	public FeatureSelection selectFeatures(Feature f, int[] res) {
		featureMap_ = new FeatureMap(f);
		List<Feature> froots = new ArrayList<Feature>();
		findSetRoots(f, froots);
		List<List<FeatureSelection>> sets = new ArrayList<List<FeatureSelection>>();
		findSets(f, sets);
		List<Feature> mand = new ArrayList<Feature>();
		findMandatoryFeatures(f, mand);

		int[] msize = getSize(mand);
		System.out.print("Mandatory Size[");
		for(int i = 0; i < msize.length; i++){
			System.out.print(msize[i]+",");
		}
		System.out.print("]\n");
		
		System.out.print("Mandatory [");
		for (Feature m : mand) {
			System.out.print(m.getName() + ",");
		}
		System.out.print("]\n");

		for (List<FeatureSelection> set : sets) {
			System.out.println("----------------Set-----------------");
			for (FeatureSelection s : set) {
				System.out.println(s);
			}
		}
		
		List<FeatureSelection> itemslist = new ArrayList<FeatureSelection>();
		
		for(int j = 0; j < sets.size(); j++){
			List<FeatureSelection> set = sets.get(j);			
			int count = itemslist.size();
			itemslist.addAll(set);						
		}
		
		int[] values = new int[itemslist.size()];
		int[][] sizes = new int[itemslist.size()][res.length];
		for(int i = 0; i < itemslist.size(); i++){
			int[] size = getSize(itemslist.get(i).extractWithRequired());
			for(int k = 0; k < size.length; k++)
				size[k] -= msize[k];
			sizes[i] = size;
			values[i] = itemslist.get(i).getValue();
		}
		
		int[] setmap = new int[itemslist.size()];
		int count = 0;
		for(int j = 0; j < sets.size(); j++){
			List<FeatureSelection> set = sets.get(j);			
			for(int i = 0; i < set.size(); i++){
				setmap[count + i] = j;
			}
			count += set.size();
		}
		
		for(int k = 0; k < res.length; k++)
			res[k] -= msize[k];
		
		FeatureSelection[] items = itemslist.toArray(new FeatureSelection[0]);
		MMKPProblem problem = new MMKPProblem(items,setmap,sizes,values,res);
		System.out.println(problem);
		
		MMKP solver = new JHEU(problem);
		List<Item> solution = solver.solve(new ValueFunction<Collection>() {
		
			public double getValue(Collection src) {
				double score = 0;
				for(Object o : src){
					score += ((Item)o).getValue();
				}
				return score;
			}
		
		});
		FeatureSelection sel = new FeatureSelection(featureMap_);
		for(Item i : solution){
			sel.or((FeatureSelection)i.getItem());
		}
		System.out.println(solution);
		System.out.println("val:"+sel.getValue());
		return sel;
	}

	public static void main(String[] args) {
		try {
			String fm = FileUtils.readFileToString(new File(args[0]), null);
			Map<String, Feature> lookup = new HashMap<String, Feature>();
			Feature f = readFeatureModel(fm, lookup);
			f.printTree("", "");

			ProblemParser parser = new ProblemParser();
			parser.setErrorOnUndefinedDirectives(false);
			RefreshProblem problem = parser.parseSourceProblem(fm);
			Target target = Target.loadFrom("LocalHost{}");
			problem.getTargetItems().add(target);

			Map<String,Integer> vals = new HashMap<String, Integer>();
			FeatureMap map = new FeatureMap(f);
			for(Integer i : map.keySet()){
				int v = Util.random(1, 15);
				map.get(i).setValue(v);
				vals.put(map.get(i).getName(),v);
			}
			
			Map<String, Feature> features = new HashMap<String, Feature>();
			for (Object src : problem.getSourceItems()) {
				Feature feat = lookup.get(src);
				
				if(vals.get(feat.getName().trim())==null)
					System.out.println(feat.getName());
				Integer val = (vals.get(feat.getName().trim()) != null)? vals.get(feat.getName().trim()) : 0;
				feat.setValue(val);
				int bw = Util.random(10, 15);
				problem.getSourceVariableValues(src).put("Bandwidth", bw);
				problem.getSourceVariableValues(src).put("Value", vals.get(feat.getName()));
				feat.setConsumedResources(new int[] { bw });
				features.put(feat.getName(), feat);
				// problem.getSourceVariableValues(src).put("RAM",
				// FeatureModelGenerator.random(5, 20));
				// problem.getSourceVariableValues(src).put("CPU",
				// FeatureModelGenerator.random(5, 20));
			}

			problem.getTargetResourceConstraintsMap(target).put("Bandwidth",
					3000);
			// problem.getTargetResourceConstraintsMap(target).put("RAM", 1500);
			// problem.getTargetResourceConstraintsMap(target).put("CPU", 1500);
			long fcfstart = System.currentTimeMillis();

			
			
			
			FMRedux redux = new FMRedux();
			redux.selectFeatures(f, new int[]{3000});
			
			
			RefreshMatrixCore core = new RefreshMatrixCore();
			problem.inject(core);
			
			long start = System.currentTimeMillis();
			core.setOptimizationFunction(ExpressionParser.getExpression("Source.Value.Sum"));
			core.setMaximizeOptimizationFunction(true);
			Map<Object,List> sol = core.nextMapping();
			int val = 0;
			for(Object o : sol.keySet()){
				if(sol.get(o).size() > 0){
					val += features.get(o).getValue();
				}
//				System.out.println("csp:"+o);
			}
			System.out.println("Solution Value: "+val);
			
			System.out.println("Solution Found:"+(sol!=null));
			System.out.println("Time:"+(System.currentTimeMillis()-start));
			
			
			//		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Feature readFeatureModel(String fm,
			Map<String, Feature> lookup) {
		List<Attributes> attrs = CSSParser.parseAttributes(fm);

		Feature root = null;
		int id = 0;

		for (Attributes feat : attrs) {
			String name = feat.getSelector().trim();
			Feature feature = lookup.get(name);
			if (feature == null) {
				feature = new Feature(name, id++);
				lookup.put(name, feature);
			}
			if (root == null)
				root = feature;

			String required = feat.getProperty("Requires");
			String xor = feat.getProperty("Select");

			if (required != null) {
				String[] reqids = required.split(",");
				for (String reqid : reqids) {
					reqid = reqid.trim();
					Feature f = lookup.get(reqid);
					if (f == null) {
						f = new Feature(reqid, id++);
						lookup.put(reqid, f);
						feature.getRequiredChildren().add(f);
						f.setParent(feature);
					}
				}
			}
			if (xor != null) {
				String[] xids = xor.split(",");
				for (int i = 1; i < xids.length; i++) {
					String reqid = xids[i];
					reqid = reqid.trim();
					Feature f = lookup.get(reqid);
					if (f == null) {
						f = new Feature(reqid, id++);
						lookup.put(reqid, f);
					}
					feature.getXorChildren().add(f);
					f.setParent(feature);
				}
			}
		}

		return root;
	}
}
