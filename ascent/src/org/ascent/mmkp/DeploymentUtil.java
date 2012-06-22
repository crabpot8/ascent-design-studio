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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeploymentUtil {

	public void saveAsDeployment(MMKPProblem mmkp, File target){
		String out = "";
		
		Map<Integer,List<Integer>> sets = mmkp.getSetsMap();
		for(Integer set : sets.keySet()){
			out += "Component"+set+" {\n";
				for(Integer node : sets.get(set)){
					
				}
			out += "}\n";
		}
	}
}
