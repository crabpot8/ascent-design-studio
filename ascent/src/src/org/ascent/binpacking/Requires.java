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

import java.util.List;

import org.ascent.configurator.RefreshCore;

public class Requires extends
		Dependency {

	public Requires(StateProvider sp, Object source,
			List depends) {
		super(sp, source, depends);
	}

	public Requires(StateProvider sp, Object source,
			Object dependency) {
		super(sp, source, dependency);
	}

	@Override
	public String getType() {
		return "Requires";
	}

	@Override
	public void propogate(Object depend, Object sourcetarget) {
		ItemState state = getStateProvider().getSourceState(
				depend);
		state.requireTarget(sourcetarget);
	}

	@Override
	public void apply(RefreshCore core) {
		for(Object st : getDependencies()){
			core.addRequiresMappingConstraint(getSource(), st);
		}
	}
	
	

}
