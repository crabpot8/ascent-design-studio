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

package org.ascent.configurator.conf.debug;

import java.util.List;

import org.ascent.configurator.RefreshCore;
import org.ascent.configurator.conf.RefreshProblem;
import org.ascent.expr.Cardinality;


public class SelectDebugger extends SourceConstraintDebugger{

	private List selectSet_;
	
	private Cardinality cardinality_;

	public SelectDebugger(RefreshCore core, RefreshProblem problem, Object src, List sset, Cardinality card) {
		super(core, problem, src);
		selectSet_ = sset;
		cardinality_ = card;
	}

	
	public String getError() {
		List targets = getSourceTargets(getSource());

		String msg = "";
		for (Object trg : targets) {
			msg += "\n"+getSource()+" could not be mapped to "+trg+" b/c the cardinality constraint "+cardinality_+" does not hold for the set: [";
			for (Object o : selectSet_) {
				List otrgs = getSourceTargets(o);
				msg += o;
				if(otrgs.contains(trg)){
					msg += "(enabled";
					if(getProblem().getSelectedItems().contains(o)){
						msg += ",required by user";
					}
					if(getProblem().getSourceMappedInstancesCountMap().get(o) != null && getProblem().getSourceMappedInstancesCountMap().get(o).getMin() > 0){
						msg += ",required by problem definition";
					}
				}
				else{
					msg += "(disabled";
					if(getProblem().getDisabledItems().contains(o)){
						msg += ",disabled by user";
					}
					if(getProblem().getSourceMappedInstancesCountMap().get(o) != null && getProblem().getSourceMappedInstancesCountMap().get(o).getMax() == 0){
						msg += ",disabled by problem definition";
					}
				}
				msg += "),";
			}
			msg += "]";
		}
		return msg;
	}

}
