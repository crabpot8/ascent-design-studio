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

package org.ascent.configurator.conf;

import org.ascent.expr.Cardinality;


public class InstancesHandler implements ConfigDirectiveHandler {

	public void handle(RefreshProblem problem, String context, String directive, String argstr) {
		Cardinality card = Cardinality.parseCardinality(argstr.trim());
		problem.getSourceMappedInstancesCountMap().put(context,card);
	}

}
