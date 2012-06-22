 /**************************************************************************
 * Copyright 2009 Brian Dougherty                                             *
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
import java.util.List;
import java.util.Random;
public class RandDeploymentConfigGen{
	private int numComponents_;//check
	private int numNodes_;//check
	private int numResources_ ;//check
	private int numExConstraints_; //check
	private int numReqConstraints_;//check
	private int numTasksPerComponent_;//seeMakeComponents
	private int numNetworks_;
	private int numComponentInteractions_;
	
	//Range of resource consumption for components
	private double minResConsumption_;//check
	private double maxResConsumption_; //checkmaxvalue not inclusive
	
	//Range of component proc utilization
	private double minCompUtilization_; //total utilization of each comp
	private double maxCompUtilization_;
	
	//Range of resources available from the nodes
	private double minAvailResValue_;//check
	private double maxAvailResValue_;//check
	
	//Range of the resource 0 for each interaction (bandwidth)
	//Each interaction consists of one of more components
	private double minBwidthPerInteraction_;//inteaction size[0]
	private double maxBwidthPerInteraction_;
	
	//Range for the rate at which the components interact
	private double minInteractionPeriod_;//should go to interaction rate
	private double maxInteractionPeriod_;
	
	//Range of the network bandwidth between nodes
	private double minNetworkBandwidth_;//neworkLink siz[0]
	private double maxNetworkBandwidth_;
	
	//Each component has tasks.  Period at which tasks arrive at the CPU
	private double minTaskRate_;//check = comp task period
	private double maxTaskRate_;// check
	
	//Range of CPU utiliation for each task
	private double minCpuUtilPerTask_;//check = comp task util
	private double maxCpuUtilPerTask_;
	
	
	private NetworkLink[] networks_;
	private Component[] components_;
	private Node[] nodes_;
	private Interaction[] interactions_;
	private List<DeploymentConstraint> constraints_ = new ArrayList<DeploymentConstraint>();
	private DeploymentConfig dc_;
	public RandDeploymentConfigGen(){}
	
	
	public RandDeploymentConfigGen(int numComponents, int numNodes, int numResources, int numExConstraints, int numReqConstraints,
			int numTasksPerComponent, int numNetworks, int numComponentInteractions,double minResConsumption,
			double maxResConsumption, double minCompUtilization, double maxCompUtilization,
			double minAvailResValue, double maxAvailResValue, double minBwidthPerInteraction, 
			double maxBwidthPerInteraction, double minInteractionPeriod, double maxInteractionPeriod,
			double minNetworkBandwidth, double maxNetworkBandwidth, double minTaskRate, double maxTaskRate,
			double minCpuUtilPerTask, double maxCpuUtilPerTask){
		
		numComponents_=numComponents;
		numNodes_=numNodes;
		numResources_=numResources;
		numExConstraints_=numExConstraints;
		numReqConstraints_=numReqConstraints;
		numTasksPerComponent_=numTasksPerComponent;
		numNetworks_=numNetworks;
		numComponentInteractions_=numComponentInteractions;
		minResConsumption_=minResConsumption;
		maxResConsumption_=maxResConsumption;
		minCompUtilization_=minCompUtilization;
		maxCompUtilization_=maxCompUtilization;
		minAvailResValue_=minAvailResValue;
		maxAvailResValue_=maxAvailResValue;
		minBwidthPerInteraction_=minBwidthPerInteraction;
		maxBwidthPerInteraction_=maxBwidthPerInteraction;
		minInteractionPeriod_=minInteractionPeriod;
		maxInteractionPeriod_=maxInteractionPeriod;
		minNetworkBandwidth_=minNetworkBandwidth;
		maxNetworkBandwidth_=maxNetworkBandwidth;
		minTaskRate_ = minTaskRate;
		maxTaskRate_=maxTaskRate;
		minCpuUtilPerTask_= minCpuUtilPerTask;
		maxCpuUtilPerTask_=maxCpuUtilPerTask;
		networks_ = new NetworkLink[numNetworks_];
		interactions_ = new Interaction[numComponentInteractions_];
		makeComponents();
		makeNodes();
		addExConstraints();
		addReqConstraints();
		makeInteractionsAndNetowrkLinks();
		System.out.println("Interactions is " + interactions_.length);
		dc_ =makeDeploymentConfig();
		dc_.setConstraints(constraints_);
	}
	
	public DeploymentConfig makeDeploymentConfig(){
		
		return new DeploymentConfig(nodes_,networks_, components_, interactions_);
	}
	
	public void makeComponents(){
		Random r = new Random();
		double availFraction = 1 - (minResConsumption_/maxResConsumption_);
		double availDifference = availFraction * maxResConsumption_;
		int intDifference = (int)Math.round(availDifference);
		components_ = new Component [numComponents_];
		

		for(int i =0; i < numComponents_; i++){
			int [] resources = new int[numResources_];
			for(int j = 0; j <numResources_; j++){
				
				 int intRandomRes =  r.nextInt(intDifference) + (int)Math.round(minResConsumption_);//() *
				
				 if(intRandomRes >maxResConsumption_ ){
					 intRandomRes = (int) Math.round(maxResConsumption_);
					 if(intRandomRes >maxResConsumption_){
						 intRandomRes--;
					 }
				 }
				resources[j] = intRandomRes;
				
			}
			String componentId = "Component " + i;
			Component newComponent = new Component(i, componentId, resources);
			double availCU= 1 - (minCompUtilization_/maxCompUtilization_);
			double avD = availCU * maxCompUtilization_;
			int intavD = (int) Math.round(avD); // available utilization ;
			int RandomRes = r.nextInt(intavD) +(int) Math.round(minCompUtilization_);
			double availPer;
			double avDPer;
			int intavDPer;
			double RandomPer;
			for(int j = 0; j < numTasksPerComponent_-1; j++){
				
				double util = Math.random();
				
				if(util > .01){
					util = util - .01;
				}
				else{
					util = util + .01;
				}
				util = util * RandomRes;
				
				availPer= 1 - (minTaskRate_/maxTaskRate_);
				avDPer = availPer * maxTaskRate_;
				intavDPer = (int) Math.round(avDPer); // available utilization ;
				RandomPer = r.nextInt(intavD) +(int) Math.round(maxTaskRate_);
				newComponent.addTask(RandomPer, RandomRes);
				RandomRes -= util;
			}
			
			availPer= 1 - (minTaskRate_/maxTaskRate_);
			avDPer = availPer * maxTaskRate_;
			intavDPer = (int) Math.round(avDPer); // available utilization ;
			RandomPer = r.nextInt(intavD) +(int) Math.round(maxTaskRate_);
			newComponent.addTask(RandomPer, RandomRes);
			
			System.out.println("Component is " + newComponent);
			components_[i] = newComponent;
		}
	}
	
	public void makeNodes(){
		Random r = new Random();
		double availFraction = 1 - (minAvailResValue_/maxAvailResValue_);
		double availDifference = availFraction * maxAvailResValue_;
		int intDifference = (int)Math.round(availDifference);
		nodes_ = new Node [numNodes_];

		for(int i =0; i < numNodes_; i++){
			int [] resources = new int[numResources_];
			for(int j = 0; j <numResources_; j++){
				
				 int intRandomRes =  r.nextInt(intDifference) + (int)Math.round(minResConsumption_);;//() *
				 
				 if(intRandomRes >maxAvailResValue_ ){
					 intRandomRes = (int) Math.round(maxAvailResValue_);
					 if(intRandomRes >maxAvailResValue_ ){
						 intRandomRes--;
					 }
				 }
				resources[j] = intRandomRes;
				
			}
			String nodeId = "Node " + i;
			Node newNode = new Node(i, nodeId, resources);
			System.out.println("Node is " + newNode);
			nodes_[i] = newNode;
		}
		
	}
	
	public void addExConstraints(){
		int constraintsAdded = 0;
		int safetyCounter = 0;
		Random r = new Random();
		while(constraintsAdded < numExConstraints_){
			double chance = Math.random();
			for(Component c : components_){
				if( chance <=.2){ // we want to spread the constraints randomly
					int compInd = r.nextInt(numComponents_); //randomly pick the component to add.
					
					if(compInd != c.getId()){
						Colocated checkCo = new Colocated(c,components_[compInd]);
						if(!(constraints_.contains(checkCo))){
							requireNotColocated(c,components_[compInd]);
							constraintsAdded++; // num Colocated constraints added
						}
					}
					
					
				}
				if(constraintsAdded == numExConstraints_){
					break;
				}
			}
			safetyCounter++;
			if(safetyCounter == Integer.MAX_VALUE){
				System.out.println("Could not add all Constraints. See addExConstraints method.");
				return;
			}
		}
	}
	
	public void addReqConstraints(){
		int constraintsAdded = 0;
		int safetyCounter = 0;
		Random r = new Random();
		while(constraintsAdded < numReqConstraints_){
			double chance = Math.random();
			for(Component c : components_){
				if( chance <=.2){ // we want to spread the constraints randomly
					int compInd = r.nextInt(numComponents_); //randomly pick the component to add.
					
					if(compInd != c.getId()){
						NotColocated checkNotCo = new NotColocated(c,components_[compInd]);
						if(!(constraints_.contains(checkNotCo))){
							requireColocated(c,components_[compInd]);
							constraintsAdded++; // num Colocated constraints added
						}
					}
					
					
				}
				if(constraintsAdded == numExConstraints_){
					break;
				}
			}
			safetyCounter++;
			if(safetyCounter == Integer.MAX_VALUE){
				System.out.println("Could not add all Constraints. See addColoConstraints method.");
				return;
			}
		}
	}
	
	public void makeInteractionsAndNetowrkLinks(){
		int numInteractions = 0;// numComponentInteractions_;
		int numNetworkLinks=0;
		Random r = new Random();
		while(numNetworkLinks <numNetworks_){
			
			 double entry = Math.random();
			 if(entry > .2){
				int node1 = r.nextInt(nodes_.length-1);
				int node2 =r.nextInt(nodes_.length-1);
				while(node2 == node1){
					node2 =r.nextInt(nodes_.length-1);
				}
				
				Node [] nlNodes = new Node[2];
				nlNodes[0] = nodes_[node1];
				nlNodes[1] = nodes_[node2];
				makeNetwork(nlNodes,numNetworkLinks);
				numNetworkLinks++;
			 }
				
		}
		
		while(numInteractions<numComponentInteractions_){
			double entry = Math.random();
			 if(entry > .2){
				int comp1 = r.nextInt(components_.length-1);
				int comp2 =r.nextInt(components_.length-1);
				while(comp2 == comp1){
					comp2 =r.nextInt(components_.length-1);
				}
				
				Component [] interComponents = new Component[2];
				interComponents[0] = components_[comp1];
				interComponents[1] = components_[comp2];
				makeInteraction(numInteractions, interComponents);
				numInteractions++;
			 }
		}
		
		for(Interaction inter : interactions_){
			for(Component comp : inter.getParticipants()){
				comp.addInteraction(inter);
			}
		}
		for(NetworkLink nl : networks_){
			for(Node node : nl.getNodes()){
				node.addNetworkLink(nl);
			}
		}
		
		
	}
	
	public void makeInteraction(int id, Component[] comps){
		Random r = new Random();
		double availFraction = 1 - (minInteractionPeriod_/maxInteractionPeriod_);
		double availDifference = availFraction * maxInteractionPeriod_;
		int intDifference = (int) (r.nextInt(((int)Math.round(availDifference))) + minInteractionPeriod_);
		r = new Random();
		double availBwidthPerFraction = 1 - (minBwidthPerInteraction_/maxBwidthPerInteraction_);
		double availBwidthPerDifference = availBwidthPerFraction * maxBwidthPerInteraction_;
		int intBwidthPerDifference = (int)(r.nextInt(((int)Math.round(availBwidthPerDifference)))+minBwidthPerInteraction_);
		int [] inerRes = new int[1];
		inerRes[0] = intBwidthPerDifference;
		String interString = "Inteaction " + id;
		System.out.println(" interaction has " + inerRes +" and " + availDifference);
		Interaction interaction = new Interaction(id,interString, inerRes,intDifference);
		interaction.setParticipants(comps);
		interactions_[id] = interaction;		
		
	}

	public void makeNetwork(Node [] nodes, int id){
		Random r = new Random();
		double availFraction = 1 - (minNetworkBandwidth_/maxNetworkBandwidth_);
		double availDifference = availFraction * maxNetworkBandwidth_;
		int intDifference = (int) (r.nextInt(((int)Math.round(availDifference))) + minNetworkBandwidth_);
		
		//int intBwidthPerDifference = (int)Math.round(availDifference);
		int [] inerRes = new int[1];
		inerRes[0] = intDifference;//intBwidthPerDifference;
		String interString = "Network " + id;
		NetworkLink nl= new NetworkLink(id,interString,nodes, inerRes);
	
		networks_[id] = nl;
		
	}
	
	public DeploymentConfig getDc(){
		return dc_;
	}
	public static void main(String args[]){
		RandDeploymentConfigGen rdcg = new RandDeploymentConfigGen(10,5,3,2,3,4,2,3,5,20,14,30,10,40,3,10,4,14,5,12,1,4,2,8);
		DeploymentConfig dc = rdcg.getDc();
		System.out.println("DC = " + dc);
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
}

