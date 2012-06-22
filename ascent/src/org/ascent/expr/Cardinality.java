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

package org.ascent.expr;

public class Cardinality {
	
	public static final Cardinality UNBOUNDED = new Cardinality(0,Integer.MAX_VALUE);
	
	public static Cardinality parseCardinality(String card){
		int min = 0;
		int max = Integer.MAX_VALUE;
		if(card.indexOf("..") > 0){
			int start = card.indexOf("..");
			int end = start + 2;
			String minstr = card.substring(0,start);
			String maxstr = card.substring(end);
			if(maxstr.equals("*")){
				maxstr = ""+Integer.MAX_VALUE;
			}
			min = Integer.parseInt(minstr);
			max = Integer.parseInt(maxstr);
		}
		else{
			if(!card.equals("*")){
				int val = Integer.parseInt(card);
				min = val;
				max = val;
			}
		}
		return new Cardinality(min,max);
	}
	private int min_;
	private int max_;
	public Cardinality(int min, int max){
		min_ = min;
		max_ = max;
	}
	public int getMax() {
		return max_;
	}
	public void setMax(int max) {
		max_ = max;
	}
	public int getMin() {
		return min_;
	}
	public void setMin(int min) {
		min_ = min;
	}
	
	public String toString(){
		return "["+getMin()+".."+getMax()+"]";
	}
	
}
