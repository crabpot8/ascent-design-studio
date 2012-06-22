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

package org.ascent.injectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ascent.injectors.annotations.AnnotationHandler;
import org.ascent.injectors.annotations.InjectionException;
import org.ascent.injectors.annotations.InvalidInjectionException;
import org.ascent.injectors.annotations.ReferenceCapableAnnotationHandler;
import org.ascent.injectors.annotations.SupportsDisable;
import org.ascent.util.ParsingUtil;


public abstract class AbstractAnnotationBasedInjector extends
		AbstractTracingInjector implements AnnotationBasedInjector, AnnotationConstants {

	public class UnknownAnnotationTypeException extends RuntimeException {

		public UnknownAnnotationTypeException(String arg0) {
			super(arg0);
		}

	}

	private Map<String, AnnotationHandler> handlerMap_ = new HashMap<String, AnnotationHandler>(
			19);

	private FeatureMatcher featureMatcher_ = new BasicMatcher();
	
	private boolean autoTrimCommas_ = true;

	public abstract Pattern getHeadPattern();

	public abstract Pattern getTailPattern();

	public abstract int getFeaturePatternGroup();

	public abstract int getAnnotationTypePatternGroup();

	public abstract int getArgsPatternGroup();

	public abstract int getWithPatternGroup();

	public abstract String getHeadTerminator();

	public abstract String getTailPatternPrefix();

	public abstract String getTailPatternPostfix();

	public abstract String getTailPatternSelfTerminatingSuffix();

	public void addAnnotationHandler(AnnotationHandler h) {
		handlerMap_.put(h.getType(), h);
	}

	public void addAnnotationHandler(String type, AnnotationHandler h) {
		handlerMap_.put(type, h);
	}

	public void removeAnnotationHandler(AnnotationHandler h) {
		handlerMap_.remove(h.getType());
	}

	public void removeAnnotationHandler(String type) {
		handlerMap_.remove(type);
	}

	public AnnotationHandler getHandler(String annotationtype) {
		return handlerMap_.get(annotationtype);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.refresh.injectors.Injector#inject(java.util.Map, java.util.Map,
	 *      java.lang.String)
	 */
	protected String injectImpl(Map<String, List> features,
			Map<String, Map> values, String conf) {

		values = processInjections(features, values);

		StringBuffer injected = new StringBuffer(conf.length());
		int last = 0;

		Matcher m = getHeadPattern().matcher(conf);

		while (m.find()) {
			int start = m.start();
			int end = m.end();

			String featurename = m.group(getFeaturePatternGroup());
			String annotationtype = m.group(getAnnotationTypePatternGroup());
			String args = m.group(getArgsPatternGroup());
			String head = conf.substring(start, end);

			if (last < start) {
				injected.append(conf.substring(last, start));
			}

			String with = null;
			String tail = null;
			String template = null;
			int estart = -1;
			if (head.endsWith(getHeadTerminator())) {
				estart = conf.indexOf(getTailPatternPrefix(),end+1);
				int eend = conf.indexOf(getTailPatternPostfix(), estart
						+ getTailPatternPrefix().length());
				last = eend + getTailPatternPostfix().length();
				template = conf.substring(end, estart);
				tail = conf.substring(estart, last);
				Matcher tm = getTailPattern().matcher(tail);
				if (tm.find()) {
					with = tm.group(getWithPatternGroup());
				}
			} else {
				estart = conf.indexOf(getTailPatternSelfTerminatingSuffix(),
						end);
				last = estart + getTailPatternSelfTerminatingSuffix().length();
				if(last <= start)
					throw new InvalidInjectionException("There is an unterminated annotation. Annotation : "+conf.substring(start));
				head = conf.substring(start, last);
				int tend = conf.lastIndexOf("}", last - 1);
				template = conf.substring(end, tend);
			}

			List matches = featureMatcher_.match(featurename, features);
			boolean enabled = matches.size() > 0;

			if (enabled) {
				String result = "";
				for (Object fkey : matches) {
					featurename = "" + fkey;
					Map bindings = values.get(featurename);
					bindings.put(TARGETS_KEY, features.get(fkey));
					
					try {
						if (getEnableTracing()) {
							injected.append(getBindingsTrace(featurename, head,
									tail, template, bindings,enabled));
						}
						String processed = process(annotationtype, template,
								featurename, args, bindings, values, with, enabled);
						result += processed;

					} catch (RuntimeException e) {
						InjectionException ex = new InjectionException(e);
						ex.setHead(head);
						ex.setTail(tail);
						ex.setTailStart(estart);
						ex.setHeadStart(start);
						ex.setTemplate(template);
						ex.setFeature(featurename);
						ex.setBindings(bindings);
						throw ex;
					}
				}
				if(autoTrimCommas_ && result.trim().endsWith(",")){
					result = result.trim();
					result = result.substring(0,result.length()-1);
				}
				injected.append(result);
			} else {
				Map bindings = values.get(featurename);
				try {

					if (getEnableTracing()) {
						injected.append(getBindingsTrace(featurename, head,
								tail, template, bindings,enabled));
					}
					String processed = process(annotationtype, template,
							featurename, args, bindings, values, with, enabled);
					injected.append(processed);

				} catch (RuntimeException e) {
					InjectionException ex = new InjectionException(e);
					ex.setHead(head);
					ex.setTail(tail);
					ex.setTailStart(estart);
					ex.setHeadStart(start);
					ex.setTemplate(template);
					ex.setFeature(featurename);
					ex.setBindings(bindings);
					throw ex;
				}
			}
		}

		if (last != conf.length()) {
			injected.append(conf.substring(last, conf.length()));
		}

		String result = injected.toString();

		return result;
	}

	public String process(String annotationtype, String template,
			String feature, String argstr, Map bindings, Map<String,Map> globalbindings, String withstmt,
			boolean enabled) {
		Map args = getArgs(argstr);
		if(bindings != null)
			bindings.put(FEATURE_KEY, feature);
		args.put(FEATURE_KEY, feature);
		args.put(WITH_KEY, withstmt);
		AnnotationHandler handler = getHandler(annotationtype);
		String value = "";
		if (handler == null) {
			throw new UnknownAnnotationTypeException(
					"Could not find an org.refresh.injectors.XMLAnnotationHandler to handle the annotation type \""
							+ annotationtype
							+ "\". You probably made a typo in your annotation name or you haven't installed the correct annotation into the org.refresh.injectors.XMLInjector or you installed your XMLAnnotationHandler under the wrong name.");
		}
		if (enabled) {
			if(handler instanceof ReferenceCapableAnnotationHandler)
				value = ((ReferenceCapableAnnotationHandler)handler).handle(template, args, bindings, globalbindings);
			else
				value = handler.handle(template, args, bindings);
		}
		else if(handler instanceof SupportsDisable){
			value = ((SupportsDisable)handler).handleDisabled(template, args, bindings);
		}

		return value;
	}

	public Map getArgs(String argstr) {
		if (argstr == null)
			return new HashMap();

		return ParsingUtil.parseSimpleMap(argstr, "]");
	}

	public Map<String, Map> processInjections(Map<String, List> features,
			Map<String, Map> values) {
		Map<String, Map> ivals = new HashMap<String, Map>();
		for (Object key : features.keySet()) {

			if (features.get(key) != null && features.get(key).size() > 0) {
				Map<String, Object> bindings = values.get(key);
				if (bindings != null) {
					for (String var : bindings.keySet()) {
						String[] resolved = resolve("" + key, var);
						String tfeat = resolved[0];
						String tvar = resolved[1];
						Map fvalues = ivals.get(tfeat);
						if (fvalues == null) {
							fvalues = new HashMap<String, Map>();
							ivals.put(tfeat, fvalues);
						}
						Object eval = fvalues.get(tvar);
						if (eval != null && !(eval instanceof List)) {
							throw new InvalidInjectionException(
									"The variable \""
											+ var
											+ "\" in context \""
											+ key
											+ "\" is not a list and and already has a value set.");
						}

						Object dval = bindings.get(var);
						if (eval != null) {
							if (!(dval instanceof List))
								throw new InvalidInjectionException(
										"The variable \""
												+ var
												+ "\" in context \""
												+ key
												+ "\" is a list but the value being inserted into it by "
												+ key
												+ " is not specified as a list (it is probably missing \"[ ]\" around the value... i.e. if you have "
												+ var + "=" + dval
												+ "; you need " + var + "=["
												+ dval + "];).");
							List lval = (List) eval;
							List toadd = (List) dval;
							lval.addAll(toadd);
							dval = lval;
						}
						fvalues.put(tvar, dval);
					}
				}

				Map fvalues = ivals.get(key);
				if (fvalues == null) {
					fvalues = new HashMap<String, Map>();
					ivals.put("" + key, fvalues);
				}
				fvalues.put("targets", features.get(key));
			}
		}
		return ivals;
	}

	public FeatureMatcher getFeatureMatcher() {
		return featureMatcher_;
	}

	public void setFeatureMatcher(FeatureMatcher featureMatcher) {
		featureMatcher_ = featureMatcher;
	}

	public boolean getAutoTrimCommas() {
		return autoTrimCommas_;
	}

	public void setAutoTrimCommas(boolean autoTrimCommas) {
		autoTrimCommas_ = autoTrimCommas;
	}

	
}
