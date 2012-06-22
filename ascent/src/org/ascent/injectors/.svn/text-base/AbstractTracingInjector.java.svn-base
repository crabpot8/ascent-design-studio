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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.security.action.GetLongAction;


public abstract class AbstractTracingInjector implements Injector {

	protected Map<String, Map<String, List<String>>> bindingsTraceInformation_;
	
	private boolean enableTracing_ = true;
	
	
	
	public String inject(Map<String,List> features,
			Map<String, Map> values, String conf) {
		
		if (bindingsTraceInformation_ != null)
			bindingsTraceInformation_.clear();

		if (enableTracing_)
			buildTraceMap(features, values);

		return injectImpl(features,values,conf);
	}
	
	protected abstract String injectImpl(Map<String, List> features,
			Map<String, Map> values, String conf);
	
	public String indent(String xml, String indent) {
		return indent(xml,indent,getTraceLinePrefix(),getTraceLineSuffix());
	}
	
	public String indent(String xml, String indent, String prefix, String postfix) {
		if(xml.indexOf("\n") < 0)
			return prefix + indent + xml + postfix;
		
		xml = prefix + indent + xml.replaceAll("\\n", "\n" + indent)+postfix;
		return xml;
	}
	
	public String[] resolve(String context, String var) {
		int first = var.indexOf(".");
		String[] resolved = new String[2];
		if (first > 0) {
			resolved[0] = var.substring(0, first);
			resolved[1] = var.substring(first + 1);
		} else {
			resolved[0] = context;
			resolved[1] = var;
		}
		return resolved;
	}
	
	public void addTrace(Object feature, String var, Object val) {
		String[] resolved = resolve("" + feature, var);
		String trgfeat = resolved[0];
		String varname = resolved[1];
		Map<String, List<String>> trace = bindingsTraceInformation_
				.get(trgfeat);
		if (trace == null) {
			trace = new HashMap<String, List<String>>();
			bindingsTraceInformation_.put(trgfeat, trace);
		}
		List<String> binders = trace.get(varname);
		if (binders == null) {
			binders = new ArrayList<String>();
			trace.put(varname, binders);
		}
		if (val instanceof List) {
			List vs = (List) val;
			for (Object o : vs)
				binders.add("" + feature);
		} else {
			binders.add("" + feature);
		}
	}
	
	public void buildTraceMap(Map<String, List> features,
			Map<String, Map> values) {
		bindingsTraceInformation_ = new HashMap<String, Map<String, List<String>>>();
		for (Object key : features.keySet()) {
			if (features.get(key) != null && features.get(key).size() > 0) {
				Map<String, Object> bindings = values.get(key);
				if (bindings != null) {
					for (String var : bindings.keySet()) {
						addTrace(key, var, bindings.get(var));
					}
				}
			}
		}
	}

	public String getBindingsTrace(String feature, String head, String tail,
			String template, Map bindings, boolean enabled) {
		
		String indent = "                  |";
		String trace = getTracePrefix();
		if(!enabled)
			  trace += getTraceLinePrefix()+"  ***None of the required features were enabled.***"+getTraceLineSuffix()+"\n";
		trace += getTraceLinePrefix()+"  Required Feature:" + feature + getTraceLineSuffix()+ "\n";
		trace += getTraceLinePrefix()+"  Head            :"+getTraceLineSuffix()+"\n" + indent(stripComments(head), indent,"","")+getTraceLineSuffix()
				+ "\n";
		if (tail != null)
			trace += getTraceLinePrefix()+"  Tail            :"+getTraceLineSuffix()+"\n"
					+ indent(stripComments(tail), indent) + "\n";
		if (template != null)
			trace += getTraceLinePrefix()+"  Template        :"+getTraceLineSuffix()+"\n"
					+ indent(stripComments(template), indent) +"\n";

		if(enabled){
		if (bindingsTraceInformation_ != null) {
			trace += getTraceLinePrefix()+"  Bindings:"+ getTraceLineSuffix()+"\n";
			Map<String, List<String>> producermap = bindingsTraceInformation_
					.get(feature);
			if (producermap != null) {
				for (Object key : bindings.keySet()) {
					Object val = bindings.get(key);
					List binders = producermap.get("" + key);
					if (binders != null && binders.size() > 0) {
						if (val instanceof List) {
							List vals = (List) val;
							trace += getTraceLinePrefix()+"    key:\"" + key
									+ "\" is bound to the list:"+ getTraceLineSuffix()+"\n" + getTraceLinePrefix()+"     ["+getTraceLineSuffix();
							for (int i = 0; i < vals.size(); i++) {
								Object v = vals.get(i);
								Object binder = binders.get(i);
								trace += "\n"+getTraceLinePrefix()+"      item:\"" + v
										+ "\" bound by the feature(s):"
										+ binder+getTraceLineSuffix();
							}
							trace += "\n"+getTraceLinePrefix()+"     ]"+getTraceLineSuffix()+"\n";
						} else {
							trace += getTraceLinePrefix()+"    key:" + key
									+ " bound by the feature(s):"
									+ toBinderString(binders) + " to:\"" + val
									+ "\""+getTraceLineSuffix()+"\n";
						}
					}
				}
			}
		}
		}
		
		trace += getTraceSuffix();
		return trace;
	}
	
	protected String toBinderString(List binders) {
		String str = "[";
		for (Object b : binders) {
			str += b + ",";
		}
		if (str.endsWith(","))
			str = str.substring(0, str.length() - 1);
		str += "]";
		return str;
	}
	
	/**
	 * @return the bindingsTraceInformation
	 */
	public Map<String, Map<String, List<String>>> getBindingsTraceInformation() {
		return bindingsTraceInformation_;
	}

	/**
	 * @param bindingsTraceInformation
	 *            the bindingsTraceInformation to set
	 */
	public void setBindingsTraceInformation(
			Map<String, Map<String, List<String>>> bindingsTraceInformation) {
		bindingsTraceInformation_ = bindingsTraceInformation;
	}

	/**
	 * @return the enableTracing
	 */
	public boolean getEnableTracing() {
		return enableTracing_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.refresh.injectors.Injector#setEnableTracing(boolean)
	 */
	public void setEnableTracing(boolean enableTracing) {
		enableTracing_ = enableTracing;
	}
	
	public String stripComments(String cstr){
		return cstr;
	}

	public String getTraceLinePrefix(){
		return "";
	}
	
	public String getTraceLineSuffix(){
		return "";
	}
	
	public String getTracePrefix(){
		return "";
	}
	
	public String getTraceSuffix(){
		return "";
	}
	
}
