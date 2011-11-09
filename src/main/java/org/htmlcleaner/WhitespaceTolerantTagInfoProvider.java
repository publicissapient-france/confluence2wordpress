/**
 * Copyright 2011 Alexandre Dutra
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
