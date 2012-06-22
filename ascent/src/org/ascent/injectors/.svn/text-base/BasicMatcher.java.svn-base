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

package org.ascent.injectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BasicMatcher implements FeatureMatcher {
	public static final String FEATURE_WILDCARD = "*";

	
	public List match(String pattern, Map<String, List> features) {
		int index = pattern.indexOf(FEATURE_WILDCARD);
		
		ArrayList result = new ArrayList();
		
		if (index > -1) {
			String pre = pattern;
			String post = "";

			if (index == 0 && pattern.length() == 1) {
				pre = "";
				post = "";
			} else if (index > -1) {

				if (index == 0) {
					pre = "";
					post = pattern.substring(index + 1);
				} else {
					pre = pattern.substring(0, index);
					post = "";
				}
			}
			
			for(String key : features.keySet()){
				if(key.startsWith(pre) && key.endsWith(post) && features.get(key) != null && features.get(key).size() > 0){
					result.add(key);
				}
			}
		}
		else{
			if(features.get(pattern) != null && features.get(pattern).size() > 0){
				result.add(pattern);
			}
		}

		return result;
	}

}
