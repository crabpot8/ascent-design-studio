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

package org.ascent.injectors.annotations;

import java.util.Map;

import org.ascent.injectors.XMLInjector;


public class ReplaceAnnotation extends AbstractAnnotation {

	public static final String TYPE = "replace";

	public ReplaceAnnotation() {
		super(TYPE);
	}

	@Override
	protected String handleImpl(String template, Map options, Map bindings, Map<String,Map> gbinds) {
		String with = (String) options.get(XMLInjector.WITH_KEY);
		if (with == null || (options != null && options.size() > 2)) {
			for (Object key : options.keySet()) {
				String target = "" + key;
				String replacementval = "" + options.get(key);
				if(replacementval.equals("${with}")){
					replacementval = with;
				}
				else{
					replacementval = getTemplateInjector().inject(replacementval,""+bindings.get(FEATURE_KEY), gbinds);
				}
				
				String[] regexs = getTargetRegex(target, replacementval);
				
				if(regexs == null)
					continue;
				
				String replaceex = regexs[0];
				replacementval = regexs[1];
				
				if(replaceex == null){
					continue;
				}

				if(replacementval.indexOf("${") > -1){
					throw new InvalidInjectionException("The injection value for the key "+key+" contains unbound variables. The injection value is \""+replacementval+"\"");
				}
				
				template = template.replaceAll(replaceex, replacementval);
			}
		}
		else{
			template = with;
		}
		return template;
	}
	
	public String[] getTargetRegex(String target, String replacement){
		return new String[]{"\\$\\{"+target+"\\}",replacement};
	}

}
