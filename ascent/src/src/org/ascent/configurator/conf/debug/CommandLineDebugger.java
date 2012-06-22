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

package org.ascent.configurator.conf.debug;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.StringTokenizer;

import org.ascent.configurator.RefreshEngine;
import org.ascent.configurator.conf.ConfigDirectiveHandler;
import org.ascent.configurator.conf.ProblemParser;
import org.ascent.configurator.conf.RefreshProblem;
import org.ascent.expr.Cardinality;


public class CommandLineDebugger implements ConfigDirectiveHandler {

	private String data_ = "";

	private RefreshProblem problem_;

	private BufferedReader commands_ = new BufferedReader(
			new InputStreamReader(System.in));

	private PrintStream output_ = System.out;

	public String getCommand() throws IOException {
		return commands_.readLine();
	}

	public void load(StringTokenizer args) throws FileNotFoundException,
			IOException {
		while (args.hasMoreTokens()) {
			String file = args.nextToken();
//			data_ += IOUtils.toString(new FileInputStream(file));
			output_.println("loaded [" + file + "]");
		}
	}

	public void init() {
		output_.println("Parsing problem data...");
		ProblemParser parser = new ProblemParser();
		parser.setDefaultDirectiveHandler(this);
		problem_ = parser.parseSourceProblem(data_);
		output_.println("Problem parsed.");
	}

	public void select(StringTokenizer tk) {
		while (tk.hasMoreTokens())
			problem_.getSelectedItems().add(tk.nextToken());
	}

	public void disable(StringTokenizer tk) {
		while (tk.hasMoreTokens())
			problem_.getDisabledItems().add(tk.nextToken());
	}

	public void enable(StringTokenizer tk) {
		while (tk.hasMoreTokens())
			problem_.getDisabledItems().remove(tk.nextToken());
	}

	public void deSelect(StringTokenizer tk) {
		while (tk.hasMoreTokens())
			problem_.getSelectedItems().remove(tk.nextToken());
	}

	public void unRequire(StringTokenizer tk) {
		while (tk.hasMoreTokens())
			problem_.getSourceMappedInstancesCountMap().remove(tk.nextToken());
	}

	public void require(StringTokenizer tk) {
		while (tk.hasMoreTokens()) {
			Cardinality card = new Cardinality(1, 1);
			problem_.getSourceMappedInstancesCountMap().put(tk.nextToken(),
					card);
		}
	}
	
	public void listSources(){
		output_.println("Sources:");
		for(Object o : problem_.getSourceItems())
			output_.println("  "+o);
	}
	
	public void listTargets(){
		output_.println("Targets:");
		for(Object o : problem_.getTargetItems())
			output_.println("  "+o);
	}

	public void createTarget(StringTokenizer tk) {
		problem_.getTargetItems().add(tk.nextToken());
	}

	public void debug() {
		if (problem_ == null)
			init();
		RefreshEngine engine = new RefreshEngine(problem_);
		RefreshDebugger debugger = new RefreshDebugger(engine.getCore(),problem_);
		List<Conflict> failedcons = debugger.debug(engine.debug());
		for (Conflict con : failedcons)
			System.out.println(con);
	}
	
	public void reset(){
		problem_ = null;
		data_ = null;
	}
	
	public void resetProblem(){
		problem_ = null;
	}

	public StringTokenizer tokenize(String cmd) {
		return new StringTokenizer(cmd, " ", false);
	}

	
	public void handle(RefreshProblem problem, String context,
			String directive, String argstr) {
		output_.println("Undefined directive: [" + directive + " args{"
				+ argstr + "}]");
	}

	public static void main(String[] argstr) {
		CommandLineDebugger debugger = new CommandLineDebugger();

		boolean running = true;
		while (running) {
			try {
				String cmd = debugger.getCommand();
				if (cmd.trim().length() > 0) {
					StringTokenizer args = debugger.tokenize(cmd);
					cmd = args.nextToken();
					if ("load".equals(cmd))
						debugger.load(args);
					else if ("load".equals(cmd))
						debugger.load(args);
					else if ("require".equals(cmd))
						debugger.require(args);
					else if ("select".equals(cmd))
						debugger.select(args);
					else if ("unrequire".equals(cmd))
						debugger.unRequire(args);
					else if ("deselect".equals(cmd))
						debugger.deSelect(args);
					else if ("disable".equals(cmd))
						debugger.disable(args);
					else if ("enable".equals(cmd))
						debugger.enable(args);
					else if ("debug".equals(cmd))
						debugger.debug();
					else if ("init".equals(cmd))
						debugger.init();
					else if ("target".equals(cmd))
						debugger.createTarget(args);
					else if ("targets".equals(cmd))
						debugger.listTargets();
					else if ("sources".equals(cmd))
						debugger.listSources();
					else if ("reset".equals(cmd))
						debugger.reset();
					else if ("reset-problem".equals(cmd))
						debugger.resetProblem();
					else
						System.out.println("Unknown command:"+cmd);
					System.out.println("Ok");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
