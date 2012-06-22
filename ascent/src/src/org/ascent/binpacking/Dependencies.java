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


package org.ascent.binpacking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Dependencies implements Iterable<Dependency> {
	private List<Dependency> dependencies_ = new ArrayList<Dependency>();

	public void update(Object target) {
		for (Dependency d : dependencies_) {
			d.update(target);
		}
	}

	public boolean add(Dependency arg0) {
		return dependencies_.add(arg0);
	}

	public boolean remove(Object arg0) {
		return dependencies_.remove(arg0);
	}

	public Iterator<Dependency> iterator() {
		return dependencies_.iterator();
	}

	public int size() {
		return dependencies_.size();
	}

}

