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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.configurator.UnsolvableException;

public class ItemState extends AbstractState {
	
	private Map attributes_;
	private List currentTargets_;
	private List validTargets_;
	private List validPresets_;
	private List requiredTargets_;
	private List excludedTargets_;
	private Map<Object,List> excludersMap_;
	private List allTargets_;

	private int currentInstances_ = 0;
	private int minInstances_ = 0;
	private int maxInstances_ = Integer.MAX_VALUE;
	private int[] sizeWithDependencies_;
	

	public ItemState() {
	}

	public ItemState(List vtargets) {
		validTargets_ = vtargets;
	}

	public List getValid() {
		return validTargets_;
	}

	public List getValidPresets() {
		return validPresets_;
	}

	public void setValidPresets(List validPresets) {
		validPresets_ = validPresets;
	}

	public List getCurrentTargets() {
		if (currentTargets_ == null)
			currentTargets_ = new ArrayList();
		return currentTargets_;
	}

	public void setCurrentTargets(List currentTargets) {

		currentTargets_ = currentTargets;
	}

	public void setValid(List validTargets) {
		validPresets_ = new ArrayList();
		if (validTargets != null)
			validPresets_.addAll(validTargets);

		validTargets_ = validTargets;
	}

	public void setRequired(List requiredTargets) {
		requiredTargets_ = requiredTargets;
	}

	public void setExcluded(List excludedTargets) {
		excludedTargets_ = excludedTargets;
	}

	public int getCurrentInstances() {
		return currentInstances_;
	}

	public void setCurrentInstances(int currentInstances) {
		currentInstances_ = currentInstances;
	}

	public int getMinInstances() {
		return minInstances_;
	}

	public void setMinInstances(int minInstances) {
		minInstances_ = minInstances;
	}

	public int getMaxInstances() {
		return maxInstances_;
	}

	public void setMaxInstances(int maxInstances) {
		maxInstances_ = maxInstances;
	}

	public boolean needsFurtherMapping() {
		return currentInstances_ < minInstances_;
	}

	public boolean canMapFurther() {
		return currentInstances_ < maxInstances_;
	}

	public void addTarget(Object target) {
		if (!canMapFurther())
			throw new UnsolvableException(
					"A components was mapped to more targets than its maximum number of instances.");

		currentInstances_++;
		getCurrentTargets().add(target);

		if (currentInstances_ == maxInstances_) {
			for (Object t : requiredTargets_) {
				if (!getCurrentTargets().contains(t)) {
					throw new UnsolvableException(
							"A component needed to be mapped to more instances than its total instance count.");
				}
			}
		}
	}

	public void requireTarget(Object target) {
		if (excludedTargets_ != null && excludedTargets_.contains(target))
			throw new UnsolvableException(
					"A component was both excluded and required to be mapped to the same target.");
		if (validTargets_ != null && !validTargets_.contains(target))
			throw new UnsolvableException(
					"A component was both required to be mapped to a target that cannot support it.");

		getRequired().add(target);
	}

	public void excludeTarget(Object target, Object excluder) {
		if (requiredTargets_ != null && requiredTargets_.contains(target))
			throw new UnsolvableException(
					"A component was both excluded and required to be mapped to the same target.");

		getExcluded().add(target);
		if (validTargets_ != null)
			validTargets_.remove(target);
		else {
			validTargets_ = new ArrayList(allTargets_.size());
			validTargets_.addAll(allTargets_);
			validTargets_.remove(target);
		}
		if(excludersMap_ == null){
			excludersMap_ = new HashMap<Object, List>(7);
		}
		List exc = excludersMap_.get(target);
		if(exc == null){
			exc = new ArrayList(7);
			excludersMap_.put(target, exc);
		}
		exc.add(excluder);
	}

	public List getRequired() {
		if (requiredTargets_ == null)
			requiredTargets_ = new ArrayList();

		return requiredTargets_;
	}

	public List getExcluded() {
		if (excludedTargets_ == null)
			excludedTargets_ = new ArrayList();

		return excludedTargets_;
	}

	public Map getAttributes() {
		if (attributes_ == null)
			attributes_ = new HashMap();

		return attributes_;
	}

	public void setAttribute(Object key, Object value) {
		getAttributes().put(key, value);
	}

	public Object getAttribute(Object key, Object value) {
		return getAttributes().get(key);
	}

	

	public List getAllTargets() {
		return allTargets_;
	}

	public void setAllTargets(List allTargets) {
		allTargets_ = allTargets;
	}

	public String toString() {
		String w = super.toString()+"{";
		if (getValid() != null) {
			for (int i = 0; i < getValid().size(); i++) {
				w += getValid().get(i);
				if (i != getValid().size() - 1)
					w += ",";
			}
		} else {
			w += "*";
		}
		w += "}";
		return w;
	}

	public int[] getSizeWithDependencies() {
		if(sizeWithDependencies_ == null)
			return getSize();
		
		return sizeWithDependencies_;
	}

	public void setSizeWithDependencies(int[] sizeWithDependencies) {
		sizeWithDependencies_ = sizeWithDependencies;
	}
	
	
}
