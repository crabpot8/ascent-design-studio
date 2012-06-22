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

package org.ascent.configurator;

import java.util.HashMap;
import java.util.Map;


public abstract class AbstractRefreshCore implements RefreshCore {
	
	public class UndefinedAttributeException extends RuntimeException {

		public UndefinedAttributeException(String arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}

	}

	public class UnsupportedOperationException extends RuntimeException {

		public UnsupportedOperationException(String arg0) {
			super(arg0);
		}

	}

	public class UnsupportedExpressionType extends RuntimeException {

		public UnsupportedExpressionType(String arg0) {
			super(arg0);
		}

	}
	
	private boolean zeroUndefinedVariables_ = true;

	protected Map<Object, Map<Object, Object>> sourceVariableValuesTable_ = new HashMap<Object, Map<Object, Object>>();

	protected Map<Object, Map<Object, Object>> targetVariableValuesTable_ = new HashMap<Object, Map<Object, Object>>();

	public void setTargetVariableValues(Object target, Map values) {
		targetVariableValuesTable_.put(target, values);
	}

	public void setTargetVariableValues(Object target, Object[] values) {
		Map valuemap = new HashMap();
		for (int i = 0; i < values.length; i += 2) {
			valuemap.put(values[i], values[i + 1]);
		}
		targetVariableValuesTable_.put(target, valuemap);
	}

	public void setSourceVariableValues(Object source, Map values) {
		sourceVariableValuesTable_.put(source, values);
	}

	public void setSourceVariableValues(Object source, Object[] values) {
		Map valuemap = new HashMap();
		for (int i = 0; i < values.length; i += 2) {
			valuemap.put(values[i], values[i + 1]);
		}
		sourceVariableValuesTable_.put(source, valuemap);
	}
	
	public Map getTargetVariableValues(Object target) {
		Map vals = targetVariableValuesTable_.get(target);
		if (vals == null) {
			vals = new HashMap();
			targetVariableValuesTable_.put(target, vals);
		}
		return vals;
	}

	public Map getSourceVariableValues(Object source) {
		Map vals = sourceVariableValuesTable_.get(source);
		if (vals == null) {
			vals = new HashMap();
			sourceVariableValuesTable_.put(source, vals);
		}
		return vals;
	}
	
	/**
	 * @turn the zeroUndefinedVariables
	 */
	public boolean getZeroUndefinedVariables() {
		return zeroUndefinedVariables_;
	}

	/**
	 * @param zeroUndefinedVariables
	 *            the zeroUndefinedVariables to set
	 */
	public void setZeroUndefinedVariables(boolean zeroUndefinedVariables) {
		zeroUndefinedVariables_ = zeroUndefinedVariables;
	}
	
	public void clearVariableTables(){
		sourceVariableValuesTable_.clear();
		targetVariableValuesTable_.clear();
		sourceVariableValuesTable_ = new HashMap<Object, Map<Object,Object>>();
		targetVariableValuesTable_ = new HashMap<Object, Map<Object,Object>>();
	}
}
