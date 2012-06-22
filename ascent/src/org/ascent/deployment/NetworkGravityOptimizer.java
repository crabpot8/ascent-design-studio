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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.HasSize;
import org.ascent.Util;
import org.ascent.binpacking.Packer;

public class NetworkGravityOptimizer {

	private class GravitySorter implements Comparator<Component> {
		private Map<Component, Integer> gravityList_;

		public GravitySorter(Map<Component, Integer> gravityList) {
			super();
			gravityList_ = gravityList;
		}

		public int compare(Component o1, Component o2) {
			return gravityList_.get(o1) - gravityList_.get(o2);
		}

	}

	public class InteractionGroupComparator implements
			Comparator<InteractionGroup> {
		private int resource_ = 0;

		public int compare(InteractionGroup o1, InteractionGroup o2) {
			return o2.getSize()[resource_] - o1.getSize()[resource_];
		}

	}

	public class TargetGroupSize implements HasSize {
		private InteractionGroup group_;
		private int[] size_;

		public TargetGroupSize(InteractionGroup group) {
			super();
			group_ = group;
			size_ = new int[group_.getTargetComponents().get(0).getSize().length];
			for (int i = 0; i < group_.getTargetComponents().size(); i++) {
				size_ = increment(size_, group_.getTargetComponents().get(i)
						.getSize());
			}
		}

		public int[] getSize() {
			return size_;
		}

		public void setSize(int[] size) {
			size_ = size;
		}
	}

	public class InteractionGroup {
		private List<Component> targetComponents_ = new ArrayList<Component>();

		private int[] size_;
		private Component source_;
		private Node target_;

		public InteractionGroup(Component source, Node target) {
			super();
			source_ = source;
			target_ = target;
		}

		public InteractionGroup(Component source, Node target, int[] size) {
			super();
			source_ = source;
			target_ = target;
			size_ = size;
		}

		public int[] getSize() {
			return size_;
		}

		public void setSize(int[] size) {
			size_ = size;
		}

		public Component getSource() {
			return source_;
		}

		public void setSource(Component source) {
			source_ = source;
		}

		public Node getTarget() {
			return target_;
		}

		public void setTarget(Node target) {
			target_ = target;
		}

		public List<Component> getTargetComponents() {
			return targetComponents_;
		}

		public void setTargetComponents(List<Component> targetComponents) {
			targetComponents_ = targetComponents;
		}

		public String toString() {
			String str = "" + source_.getLabel() + "-->" + target_.getLabel()
					+ "(";
			for (int i = 0; i < size_.length; i++) {
				if (i != 0)
					str += ",";
				str += size_[i];
			}
			str += ")";
			return str;
		}
	}

	private Packer packer_ = new Packer();
	private DeploymentConfig config_;
	private List<Interaction> queue_;

	private boolean allowBidirectionalSwaps_ = true;
	private boolean allowPushedSwaps_ = true;

