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

package org.ascent.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class ParsingUtil {

	public static final String ARGS_SPLITTER = "[\\;\\=]";

	public static final String KEY_VALUE_SPLITTER = "=";

	public static Map parseMap(String map) {
		map = map.replaceAll("\\/\\*[^\\*]*[^\\/]*\\*\\/", "");
		return parseMap(new StringTokenizer(map, "{};[]" + KEY_VALUE_SPLITTER,
				true), false, "}");
	}

	public static Map parseSimpleMap(String map) {
		return parseSimpleMap(map, "}");
	}

	public static Map parseSimpleMap(String map, String enddelim) {
		map = map.replaceAll("\\/\\*[^\\*]*[^\\/]*\\*\\/", "");
		return parseMap(new StringTokenizer(map, "{};[]" + KEY_VALUE_SPLITTER,
				true), true, enddelim);
	}

	public static List parseList(String list) {
		list = list.replaceAll("\\/\\*[^\\*]*[^\\/]*\\*\\/", "");
		return parseList(new StringTokenizer(list, "{},[]", true), false);
	}

	public static List parseFlatList(String list) {
		list = list.replaceAll("\\/\\*[^\\*]*[^\\/]*\\*\\/", "");
		return parseList(new StringTokenizer(list, "{},[]", true), true);
	}

	public static List parseList(StringTokenizer tokens, boolean flat) {
		tokens.nextToken();
		ArrayList list = new ArrayList(tokens.countTokens());
		String value = "";
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			Object item = token;

			if (token.equals("]"))
				break;
			if (token.equals(",")) {
				value = value.trim();
				if (value.startsWith("[") && value.endsWith("]") && !flat) {
					item = parseList(value);
				} else if (value.startsWith("{") && value.endsWith("}")
						&& !flat) {
					item = parseMap(value);
				} else {
					item = value;
				}
				list.add(item);
				value = "";
				continue;
			} else if (token.equals("[")) {
				token = "[" + parseEscapedValue(tokens, "[", "]");
			} else if (token.equals("{")) {
				token = "{" + parseEscapedValue(tokens, "{", "}");
			}

			value += token;

		}
		if (value.trim().length() != 0) {
			Object item = value;
			value = value.trim();
			if (value.startsWith("[") && value.endsWith("]") && !flat) {
				item = parseList(value);
			} else if (value.startsWith("{") && value.endsWith("}") && !flat) {
				item = parseMap(value);
			}

			list.add(item);
		}
		return list;
	}

	public static Map parseMap(StringTokenizer style, boolean simple,
			String enddelim) {
		style.nextToken();

		boolean inval = false;
		String token = null;
		String key = null;
		HashMap props = new HashMap();
		String value = "";
		while (style.hasMoreTokens()) {

			token = style.nextToken();

			token = token.trim();
			if (token.length() < 1)
				continue;

			if (token.equals(enddelim)) {
				if (inval && (value == null || value.trim().length() == 0))
					throw new ArgumentsFormatException(
							"The arguments string is not formatted properly. (hint: there is either a missing semicolon or equals sign)");
				if (inval) {
					if (value.startsWith("[") && value.endsWith("]") && !simple)
						props.put(key, parseList(value));
					else if (value.startsWith("{") && value.endsWith("}")
							&& !simple)
						props.put(key, parseMap(value));
					else
						props.put(key, value.trim());
				}
				break;
			}
			if (token.equals(KEY_VALUE_SPLITTER))
				inval = true;
			if (token.equals(";")
					&& (value == null || value.trim().length() == 0))
				throw new ArgumentsFormatException(
						"The arguments string is not formatted properly. (hint: there is either a missing semicolon or equals sign)");
			if (token.equals(";")) {
				inval = false;
				value = value.trim();
				if (value.startsWith("[") && value.endsWith("]") && !simple)
					props.put(key, parseList(value));
				else if (value.startsWith("{") && value.endsWith("}")
						&& !simple)
					props.put(key, parseMap(value));
				else
					props.put(key, value);

				value = "";
				key = null;
			}
			if (token.equals("{") && inval)
				token = "{" + parseEscapedValue(style, "{", "}");
			if (token.equals("["))
				token = "[" + parseEscapedValue(style, "[", "]");
			if (!token.equals(";") && !token.equals(KEY_VALUE_SPLITTER)) {
				if (key == null) {
					key = token.trim();
				} else {
					value += token;
				}
			}

		}

		return props;
	}

	public static String parseEscapedValue(StringTokenizer tk,
			String startdelim, String enddelim) {
		int lhs = 1;

		String val = "";
		while (lhs > 0 && tk.hasMoreTokens()) {
			String token = tk.nextToken();
			if (token.equals(startdelim)) {
				lhs++;
				val += startdelim;
			} else if (token.equals(enddelim)) {
				lhs--;
				val += enddelim;
			} else
				val += token;
		}
		return val;
	}

	public static Integer toInt(Object val) {
		if (val == null) {
			return "null".hashCode();
		} else if (val instanceof Integer) {
			return (Integer) val;
		} else if (val instanceof String) {
			try {
				val = ((String)val).trim();
				int v = Integer.parseInt((String) val);
				return new Integer(v);
			} catch (Exception e) {
			}
		}

		return val.hashCode();
	}
}
