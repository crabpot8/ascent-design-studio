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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.configurator.conf.RefreshProblem;
import org.ascent.util.ParsingUtil;

public class BasicProbeResultHandler implements ProbeResultHandler {

	public void applyResults(RefreshProblem problem, Probe probe) {
		if (probe instanceof TargetFinder) {
			TargetFinder targetinfo = (TargetFinder) probe;
			List targets = targetinfo.getTargets();
			problem.setTargetItems(targets);

			for (Object o : targets) {
				Map props = targetinfo.getTargetProperties(o);
				Map<Object, Object> vals = new HashMap<Object, Object>();
				for (Object key : props.keySet()) {
					vals.put(key, props.get(key));
				}
				problem.getTargetVariableValuesTable().put(o, vals);
			}
		} else if (probe instanceof PropertyProbe) {
			PropertyProbe pp = (PropertyProbe) probe;
			Map props = pp.getProperties();
			for (Object key : props.keySet()) {
				problem.getVariablesTable().put(key,
						ParsingUtil.toInt(props.get(key)));
			}
		} else if (probe instanceof FeatureProbe) {
			FeatureProbe fp = (FeatureProbe) probe;
			Object feature = fp.getFeature();
			int val = fp.getEnabled();

			if (val == 0)
				problem.getDisabledItems().add(feature);
		}
	}
}