	public void optimize(DeploymentPlan plan) {

		// Given a component A, hosted on node N1
		//
		// An interaction set is:
		//
		// A series of interactions with components Ai all hosted on a node Ni
		// != N1
		//
		// Local gravity is defined as:
		// A series of interactions between A and the other components hosted on
		// N1

		// Algorithm:
		// 1. Sort the interaction sets such that the largest interaction
		// sets come first
		// 2. Take the largest interaction set and see if A can be moved to
		// Ni.
		//
		// A can be moved to Ni iff the interaction set exceeds the
		// local gravity for A
		//
		// 2.a If it can be, move it there and update all of the interaction
		// sets
		// 2.b If it can not move, remove the interaction from the list and pick
		// the next one
		// 3. Repeat the process until there are no interactions left in the
		// queue

		List<InteractionGroup> queue = findInteractionGroups(plan);
		Collections.sort(queue, new InteractionGroupComparator());
		
		// We need to make sure that the deployment problem spec
		// and the packer we are using have the same set of
		// resource consumption policies
		packer_.setResourceConsumptionPolicies(plan.getDeploymentConfiguration().getResourceConsumptionPolicies());

		while (queue.size() > 0) {
			// Find the current biggest interaction group
			InteractionGroup g = queue.remove(0);

			if(Util.allNegative(g.getSize()))
					break;
			// First, try to eliminate the interaction group
			// by placing it's source on the target node
			if (fits(plan, g.getSource(), g.getTarget())) {
				plan.moveTo(g.getSource(), g.getTarget());
				// Update all of the interaction groups to 
				// reflect the move
				queue = findInteractionGroups(plan);
				Collections.sort(queue, new InteractionGroupComparator());
			} else if (allowBidirectionalSwaps_
					&& canMoveTargetsToSource(plan, g)) {
				// if the source won't fit on the target,
				// check if all of the components on the target
				// end can be moved to the source's node instead

				Node host = plan.getHost(g.getSource());
				Map<Component, Node> swapplan = new HashMap<Component, Node>();
				for (Component c : g.getTargetComponents()) {
					swapplan.put(c, host);
				}

				// Now, we check and see if this move is
				// acutally going to be beneficial before we
				// implement it.
				int[] change = calculateNetChange(plan, swapplan);
				if (Util.allNegative(change)) {
					for (Component gc : g.getTargetComponents()) {
						plan.moveTo(gc, host);
					}
					
					// Update all of the interaction groups to 
					// reflect the move
					queue = findInteractionGroups(plan);
					Collections.sort(queue, new InteractionGroupComparator());
				}
			} else if (allowPushedSwaps_) {
				// the only thing left to try is forcing the
				// move and then trying to push other components
				// on the target off to other nodes and seeing
				// if it produces a better solution

				boolean push = false;
				Map<Component, Node> pushplan = findPushPlan(plan, g
						.getSource(), g.getTarget(), g);
				if (pushplan != null) {
					pushplan.put(g.getSource(), g.getTarget());
					// At this point, we need to calculate the
					// new network value and see if it is an
					// improvement (i.e. does the push help?)
					int[] effect = calculateNetChange(plan, pushplan);
					push = Util.allNegative(effect);
				}
				if (push) {
					// Looks good...lets do it!
					for (Component c : pushplan.keySet()) {
						plan.moveTo(c, pushplan.get(c));
					}
					
					// Update all of the interaction groups to 
					// reflect the move
					queue = findInteractionGroups(plan);
					Collections.sort(queue, new InteractionGroupComparator());
				} else {
					// Now, should we try pushing the target
					// items onto the source...eg...do the same
					// thing but in reverse?
					Node host = plan.getHost(g.getSource());
					pushplan = findPushPlan(plan, new TargetGroupSize(g), host,
							g);

					if (pushplan != null) {

						for (Component t : g.getTargetComponents()) {
							pushplan.put(t, host);
						}
						// At this point, we need to calculate the
						// new network value and see if it is an
						// improvement (i.e. does the push help?)
						int[] effect = calculateNetChange(plan, pushplan);

						if (Util.allNegative(effect)) {
							// Looks good...lets do it!
							for (Component c : pushplan.keySet()) {
								plan.moveTo(c, pushplan.get(c));
							}
							
							// Update all of the interaction groups to 
							// reflect the move
							queue = findInteractionGroups(plan);
							Collections.sort(queue,
									new InteractionGroupComparator());
						}
					}
				}
			}
		}

	}

	
	/**
	 * This method sorts the components by their local gravity. 
	 * Components with less gravity should 
	 * @param comps
	 * @return
	 */
	public List<Component> orderByLocalGravity(List<Component> comps) {
		Map<Component, Integer> gravity = new HashMap<Component, Integer>();
		for (Component c : comps) {
			gravity.put(c,0);
		}
		for (Component c : comps) {
			for (Interaction i : c.getInteractions()) {
				for (Component t : i.getParticipants()) {
					int v = (gravity.get(t) != null) ? gravity.get(t) : 0;
					
					/*
					 * This should probably allow for more than
					 * just one integer value from the array. At
					 * some point, this should be extended to have
					 * a multi-dimensional view of size.
					 */
					gravity.put(t, v + getInteractionSize(i)[0]);
				}
			}
		}
		Collections.sort(comps, new GravitySorter(gravity));
		return comps;
	}

