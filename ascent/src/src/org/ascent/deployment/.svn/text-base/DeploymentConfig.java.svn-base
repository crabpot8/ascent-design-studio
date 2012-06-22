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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascent.ProblemConfigImpl;
import org.ascent.ResourceConsumptionPolicy;
import org.ascent.Util;
import org.ascent.VectorSolution;
import org.ascent.binpacking.Bin;
import org.ascent.binpacking.BinPackingProblem;
import org.ascent.binpacking.FFDBinPacker;
import org.ascent.binpacking.Packer;
import org.ascent.binpacking.RandomItemPacker;
import org.ascent.binpacking.ValueFunction;

public class DeploymentConfig extends ProblemConfigImpl {
	
	private ValueFunction<VectorSolution> scoringFunction_ = new ValueFunction<VectorSolution>() {

		public double getValue(VectorSolution src) {
			if (src.getArtifact() == null) {
				int score = scoreDeployment(getDeploymentPlan(src));
				src.setArtifact(score);
			}
			return (Integer) src.getArtifact();
		}
	};
	
	protected NetworkLink[] networks_;
	protected Component[] components_;
	protected Node[] nodes_;
	protected Interaction[] interactions_;
	protected int[] networkResourceCoeffs_;
	protected ResourceConsumptionPolicy[] resourcePolicies_;
	protected Packer packer_ = new Packer();
	protected boolean acceptInfeasibleSolutions_ = true;

	private boolean singleNetwork_ = false;
	private Map<String, NetworkLink[]> linkCache_ = new HashMap<String, NetworkLink[]>();
	private List<Node> nStart_ = new ArrayList<Node>();
	private List<Component> cStart_ = new ArrayList<Component>();
	private List<NetworkLink> nlStart_ = new ArrayList<NetworkLink>();
	private List<Interaction> iStart_ = new ArrayList<Interaction>();
	private List<DeploymentConstraint> constraints_ = new ArrayList<DeploymentConstraint>();
	private Map<Component, Node> prePlacedComponents_ = new HashMap<Component, Node>();

	public DeploymentConfig(Node[] nodes, NetworkLink[] networks,
			Component[] components, Interaction[] interactions) {
		super(components.length, 0, nodes.length - 1);
		nodes_ = nodes;
		networks_ = networks;
		components_ = components;
		interactions_ = interactions;

		orderElements();
	}

	public DeploymentConfig() {
		super(0, 0, 0);
	}

	public DeploymentConfig(DeploymentConfig toclone) {
		super(0, 0, 0);

		if (toclone.nodes_ == null || toclone.nodes_.length == 0)
			toclone.init();

		nodes_ = new Node[toclone.getNodes().length];
		System.arraycopy(toclone.nodes_, 0, nodes_, 0, nodes_.length);

		networks_ = new NetworkLink[toclone.networks_.length];
		System.arraycopy(toclone.networks_, 0, networks_, 0, networks_.length);

		components_ = new Component[toclone.components_.length];
		System.arraycopy(toclone.components_, 0, components_, 0,
				components_.length);

		interactions_ = new Interaction[toclone.interactions_.length];
		System.arraycopy(toclone.interactions_, 0, interactions_, 0,
				interactions_.length);

		orderElements();

		init();
	}

	public DeploymentConfig(int positions, int bmin, int bmax) {
		super(positions, bmin, bmax);
	}

	public void init() {
		if (nodes_ == null) {
			nodes_ = nStart_.toArray(new Node[0]);
			components_ = cStart_.toArray(new Component[0]);
			networks_ = nlStart_.toArray(new NetworkLink[0]);
			interactions_ = iStart_.toArray(new Interaction[0]);
		}

		if (boundaries_.length != components_.length) {
			boundaries_ = new int[components_.length][2];
			for (int i = 0; i < boundaries_.length; i++) {
				boundaries_[i] = new int[] { 0, nodes_.length - 1 };
			}
		}

		boolean singlenetwork = false;
		if (networks_.length == 1) {

			singlenetwork = true;
			for (Node n : nodes_) {
				if (!networks_[0].connectsTo(n)) {
					singlenetwork = false;
					break;
				}
			}
		}
		singleNetwork_ = singlenetwork;
	}

