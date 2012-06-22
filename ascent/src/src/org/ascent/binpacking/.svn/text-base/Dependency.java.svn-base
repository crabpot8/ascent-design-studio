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
import java.util.Arrays;
import java.util.List;

import org.ascent.configurator.RefreshCore;

 
public abstract class Dependency {
	private StateProvider stateProvider_;
	private Object source_;
	private List dependencies_ = new ArrayList();

	public Dependency(StateProvider sp) {
		stateProvider_ = sp;
	}

	public Dependency(StateProvider sp, Object source) {
		stateProvider_ = sp;
		source_ = source;
	}

	public Dependency(StateProvider sp, Object source,
			List dependencies) {
		stateProvider_ = sp;
		source_ = source;
		dependencies_ = dependencies;
	}
	
	public Dependency(StateProvider sp, Object source,
			Object dependency) {
		stateProvider_ = sp;
		source_ = source;
		dependencies_ = Arrays.asList(dependency);
	}

	public void update(Object target) {
		for (Object item : dependencies_) {
			propogate(item, target);
		}
	}

	public Object getSource() {
		return source_;
	}

	public void setSource(Object source) {
		source_ = source;
	}

	public List getDependencies() {
		return dependencies_;
	}

	public void setDependencies(List dependencies) {
		dependencies_ = dependencies;
	}

	public String toString() {
		String val = source_ + " " + getType() + " [";
		for (Object item : dependencies_) {
			val += item + ",";
		}
		if (val.endsWith(","))
			val = val.substring(0, val.length() - 1);
		val += "]";
		return val;
	}

	public StateProvider getStateProvider() {
		return stateProvider_;
	}

	public void setStateProvider(StateProvider stateProvider) {
		stateProvider_ = stateProvider;
	}

	public abstract String getType();

	public abstract void propogate(Object depend, Object sourcetarget);

	public abstract void apply(RefreshCore core);
}
