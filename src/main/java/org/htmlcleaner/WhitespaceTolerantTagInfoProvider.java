package org.htmlcleaner;


public class WhitespaceTolerantTagInfoProvider extends DefaultTagProvider implements ITagInfoProvider {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6349860503307965029L;

	@Override
	public TagInfo put(String key, TagInfo value) {
		WhitespaceTolerantTagInfo info = new WhitespaceTolerantTagInfo(value.getName(), value.getContentType(), value.getBelongsTo(), value.isDeprecated(), value.isUnique(), value.isIgnorePermitted());
		info.setChildTags(value.getChildTags());
		info.setContinueAfterTags(value.getContinueAfterTags());
		info.setCopyTags(value.getCopyTags());
		info.setFatalTag(value.getFatalTag());
		info.setHigherTags(value.getHigherTags());
		info.setMustCloseTags(value.getMustCloseTags());
		info.setPermittedTags(value.getPermittedTags());
		info.setRequiredParent(value.getRequiredParent());
		return super.put(key, info);
	}

}
