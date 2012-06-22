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

import java.util.List;
import java.util.Map;

import org.ascent.configurator.conf.RefreshProblem;
import org.ascent.configurator.conf.debug.SolutionConsistencyChecker;

/**
 * This class provides a number of static
 * utility methods for dealing with solutions
 * from a <code>RefreshCore</code>.
 * 
 * @author Jules White
 *
 */
public class Solutions {

	private Solutions(){}
	
	/**
	 * This method checks that the specified solution
	 * is consistent with all of the constraints in
	 * the specified <code>RefreshProblem</code>. If
	 * the solution is not consistent, a <code>RuntimeException</code>
	 * is thrown. A solution is consistent if all of the source
	 * and target items from the solution are defined in the
	 * problem and all of the mapping constraints from the
	 * problem are observed by the mapping in the solution.
	 * 
	 * @param problem the RefreshProblem that the solution must be
	 *                consistent with
	 * @param solution the solution from the RefreshCore that is being
	 *                 checked for consistency
	 */
	public static void assertConsistent(RefreshProblem problem, Map<Object,List> solution){
		SolutionConsistencyChecker c = new SolutionConsistencyChecker(problem);
		c.assertConsistent(solution);
	}
}
