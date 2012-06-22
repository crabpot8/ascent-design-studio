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

import org.ascent.injectors.annotations.ForEachAnnotation;
import org.ascent.injectors.annotations.IfEnabledAnnotation;
import org.ascent.injectors.annotations.InsertAnnotation;
import org.ascent.injectors.annotations.XMLReplaceAnnotation;


public class XMLInjector extends CommentBasedInjector {

	public static XMLInjector createDefaultInjector() {
		XMLInjector injector = new XMLInjector();
		injector.addAnnotationHandler(new ForEachAnnotation());
		injector.addAnnotationHandler(new InsertAnnotation());
		injector.addAnnotationHandler(new XMLReplaceAnnotation());
		injector.addAnnotationHandler(new IfEnabledAnnotation());
		return injector;
	}

	public static final String MULTI_LINE_COMMENT_START = "<!--";
	public static final String MULTI_LINE_COMMENT_END = "-->";
	
	public XMLInjector(){
		super(MULTI_LINE_COMMENT_START,MULTI_LINE_COMMENT_END);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.refresh.injectors.Injector#inject(java.util.Map, java.util.Map,
	 *      java.lang.String)
	 */
	protected String injectImpl(Map<String, List> features,
			Map<String, Map> values, String conf) {

		
		String result = super.injectImpl(features, values, conf);

		if (getStripSpaces()) {
			result = result.replaceAll("\\>[\\s]+\\<+", "><");
			result = result.replaceAll("[\\n]", "");
		}

		return result;
	}

}
