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

package org.ascent.deployment;

import java.util.List;

public class PlacementConstraint implements DeploymentConstraint {

	private Component source_;
	private List<Node> validHosts_;

	public PlacementConstraint(Component source, List<Node> validHosts) {
		super();
		source_ = source;
		validHosts_ = validHosts;
	}

	public boolean isEnforced(DeploymentPlan plan) {
		Node host = plan.getHost(source_);
		if (host != null) {
			return validHosts_.contains(host);
		}
		return true;
	}

	public Component getSource() {
		return source_;
	}

	public void setSource(Component source) {
		source_ = source;
	}

	public List<Node> getValidHosts() {
		return validHosts_;
	}

	public void setValidHosts(List<Node> validHosts) {
		validHosts_ = validHosts;
	}

}
