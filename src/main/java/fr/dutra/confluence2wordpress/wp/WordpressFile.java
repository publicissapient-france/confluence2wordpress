/**
 * Copyright 2011-2012 Alexandre Dutra
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
package fr.dutra.confluence2wordpress.wp;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({"data"})
public class WordpressFile {

	private String fileName;

	private String url;

	private String mimeType;

	private Integer height = null;

	private Integer width = null;

	private boolean alternative = false;
	
	private Map<String, WordpressFile> alternatives = new HashMap<String, WordpressFile>();

	private transient byte[] data;

	public WordpressFile() {
		super();
	}

	public WordpressFile(String fileName) {
		super();
		this.fileName = fileName;
	}

	public WordpressFile(String fileName, String mimeType, byte[] data) {
		super();
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.data = data;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isAlternative() {
		return alternative;
	}

	public void setAlternative(boolean alternative) {
		this.alternative = alternative;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String type) {
		this.mimeType = type;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Map<String, WordpressFile> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(Map<String, WordpressFile> alternatives) {
		this.alternatives = alternatives;
	}

	public WordpressFile getAlternative(String key) {
		return alternatives.get(key);
	}

	public void putAlternative(String key, WordpressFile value) {
		alternatives.put(key, value);
	}

	public WordpressFile getBestAlternative(Integer width) {
		if(width == null || this.width == null) {
			return this;
		}
		int bestDelta = Math.abs(width - this.width);
		WordpressFile bestAlternative = this;
		for (WordpressFile wf : alternatives.values()) {
			if(wf.width != null) {
				int delta = Math.abs(width - wf.width);
				if (delta < bestDelta) {
					bestDelta = delta;
					bestAlternative = wf;
				}
			}
		}
		return bestAlternative;
	}

}