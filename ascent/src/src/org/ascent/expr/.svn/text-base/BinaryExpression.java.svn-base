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

package org.ascent.expr;

public class BinaryExpression extends Expression{
	private Expression rightHandSide_;
	private Expression leftHandSide_;
	private String operation_;
	
	
	public BinaryExpression(Expression lhs, Expression rightHandSide, String operation) {
		super();
		rightHandSide_ = rightHandSide;
		operation_ = operation;
		leftHandSide_ = lhs;
	}
	public Expression getRightHandSide(){
		return rightHandSide_;
	}
	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation_;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		operation_ = operation;
	}
	/**
	 * @return the leftHandSide
	 */
	public Expression getLeftHandSide() {
		return leftHandSide_;
	}
	/**
	 * @param leftHandSide the leftHandSide to set
	 */
	public void setLeftHandSide(Expression leftHandSide) {
		leftHandSide_ = leftHandSide;
	}
	/**
	 * @param rightHandSide the rightHandSide to set
	 */
	public void setRightHandSide(BinaryExpression rightHandSide) {
		rightHandSide_ = rightHandSide;
	}
	

	
}
