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

package org.ascent.choco.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ascent.configurator.AbstractRefreshChocoCore;

import choco.AbstractProblem;
import choco.Constraint;
import choco.integer.IntDomainVar;
import choco.integer.IntVar;

public class ChocoInternalBasicConstraintDebugger implements ChocoCoreInternalConstraintDebugger {

	private AbstractProblem problem_;

	private String description_;

	private String name_;

	private Constraint constraint_;

	private IntDomainVar errorBit_;

	private Object source_;
	
	private AbstractRefreshChocoCore core_;

	public ChocoInternalBasicConstraintDebugger(AbstractRefreshChocoCore core, AbstractProblem problem, Object source,
			String name, String description, Constraint constraint) {
		super();
		problem_ = problem;
		description_ = description;
		constraint_ = constraint;
		name_ = name;
		source_ = source;
		core_ = core;
	}

	public Object getSource() {
		return source_;
	}

	public void setSource(Object source) {
		source_ = source;
	}

	public Constraint getConstraint() {
		return constraint_;
	}

	public void setConstraint(Constraint constraint) {
		constraint_ = constraint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.refresh.core.choco.debug.ConstraintDebugger#getErrorBit()
	 */
	public IntDomainVar getErrorBit() {
		errorBit_ = problem_.makeBoundIntVar(name_, 0, 1);
		return errorBit_;
	}

	public String getError() {
		return description_;
	}

	public String toString() {
		String vmsg = (errorBit_.getVal() == 1) ? " is violoated"
				: " is not violated";
		return description_ + vmsg;
	}

	public AbstractRefreshChocoCore getCore() {
		return core_;
	}

	public void setCore(AbstractRefreshChocoCore core) {
		core_ = core;
	}
	
	public List getSourceTargets(Object src){
		ArrayList result = new ArrayList();
		Map<Object,IntVar> ttable = core_.getTargetTable(src);
		for(Object o : ttable.keySet()){
			if(((IntDomainVar)ttable.get(o)).getVal() == 1){
				result.add(o);
			}
		}
		return result;
	}
}
