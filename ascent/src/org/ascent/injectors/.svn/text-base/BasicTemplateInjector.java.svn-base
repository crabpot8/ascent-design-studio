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

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BasicTemplateInjector implements TemplateInjector{

	public static final String DEFAULT_KEY = "value";
	
	public static final String VARIABLE_PATTERN_DEF = "\\$\\{[\\s]*([^\\}]+)+\\}";
	public static final Pattern VARIABLE_PATTERN = Pattern.compile(VARIABLE_PATTERN_DEF);
	public static final int VARIABLE_NAME_GROUP = 1;
	
	private static BasicTemplateInjector instance_;
	
	public static BasicTemplateInjector getInstance(){
		if(instance_ == null){
			instance_ = new BasicTemplateInjector();
		}
		return instance_;
	}
	
	public static final String KEY_PREFIX = "\\$\\{[\\s]*";
	public static final String KEY_POSTFIX = "[\\s]*\\}";
	
	private BasicTemplateInjector(){}
	
	public String inject(String template, Object value){
		if(value instanceof Map){
			return injectMap((Map)value, template);
		}
		else if(value instanceof List){
			return injectListKey(DEFAULT_KEY, (List)value, template);
		}
		else {
			return injectObjectKey(DEFAULT_KEY, value, template);
		}
	}
	
	public String injectMap(Map values, String template){
		for(Object key : values.keySet()){
			Object value = values.get(key);
			if(value instanceof List){
				template = injectListKey(""+key, (List)value, template);
			}
			else{
				template = injectObjectKey(""+key, value, template);
			}
		}
		return template;
	}
	
	public String inject(String template, String defprefix, Map<String,Map> vars){
		Matcher matcher = VARIABLE_PATTERN.matcher(template);
		StringBuffer result = new StringBuffer();
		
		int last = 0;
		while(matcher.find()){
			int start = matcher.start();
			int end = matcher.end();
			String var = matcher.group(VARIABLE_NAME_GROUP);
			String val = resolve(vars, defprefix, var);
			
			if(start != last)
				result.append(template.substring(last,start));
			
			result.append(val);
			last = end;
		}
		
		if(last != template.length())
			result.append(template.substring(last));
		
		return result.toString();
	}
	
	public String resolve(Map<String,Map> vars, String defprefix, String var){
		Object val = resolveObject(vars, defprefix, var);
		return toString(val);
	}
	
	public String toString(Object o){
		return ""+o;
	}
	
	public Object resolveObject(Map<String,Map> vars, String defprefix, String var){
		int dot = var.indexOf(".");
		
		String feat = defprefix;

		if(dot > 0){
			feat = var.substring(0,dot);
			var = var.substring(dot+1);
		}
		
		Map vals = vars.get(feat);
		if(vals == null)
			throw new VariableNotInScopeException(defprefix,var);
		
		Object val = vals.get(var);
		if(val == null)
			throw new VariableNotInScopeException(defprefix,var);
		
		return val;
	}
	
	public String injectObjectKey(String key, Object value, String template){
		return template.replaceAll(KEY_PREFIX+key+KEY_POSTFIX, ""+value);
	}
	
	public String injectListKey(String key, List value, String template){
		String lval = null;
		if(value == null || value.size() == 0)
			lval = "[]";
		else
			lval = value.toString();
		return template.replaceAll(KEY_PREFIX+key+KEY_POSTFIX, lval);
	}
}
