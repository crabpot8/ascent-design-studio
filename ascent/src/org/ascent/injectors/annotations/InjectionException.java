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

package org.ascent.injectors.annotations;

import java.util.Map;


public class InjectionException extends RuntimeException {

	private String feature_;
	private Map bindings_;
	private String template_;
	private String head_;
	private String tail_;
	private int headStart_;
	private int tailStart_;
	
	public InjectionException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InjectionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public InjectionException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public InjectionException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the head
	 */
	public String getHead() {
		return head_;
	}

	/**
	 * @param head the head to set
	 */
	public void setHead(String head) {
		head_ = head;
	}

	/**
	 * @return the tail
	 */
	public String getTail() {
		return tail_;
	}

	/**
	 * @param tail the tail to set
	 */
	public void setTail(String tail) {
		tail_ = tail;
	}

	

	/**
	 * @return the headStart
	 */
	public int getHeadStart() {
		return headStart_;
	}

	/**
	 * @param headStart the headStart to set
	 */
	public void setHeadStart(int headStart) {
		headStart_ = headStart;
	}

	/**
	 * @return the tailStart
	 */
	public int getTailStart() {
		return tailStart_;
	}

	/**
	 * @param tailStart the tailStart to set
	 */
	public void setTailStart(int tailStart) {
		tailStart_ = tailStart;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template_;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(String template) {
		template_ = template;
	}

	
	/**
	 * @return the bindings
	 */
	public Map getBindings() {
		return bindings_;
	}

	/**
	 * @param bindings the bindings to set
	 */
	public void setBindings(Map bindings) {
		bindings_ = bindings;
	}

	/**
	 * @return the feature
	 */
	public String getFeature() {
		return feature_;
	}

	/**
	 * @param feature the feature to set
	 */
	public void setFeature(String feature) {
		feature_ = feature;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		String msg = "Injection Exception Detail:{\n";
		msg += "  Feature:"+getFeature()+"\n";
		msg += "  The exception occurred while processing the annotation:\n";
		msg += "      head=\""+getHead()+"\" starting at index="+getHeadStart()+"\n";
		if(getTail() != null && getTailStart() > -1)
			msg += "      tail=\""+getTail()+"\" starting at index="+getHeadStart()+"\n";
		msg += "      template=\""+getTemplate()+"\"\n";
		msg += "      available bindings:[";
		if(getBindings() != null)
			for(Object key : getBindings().keySet()){
				msg += ""+key+"=\""+getBindings().get(key)+"\" ";
			}
		msg += "]\n";
		msg += "  }\n";
		
		if(getCause() != null)
			msg += "Caused by: "+getCause().getMessage();
		
		
		
		msg += super.getMessage();
		return msg;
	}
	
	

}
