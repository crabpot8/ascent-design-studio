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

import java.util.ArrayList;
import java.util.List;

import org.ascent.expr.Cardinality;


public class SelectHandler implements ConfigDirectiveHandler {

	public void handle(RefreshProblem problem, String context,
			String directive, String argstr) {
		String[] rfeatures = argstr.split(",");
		String cardstr = null;
		if(rfeatures != null && rfeatures.length > 0){
			List req = new ArrayList();
			for(String rfeat : rfeatures){
				if(cardstr == null){
					cardstr = rfeat.trim();
					if(cardstr.startsWith("[")){
						cardstr = cardstr.substring(1,cardstr.length()-1);
					}
					Cardinality card = Cardinality.parseCardinality(cardstr);
					req.add(card);
				}
				else
					req.add(rfeat.trim());
			}
		
			
			problem.getSelectMap().put(context,req);
		}
	}

}
