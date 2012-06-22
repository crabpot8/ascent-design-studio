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


public class Conflict {
	private Object source_;
	private ConstraintReference constraint_;
	private String explanation_;

	public Conflict(Object source, String explanation) {
		super();
		source_ = source;
		explanation_ = explanation;
	}

	public Conflict(Object source, ConstraintReference constraint,
			String explanation) {
		super();
		source_ = source;
		constraint_ = constraint;
		explanation_ = explanation;
	}

	public Object getSource() {
		return source_;
	}

	public void setSource(Object source) {
		source_ = source;
	}

	public String getExplanation() {
		return explanation_;
	}

	public void setExplanation(String explanation) {
		explanation_ = explanation;
	}

	public ConstraintReference getConstraint() {
		return constraint_;
	}

	public void setConstraint(ConstraintReference constraint) {
		constraint_ = constraint;
	}

	public String toString(){
		return "Unable to map:"+getSource()+" reason:[\n  "+getExplanation().trim()+"\n]";
	}
}
