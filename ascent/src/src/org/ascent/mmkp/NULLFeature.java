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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.ascent.configurator.AbstractRefreshChocoCore;
import org.ascent.configurator.RefreshCore;

public class NULLFeature extends Feature {

	public NULLFeature(){
		super("NULL",-1);
	}

	@Override
	public void collectFeatures(List<Feature> list) {
		
	}

	@Override
	public List<Feature> getCrossTreeDependants() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<Feature> getCrossTreeExcludes() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<Feature> getCrossTreeRequires() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<Feature> getOptionalChildren() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<Feature> getRequiredChildren() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<Feature> getXorChildren() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void insert(RefreshCore core) {
		
	}

	@Override
	public void insertAndClear(RefreshCore core) {
		
	}

	@Override
	public boolean isDescendant(Feature f) {
		return false;
	}

	@Override
	public boolean isTransparent() {
		return false;
	}

	@Override
	public void observe(AbstractRefreshChocoCore core, boolean o) {
		
	}

	@Override
	public void observe(AbstractRefreshChocoCore core, int errorrate, boolean o) {
		
	}

	@Override
	public void propagateConstraints() {
		
	}

	@Override
	public void propagateConsumedResources(Feature f) {
		
	}

	@Override
	public void setCrossTreeDependants(List<Feature> crossTreeDependants) {
		
	}

	@Override
	public void setCrossTreeExcludes(List<Feature> crossTreeExcludes) {
		
	}

	@Override
	public void setCrossTreeRequires(List<Feature> crossTreeRequires) {
		
	}

	@Override
	public void setId(int id) {
		
	}

	@Override
	public void setName(String name) {
		
	}

	@Override
	public void setOptionalChildren(List<Feature> optionalChildren) {
		
	}

	@Override
	public void setRequiredChildren(List<Feature> requiredChildren) {
		
	}

	@Override
	public void setTransparent(boolean transparent) {
		
	}

	@Override
	public void setXorChildren(List<Feature> xorChildren) {
		
	}

	@Override
	public void updateCrossTreeExcludes(List<Feature> crossTreeExcludes) {
		
	}

	@Override
	public void updateCrossTreeRequires(List<Feature> crossTreeRequires) {
		
	}

	@Override
	public void validate(Map<Object, List> sol) {
		
	}
	
	
}
