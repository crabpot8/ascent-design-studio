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

package org.ascent.deployment;

import java.util.ArrayList;
import java.util.List;

public abstract class ColocationConstraint implements DeploymentConstraint {

	private Component source_;
	private List<Component> targets_ = new ArrayList<Component>();

	public ColocationConstraint(Component source, List<Component> targets) {
		super();
		source_ = source;
		targets_ = targets;
	}
	
	public ColocationConstraint(Component source, Component target) {
		super();
		source_ = source;
		targets_.add(target);
	}

	public ColocationConstraint(Component source) {
		super();
		source_ = source;
	}

	public Component getSource() {
		return source_;
	}

	public void setSource(Component source) {
		source_ = source;
	}

	public List<Component> getTargets() {
		return targets_;
	}

	public void setTargets(List<Component> targets) {
		targets_ = targets;
	}

	public boolean isEnforced(DeploymentPlan plan) {
		Node host = plan.getHost(source_);
		if(host != null){
			for(Component c : targets_){
				if(!isEnforced(host,plan.getHost(c))){
					return false;
				}
			}
		}
		return true;
	}

	public abstract boolean isEnforced(Node sourcehost, Node targethost);
}
