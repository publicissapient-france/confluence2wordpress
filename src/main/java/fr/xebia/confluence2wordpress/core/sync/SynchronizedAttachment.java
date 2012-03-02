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
package fr.xebia.confluence2wordpress.core.sync;

import com.atlassian.confluence.pages.Attachment;

import fr.xebia.confluence2wordpress.wp.WordpressFile;

public class SynchronizedAttachment {

    private WordpressFile wordpressFile;

	private String thumbnailPath;

	private String attachmentPath;

	private Integer attachmentVersion;

	private long attachmentId;

	public SynchronizedAttachment() {
		super();
	}

	public SynchronizedAttachment(Attachment confluenceAttachment, WordpressFile wordpressFile) {
		super();
		this.wordpressFile = wordpressFile;
		this.attachmentPath = confluenceAttachment.getDownloadPathWithoutVersion();
		this.thumbnailPath = attachmentPath.replace("/attachments/", "/thumbnails/");
		this.attachmentVersion = confluenceAttachment.getAttachmentVersion();
		this.attachmentId = confluenceAttachment.getId();
	}
	
	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public String getAttachmentPath() {
		return attachmentPath;
	}

	public Integer getAttachmentVersion() {
		return attachmentVersion;
	}

	public long getAttachmentId() {
		return attachmentId;
	}

	public WordpressFile getWordpressFile() {
		return wordpressFile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (attachmentId ^ (attachmentId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SynchronizedAttachment other = (SynchronizedAttachment) obj;
		if (attachmentId != other.attachmentId)
			return false;
		return true;
	}

}