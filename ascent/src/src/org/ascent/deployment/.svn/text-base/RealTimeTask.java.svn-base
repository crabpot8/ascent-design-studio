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

package org.ascent.deployment;

public class RealTimeTask {
	private double period_ = 0;
	private double utilization_ = 0;

	public RealTimeTask() {
	}

	public RealTimeTask(double period, double util) {
		super();
		period_ = period;
		utilization_ = util;
	}

	public double getPeriod() {
		return period_;
	}

	public void setPeriod(double period) {
		period_ = period;
	}

	public double getUtilization() {
		return utilization_;
	}

	public void setUtilization(double utilization) {
		utilization_ = utilization;
	}

	public String toString(){
		return ""+utilization_+"@1/"+period_;
	}
}
