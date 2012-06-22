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


package org.ascent.configurator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.ascent.configurator.conf.RefreshProblem;
import org.ascent.configurator.conf.debug.ConstraintReference;
import org.ascent.injectors.BasicConfigurationBinder;
import org.ascent.injectors.Injector;
import org.ascent.injectors.XMLInjector;
import org.ascent.probe.BasicProbeResultHandler;
import org.ascent.probe.Probe;
import org.ascent.probe.ProbeResultHandler;
import org.ascent.probe.ProbeSet;

public class RefreshEngine {

	private static Logger logger_ = Logger.getLogger(RefreshEngine.class
			.getName());

	private RefreshProblem problem_;

	private BasicConfigurationBinder binder_;

	private ProbeResultHandler resultHandler_ = new BasicProbeResultHandler();

	private ProbeSet probes_ = new ProbeSet();

	private boolean enableTracing_ = true;

	private boolean stripSpaces_ = false;
	
	private Injector injector_;
	
	private Map solution_;
	
	private Map<String, Map> injectionValues_;

	private RefreshCore core_ = new RefreshMatrixCore();

	public RefreshEngine(RefreshProblem problem) {
		problem_ = problem;
	}

	public BasicConfigurationBinder getBinder() {
		return binder_;
	}

	public void setBinder(BasicConfigurationBinder binder) {
		binder_ = binder;
	}

	public RefreshProblem getProblem() {
		return problem_;
	}

	public void setProblem(RefreshProblem problem) {
		problem_ = problem;
	}

	public ProbeResultHandler getResultHandler() {
		return resultHandler_;
	}

	public void setResultHandler(ProbeResultHandler resultHandler) {
		resultHandler_ = resultHandler;
	}

	public ProbeSet getProbes() {
		return probes_;
	}

	public void setProbes(ProbeSet probes) {
		probes_ = probes;
	}

	public void runProbes() {
		for (Probe p : probes_.getProbes()) {
			p.run();
		}
		for (Probe p : probes_.getProbes()) {
			resultHandler_.applyResults(problem_, p);
		}
	}

	public boolean getEnableTracing() {
		return enableTracing_;
	}

	public void setEnableTracing(boolean enableTracing) {
		enableTracing_ = enableTracing;
		if (enableTracing_)
			stripSpaces_ = false;
	}

	public boolean getStripSpaces() {
		return stripSpaces_;
	}

	public void setStripSpaces(boolean stripSpaces) {
		stripSpaces_ = stripSpaces;
	}
	
	public void reset(){
		binder_ = null;
		solution_ = null;
		injectionValues_ = null;
		core_ = new RefreshMatrixCore();
	}

	public void run() {
		RefreshProblem problem = getProblem();

		runProbes();

		problem.inject(this);
	
		try {
			Map configuration = core_.nextMapping();

			if (configuration != null) {

				Map<String, List> enabled = new HashMap<String, List>();
				Map<String, Map> injections = new HashMap<String, Map>();
				for (Object o : configuration.keySet()) {
					enabled.put("" + o, (List) configuration.get(o));
					Map<String, Object> ivals = problem.getInjectionValues(o);
					Map ctxvars = core_.getContextVariableValues(o);
					if (ivals == null && ctxvars != null) {
						ivals = new HashMap<String, Object>();
					}
					for (Object key : ctxvars.keySet()) {
						ivals.put("" + key, ctxvars.get(key));
					}
					injections.put("" + o, ivals);

				}

				injectionValues_ = injections;
				solution_ = configuration;
				
				
				Injector inj = getInjector();
				binder_ = new BasicConfigurationBinder(inj, enabled, injections);
			}
		} catch (RuntimeException e) {
			logger_.severe("Error in RefreshEngine.run():" + e);
		}
	}
	
	public List<ConstraintReference> debug(){
		RefreshProblem problem = getProblem();

		runProbes();

		((RefreshMatrixCore)core_).setDebug(true);
		
		problem.inject(this);
		
		return ((RefreshMatrixCore)core_).debug();
	}

	public RefreshCore getCore() {
		return core_;
	}

	public void setCore(RefreshCore core) {
		core_ = core;
	}

	public Object getVariableValue(String var) {
		return core_.getVariableValue(var);
	}

	public Map getSolution() {
		return solution_;
	}

	public void setSolution(Map solution) {
		solution_ = solution;
	}

	public Map<String, Map> getInjectionValues() {
		return injectionValues_;
	}

	public void setInjectionValues(Map<String, Map> injectionValues) {
		injectionValues_ = injectionValues;
	}

	public Injector getInjector() {
		if(injector_ == null){
			XMLInjector inj = XMLInjector.createDefaultInjector();
			inj.setEnableTracing(enableTracing_);
			inj.setStripSpaces(stripSpaces_);
			injector_ = inj;
		}
		return injector_;
	}

	public void setInjector(Injector injector) {
		injector_ = injector;
	}

	
}