	protected void orderElements() {
		Arrays.sort(networks_);
		Arrays.sort(nodes_);
		Arrays.sort(components_);
		Arrays.sort(interactions_);
	}

	public Node addNode(String id, int[] res) {
		Node n = new Node(nStart_.size(), id, res);
		n.setNetworkLinks(new NetworkLink[0]);
		nStart_.add(n);
		return n;
	}

	public Component addComponent(String id, int[] res) {
		Component c = new Component(cStart_.size(), id, res);
		c.setInteractions(new Interaction[0]);
		cStart_.add(c);
		return c;
	}

	public NetworkLink addNetwork(String id, Node[] nodes, int[] res) {
		NetworkLink nl = new NetworkLink(nlStart_.size(), id, nodes, res);
		for (Node n : nodes) {
			NetworkLink[] nets = n.getNetworkLinks();
			if (nets == null) {
				nets = new NetworkLink[1];
				n.setNetworkLinks(nets);
			} else {
				NetworkLink[] nints = new NetworkLink[nets.length + 1];
				System.arraycopy(nets, 0, nints, 0, nets.length);
				n.setNetworkLinks(nints);
			}
			n.getNetworkLinks()[n.getNetworkLinks().length - 1] = nl;
		}
		nlStart_.add(nl);
		return nl;
	}

	public Interaction addInteraction(String id, int[] res, double rate,
			Component[] comps) {
		Interaction i = new Interaction(iStart_.size(), id, res, rate);
		i.setParticipants(comps);

		iStart_.add(i);
		for (Component c : comps) {
			Interaction[] ints = c.getInteractions();
			if (ints == null) {
				ints = new Interaction[1];
				c.setInteractions(ints);
			} else {
				Interaction[] nints = new Interaction[ints.length + 1];
				System.arraycopy(ints, 0, nints, 0, ints.length);
				c.setInteractions(nints);
			}
			c.getInteractions()[c.getInteractions().length - 1] = i;
		}
		return i;
	}

	public Map<Object, ResourceConsumptionPolicy> getResourceConsumptionPolicies() {
		return packer_.getResourceConsumptionPolicies();
	}

	public NetworkLink[] getNetworks() {
		return networks_;
	}

	public void setNetworks(NetworkLink[] networks) {
		networks_ = networks;
	}

	public Component[] getComponents() {
		return components_;
	}

	public void setComponents(Component[] components) {
		components_ = components;
	}

	public Node[] getNodes() {
		return nodes_;
	}

	public void setNodes(Node[] nodes) {
		nodes_ = nodes;
	}

	public Interaction[] getInteractions() {
		return interactions_;
	}

	public void setInteractions(Interaction[] interactions) {
		interactions_ = interactions;
	}

	public ResourceConsumptionPolicy[] getResourcePolicies() {
		return resourcePolicies_;
	}

	public void setResourcePolicies(ResourceConsumptionPolicy[] resourcePolicies) {
		resourcePolicies_ = resourcePolicies;
	}

	public boolean isAcceptInfeasibleSolutions() {
		return acceptInfeasibleSolutions_;
	}

	public void setAcceptInfeasibleSolutions(boolean acceptInfeasibleSolutions) {
		acceptInfeasibleSolutions_ = acceptInfeasibleSolutions;
	}

	public Packer getPacker() {
		return packer_;
	}

	public void setPacker(Packer packer) {
		packer_ = packer;
	}

	public int[] sumResources(ModelElement[] els) {
		int total = els[0].resources_.length;
		int[] res = new int[total];
		for (int i = 0; i < total; i++)
			for (int j = 0; j < els.length; j++)
				res[i] += els[j].resources_[i];

		return res;
	}