	public Map<Component, Node> findPushPlan(DeploymentPlan p, HasSize c,
			Node n, InteractionGroup g) {
		Map<Component, Node> pushplan = new HashMap<Component, Node>();
		List<Component> hosted = new ArrayList<Component>(Arrays.asList(p
				.getHostedComponents(n)));

		// Figure out how much we are overconsuming resources
		// by pushing the component onto the node
		int[] resid = packer_.insert(c, hosted, n);

		// --------------------------------------------------
		// --------------------------------------------------
		// Shouldn't there be a loop here that first tries
		// to push stuff off that isn't in the interaction group?
		// Or maybe we pretend the new component is on the node
		// and then we sort based on least local gravity?
		// --------------------------------------------------
		// --------------------------------------------------
		DeploymentPlan p2 = p.clonePlan();

		List locked = new ArrayList();
		locked.add(g.getSource());
		for (Component tc : g.getTargetComponents())
			locked.add(tc);

		if (c instanceof Component) {
			hosted.add((Component) c);
			orderByLocalGravity(hosted);
			hosted.remove(c);
			p2.moveTo((Component) c, n);
		} else {
			// To do...handle complete interaction
			// groups that are being pushed
			TargetGroupSize gs = (TargetGroupSize) c;
			for (Component tc : gs.group_.getTargetComponents()) {
				hosted.add(tc);
				p2.moveTo(tc, n);
			}
			orderByLocalGravity(hosted);
			for (Component tc : gs.group_.getTargetComponents())
				hosted.remove(tc);
		}

		// We should sort based on gravity before doing this...
		// Collections.sort(hosted,someGravitySorter);
		for (Component pc : hosted) {

			if (locked.contains(pc))
				continue;

			// Find an alternate node for the component
			Node alt = findAlternateHost(p2, pc, n);
			if (alt != null) {

				// Figure out how the move is going to impact
				// the network
				int[] nc = calculateNetChangeOfMove(p, pushplan, pc, alt);

				// Update the plan for pushing components off the ndoe
				pushplan.put(pc, alt);

				p2.moveTo(pc, alt);

				
				/*
				 *  -------------------------------------------
				 *  This is a BUG
				 *  The net increase for CPU will be more than
				 *  just the utilization of the component, it
				 *  must be recalculated using Liu & Layland, etc.
				 *  
				 *  We should probably just call fits(...) with
				 *  the new allocation and see if it works. That
				 *  will preserve the black box nature of the
				 *  algorithm.
				 *  -------------------------------------------
				 */
				// Update the current overconsumed resource count
				increment(resid, pc.getSize());

				// Once we have pushed enough stuff off the node
				// to be back in the black in terms of resources,
				// we can stop
				if (Util.allNonNegative(resid)) {
					break;
				}
			}
		}
		
		// We couldn't push enough stuff off to
		// make the move feasible...
		if (!Util.allNonNegative(resid))
			return null;

		return pushplan;
	}

	public Node findAlternateHost(DeploymentPlan p, Component c, Node curr) {
		for (Node n : p.getDeploymentConfiguration().getNodes()) {
			if (n != curr) {
				if (fits(p, c, n)) {
					return n;
				}
			}
		}
		return null;
	}

	/**
	 * This method calculates the net effect of a series of moves of components
	 * from one node to another. The swapplan is the list of components that are
	 * being moved and their new hosts. The method returns an array representing
	 * the net change on the resources available on the network. Negative values
	 * in the array indicate that resources are being freed (the move is
	 * decreasing network load).
	 * 
	 * @param plan -
	 *            the current deployment state
	 * @param swapplan -
	 *            the components that are being moved at the same time
	 * @return the net effect on the network
	 */
	public int[] calculateNetChange(DeploymentPlan plan,
			Map<Component, Node> swapplan) {
		int[] change = null;
		for (Component c : swapplan.keySet()) {
			int[] lc = calculateNetChangeOfMove(plan, swapplan, c, swapplan
					.get(c));
			if (change == null)
				change = lc;
			else
				increment(change, lc);
		}
		return change;
	}

	/**
	 * This method returns true if all components on the target end of the
	 * interaction group can be moved to the node hosting the source of the
	 * interaction group.
	 * 
	 * @param p
	 * @param g
	 * @return
	 */
	public boolean canMoveTargetsToSource(DeploymentPlan p, InteractionGroup g) {
		Node sh = p.getHost(g.getSource());
		return fits(p, new TargetGroupSize(g), sh);
	}

	/**
	 * This method searches through a deployment plan and finds all interaction
	 * groups. An interaction group is a component's communication with a one or
	 * more components on another node.
	 * 
	 * Given a component A, hosted on node N1
	 * 
	 * An interaction group is:
	 * 
	 * A series of interactions with components Ai all hosted on a node Ni != N1
	 * 
	 * @param plan
	 * @return
	 */
	public List<InteractionGroup> findInteractionGroups(DeploymentPlan plan) {
		List<InteractionGroup> groups = new ArrayList<InteractionGroup>();

		for (Component c : plan.getDeploymentConfiguration().getComponents()) {

			Node host = getHost(plan, c);
			Map<Node, int[]> intmap = new HashMap<Node, int[]>();
			Map<Node, List<Component>> cmpmap = new HashMap<Node, List<Component>>();
			int[] local = null;

			for (Interaction i : c.getInteractions()) {

				if (local == null) {
					local = new int[i.getSize().length];
					intmap.put(host, local);
				}

				for (Component t : i.getParticipants()) {
					if (t != c) {
						Node n = getHost(plan, t);
						int[] val = (intmap.get(n) != null) ? intmap.get(n)
								: new int[i.getSize().length];
						intmap.put(n, increment(val, getInteractionSize(i)));

						List<Component> comps = cmpmap.get(n);
						if (comps == null) {
							comps = new ArrayList<Component>();
							cmpmap.put(n, comps);
						}
						comps.add(t);

					}
				}
			}

			local = intmap.get(host);
			for (Node n : intmap.keySet()) {
				if (n != host) {
					int[] res = intmap.get(n);
					if(Util.allPositive(res)){
						InteractionGroup g = new InteractionGroup(c, n, decrement(
							res, local));
						g.setTargetComponents(cmpmap.get(n));
						groups.add(g);
					}
				}
			}
		}
		return groups;
	}

