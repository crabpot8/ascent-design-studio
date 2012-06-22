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

package org.ascent.configurator.featuremodeling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ascent.configurator.AbstractRefreshChocoCore;
import org.ascent.configurator.RefreshCore;

public class Feature implements Serializable {

	private static int maxObservationErrors_ = 0;
	private static boolean allowCrossTreeConstraints_ = true;

	private int id_;
	private String name_;
	private Feature parent_;
	private boolean transparent_ = false;
	private int[] consumedResources_;
	private String tag_;
	private int value_ = 0;
	private List<Feature> crossTreeDependants_ = new ArrayList<Feature>();
	private List<Feature> requiredChildren_ = new ArrayList<Feature>();
	private List<Feature> crossTreeExcludes_ = new ArrayList<Feature>();
	private List<Feature> crossTreeRequires_ = new ArrayList<Feature>();
	private List<Feature> optionalChildren_ = new ArrayList<Feature>();
	private List<Feature> xorChildren_ = new ArrayList<Feature>();

	public Feature(String name, int id) {
		super();
		name_ = name;
		id_ = id;
	}

	public int getId() {
		return id_;
	}

	public void setId(int id) {
		id_ = id;
	}

	public String getName() {
		return name_;
	}

	public void setName(String name) {
		name_ = name;
	}

	public List<Feature> getRequiredChildren() {
		return requiredChildren_;
	}

	public void setRequiredChildren(List<Feature> requiredChildren) {
		requiredChildren_ = requiredChildren;
	}

	public List<Feature> getOptionalChildren() {
		return optionalChildren_;
	}

	public void setOptionalChildren(List<Feature> optionalChildren) {
		optionalChildren_ = optionalChildren;
	}

	public List<Feature> getXorChildren() {
		return xorChildren_;
	}

	public void setXorChildren(List<Feature> xorChildren) {
		xorChildren_ = xorChildren;
	}

	public List<Feature> getCrossTreeExcludes() {
		return crossTreeExcludes_;
	}

	public void setCrossTreeExcludes(List<Feature> crossTreeExcludes) {
		crossTreeExcludes_ = crossTreeExcludes;
		updateCrossTreeExcludes(crossTreeExcludes);
	}

	public List<Feature> getCrossTreeDependants() {
		return crossTreeDependants_;
	}

	public void setCrossTreeDependants(List<Feature> crossTreeDependants) {
		crossTreeDependants_ = crossTreeDependants;
	}

	public List<Feature> getCrossTreeRequires() {
		return crossTreeRequires_;
	}

	public void setCrossTreeRequires(List<Feature> crossTreeRequires) {
		crossTreeRequires_ = crossTreeRequires;
		updateCrossTreeRequires(crossTreeRequires);
	}

	public int getValue() {
		return value_;
	}

	public void setValue(int value) {
		value_ = value;
	}

	public boolean isAncestor(Feature f) {
		if (f.getId() > getId())
			return false;

		if (parent_ == null) {
			return false;
		} else if (parent_ == f) {
			return true;
		} else {
			return parent_.isAncestor(f);
		}
	}

	public boolean isDescendant(Feature f) {
		if (f.getId() < getId())
			return false;

		for (Feature c : requiredChildren_) {
			if (c == f)
				return true;

			if (c.isDescendant(f))
				return true;
		}
		for (Feature c : xorChildren_) {
			if (c == f)
				return true;

			if (c.isDescendant(f))
				return true;
		}
		for (Feature c : optionalChildren_) {
			if (c == f)
				return true;

			if (c.isDescendant(f))
				return true;
		}

		return false;
	}

	public Feature getParent() {
		return parent_;
	}

	public void setParent(Feature parent) {
		parent_ = parent;
	}

