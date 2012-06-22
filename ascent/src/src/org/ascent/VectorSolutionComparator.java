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

package org.ascent;

import java.util.Comparator;

import org.ascent.binpacking.ValueFunction;

public class VectorSolutionComparator implements Comparator<VectorSolution>{

	private ValueFunction<VectorSolution> valueFunction_;
	
	
	public VectorSolutionComparator(ValueFunction<VectorSolution> valueFunction) {
		super();
		valueFunction_ = valueFunction;
	}


	public ValueFunction<VectorSolution> getValueFunction() {
		return valueFunction_;
	}


	public void setValueFunction(ValueFunction<VectorSolution> valueFunction) {
		valueFunction_ = valueFunction;
	}


	public int compare(VectorSolution o1, VectorSolution o2) {
		return (int)Math.rint(valueFunction_.getValue(o1) - valueFunction_.getValue(o2));
	}

}
