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
import java.util.List;

public class Util {

	public static void collectRequired(Dependencies deps, List all,
			DependencyManager m) {
		for (Dependency d : deps) {
			if (d instanceof Requires) {
				List refs = d.getDependencies();
				for (Object o : refs) {
					if (!all.contains(o)) {
						Dependencies odeps = m.getDependencies(o);
						collectRequired(odeps, all, m);
						all.add(o);
					}
				}
			}
		}
	}

	public static void collectExcluded(Object src, List excluded,
			DependencyManager m) {
		List required = new ArrayList();
		required.add(src);
		collectRequired(m.getDependencies(src), required, m);
		collectExcluded(required, excluded, m);
	}

	public static void collectExcluded(List srcs, List excluded,
			DependencyManager m) {
		for (Object src : srcs) {

			Dependencies deps = m.getDependencies(src);
			for (Dependency d : deps) {
				if (d instanceof Excludes) {
					List refs = d.getDependencies();
					for (Object o : refs) {
						if (!excluded.contains(o)) {
							excluded.add(o);
						}
					}
				}
			}
		}
	}
	
	public static boolean willFit(ItemState ss, BinState ts) {
		for (int i = 0; i < ss.getSizeWithDependencies().length; i++)
			if (ss.getSizeWithDependencies()[i] > ts.getSize()[i])
				return false;

		return true;
	}

	public static void insert(ItemState ss, BinState ts) {
		for (int i = 0; i < ss.getSize().length; i++)
			ts.getSize()[i] -= ss.getSize()[i];
	}

	public static void remove(ItemState ss, BinState ts) {
		for (int i = 0; i < ss.getSize().length; i++)
			ts.getSize()[i] += ss.getSize()[i];
	}
}