	public int[] sum(int[] a, int[] b) {
		int total = a.length;
		int[] res = new int[total];
		for (int i = 0; i < total; i++)
			res[i] += (a[i] + b[i]);

		return res;
	}

	public NetworkLink[] getLinks(Node[] nodes) {
		if (nodes.length > 1) {
			ArrayList<NetworkLink> links = new ArrayList<NetworkLink>();
			NetworkLink[] possible = getLinks(nodes[0], nodes[1]);
			for (NetworkLink l : possible) {
				boolean valid = true;
				for (int i = 2; i < nodes.length; i++) {
					if (!l.connectsTo(nodes[i])) {
						valid = false;
						break;
					}
				}
				if (valid) {
					links.add(l);
				}
			}
			return links.toArray(new NetworkLink[0]);
		} else {
			return new NetworkLink[] { new LocalHostLink(0, "LocalHostLink",
					nodes[0], interactions_[0].resources_.length) };
		}
	}

	public static String toString(int[] res) {
		return Util.toString(res);
	}

	public int[] residuals(ModelElement[] hosted, ModelElement host) {
		int[] sums = sumResources(hosted);
		int[] avail = host.resources_;
		int[] resid = residuals(avail, sums);
		return resid;
	}

	public int[] residuals(int[] available, int[] consumed) {
		if (available.length != consumed.length)
			throw new DeploymentProblemSpecException(
					"The network links and interactions must specify an identical number of resources (provided/consumed)!");

		int[] resid = new int[available.length];
		for (int i = 0; i < available.length; i++) {
			resid[i] = available[i] - consumed[i];
		}
		return resid;
	}

	public NetworkLink[] getLinks(Node a, Node b) {
		if (singleNetwork_)
			return networks_;

		String k1 = a.getLabel() + "--" + b.getLabel();

		NetworkLink[] links = linkCache_.get(k1);
		if (links == null) {
			String k2 = b.getLabel() + "--" + a.getLabel();
			links = linkCache_.get(k2);
		}
		if (links == null) {
			List<NetworkLink> linksl = new ArrayList<NetworkLink>();
			for (NetworkLink l : a.getNetworkLinks()) {
				if (l.connectsTo(b)) {
					linksl.add(l);
				}
			}
			links = linksl.toArray(new NetworkLink[0]);
			linkCache_.put(k1, links);
			String k2 = b.getLabel() + "--" + a.getLabel();
			linkCache_.put(k2, links);
		}

		return links;
	}

	public boolean fits(ModelElement[] hosted, ModelElement host) {
		int[] resids = residuals(hosted, host);

		for (int r : resids) {
			if (r < 0) {
				return false;
			}
		}

		return true;
	}

	public void requireNotColocated(Component a, Component b) {
		getConstraints().add(new NotColocated(a, b));
	}

	public void requireColocated(Component a, Component b) {
		getConstraints().add(new Colocated(a, b));
	}

	public List<DeploymentConstraint> getConstraints() {
		return constraints_;
	}

	public void setConstraints(List<DeploymentConstraint> constraints) {
		constraints_ = constraints;
	}

