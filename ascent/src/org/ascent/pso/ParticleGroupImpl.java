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

package org.ascent.pso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ascent.VectorSolution;
import org.ascent.binpacking.ValueFunction;

public class ParticleGroupImpl implements ParticleGroup {

	private Comparator<Particle> comparator_ = new Comparator<Particle>() {
	
		public int compare(Particle o1, Particle o2) {
			ValueFunction<VectorSolution> fit = o1.getPso().getFitnessFunction();
			return (int)Math.rint(fit.getValue(o1.getPosition()) - fit.getValue(o2.getPosition()));
		}
	};
	
	private ValueFunction<VectorSolution> valueFunction_;
	private List<Particle> particles_ = new ArrayList<Particle>();
	
	public Particle getBestLocalPosition(Particle p) {
		return particles_.get(particles_.size() - 1);
	}

	public void moved(Particle p) {
		Collections.sort(particles_,comparator_);
	}

	public List<Particle> getParticles() {
		return particles_;
	}

	public void setParticles(List<Particle> particles) {
		particles_ = particles;
	}

}
