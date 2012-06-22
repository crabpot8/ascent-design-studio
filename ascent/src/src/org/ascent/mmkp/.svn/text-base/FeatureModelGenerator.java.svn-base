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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.ascent.configurator.RefreshCore;


public class FeatureModelGenerator {

	private int id_ = 0;
	private int maxDepth_ = 3;

	private int crossTreeConstraintTotal_;

	public synchronized int id() {
		id_++;
		return id_;
	}

	private int total_;
	private List<Feature> features_ = new ArrayList<Feature>();

	public int random(int min, int max) {
		double rand = Math.random();
		double range = max - min;
		double delta = range * rand;
		return (int) Math.rint((min + delta));
	}

	public Feature createFeatureModel(RefreshCore core, int totalfeatures) {
		return createFeatureModel(core, totalfeatures, 0);
	}

	public Feature createFeatureModel(RefreshCore core, int totalfeatures,
			int crosscons) {

		total_ = totalfeatures - 1;
		Feature root = createFeature();

		while (total_ > 0) {
			createMandatoryChild(root);
		}

		for (int i = 0; i < crosscons; i++) {
			int par = random(5, features_.size() - 1);
			Feature parent = features_.get(par);
			int mode = random(0, 1);

			if (features_.size() > 2 && parent != root) {
				int trg = random(parent.getId(), features_.size() - 2);
				
				while(trg >= features_.size())
					trg--;
				Feature target = features_.get(trg);

				while (target == root || target == parent
						|| parent.isAncestor(target)
						|| parent.isDescendant(target)
						|| target.isAncestor(parent)
						|| target.isDescendant(parent)
						|| target.getParent() == parent.getParent()
						|| parent.getCrossTreeExcludes().contains(target)
						|| parent.getCrossTreeRequires().contains(target)
						|| target.getCrossTreeExcludes().contains(target)
						|| target.getCrossTreeRequires().contains(target)) {
					trg = random(0, features_.size() - 2);
					while(trg >= features_.size())
						trg--;
					target = features_.get(trg);
				}

				if (mode == 0) {
					parent.getCrossTreeExcludes().add(features_.get(trg));
				} else {
					parent.getCrossTreeRequires().add(features_.get(trg));
				}
			} else {
				break;
			}
		}

		// System.out.print("&");

		core
				.setSetsToMap(features_, Arrays
						.asList(new String[] { "selected" }));

		root.insert(core);

		core.requireMapped(root);

		// root.printTree("", "");

		// System.out.print(">");

		return root;
	}

	// public void createCrossTreeConstraint(){
	// for (int i = 0; i < crosscons; i++) {
	// int par = random(0,features_.size()-1);
	// Feature parent = features_.get(par);
	// int mode = random(0, 1);
	//
	// if (features_.size() > 2 && parent != root) {
	// int trg = random(parent.getId(), features_.size() - 2);
	// Feature target = features_.get(trg);
	//				
	// while(target == root ||target == parent || parent.isAncestor(target) ||
	// parent.isDescendant(target) ||
	// parent.getCrossTreeExcludes().contains(target) ||
	// parent.getCrossTreeRequires().contains(target) ||
	// target.getCrossTreeExcludes().contains(target) ||
	// target.getCrossTreeRequires().contains(target)){
	// trg = random(0, features_.size() - 2);
	// target = features_.get(trg);
	// }
	//				
	// if (mode == 0) {
	// parent.getCrossTreeExcludes().add(features_.get(trg));
	// } else {
	// parent.getCrossTreeRequires().add(features_.get(trg));
	// }
	// } else {
	// break;
	// }
	// }
	// }

	public Feature createFeatureModel(int totalfeatures) {

		total_ = totalfeatures - 1;
		Feature root = createFeature();

		while (total_ > 0) {
			createMandatoryChild(root);
		}

		// root.printTree("", "");

		return root;
	}

	public Feature createFeature() {
		maxDepth_--;
		int id = id();
		Feature parent = new Feature("" + id, id);
		features_.add(parent);

		int children = (total_ > 15) ? 15 : total_;
		if(maxDepth_ == 0)
			children = 0;
		else
			children = random(0, children);
		total_ -= children;

		if (children > 0) {
			int xor = random(0, 1);
			if (xor == 0) {
				for (int i = 0; i < children; i++) {
					int r = random(0, 4);
					if (r < 4) {
						createMandatoryChild(parent);
					} else {
						createOptionalChild(parent);
					}
				}
			} else {
				createFeatureXorGroup(parent, children);
			}
		}
		maxDepth_++;
		return parent;
	}

	public void createMandatoryChild(Feature parent) {
		Feature child = createFeature();
		child.setParent(parent);
		parent.getRequiredChildren().add(child);
	}

	public void createOptionalChild(Feature parent) {
		Feature child = createFeature();
		child.setParent(parent);
		parent.getOptionalChildren().add(child);
	}

	public void createFeatureXorGroup(Feature parent, int size) {
		for (int i = 0; i < size; i++) {
			Feature child = createFeature();
			child.setParent(parent);
			parent.getXorChildren().add(child);
		}
	}

	public List<Feature> getFeatures() {
		return features_;
	}

	public void setFeatures(List<Feature> features) {
		features_ = features;
	}

}
