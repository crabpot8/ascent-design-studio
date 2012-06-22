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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gmt.gems.css.parser.Attributes;
import org.eclipse.gmt.gems.css.parser.CSSParser;


public class ProblemParser {
	
	public class UnknownDirectiveException extends RuntimeException {
		public UnknownDirectiveException(String msg){
			super(msg);
		}
	}
	
	private Map<String, ConfigDirectiveHandler> sourceHandlers_ = new HashMap<String, ConfigDirectiveHandler>();
	private Map<String, ConfigDirectiveHandler> targetHandlers_ = new HashMap<String, ConfigDirectiveHandler>();
	
	private boolean errorOnUndefinedDirectives_ = true;
	
	private ConfigDirectiveHandler defaultDirectiveHandler_;

	public ProblemParser(){
		sourceHandlers_.put("Set",new SetVariableHandler());
		sourceHandlers_.put("Requires",new RequiresHandler());
		sourceHandlers_.put("Required",new RequiredHandler());
		sourceHandlers_.put("Excludes",new ExcludesHandler());
		sourceHandlers_.put("Select",new SelectHandler());
		sourceHandlers_.put("Define",new DefineHandler());
		sourceHandlers_.put("Instances",new InstancesHandler());
		sourceHandlers_.put("Goal",new GoalHandler());
		sourceHandlers_.put("Target",new TargetRequirementHandler());
		sourceHandlers_.put("SubFeatures",new SubFeaturesHandler());
		sourceHandlers_.put("Evaluate",new EvaluateHandler());
		sourceHandlers_.put("DefineVar", new DefineVarHandler());
		sourceHandlers_.put("Optional", new OptionalHandler());
		
		targetHandlers_.put("Resource", new ResourceHandler());
	}
	
	public RefreshProblem parseTargetProblem(String def){
		RefreshProblem problem = new RefreshProblem();
		
		List<Attributes> attrs = CSSParser.parseAttributes(def,true,false);
		
		ArrayList features = new ArrayList();
		for(Attributes attrset : attrs){
			features.add(attrset.getSelector().trim());
		}
		problem.setTargetItems(features);
		
		for(Attributes attrset : attrs){
			configureRules(problem, attrset, false);
		}
		return problem;
	}
	
	public RefreshProblem parseSourceProblem(String def){
		RefreshProblem problem = new RefreshProblem();
		
		List<Attributes> attrs = CSSParser.parseAttributes(def,true,false);
		
		ArrayList features = new ArrayList();
		for(Attributes attrset : attrs){
			features.add(attrset.getSelector().trim());
		}
		problem.setSourceItems(features);
		
		for(Attributes attrset : attrs){
			configureRules(problem, attrset, true);
		}
		return problem;
	}
	
	public void configureRules(RefreshProblem problem, Attributes featureconf, boolean issrc){
		String context = featureconf.getSelector().trim();
		
		for(Object key : featureconf.keySet()){
			String directive = ""+key;
			ConfigDirectiveHandler handler = getHandler(directive, issrc);
			
			if(handler == null && defaultDirectiveHandler_ != null)
				handler = defaultDirectiveHandler_;
			
			if(handler == null && errorOnUndefinedDirectives_)
				throw new UnknownDirectiveException("The configuration directive \""+directive+"\" in context \""+context+"\"is not defined.");

			
			List vals = (List)featureconf.get(directive);
			for(Object o : vals){
				if(handler != null)
					handler.handle(problem, context.trim(), directive, ""+o);
			}
		}
	}
	
	public ConfigDirectiveHandler getHandler(String directive, boolean issrc){
		return sourceHandlers_.get(directive);
	}
	
	public void addSourceConfigDirectiveHandler(String directive, ConfigDirectiveHandler handler){
		sourceHandlers_.put(directive, handler);
	}
	
	public void removeSourceConfigDirectiveHandler(String directive){
		sourceHandlers_.remove(directive);
	}
	
	public void addTargetConfigDirectiveHandler(String directive, ConfigDirectiveHandler handler){
		targetHandlers_.put(directive, handler);
	}
	
	public void removeTargetConfigDirectiveHandler(String directive){
		targetHandlers_.remove(directive);
	}

	public boolean getErrorOnUndefinedDirectives() {
		return errorOnUndefinedDirectives_;
	}

	public void setErrorOnUndefinedDirectives(boolean errorOnUndefinedDirectives) {
		errorOnUndefinedDirectives_ = errorOnUndefinedDirectives;
	}

	public ConfigDirectiveHandler getDefaultDirectiveHandler() {
		return defaultDirectiveHandler_;
	}

	public void setDefaultDirectiveHandler(
			ConfigDirectiveHandler defaultDirectiveHandler) {
		defaultDirectiveHandler_ = defaultDirectiveHandler;
	}

	
	
	
}
