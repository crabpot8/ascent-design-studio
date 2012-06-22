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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ascent.injectors.AnnotationConstants;
import org.ascent.injectors.BasicTemplateInjector;
import org.ascent.injectors.TemplateInjector;


public abstract class AbstractAnnotation implements ReferenceCapableAnnotationHandler, AnnotationConstants {

	public class MissingRequiredOptionException extends RuntimeException {

		public MissingRequiredOptionException(String arg0) {
			super(arg0);
		}

	}

	public class MissingRequiredBindingException extends RuntimeException {

		public MissingRequiredBindingException(String arg0) {
			super(arg0);
		}

	}
	
	private String type_;

	private TemplateInjector templateInjector_ = BasicTemplateInjector
			.getInstance();

	private List requiredBindings_ = new ArrayList();

	private List requiredOptions_ = new ArrayList();
	
	public AbstractAnnotation(String type){
		type_ = type;
	}

	public String handle(String template, Map options, Map bindings) {
		return handle(template,options,bindings,null);
	}
	

	public String handle(String template, Map options, Map localbindings,
			Map<String,Map> globalbindings) {
		checkOptions(options, requiredOptions_);
		checkBindings(localbindings, requiredBindings_);
		return handleImpl(template, options, localbindings, globalbindings);
	}

	public void addRequiredBinding(String bindingname) {
		requiredBindings_.add(bindingname);
	}

	public void removeRequiredBinding(String bindingname) {
		requiredBindings_.remove(bindingname);
	}

	public void addRequiredOption(String optionname) {
		requiredOptions_.add(optionname);
	}

	public void removeRequiredOption(String optionname) {
		requiredOptions_.remove(optionname);
	}

	public void checkBindings(Map bindings, List requiredkeys) {
		for (Object key : requiredkeys) {
			if (!bindings.containsKey(key))
				throw new MissingRequiredBindingException(
						"A value for the required binding \"" + key
								+ "\" was not found.");
		}
	}

	public void checkOptions(Map options, List requiredkeys) {
		for (Object key : requiredkeys) {
			if (!options.containsKey(key))
				throw new MissingRequiredBindingException(
						"A value for the required option \"" + key
								+ "\" was not specified.");
		}
	}

	/**
	 * @return the requiredBindings
	 */
	public List getRequiredBindings() {
		return requiredBindings_;
	}

	/**
	 * @param requiredBindings
	 *            the requiredBindings to set
	 */
	public void setRequiredBindings(List requiredBindings) {
		requiredBindings_ = requiredBindings;
	}

	/**
	 * @return the requiredOptions
	 */
	public List getRequiredOptions() {
		return requiredOptions_;
	}

	/**
	 * @param requiredOptions
	 *            the requiredOptions to set
	 */
	public void setRequiredOptions(List requiredOptions) {
		requiredOptions_ = requiredOptions;
	}

	/**
	 * @return the templateInjector
	 */
	public TemplateInjector getTemplateInjector() {
		return templateInjector_;
	}

	/**
	 * @param templateInjector
	 *            the templateInjector to set
	 */
	public void setTemplateInjector(TemplateInjector templateInjector) {
		templateInjector_ = templateInjector;
	}

	
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type_;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		type_ = type;
	}

	protected abstract String handleImpl(String template, Map options,
			Map bindings, Map<String,Map> globalbindings);
}
