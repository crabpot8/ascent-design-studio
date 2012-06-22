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


package org.ascent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Util {

	public static List<List> permute(List s1, List s2){
		List result = new ArrayList<List>(s1.size() * s2.size());
		for(int i = 0; i < s1.size(); i++){
			Object o1 = s1.get(i);
			
			for(int j = 0; j < s2.size(); j++){
				Object o2 = s2.get(j);
				ArrayList item = new ArrayList();
				
				if(o1 instanceof Collection)
					item.addAll((Collection)o1);
				else
					item.add(o1);
			}
		}
		return result;
	}
	
	public static int random(int min, int max) {
		double rand = Math.random();
		double range = max - min;
		double delta = range * rand;
		return (int) Math.rint((min + delta));
	}
	
	public static double random(double min, double max) {
		double rand = Math.random();
		double range = max - min;
		double delta = range * rand;
		return min + delta;
	}
	
	public static boolean allNonNegative(int[] vals){
		for(int i = 0; i < vals.length; i++)
			if(vals[i] < 0)
				return false;
		return true;
	}
	
	public static boolean allPositive(int[] vals){
		for(int i = 0; i < vals.length; i++)
			if(vals[i] <= 0)
				return false;
		return true;
	}
	
	public static boolean allNegative(int[] vals){
		for(int i = 0; i < vals.length; i++)
			if(vals[i] > -1)
				return false;
		return true;
	}
	
	public static String toString(int[] res) {
		String str = "[";
		for (int i = 0; i < res.length; i++) {
			if (i != 0) {
				str += ",";
			}
			str += res[i];
		}
		str += "]";
		return str;
	}
}