	public VectorSolution[] createInitialSolutions(int count) {

		Map mapping = new HashMap();
		BinPackingProblem bp = new BinPackingProblem();
		bp.getResourcePolicies().putAll(getResourceConsumptionPolicies());
		for (Node n : getNodes()) {
			HardwareNode hn = new HardwareNode(n.getLabel(), n.getResources());
			bp.getBins().add(hn);
			mapping.put(hn, n);
			mapping.put(n, hn);
		}
		for (Component c : getComponents()) {
			SoftwareComponent cn = new SoftwareComponent(c.getLabel(), c
					.getResources());
			cn.setRealTimeTasks(c.getRealTimeTasks());
			bp.getItems().add(cn);
			mapping.put(c, cn);
		}

		for (DeploymentConstraint con : constraints_) {
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

			if (con instanceof PlacementConstraint) {
				PlacementConstraint pcon = (PlacementConstraint) con;
				SoftwareComponent sc = (SoftwareComponent) mapping.get(pcon
						.getSource());
				sc.setValidBins(new ArrayList<Bin>());
				for (Node n : pcon.getValidHosts()) {
					HardwareNode hn = (HardwareNode) mapping.get(n);
					sc.getValidBins().add(hn);
				}
			}
		}

		List<Map<Object, List>> binsols = new ArrayList<Map<Object, List>>();
		for (int i = 0; i < count - 2; i++) {
			RandomItemPacker packer = new RandomItemPacker(bp);
			Map<Object, List> sol = packer.nextMapping();
			binsols.add(sol);
		}
		binsols.add((new FFDBinPacker(bp)).nextMapping());

		int stotal = Math.min(binsols.size(), count);
		VectorSolution[] sols = new VectorSolution[stotal];

		for (int i = 0; i < stotal; i++) {
			int[] pos = new int[getComponents().length];
			Map<Object, List> sol = binsols.get(i);

			for (int j = 0; j < getComponents().length; j++) {
				SoftwareComponent c = (SoftwareComponent) mapping
						.get(getComponents()[j]);
				if (c != null && sol != null && sol.get(c) != null) {

					HardwareNode host = (HardwareNode) sol.get(c).get(0);
					Node node = (Node) mapping.get(host);
					for (int k = 0; k < getNodes().length; k++) {
						if (getNodes()[k] == node) {
							pos[j] = k;
							break;
						}
					}
				}
			}
			sols[i] = new VectorSolution(pos);
		}

		return sols;
	}

	public Map<Component, Node> getPrePlacedComponents() {
		return prePlacedComponents_;
	}

	public void setPrePlacedComponents(Map<Component, Node> prePlacedComponents) {
		prePlacedComponents_ = prePlacedComponents;
	}

	public int scoreDeployment(DeploymentPlan plan) {
		return 0;
	}

	public void printSolutionStats(VectorSolution vs) {
		DeploymentPlan plan = getDeploymentPlan(vs);
		printSolutionStats(plan);
	}
	
	public DeploymentPlan getDeploymentPlan(VectorSolution sol){
		return new DeploymentPlan(this,sol);
	}

	public void printSolutionStats(DeploymentPlan plan) {
		ResourceResidual resid = new ResourceResidual(this);
		resid.deploy(plan);

		System.out.println("Valid:" + resid.valid());
		System.out.println("Score:" + scoreDeployment(plan));
		System.out.println(plan);
		System.out.println(resid);
		System.out.println("----------------------------");
		System.out.println("Valid:" + resid.valid());
		System.out.println("Score:" + scoreDeployment(plan));
		for (Node n : nodes_) {
			if (resid.getHostedCount()[n.id_] == 0) {
				System.out.println("\t" + n.label_ + " is free");
			}
		}
	}

	public String toString() {
		String str = "Deployment Problem {\n";
		for (Node n : nodes_) {
			str += "\t" + n + "\n";
		}
		for (NetworkLink n : networks_) {
			str += "\t-->" + n + "\n";
			str += "\t\t[\n";
			for (Node ep : n.getNodes()) {
				str += "\t\t--" + ep.label_ + "(" + ep.id_ + ")\n";
			}
			str += "\t\t]\n";
		}
		for (Component c : components_) {
			str += "\t" + c + "\n";
			// System.out.println("Might Get that null" +
			// c.getInteractions().length);
			if (c.getInteractions().length > 0) {
				str += "\t\tinteractions-->[\n";
				for (Interaction inter : c.getInteractions()) {
					str += "\t\t" + inter + "\n";
				}
				str += "\t\t]\n";
			}
		}

		str += "}";
		return str;
	}
	
	public ValueFunction<VectorSolution> getFitnessFunction(){
		return scoringFunction_;
	}

	public double getScore(VectorSolution vs) {
		return scoreDeployment(getDeploymentPlan(vs));
	}
}