	public void insert(RefreshCore core) {
		for (Feature f : requiredChildren_) {
			core.addRequiresMappingConstraint(this, f);
			core.addRequiresMappingConstraint(f, this);
		}
		for (Feature f : optionalChildren_) {
			core.addRequiresMappingConstraint(f, this);
		}
		for (Feature f : crossTreeRequires_) {
			core.addRequiresMappingConstraint(this, f);
		}
		for (Feature f : crossTreeExcludes_) {
			core.addExcludesMappingConstraint(this, f);
		}

		for (Feature f : xorChildren_) {
			core.addRequiresMappingConstraint(f, this);
			for(Feature g : xorChildren_){
				if(g != f){
					core.addExcludesMappingConstraint(f, g);
				}
			}
		}
		if (xorChildren_.size() > 0) {
			core.addSelectMappingConstraint(this, xorChildren_, 1);
		}
		if (getConsumedResources() != null){
			for(int i = 0; i < getConsumedResources().length; i++){
				core.getSourceVariableValues(this).put(""+i, getConsumedResources()[i]);
			}
		}
		core.getSourceVariableValues(this).put("value", getValue());

		ArrayList<Feature> all = new ArrayList<Feature>();
		all.addAll(xorChildren_);
		all.addAll(requiredChildren_);
		all.addAll(optionalChildren_);
		for (Feature f : all) {
			f.insert(core);
		}

	}

	public void observe(AbstractRefreshChocoCore core, boolean o) {
		core.createObservation(this, o);
		ArrayList<Feature> all = new ArrayList<Feature>();
		all.addAll(xorChildren_);
		all.addAll(requiredChildren_);
		all.addAll(optionalChildren_);
		for (Feature f : all) {
			f.observe(core, o);
		}
	}

	public int random(int min, int max) {
		if (min == max)
			return min;

		double rand = Math.random();
		double range = max - min;
		double delta = range * rand;
		return (int) Math.rint((min + delta));
	}

	public void observe(AbstractRefreshChocoCore core, int errorrate, boolean o) {
		core.createObservation(this, o);

		for (Feature f : requiredChildren_) {
			int r = random(0, errorrate);
			if (maxObservationErrors_ > 0 && r == 0) {
				maxObservationErrors_--;
			} else {
				r = 1;
			}
			if (r == 0 && o) {
				f.observe(core, errorrate, false);
			} else if (r == 0 && !o) {
				f.observe(core, errorrate, true);
			} else {
				f.observe(core, errorrate, o);
			}
		}

		for (Feature f : optionalChildren_) {
			f.observe(core, errorrate, false);
		}
		if (xorChildren_.size() > 0 && o) {
			xorChildren_.get(0).observe(core, errorrate, true);
			for (int i = 1; i < xorChildren_.size(); i++) {
				xorChildren_.get(i).observe(core, errorrate, false);
			}
		}
	}

	public void insertAndClear(RefreshCore core) {
		for (Feature f : requiredChildren_) {
			core.addRequiresMappingConstraint(this, f);
			core.addRequiresMappingConstraint(f, this);
		}
		for (Feature f : optionalChildren_) {
			core.addRequiresMappingConstraint(f, this);
		}

		List<Feature> xors = new ArrayList<Feature>();
		for (Feature f : xorChildren_) {
			core.addRequiresMappingConstraint(f, this);
		}
		if (xorChildren_.size() > 0) {
			core.addSelectMappingConstraint(this, xorChildren_, 1);
		}

		ArrayList<Feature> all = new ArrayList<Feature>();
		all.addAll(xorChildren_);
		all.addAll(requiredChildren_);
		all.addAll(optionalChildren_);

		xorChildren_.clear();
		requiredChildren_.clear();
		optionalChildren_.clear();

		for (Feature f : all) {
			f.insert(core);
		}
	}

