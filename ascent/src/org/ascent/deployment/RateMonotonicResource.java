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

import java.util.List;

import org.ascent.ResourceConsumptionPolicy;

public class RateMonotonicResource implements
		ResourceConsumptionPolicy {
	
	private boolean assumeComponentTasksAreSchedulable_ = true;

	public int getResourceResidual(List consumers, Object producer,
			int avail, int consumed) {
		int tasks = consumers.size();
		if(tasks != 0 && consumers.get(0) instanceof Schedulable){
			tasks = 0;
			for(Object o : consumers){
				if(o instanceof Schedulable){
					tasks += ((Schedulable)o).getTotalTasks();
				}
				else {
					tasks++;
				}
			}
		}
		if(tasks == 1 || (assumeComponentTasksAreSchedulable_ && consumers.size() == 1))
			return avail - consumed;
		else {
			double aavail = 100 * getAvailable(tasks);
			return (int)Math.rint(aavail - consumed);
		}
	}

	public double getAvailable(int consumers){
		return consumers * (Math.pow(2, (1.0/consumers)) - 1);
	}

	public boolean isAssumeComponentTasksAreSchedulable() {
		return assumeComponentTasksAreSchedulable_;
	}

	public void setAssumeComponentTasksAreSchedulable(
			boolean assumeComponentTasksAreSchedulable) {
		assumeComponentTasksAreSchedulable_ = assumeComponentTasksAreSchedulable;
	}
	
	
}
