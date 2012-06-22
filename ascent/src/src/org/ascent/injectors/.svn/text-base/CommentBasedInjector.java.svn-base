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
import java.util.Map;
import java.util.regex.Pattern;

import org.ascent.injectors.annotations.ForEachAnnotation;
import org.ascent.injectors.annotations.IfEnabledAnnotation;
import org.ascent.injectors.annotations.InsertAnnotation;
import org.ascent.injectors.annotations.XMLReplaceAnnotation;
import org.ascent.util.ParsingUtil;


public class CommentBasedInjector extends AbstractAnnotationBasedInjector {

	public static final String FEATURE_KEY = "feature";

	public static final String WITH_KEY = "with";

	public static final int FEATURE_GROUP = 1;

	public static final int ANNOTATION_TYPE_GROUP = 3;

	public static final int ARGS_GROUP = 4;
	
	public static CommentBasedInjector createDefaultInjector(String comment) {
		CommentBasedInjector injector = new CommentBasedInjector(comment);
		injector.addAnnotationHandler(new ForEachAnnotation());
		injector.addAnnotationHandler(new InsertAnnotation());
		injector.addAnnotationHandler(new XMLReplaceAnnotation());
		injector.addAnnotationHandler(new IfEnabledAnnotation());
		return injector;
	}
	
	public static CommentBasedInjector createDefaultInjector(String start, String end) {
		CommentBasedInjector injector = new CommentBasedInjector(start, end);
		injector.addAnnotationHandler(new ForEachAnnotation());
		injector.addAnnotationHandler(new InsertAnnotation());
		injector.addAnnotationHandler(new XMLReplaceAnnotation());
		injector.addAnnotationHandler(new IfEnabledAnnotation());
		return injector;
	}

	private String headRegex_ = "[\\s]*feature\\[([\\w\\*]*)\\][\\s]*(\\(([a-zA-Z0-9\\-\\_\\#]*)[\\s]*(\\[[^\\]]*\\])?[\\s]*\\))?[\\s]*\\{[\\s]*(";

	private String featureRegex_ = "[\\s]*feature\\[()]";

	private String headTerminator_;

	private String tailTerminator_ = "}";

	private String tailPatternPostfix_ = "";

	private String tailPatternSelfTerminating_ = "}";

	private String tailTemplateRegex_ = "\\}[\\s]*with[\\s]*\\{([^\\}]*)\\}";

	private Pattern headPattern_;

	private Pattern tailPattern_;

	public static final int WITH_GROUP = 1;

	private boolean stripSpaces_ = false;
	
	private String commentStart_;
	
	private String commentStartPattern_;
	
	private String commentEnd_;
	
	private String commentEndPattern_;
	
	private String traceLinePrefix_ = "";
	
	
	
	public CommentBasedInjector(String multilinestart, String multilineend){
		commentStart_ = multilinestart;
		commentStartPattern_ = createPattern(commentStart_);
		commentEnd_ = multilineend;
		commentEndPattern_ = createPattern(commentEnd_);
		
		headRegex_ = commentStartPattern_ + headRegex_;
		headRegex_ = headRegex_ + commentEndPattern_+")?";
		headPattern_ = Pattern.compile(headRegex_);
		
		featureRegex_ = commentStartPattern_+featureRegex_;
		headTerminator_ = commentEnd_;
		
		tailTerminator_ = commentStart_+tailTerminator_;
		tailPatternPostfix_ = commentEnd_;
		
		tailPatternSelfTerminating_ = tailPatternSelfTerminating_+commentEnd_;
		tailTemplateRegex_ = tailTemplateRegex_ + commentEndPattern_;

		tailPattern_ = Pattern
				.compile(tailTemplateRegex_);
	}
	
	public CommentBasedInjector(String linestart){
		traceLinePrefix_ = linestart;
		commentStart_ = linestart;
		commentStartPattern_ = createPattern(commentStart_);
		commentEnd_ = "";
		commentEndPattern_ = createPattern(commentEnd_);
		
		headRegex_ = commentStartPattern_ + headRegex_;
		headRegex_ = headRegex_ + commentEndPattern_+")?";
		headPattern_ = Pattern.compile(headRegex_);
		
		featureRegex_ = commentStartPattern_+featureRegex_;
		headTerminator_ = commentEnd_;
		
		tailTerminator_ = commentStart_+tailTerminator_;
		tailPatternPostfix_ = commentEnd_;
		
		tailPatternSelfTerminating_ = tailPatternSelfTerminating_+commentEnd_;
		tailTemplateRegex_ = tailTemplateRegex_ + commentEndPattern_;

		tailPattern_ = Pattern
				.compile(tailTemplateRegex_);
	}
	
	public String createPattern(String example){
		String pattern = "";
		for(int i = 0; i < example.length(); i++){
			char c= example.charAt(i);
			if(!Character.isLetterOrDigit(c)){
				pattern += "\\"+c;
			}
			else{
				pattern += c;
			}
		}
		return pattern;
	}
	

	@Override
	public int getAnnotationTypePatternGroup() {
		return ANNOTATION_TYPE_GROUP;
	}

	@Override
	public int getArgsPatternGroup() {
		return ARGS_GROUP;
	}

	@Override
	public int getFeaturePatternGroup() {
		return FEATURE_GROUP;
	}

	@Override
	public Pattern getHeadPattern() {
		return headPattern_;
	}

	@Override
	public String getHeadTerminator() {
		return headTerminator_;
	}

	@Override
	public Pattern getTailPattern() {
		return tailPattern_;
	}

	@Override
	public String getTailPatternPostfix() {
		return tailPatternPostfix_;
	}

	@Override
	public String getTailPatternPrefix() {
		return tailTerminator_;
	}

	@Override
	public String getTailPatternSelfTerminatingSuffix() {
		return tailPatternSelfTerminating_;
	}

	@Override
	public int getWithPatternGroup() {
		return WITH_GROUP;
	}

	
	
	@Override
	public String getTraceLinePrefix() {
		return traceLinePrefix_;
	}

	public String stripComments(String cstr) {
		return cstr.replaceAll(commentStartPattern_, "").replaceAll(commentEndPattern_, "");
	}
	
	public String makeMultiLineCommentStart(String comment){
		if(traceLinePrefix_.length() > 0){
			comment = comment.replaceAll("\\n", "\\n"+traceLinePrefix_);
		}
		return commentStart_ + comment;
	}
	
	public String makeMultiLineCommentEnd(String comment){
		if(traceLinePrefix_.length() > 0){
			comment = comment.replaceAll("\\n", "\\n"+traceLinePrefix_);
		}
		return comment + commentEnd_ + "\n";
	}

	@Override
	public String getTracePrefix() {
		return makeMultiLineCommentStart("\nThis configuration binding was produced by the annotation:\n##########################################################\n");
	}

	@Override
	public String getTraceSuffix() {
		return makeMultiLineCommentEnd("##########################################################\n");
	}

	public Map getArgs(String argstr) {
		if (argstr == null)
			return new HashMap();

		return ParsingUtil.parseSimpleMap(argstr, "]");
	}


	public boolean getStripSpaces() {
		return stripSpaces_;
	}

	public void setStripSpaces(boolean stripSpaces) {
		stripSpaces_ = stripSpaces;
	}

	
	
}
