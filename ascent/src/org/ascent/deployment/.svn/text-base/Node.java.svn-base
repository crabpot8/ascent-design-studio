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


public class Node extends ModelElement {
	private NetworkLink[] networkLinks_ = new NetworkLink[0];

	public Node(int id, String label, int[] resources) {
		super(id, label, resources);
	}

	public NetworkLink[] getNetworkLinks() {
		return networkLinks_;
	}
	
	public void addNetworkLink(NetworkLink nl ){
		NetworkLink[] oldNetworkLinks = networkLinks_;
		networkLinks_ = new NetworkLink[oldNetworkLinks.length+1];
		for(int i= 0 ; i < oldNetworkLinks.length; i ++){
			networkLinks_[i] = oldNetworkLinks[i]; 
		}
		networkLinks_[oldNetworkLinks.length] = nl;
		
	}
	
	public void setNetworkLinks(NetworkLink[] networkLinks) {
		networkLinks_ = networkLinks;
	}

}

