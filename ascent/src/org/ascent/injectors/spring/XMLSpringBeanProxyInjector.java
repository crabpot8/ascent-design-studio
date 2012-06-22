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

package org.ascent.injectors.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class XMLSpringBeanProxyInjector {

	public static final String BEAN_XML_END_TAG = "</beans>";

	public static final String BEAN_XML_END_TAG_PATTERN_DEF = "\\<\\/beans\\>";

	public static final String BEAN_REF_PATTERN_PREFIX_DEF = "ref[\\s]*=[\\s]*\\\"[\\s]*";

	public static final String BEAN_REF_PATTERN_POSTFIX_DEF = "[\\s]*\\\"";

	public static final String BEAN_ID_PATTERN_DEF = "\\<bean[^\\>]*id[\\s]*=[\\s]*\\\"[\\s]*([^\\\"]+)+[\\s]*\\\"[^\\>]*\\>";

	public static final String BEAN_DEC_PATTERN_PREFIX_DEF = "\\<bean[^\\>]*id[\\s]*=[\\s]*\\\"[\\s]*(";

	public static final String BEAN_DEC_PATTERN_POSTFIX_DEF = ")+[\\s]*\\\"[^\\>]*\\>";

	public static final int BEAN_ID_GROUP = 1;

	public static final Pattern ID_PATTERN = Pattern
			.compile(BEAN_ID_PATTERN_DEF);

	public static final String BEAN_ID_PLACEHOLDER = "\\$\\{[\\s]*beanId[\\s]*\\}";

	public static final String PROXY_ID_PLACEHOLDER = "\\$\\{[\\s]*proxyId[\\s]*\\}";

	public String injectProxiesForEveryBean(String beanxml, String proxycode,
			String proxyprefix, String proxysuffix) {
		List<String> beanids = extractBeanIds(beanxml);
		for (String beanid : beanids) {
			String proxyedbeanid = proxyprefix + beanid + proxysuffix;
			String proxy = proxycode.replaceAll(BEAN_ID_PLACEHOLDER,
					proxyedbeanid);
			proxy = proxy.replaceAll(PROXY_ID_PLACEHOLDER, beanid);
			beanxml = injectProxyViaRenaming(beanxml, beanid, proxyedbeanid,
					proxy);
		}

		return beanxml;
	}

	public String injectProxyViaRenamingAndTemplate(String beanid,
			String nbeanid, String beanxml, String proxycode) {
		String proxy = proxycode.replaceAll(BEAN_ID_PLACEHOLDER, nbeanid);
		proxy = proxy.replaceAll(PROXY_ID_PLACEHOLDER, beanid);
		beanxml = injectProxyViaRenaming(beanxml, beanid, nbeanid, proxy);

		return beanxml;
	}

	public List<String> extractBeanIds(String beanxml) {
		ArrayList<String> result = new ArrayList<String>();
		Matcher idfinder = ID_PATTERN.matcher(beanxml);
		while (idfinder.find()) {
			if (!inComment(beanxml, idfinder.start())) {
				result.add(idfinder.group(BEAN_ID_GROUP));
			}
		}
		return result;
	}

	public boolean inComment(String xml, int index) {
		int start = xml.lastIndexOf("<!--",index);
		int end = xml.lastIndexOf("-->",index);
		if (start < index && start > -1)
			return end < start;
		else
			return false;

	}

	public String injectProxyViaReferenceSwapping(String beanxml,
			String beanid, String proxyid, String proxycode) {
		StringBuffer buff = new StringBuffer(beanxml.length());

		beanxml = appendBeanCode(beanxml, proxycode);

		Matcher refmatcher = Pattern.compile(
				BEAN_REF_PATTERN_PREFIX_DEF + beanid
						+ BEAN_REF_PATTERN_POSTFIX_DEF).matcher(beanxml);

		int last = 0;
		while (refmatcher.find()) {
			int start = refmatcher.start();
			int end = refmatcher.end();

			if (last != start) {
				buff.append(beanxml.substring(last, start));
			}
			buff.append("ref=\"" + proxyid + "\"");

			last = end;
		}
		if (last != beanxml.length()) {
			buff.append(beanxml.substring(last, beanxml.length()));
		}

		return buff.toString();
	}

	public String injectProxyViaRenaming(String beanxml, String beanid,
			String newbeanid, String proxycode) {
		StringBuffer buff = new StringBuffer(beanxml.length());

		Matcher refmatcher = Pattern.compile(
				BEAN_DEC_PATTERN_PREFIX_DEF + beanid
						+ BEAN_DEC_PATTERN_POSTFIX_DEF).matcher(beanxml);

		int last = 0;
		while (refmatcher.find()) {
			int start = refmatcher.start(BEAN_ID_GROUP);
			int end = refmatcher.end(BEAN_ID_GROUP);

			if(inComment(beanxml, start))
				continue;
			
			if (last != start) {
				buff.append(beanxml.substring(last, start));
			}
			buff.append(newbeanid);

			last = end;
		}
		if (last != beanxml.length()) {
			buff.append(beanxml.substring(last, beanxml.length()));
		}

		beanxml = appendBeanCode(buff.toString(), proxycode);

		return beanxml;
	}

	public String appendBeanCode(String beanxml, String toappend) {
		return beanxml.replaceAll(BEAN_XML_END_TAG_PATTERN_DEF, toappend
				+ BEAN_XML_END_TAG);
	}
}
