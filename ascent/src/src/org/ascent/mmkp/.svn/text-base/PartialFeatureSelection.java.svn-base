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


package org.ascent.mmkp;

import java.util.ArrayList;
import java.util.List;

public class PartialFeatureSelection {

	private List<Feature> features_ = new ArrayList<Feature>();
	
	public PartialFeatureSelection(List<Feature> features) {
		super();
		features_ = features;
	}
	public PartialFeatureSelection() {
		super();
	}
	public List<Feature> getFeatures() {
		return features_;
	}
	public void setFeatures(List<Feature> features) {
		features_ = features;
	}
	public String toString(){
		String str = "[";
		for(int i = 0; i < features_.size(); i++){
			str += features_.get(i).getName();
			if(i != features_.size()-1)
				str += ",";
		}
		str += "]";
		return str;		
	}
}
