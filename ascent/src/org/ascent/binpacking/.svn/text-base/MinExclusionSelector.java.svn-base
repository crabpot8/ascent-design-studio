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


package org.ascent.binpacking;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinExclusionSelector implements BinSelector {

	public Object selectBin(FFDBinPacker core, Object item, List potentials, ItemState ss,
			List<BinState> binstates) {
		
		if(potentials.size() == 0)
			return null;
		
		List exc = core.getExcluded(ss.getItem());
		if (exc.size() == 0) {
			return potentials.get(0);
		} else {
			
				Map<Object, Double> vals = new HashMap<Object, Double>();
				for (Object t : potentials) {
					double v = exc.size();
					for (Object e : exc) {
						ItemState est = core.getSourceState(e);
						if(est.getValid() == null){
							v -= (1.0 / core.getTargets().size());
						}
						else if (est.getValid().contains(t)) {
							v -= (1.0 / est.getValid().size());
						}
						vals.put(t, v);
					}
				}
				Collections.sort(potentials, new LeastBoundPacker.BinSorter(vals));
				return potentials.get(0);
			
		}
	}

}