	/**
	 * This method calculates the net effect of a move of a component from one
	 * node to another. The method returns an array representing the net change
	 * on the resources available on the network. Negative values in the array
	 * indicate that resources are being freed (the move is decreasing network
	 * load).
	 * 
	 * @param plan -
	 *            the current deployment state
	 * @param c -
	 *            the current component that we are calculating the net effect
	 *            of movement for
	 * @param moveto -
	 *            the host the component is going to be moved to
	 * @return the net effect on the network
	 */
	public int[] calculateNetChangeOfMove(DeploymentPlan plan, Component c,
			Node moveto) {
		return calculateNetChangeOfMove(plan, new HashMap<Component, Node>(0),
				c, moveto);
	}

	/**
	 * This method calculates the net effect of a move of a component from one
	 * node to another. The swapplan allows the method to take into account
	 * other components that are going to be moved simultaneously. The method
	 * returns an array representing the net change on the resources available
	 * on the network. Negative values in the array indicate that resources are
	 * being freed (the move is decreasing network load).
	 * 
	 * @param plan -
	 *            the current deployment state
	 * @param swapplan -
	 *            the components that are being moved at the same time
	 * @param c -
	 *            the current component that we are calculating the net effect
	 *            of movement for
	 * @param moveto -
	 *            the host the component is going to be moved to
	 * @return the net effect on the network
	 */
	public int[] calculateNetChangeOfMove(DeploymentPlan plan,
			Map<Component, Node> swapplan, Component c, Node moveto) {
		Node currhost = getHost(plan, c);
		int[] curr = null;
		int[] moved = null;

		if(c.getInteractions().length == 0)
			return new int[plan.getDeploymentConfiguration().getNetworks()[0].getResources().length];
		
		for (Interaction i : c.getInteractions()) {
			int[] size = getInteractionSize(i);

			if (curr == null) {
				curr = new int[size.length];
			}
			if (moved == null) {
				moved = new int[size.length];
			}

			for (Component o : i.getParticipants()) {
				if (o != c) {
					Node chost = getHost(plan, o);
					Node nhost = (swapplan.get(o) != null) ? swapplan.get(o)
							: chost;

					if (nhost != moveto)
						increment(moved, size);
					if (chost != currhost)
						increment(curr, size);
				}
			}
		}
		return decrement(moved, curr);
	}

	/**
	 * Decrements one array by the values in the other array.
	 * 
	 * @param todecrement
	 *            (NOTE: This array will get changed)!!!
	 * @param byamount
	 * @return the decremented array
	 */
	public int[] decrement(int[] todecrement, int[] byamount) {
		if(todecrement == null || byamount == null)
			System.out.println();
		for (int i = 0; i < todecrement.length; i++) {
			todecrement[i] -= byamount[i];
		}
		return todecrement;
	}

	/**
	 * Increments one array by the values in the other array.
	 * 
	 * @param toincrement
	 *            (NOTE: This array will get changed)!!!
	 * @param byamount
	 * @return the incremented array
	 */
	public int[] increment(int[] toincrement, int[] byamount) {
		for (int i = 0; i < toincrement.length; i++) {
			toincrement[i] += byamount[i];
		}
		return toincrement;
	}

	public Node getHost(DeploymentPlan plan, Component c) {
		return plan.getHost(c);
	}

	/**
	 * Returns true if the component will fit onto a node given its current
	 * resource state.
	 * 
	 * @param plan
	 * @param c
	 * @param n
	 * @return
	 */
	public boolean fits(DeploymentPlan plan, HasSize c, Node n) {
		int[] resid = packer_.insert(c, Arrays.asList(plan
				.getHostedComponents(n)), n);
		return Util.allNonNegative(resid);
	}
	
	/**
	 * This method returns the *size* of an interaction. This
	 * method can be overridden to calculate the size differently.
	 * The default is (interaction period) * (size[i]).
	 * @param i
	 * @return
	 */
	public int[] getInteractionSize(Interaction i){
		return new int[]{(int)Math.rint(i.getSize()[0] * i.getRate())};
	}
}