	public void validate(Map<Object, List> sol) {
		boolean selected = sol.get(this).size() > 0;

		ArrayList<Feature> all = new ArrayList<Feature>();
		all.addAll(xorChildren_);
		all.addAll(requiredChildren_);
		all.addAll(optionalChildren_);
		for (Feature f : all) {
			if (!selected && sol.get(f).size() > 0)
				throw new RuntimeException(f
						+ " cannot be selected without its parent " + this);
			f.validate(sol);
		}

		for (Feature f : requiredChildren_) {
			if (sol.get(f).size() == 0 && selected)
				throw new RuntimeException(getName() + " requires " + f);
		}

		for (Feature f : crossTreeRequires_) {
			if (selected && sol.get(f).size() == 0)
				throw new RuntimeException(getName() + " requires " + f);
		}

		for (Feature f : crossTreeExcludes_) {
			if (selected && sol.get(f).size() == 1)
				throw new RuntimeException(getName() + " excludes " + f);
		}

		int count = 0;
		for (Feature f : xorChildren_) {
			if (sol.get(f).size() > 0)
				count++;
		}

		if (count != 1 && xorChildren_.size() != 0 && selected)
			throw new RuntimeException(getName() + " requires at most one of "
					+ xorChildren_ + " to be selected");

	}

	public void updateCrossTreeExcludes(List<Feature> crossTreeExcludes) {
		for (Feature f : crossTreeExcludes) {
			if (!f.getCrossTreeExcludes().contains(this))
				f.getCrossTreeExcludes().add(this);
		}
	}

	public void updateCrossTreeRequires(List<Feature> crossTreeRequires) {
		for (Feature f : crossTreeRequires) {
			if (!f.getCrossTreeDependants().contains(this))
				f.getCrossTreeDependants().add(this);
		}
	}

	public void collectCrossTreeConstraints(List<Feature> cr) {
		cr.addAll(getCrossTreeRequires());
		cr.addAll(getCrossTreeExcludes());
		for (Feature f : requiredChildren_) {
			f.collectCrossTreeConstraints(cr);
		}
	}

	public void collectConsumedResources(int[] cr) {
		for (int i = 0; i < cr.length; i++)
			cr[i] += consumedResources_[i];

		for (Feature f : requiredChildren_) {
			f.collectConsumedResources(cr);
		}
	}

	public void propagateConstraints() {
		for (Feature f : requiredChildren_) {
			f.getCrossTreeRequires().addAll(getCrossTreeRequires());
			f.updateCrossTreeRequires(getCrossTreeRequires());
			f.getCrossTreeExcludes().addAll(getCrossTreeExcludes());
			f.updateCrossTreeExcludes(getCrossTreeExcludes());
			propagateConsumedResources(f);
		}
		for (Feature f : optionalChildren_) {
			f.getCrossTreeRequires().addAll(getCrossTreeRequires());
			f.updateCrossTreeRequires(getCrossTreeRequires());
			f.getCrossTreeExcludes().addAll(getCrossTreeExcludes());
			f.updateCrossTreeExcludes(getCrossTreeExcludes());
			propagateConsumedResources(f);
		}
		for (Feature f : xorChildren_) {
			f.getCrossTreeRequires().addAll(getCrossTreeRequires());
			f.updateCrossTreeRequires(getCrossTreeRequires());
			f.getCrossTreeExcludes().addAll(getCrossTreeExcludes());
			f.updateCrossTreeExcludes(getCrossTreeExcludes());
			propagateConsumedResources(f);
		}
	}

	public void propagateConsumedResources(Feature f) {
		if (consumedResources_ != null) {
			for (int i = 0; i < consumedResources_.length; i++) {
				f.consumedResources_[i] += consumedResources_[i];
			}
		}
	}

	public String toString() {
		return getName();//+" MCV:"+getMaxConfValue();
	}

	public String consumedResourcesToString(){
		String str = "";
		if(consumedResources_ != null){
			str += "(";
			for(int i = 0; i < consumedResources_.length; i++){
				str += consumedResources_[i];
				if(i != consumedResources_.length-1){
					str += ",";
				}
			}
			str += ")";
		}
		return str;
	}
	
