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

package org.ascent.configurator.featuremodeling;

import java.util.HashSet;
import java.util.Set;

import org.ascent.mmkp.SparseBitSet;

public class FeatureSelection {
	private SparseBitSet bits_ = new SparseBitSet();
	private FeatureMap featureMap_;

	private int[] size_;
	private int value_ = 0;
	private boolean dirty_ = true;

	public FeatureSelection(FeatureMap map) {
		featureMap_ = map;
	}

	public Set<Feature> extract() {
		Set<Feature> features = new HashSet<Feature>(bits_.size());
		for (Object o : bits_.bitset) {
			Integer i = (Integer) o;
			Feature f = getFeature(i);
			features.add(f);
			f.getRequiredTree(features);
		}

		return features;
	}

	public Set<Feature> extractWithRequired() {
		Set<Feature> features = extract();
		Set<Feature> all = new HashSet<Feature>();
		for(Feature f : features){
			getRequired(f, all);
		}
		return all;
	}

	public void getRequired(Feature f, Set<Feature> visited) {

		if (!visited.contains(f)) {
			visited.add(f);

			if (f.getParent() != null) {
				getRequired(f.getParent(), visited);
			}
			for (Feature c : f.getRequiredChildren()) {
				getRequired(c, visited);
			}
		}
	}

	public Feature getFeature(int index) {
		return featureMap_.get(index);
	}

	public int getValue() {
		if (dirty_) {
			value_ = 0;
			for (Feature f : extract()) {
				value_ += f.getValue();
			}
		}
		return value_;
	}

	public int[] getSize() {
		if (dirty_) {
			Set<Feature> feats = extract();
			if (feats.size() > 0) {
				size_ = new int[feats.iterator().next().getConsumedResources().length];
				for (Feature f : extract()) {
					for (int i = 0; i < size_.length; i++) {
						size_[i] += f.getConsumedResources()[i];
					}
				}
			}
		}
		return size_;
	}

	public void add(Feature f) {
		bits_.set(f.getId());
	}

	public void or(FeatureSelection other) {
		bits_.or(other.bits_);
	}

	public String toString() {
		String selstr = "[";
		Set<Feature> feats = extractWithRequired();
		for (Feature f : feats) {
			selstr += f.getName() + ",";
		}
		selstr += "]";
		return selstr;
	}
}
