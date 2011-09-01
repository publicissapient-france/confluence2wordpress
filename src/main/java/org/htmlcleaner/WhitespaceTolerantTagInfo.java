package org.htmlcleaner;

import fr.xebia.confluence2wordpress.util.html.HtmlUtils;

public class WhitespaceTolerantTagInfo extends TagInfo {
	
	public WhitespaceTolerantTagInfo(String name, int contentType, int belongsTo, boolean depricated, boolean unique, boolean ignorePermitted) {
		super(name, contentType, belongsTo, depricated, unique, ignorePermitted);
	}

	@Override
	boolean allowsItem(BaseToken token) {
		if(token instanceof ContentNode) {
			if(HtmlUtils.isHtmlWhitespace(((ContentNode) token).getContent().toString())){
				return true;
			}
		}
		return super.allowsItem(token);
	}

}
