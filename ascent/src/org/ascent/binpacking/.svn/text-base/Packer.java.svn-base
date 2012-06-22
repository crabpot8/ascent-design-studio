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

import org.ascent.HasSize;
import org.ascent.ResourceConsumptionPolicy;

public class Packer {

	private Map<Object, ResourceConsumptionPolicy> resourceConsumptionPolicies_ = new HashMap<Object, ResourceConsumptionPolicy>();

	public int[] insert(HasSize item, List<? extends HasSize> itemsinbin,
			HasSize bin) {
		int[] avail = new int[bin.getSize().length];
		for (int i = 0; i < avail.length; i++) {
			ResourceConsumptionPolicy policy = resourceConsumptionPolicies_
					.get(i);
			if (policy != null) {
				ArrayList dep = new ArrayList();
				dep.add(item);
				int cons = item.getSize()[i];
				for (HasSize st : itemsinbin) {
					dep.add(st);
					cons += st.getSize()[i];
				}
				int tsize = bin.getSize()[i];

				avail[i] = policy.getResourceResidual(dep, bin, tsize, cons);
			} else {
				int cons = item.getSize()[i];
				for (HasSize st : itemsinbin) {
					cons += st.getSize()[i];
				}
				avail[i] = bin.getSize()[i] - cons;
			}
		}
		return avail;
	}

	public Map<Object, ResourceConsumptionPolicy> getResourceConsumptionPolicies() {
		return resourceConsumptionPolicies_;
	}

	public void setResourceConsumptionPolicies(
			Map<Object, ResourceConsumptionPolicy> resourceConsumptionPolicies) {
		resourceConsumptionPolicies_ = resourceConsumptionPolicies;
	}

}
