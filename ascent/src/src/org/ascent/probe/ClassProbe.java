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


package org.ascent.probe;

public class ClassProbe implements FeatureProbe {

	private String className_;

	private String property_;

	private int enabled_;

	public ClassProbe() {
	}

	public ClassProbe(String property, String className) {
		super();
		className_ = className;
		property_ = property;
	}

	public String getClassName() {
		return className_;
	}

	public void setClassName(String className) {
		className_ = className;
	}

	public String getProperty() {
		return property_;
	}

	public void setProperty(String property) {
		property_ = property;
	}

	public void run() {
		try {
			Class c = Class.forName(getClassName());
			enabled_ = 1;
		} catch (Throwable e) {
			enabled_ = 0;
		}
	}
	
	

	public int getEnabled() {
		return enabled_;
	}

	public Object getFeature() {
		return getProperty();
	}

}
