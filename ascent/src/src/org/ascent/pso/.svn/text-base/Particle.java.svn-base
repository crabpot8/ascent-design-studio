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

import org.ascent.VectorSolution;

public class Particle {
	
	private boolean useRandomMultiplier_ = true;
	private ParticleGroup group_;
	private Pso pso_;
	private VectorSolution position_;
	private int[] velocity_;

	public Particle(VectorSolution pos, Pso pso, ParticleGroup g) {
		position_ = pos;
		velocity_ = new int[pos.getPosition().length];
		pso_ = pso;
		group_ = g;
	}

	public String toString() {
		double v = (pso_ != null && pso_.getFitnessFunction() != null) ? pso_.getFitnessFunction().getValue(position_):0;
		String str = "Particle ("+v+")[";
		for (int i : position_.getPosition()) {
			str += i + ",";
		}
		str += "] v{";
		for (int i = 0; i < velocity_.length; i++) {
			str += velocity_[i] + ",";
		}
		str += "}";

		return str;
	}

	public void update() {
		int[] veloc = calclateNewVelocity(pso_.getGlobalBest(), group_.getBestLocalPosition(this).position_, position_);
		position_ = updatePosition(position_, veloc);
		velocity_ = veloc;
		if (!pso_.isGlobalBestMustBeFeasible() || pso_.isFeasible(position_)) {
			double val = pso_.getFitnessFunction().getValue(position_);
			if (val > pso_.getGlobalBestValue()) {
				pso_.setGlobalBest(position_.cloneSolution());
				pso_.setGlobalBestValue(val);
				pso_.setLeader(this);
			}
			group_.moved(this);
		}
	}

	public int[] calclateNewVelocity(VectorSolution gbest, VectorSolution lbest,
			VectorSolution curr) {
		int[] veloc = new int[gbest.getPosition().length];
		for (int i = 0; i < veloc.length; i++) {
			if (curr.getPosition().length > i) {
				int loc = curr.getPosition()[i];
				int gloc = gbest.getPosition()[i];
				int lloc = lbest.getPosition()[i];
				double gr = (useRandomMultiplier_)? Math.random() : 1;
				double lr = (useRandomMultiplier_)? Math.random() : 1;
				veloc[i] = ((int) Math.rint(pso_.getInertia() * velocity_[i]))
						+ ((int) Math.rint(pso_.getGlobalLearningRate()
								* (gloc - loc) * gr))
						+ ((int) Math.rint(pso_.getLocalLearningRate()
								* (lloc - loc) * lr));

				if (veloc[i] > 0)
					veloc[i] = Math.min(veloc[i], pso_.getVelocityMax());
				else if (veloc[i] < 0)
					veloc[i] = Math.max(veloc[i], -1 * pso_.getVelocityMax());
			}
		}
		return veloc;
	}

	public VectorSolution updatePosition(VectorSolution curr, int[] veloc) {
		for (int i = 0; i < veloc.length; i++) {
			int loc = curr.getPosition()[i];
			int nloc = loc + veloc[i];
			if (nloc < 0)
				nloc = 0;
			else if (nloc > getComponentMax(i))
				nloc = getComponentMax(i);

			curr.getPosition()[i] = nloc;
		}
		return new VectorSolution(curr.getPosition());
	}

	public boolean equals(Object o) {
		Particle p = (Particle) o;
		for (int i = 0; i < position_.getPosition().length; i++) {
			if (position_.getPosition()[i] != p.position_.getPosition()[i])
				return false;
		}
		for (int i = 0; i < velocity_.length; i++) {
			if (velocity_[i] != p.velocity_[i])
				return false;
		}
		return true;
	}

	public int getComponentMax(int i) {
		return pso_.getPositionBoundaries()[i][1];
	}

	public int getComponentMin(int i) {
		return pso_.getPositionBoundaries()[i][0];
	}

	public VectorSolution getPosition() {
		return position_;
	}

	public void setPosition(VectorSolution position) {
		position_ = position;
	}

	public ParticleGroup getGroup() {
		return group_;
	}

	public void setGroup(ParticleGroup group) {
		group_ = group;
	}

	public Pso getPso() {
		return pso_;
	}

	public void setPso(Pso pso) {
		pso_ = pso;
	}

}
