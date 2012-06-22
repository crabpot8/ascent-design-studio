/**************************************************************************
 * Copyright 2009 Brian Dougherty                                          *
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
package org.ascent.hugh;

public class Personality {
	double tenac_;
	double superstitious_;
	double spread_;
	double highRoll_;
	String name_;
	
	public Personality(){
		tenac_ = 5.0;
		superstitious_ = 5.0;
		spread_ = 5.0;
		highRoll_ = 5.0;
		name_ = "DEFAULT";
	}
	
	public Personality(double tenac, double superst, double spread, double highRoll, String name){
		tenac = tenac_;
		superstitious_ = superst;
		spread_ = spread;
		highRoll_ = highRoll;
		name_ = name;
		
	}

	public double getTenac_() {
		return tenac_;
	}

	public void setTenac_(double tenac_) {
		this.tenac_ = tenac_;
	}

	public double getSuperstitious_() {
		return superstitious_;
	}

	public void setSuperstitious_(double superstitious_) {
		this.superstitious_ = superstitious_;
	}

	public double getSpread_() {
		return spread_;
	}

	public void setSpread_(double spread_) {
		this.spread_ = spread_;
	}

	public double getHighRoll_() {
		return highRoll_;
	}

	public void setHighRoll_(double highRoll_) {
		this.highRoll_ = highRoll_;
	}

	public String getName_() {
		return name_;
	}

	public void setName_(String name_) {
		this.name_ = name_;
	}
			
}
