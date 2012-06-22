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

import org.ascent.ResourceConsumptionPolicy;
import org.ascent.realtime.ResponseTimeAnalysis;

public class RateMonotonicResponseTimeResource implements
		ResourceConsumptionPolicy {
	
	public int getResourceResidual(List consumers, Object producer,
			int avail, int consumed) {
		
		List<RealTimeTask> tasks = new ArrayList<RealTimeTask>();
		for(Object o : consumers){
			if(o instanceof Schedulable){
				tasks.addAll(((Schedulable)o).getRealTimeTasks());
			}
		}
		
		if(ResponseTimeAnalysis.schedulable(tasks)){
			return 1;
		}
		else{
			return -1;
		}
	}

}
