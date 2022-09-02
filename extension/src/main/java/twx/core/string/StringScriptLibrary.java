package twx.core.string;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class StringScriptLibrary {

	public static Boolean match_topic(String subscribe, String topic) {
		return match_topic_man3(subscribe, topic);
    }

	public static Boolean match_topic_regex(String subscribe, String topic) {
		String topicExp = subscribe//
				.replaceAll("\\$", "\\\\\\$")//
				.replaceAll("\\+", "[^/]+")//
				.replaceAll("/\\#$", "(\\$|/.+)");

		Pattern pattern = Pattern.compile(topicExp);
		boolean match = pattern.matcher(topic).matches();
		return match;
    }
  
	public static Boolean match_topic_man(String sub, String topic) {
		boolean match = false;

		Integer sPos = 0;
		Integer sLen = sub.length();
		Integer tPos = 0;
		Integer tLen = topic.length();

		while (sPos < sLen) {
			if (tPos < tLen && (topic.charAt(tPos) == '+' || topic.charAt(tPos) == '#')) {
				return false;
				// return MOSQ_ERR_INVAL;
			}
			if (tPos == tLen || sub.charAt(sPos) != topic.charAt(tPos)) {
				if (sub.charAt(sPos) == '+') {
					/* Check for bad "+foo" or "a/+foo" subscription */
					if (sPos > 0 && sub.charAt(sPos - 1) != '/') {
						return false;
						// return MOSQ_ERR_INVAL;
					}
					/* Check for bad "foo+" or "foo+/a" subscription */
					if (sPos + 1 < sLen && sub.charAt(sPos + 1) != '/') {
						return false;
						// return MOSQ_ERR_INVAL;
					}
					sPos++;
					while (tPos < tLen && topic.charAt(tPos) != '/') {
						if (topic.charAt(tPos) == '+' || topic.charAt(tPos) == '#') {
							return false;
							// return MOSQ_ERR_INVAL;
						}
						tPos++;
					}
					if (tPos == tLen && sPos == sLen) {
						return true;
						// match = true;
						// return MOSQ_ERR_SUCCESS;
					}
				} else if (sub.charAt(sPos) == '#') {
					/* Check for bad "foo#" subscription */
					if (sPos > 0 && sub.charAt(sPos - 1) != '/') {
						return false;
						// return MOSQ_ERR_INVAL;
					}
					/* Check for # not the final character of the sub, e.g. "#foo" */
					if (sPos + 1 < sLen) {
						return false;
						// return MOSQ_ERR_INVAL;
					} else {
						while (tPos < tLen) {
							if (topic.charAt(tPos) == '+' || topic.charAt(tPos) == '#') {
								return false;
								// return MOSQ_ERR_INVAL;
							}
							tPos++;
						}
						return true;
						// match = true;
						// return MOSQ_ERR_SUCCESS;
					}
				} else {
					/* Check for e.g. foo/bar matching foo/+/# */
					if (tPos == tLen && sPos > 0 && sub.charAt(sPos - 1) == '+' && sub.charAt(sPos) == '/'
							&& sPos + 1 < sLen && sub.charAt(sPos + 1) == '#') {
						return true;
						// match = true;
						// return MOSQ_ERR_SUCCESS;
					}

					/* There is no match at this point, but is the sub invalid? */
					while (sPos < sLen) {
						if (sub.charAt(sPos) == '#' && sPos + 1 < sLen) {

							return false;
							// return MOSQ_ERR_INVAL;
						}
						sPos++;
					}

					/* Valid input, but no match */
					return false;
					// return MOSQ_ERR_SUCCESS;
				}
			} else {
				/* sub.charAt(sPos) == topic.charAt(tPos) */
				if (tPos + 1 == tLen) {
					/* Check for e.g. foo matching foo/# */
					if (sPos + 3 == sLen && sub.charAt(sPos + 1) == '/' && sub.charAt(sPos + 2) == '#') {
						return true;
						// match = true;
						// return MOSQ_ERR_SUCCESS;
					}
				}
				sPos++;
				tPos++;
				if (sPos == sLen && tPos == tLen) {
					return true;
					// match = true;
					// return MOSQ_ERR_SUCCESS;
				} else if (tPos == tLen && sub.charAt(sPos) == '+' && sPos + 1 == sLen) {
					if (sPos > 0 && sub.charAt(sPos - 1) != '/') {
						return false; // return MOSQ_ERR_INVAL;
					}
					sPos++;
					return true;
					// match = true;
					// return MOSQ_ERR_SUCCESS;
				}
			}
		}
		if (tPos < tLen || sPos < sLen) {
			match = false;
		}
		return match;
	}

	public static Boolean match_topic_man2(String sub_str, String topic_str) {
		boolean match = false;
		byte[] 	sub = sub_str.getBytes();
		Integer sPos = 0;
		Integer sLen = sub_str.length();
		byte[] 	topic = topic_str.getBytes();
		Integer tPos = 0;
		Integer tLen = topic_str.length();

		while (sPos < sLen) {
			if (tPos < tLen && (topic[tPos] == '+' || topic[tPos] == '#')) {
				return false;
				// return MOSQ_ERR_INVAL;
			}
			if (tPos == tLen || sub[sPos] != topic[tPos]) {
				if (sub[sPos] == '+') {
					/* Check for bad "+foo" or "a/+foo" subscription */
					if (sPos > 0 && sub[sPos - 1] != '/') {
						return false;
						// return MOSQ_ERR_INVAL;
					}
					/* Check for bad "foo+" or "foo+/a" subscription */
					if (sPos + 1 < sLen && sub[sPos + 1] != '/') {
						return false;
						// return MOSQ_ERR_INVAL;
					}
					sPos++;
					while (tPos < tLen && topic[tPos] != '/') {
						if (topic[tPos] == '+' || topic[tPos] == '#') {
							return false;
							// return MOSQ_ERR_INVAL;
						}
						tPos++;
					}
					if (tPos == tLen && sPos == sLen) {
						return true;
						// match = true;
						// return MOSQ_ERR_SUCCESS;
					}
				} else if (sub[sPos] == '#') {
					/* Check for bad "foo#" subscription */
					if (sPos > 0 && sub[sPos - 1] != '/') {
						return false;
						// return MOSQ_ERR_INVAL;
					}
					/* Check for # not the final character of the sub, e.g. "#foo" */
					if (sPos + 1 < sLen) {
						return false;
						// return MOSQ_ERR_INVAL;
					} else {
						while (tPos < tLen) {
							if (topic[tPos] == '+' || topic[tPos] == '#') {
								return false;
								// return MOSQ_ERR_INVAL;
							}
							tPos++;
						}
						return true;
					}
				} else {
					/* Check for e.g. foo/bar matching foo/+/# */
					if (tPos == tLen && sPos > 0 && sub[sPos - 1] == '+' && sub[sPos] == '/' && sPos + 1 < sLen && sub[sPos + 1] == '#') {
						return true;
						// match = true;
						// return MOSQ_ERR_SUCCESS;
					}
					/* There is no match at this point, but is the sub invalid? */
					while (sPos < sLen) {
						if (sub[sPos] == '#' && sPos + 1 < sLen) {
							return false;
							// return MOSQ_ERR_INVAL;
						}
						sPos++;
					}
					/* Valid input, but no match */
					return false;
					// return MOSQ_ERR_SUCCESS;
				}
			} else {
				/* sub[sPos] == topic[tPos] */
				if (tPos + 1 == tLen) {
					/* Check for e.g. foo matching foo/# */
					if (sPos + 3 == sLen && sub[sPos + 1] == '/' && sub[sPos + 2] == '#') {
						return true;
					}
				}
				sPos++;
				tPos++;
				if (sPos == sLen && tPos == tLen) {
					return true;
				} else if (tPos == tLen && sub[sPos] == '+' && sPos + 1 == sLen) {
					if (sPos > 0 && sub[sPos - 1] != '/') {
						return false; // return MOSQ_ERR_INVAL;
					}
					sPos++;
					return true;
				}
			}
		}
		if (tPos < tLen || sPos < sLen) {
			match = false;
		}
		return match;
	}

	/**
	 * The forward slash (/) is used to separate each level within a topic tree and
	 * provide a hierarchical structure to the topic space. The use of the topic
	 * level separator is significant when the two wildcard characters are
	 * encountered in topics specified by subscribers.
	 */
	public static final String TOPIC_LEVEL_SEPARATOR = "/";

	/**
	 * Multi-level wildcard The number sign (#) is a wildcard character that matches
	 * any number of levels within a topic.
	 */
	public static final String MULTI_LEVEL_WILDCARD = "#";

	/**
	 * Single-level wildcard The plus sign (+) is a wildcard character that matches
	 * only one topic level.
	 */
	public static final String SINGLE_LEVEL_WILDCARD = "+";

	/**
	 * Multi-level wildcard pattern(/#)
	 */
	public static final String MULTI_LEVEL_WILDCARD_PATTERN = TOPIC_LEVEL_SEPARATOR + MULTI_LEVEL_WILDCARD;

	/**
	 * Topic wildcards (#+)
	 */
	public static final String TOPIC_WILDCARDS = MULTI_LEVEL_WILDCARD + SINGLE_LEVEL_WILDCARD;

	// topic name and topic filter length range defined in the spec
	private static final int MIN_TOPIC_LEN = 1;
	private static final int MAX_TOPIC_LEN = 65535;
	private static final char NUL = '\u0000';

	
	public static void validate(String topicString, boolean wildcardAllowed) throws IllegalArgumentException {
		int topicLen = 0;
		try {
			topicLen = topicString.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e.getMessage());
		}

		// Spec: length check
		// - All Topic Names and Topic Filters MUST be at least one character
		// long
		// - Topic Names and Topic Filters are UTF-8 encoded strings, they MUST
		// NOT encode to more than 65535 bytes
		if (topicLen < MIN_TOPIC_LEN || topicLen > MAX_TOPIC_LEN) {
			throw new IllegalArgumentException(String.format("Invalid topic length, should be in range[%d, %d]!",
					new Object[] { Integer.valueOf(MIN_TOPIC_LEN), Integer.valueOf(MAX_TOPIC_LEN) }));
		}

		// *******************************************************************************
		// 1) This is a topic filter string that can contain wildcard characters
		// *******************************************************************************
		if (wildcardAllowed) {
			// Only # or +
			if (StringUtils.equalsAny(topicString, new String[] { MULTI_LEVEL_WILDCARD, SINGLE_LEVEL_WILDCARD })) {
				return;
			}

			// 1) Check multi-level wildcard
			// Rule:
			// The multi-level wildcard can be specified only on its own or next
			// to the topic level separator character.

			// - Can only contains one multi-level wildcard character
			// - The multi-level wildcard must be the last character used within
			// the topic tree
			if (StringUtils.countMatches(topicString, MULTI_LEVEL_WILDCARD) > 1
					|| (topicString.contains(MULTI_LEVEL_WILDCARD)
							&& !topicString.endsWith(MULTI_LEVEL_WILDCARD_PATTERN))) {
				throw new IllegalArgumentException(
						"Invalid usage of multi-level wildcard in topic string: " + topicString);
			}

			// 2) Check single-level wildcard
			// Rule:
			// The single-level wildcard can be used at any level in the topic
			// tree, and in conjunction with the
			// multilevel wildcard. It must be used next to the topic level
			// separator, except when it is specified on
			// its own.
			validateSingleLevelWildcard(topicString);

			return;
		}

		// *******************************************************************************
		// 2) This is a topic name string that MUST NOT contains any wildcard characters
		// *******************************************************************************
		if (StringUtils.containsAny(topicString, TOPIC_WILDCARDS)) {
			throw new IllegalArgumentException("The topic name MUST NOT contain any wildcard characters (#+)");
		}
	}
	
	private static void validateSingleLevelWildcard(String topicString) {
		char singleLevelWildcardChar = SINGLE_LEVEL_WILDCARD.charAt(0);
		char topicLevelSeparatorChar = TOPIC_LEVEL_SEPARATOR.charAt(0);

		char[] chars = topicString.toCharArray();
		int length = chars.length;
		char prev = NUL, next = NUL;
		for (int i = 0; i < length; i++) {
			prev = (i - 1 >= 0) ? chars[i - 1] : NUL;
			next = (i + 1 < length) ? chars[i + 1] : NUL;

			if (chars[i] == singleLevelWildcardChar) {
				// prev and next can be only '/' or none
				if (prev != topicLevelSeparatorChar && prev != NUL || next != topicLevelSeparatorChar && next != NUL) {
					throw new IllegalArgumentException(
							String.format("Invalid usage of single-level wildcard in topic string '%s'!",
									new Object[] { topicString }));

				}
			}
		}
	}
	
	public static Boolean match_topic_man3(String topicFilter, String topicName) {
		
		int topicPos = 0;
		int filterPos = 0;
		int topicLen = topicName.length();
		int filterLen = topicFilter.length();

		StringScriptLibrary.validate(topicFilter, true);
		StringScriptLibrary.validate(topicName, false);

		if (topicFilter.equals(topicName)) {
			return true;
		}

		while (filterPos < filterLen && topicPos < topicLen) {
			if (topicFilter.charAt(filterPos) == '#') {
				/*
				 * next 'if' will break when topicFilter = topic/# and topicName topic/A/,
				 * but they are matched
				 */
				topicPos 	= topicLen;
				filterPos 	= filterLen;
				break;
			}
			if (topicName.charAt(topicPos) == '/' && topicFilter.charAt(filterPos) != '/')
				break;
			if (topicFilter.charAt(filterPos) != '+' && topicFilter.charAt(filterPos) != '#'
					&& topicFilter.charAt(filterPos) != topicName.charAt(topicPos))
				break;
			if (topicFilter.charAt(filterPos) == '+') { // skip until we meet the next separator, or end of string
				int nextpos = topicPos + 1;
				while (nextpos < topicLen && topicName.charAt(nextpos) != '/')
					nextpos = ++topicPos + 1;
			}
			filterPos++;
			topicPos++;
		}

		if ((topicPos == topicLen) && (filterPos == filterLen)) {
			return true;
		} else {
			/*
			 * https://github.com/eclipse/paho.mqtt.java/issues/418
			 * Covers edge case to match sport/# to sport
			 */
			if ((topicFilter.length() - filterPos > 0) && (topicPos == topicLen)) {
				if (topicName.charAt(topicPos - 1) == '/' && topicFilter.charAt(filterPos) == '#')
					return true;
				if (topicFilter.length() - filterPos > 1 && topicFilter.substring(filterPos, filterPos + 2).equals("/#")) {
					return true;
				}
			}
		}
		return false;
	}
}
