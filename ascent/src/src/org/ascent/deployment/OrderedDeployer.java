/**************************************************************************
 * Copyright 2009 Jules White                                              *
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.VectorSolution;
import org.ascent.binpacking.BinPackingProblem;
import org.ascent.binpacking.FFDBinPacker;
import org.ascent.binpacking.LeastBoundPacker;

/**
 * This class provides a method to setup a bin-packer to
 * place items in bins per a set order. The deploy 
 * method forces an FFDBinPacker variant to perform as
 * if it was solving an online bin-packing problem. This
 * is class is typically used by solvers that want to
 * store the solution state as a permutation of the inputs
 * to a bin-packing problem. 
 * @author jules
 *
 */
public class OrderedDeployer {

	private BinPackingProblem binProblem_;
	private DeploymentConfig conf_;
	private Map mapping_;

	public OrderedDeployer(DeploymentConfig conf) {
		conf_ = conf;
		init();
	}

	protected void init() {
		Map mapping = new HashMap();
		BinPackingProblem bp = new BinPackingProblem();
		bp.getResourcePolicies().putAll(conf_.getResourceConsumptionPolicies());
		for (Node n : conf_.getNodes()) {
			HardwareNode hn = new HardwareNode(n.getLabel(), n.getResources());
			bp.getBins().add(hn);
			mapping.put(hn, n);
		}
		for (Component c : conf_.getComponents()) {
			SoftwareComponent cn = new SoftwareComponent(c.getLabel(), c
					.getResources());
			cn.setRealTimeTasks(c.getRealTimeTasks());
			bp.getItems().add(cn);
			mapping.put(c, cn);
		}
		for (DeploymentConstraint con : conf_.getConstraints()) {
			if (con instanceof NotColocated) {
				NotColocated ncon = (NotColocated) con;
				SoftwareComponent sc = (SoftwareComponent) mapping.get(ncon
						.getSource());

				for (Component c : ncon.getTargets()) {
					SoftwareComponent tc = (SoftwareComponent) mapping.get(c);
					sc.getExclusions().add(tc);
				}
			}
			if (con instanceof Colocated) {
				Colocated ncon = (Colocated) con;
				SoftwareComponent sc = (SoftwareComponent) mapping.get(ncon
						.getSource());

				for (Component c : ncon.getTargets()) {
					SoftwareComponent tc = (SoftwareComponent) mapping.get(c);
					sc.getDependencies().add(tc);
				}
			}
		}
		mapping_ = mapping;
		binProblem_ = bp;
	}
	
	/**\
	 * This method uses an FFDBinPacker and the specified packing
	 * order and configures the packer to pack the items in
	 * the predefined order. The method returns the result
	 * of packing the items in the given order with the
	 * FFDBinPacker.
	 * 
	 * @param order - the order to pack items in
	 * @return
	 */
	public DeploymentPlan deploy(VectorSolution order) {
		return deploy(new LeastBoundPacker(),order);
	}

	/**\
	 * This method takes a bin-packer and a specified packing
	 * order and configures the packer to pack the items in
	 * the predefined order. The method returns the result
	 * of packing the items in the given order with the
	 * provided bin-packer.
	 * 
	 * 
	 * @param core - the bin-packer to use
	 * @param order - the order to pack items in
	 * @return
	 */
	public DeploymentPlan deploy(FFDBinPacker core,
			VectorSolution order) {

		//Setup the bin packer with the constraints
		core.configure(binProblem_);

		//Force the bin packer to select source items in
		//the required order
		for (int i = 0; i < order.getPosition().length; i++) {
			core.getPreSelectionQueue().add(
					mapping_.get(conf_.getComponents()[order.getPosition()[i]]));
		}

		//Find a packing
		Map<Object, List> sol = core.nextMapping();

		//Now map the packing solution back to the
		//components used by the problem
		int[] pos = new int[conf_.getComponents().length];
		for (int j = 0; j < conf_.getComponents().length; j++) {
			SoftwareComponent c = (SoftwareComponent) mapping_.get(conf_
					.getComponents()[j]);
			if (c != null && sol != null && sol.get(c) != null) {

				HardwareNode host = (HardwareNode) sol.get(c).get(0);
				Node node = (Node) mapping_.get(host);
				for (int k = 0; k < conf_.getNodes().length; k++) {
					if (conf_.getNodes()[k] == node) {
						pos[j] = k;
						break;
					}
				}
			}
		}

		//Turn the solution into a deploymentplan
		return new DeploymentPlan(conf_, new VectorSolution(pos));
	}
}
