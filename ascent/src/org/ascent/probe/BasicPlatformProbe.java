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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BasicPlatformProbe implements PropertyProbe {
	public static final String OS_NAME = "os.name";

	public static final String OS_ARCH = "os.arch";

	public static final String OS_VERSION = "os.versions";

	public static final String PLATFORM_MEMORY = "memory";

	public static final String PLATFORM_FREE_MEMORY = "free-memory";

	private Map properties_;

	public void run() {
		properties_ = new HashMap(19);

		Properties p = System.getProperties();
		Enumeration keys = p.propertyNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = p.getProperty(key);
			properties_.put(key, value);
		}

		long memmax = Runtime.getRuntime().maxMemory();
		properties_.put(PLATFORM_MEMORY, memmax);

		long fremem = Runtime.getRuntime().freeMemory();
		properties_.put(PLATFORM_FREE_MEMORY, fremem);
	}

	public Map getProperties() {
		return properties_;
	}

	public void setProperties(Map properties) {
		this.properties_ = properties;
	}

}
