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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.injectors.annotations.InvalidInjectionException;
import org.ascent.util.ParsingUtil;


public class SetVariableHandler implements ConfigDirectiveHandler {

	public class InvalidVariableDeclarationException extends RuntimeException {
		public InvalidVariableDeclarationException(String msg){
			super(msg);
		}
	}
	
	public String[] resolve(String context, String var){
		int first = var.indexOf(".");
		String[] resolved = new String[2];
		if(first > 0){
			resolved[0] = var.substring(0,first);
			resolved[1] = var.substring(first+1);
		}
		else{
			resolved[0] = context;
			resolved[1] = var;
		}
		return resolved;
	}
	
	public void handle(RefreshProblem problem, String context, String directive, String argstr) {
		argstr = argstr.trim();
		if(argstr.startsWith("{") && argstr.endsWith("}"))
			argstr = argstr.substring(1,argstr.length()-1);
		
		int start = argstr.indexOf("=");
		if(start < 1)
			throw new InvalidVariableDeclarationException("A variable declaration must use the form \"VAR = VALUE\" and the declaration \""+argstr+"\" in context \""+context+"\" does not (it is missing '=').");
		
		String var = argstr.substring(0,start);
		String val = argstr.substring(start+1,argstr.length());
		
		Map<String,Object> ivals = problem.getInjectionValues(context);
		if(ivals == null){
			ivals = new HashMap<String, Object>();
			problem.setInjectionValues(context, ivals);
		}
		Object eval = ivals.get(var);
		if(eval != null && !(eval instanceof List)){
			throw new InvalidInjectionException("The variable \""+var+"\" in context \""+context+"\" is not a list and and already has a value set.");
		}
		
		Object dval = val;
		if(val.startsWith("[")){
			dval = ParsingUtil.parseList(val);
		}
		else if(val.startsWith("{")){
			dval = ParsingUtil.parseMap(val);
		}
		if(eval != null){
			List lval = (List)eval;
			lval.add(dval);
			dval = lval;
		}
		
		String[] resolved = resolve(context, var);
		ivals.put(resolved[0]+"."+resolved[1],dval);
	}

}