	public void printTree(String pred, String indent) {
		String post = (isTransparent()) ? "_T" : "";
		post += consumedResourcesToString() + " v:"+value_+" MCV:"+getMaxConfValue();
		if(tag_ != null){post += " tag:"+tag_;}
		
		System.out.println(indent + pred + getName() + post);
		ArrayList<Feature> all = new ArrayList<Feature>();
		for (Feature f : requiredChildren_) {
			f.printTree("[m]", indent + " ");
		}
		for (Feature f : crossTreeRequires_) {
			System.out.println(indent + " " + "[m*]" + f.getName());
		}
		for (Feature f : crossTreeExcludes_) {
			System.out.println(indent + " " + "[!*]" + f.getName());
		}
		for (Feature f : optionalChildren_) {
			f.printTree("[o]", indent + " ");
		}
		for (Feature f : xorChildren_) {
			f.printTree("[x]", indent + " ");
		}
	}

	public static int getMaxObservationErrors() {
		return maxObservationErrors_;
	}

	public static void setMaxObservationErrors(int maxErrors_) {
		Feature.maxObservationErrors_ = maxErrors_;
	}

	public String getTag() {
		return tag_;
	}

	public void setTag(String tag) {
		tag_ = tag;
	}
	
	public void getRequiredTree(Set<Feature> set){
		Feature curr = this;
		while(curr.getParent() != null){
			curr = curr.getParent();
			set.add(curr);
			curr.getRequiredSubTree(set);
		}
		curr.getRequiredSubTree(set);
	}
	
	public void getRequiredSubTree(Set<Feature> set){
		for(Feature f : requiredChildren_){
			set.add(f);
			f.getRequiredSubTree(set);
		}
		
	}

	public void collectFeatures(List<Feature> list) {
		ArrayList<Feature> all = new ArrayList<Feature>();
		all.addAll(xorChildren_);
		all.addAll(requiredChildren_);
		all.addAll(optionalChildren_);

		for (Feature f : all) {
			f.collectFeatures(list);
		}
		list.addAll(all);
	}

	public boolean isTransparent() {
		return transparent_;
	}

	public void setTransparent(boolean transparent) {
		transparent_ = transparent;
	}

	public int[] getConsumedResources() {
		return consumedResources_;
	}

	public void setConsumedResources(int[] consumedResources) {
		consumedResources_ = consumedResources;
	}

	public int getMaxConfValue(){
		int v = getValue();
		for(Feature f : getRequiredChildren()){
			v += f.getMaxConfValue();
		}
		for(Feature f : getOptionalChildren()){
			v += f.getMaxConfValue();
		}
		
		int max = 0;
		for(Feature f : getXorChildren()){
			if(max < f.getMaxConfValue())
				max = f.getMaxConfValue();
		}
		v += max;
		return v;
	}
	
	public int getTotalDescendants(){
		int d= 0;
		for(Feature f : getRequiredChildren()){
			d += f.getTotalDescendants() + 1;
		}
		for(Feature f : getOptionalChildren()){
			d += f.getTotalDescendants() + 1;
		}
		for(Feature f : getXorChildren()){
			d += f.getTotalDescendants() + 1;
		}
		return d;
	}
	
	public Feature cloneTree(){
		
		Feature f = new Feature(getName(),id_);
		f.setValue(getValue());
		f.setTag(getTag());
		f.setTransparent(isTransparent());
		f.setConsumedResources(getConsumedResources());
		for(Feature c : requiredChildren_){
			Feature nc = c.cloneTree();
			f.getRequiredChildren().add(nc);
			nc.setParent(f);
		}
		for(Feature c : xorChildren_){
			Feature nc = c.cloneTree();
			f.getXorChildren().add(nc);
			nc.setParent(f);
		}
		for(Feature c : optionalChildren_){
			Feature nc = c.cloneTree();
			f.getOptionalChildren().add(nc);
			nc.setParent(f);
		}
		return f;
	}
}
