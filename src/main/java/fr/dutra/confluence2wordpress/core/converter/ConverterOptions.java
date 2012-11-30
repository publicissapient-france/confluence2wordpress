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
package fr.dutra.confluence2wordpress.core.converter;

import java.util.List;
import java.util.Map;

import fr.dutra.confluence2wordpress.core.sync.SynchronizedAttachment;
import fr.dutra.confluence2wordpress.util.MapUtils;

public class ConverterOptions {

	private String pageTitle;

	private String confluenceRootUrl;
    
    private boolean omitXmlDeclaration = true;

    private boolean useCdataForScriptAndStyle = false;

    private boolean omitComments = false;

    private boolean useEmptyElementTags = false;

    private boolean convertFontTagToSpan = true;

    private Map<String, String> tagTransformations = MapUtils.split("tt=code", ",", "=");

    private List<String> ignoredConfluenceMacros = null;

    private List<SynchronizedAttachment> synchronizedAttachments;

    private SyntaxHighlighterPlugin syntaxHighlighterPlugin = SyntaxHighlighterPlugin.SH_LEGACY;

    private Map<String,String> replaceTags;

    private Map<String,String> tagAttributes;

    private List<String> removeEmptyTags = null;

    private List<String> stripTags = null;

    private boolean formatHtml;
    
    public ConverterOptions() {
        this.tagTransformations = MapUtils.split("tt=code", ",", "=");
    }

    public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public boolean isOmitXmlDeclaration() {
        return omitXmlDeclaration;
    }

    public void setOmitXmlDeclaration(boolean omitXmlDeclaration) {
        this.omitXmlDeclaration = omitXmlDeclaration;
    }

    public boolean isUseCdataForScriptAndStyle() {
        return useCdataForScriptAndStyle;
    }

    public void setUseCdataForScriptAndStyle(boolean useCdataForScriptAndStyle) {
        this.useCdataForScriptAndStyle = useCdataForScriptAndStyle;
    }

    public boolean isOmitComments() {
        return omitComments;
    }

    public void setOmitComments(boolean omitComments) {
        this.omitComments = omitComments;
    }

    public boolean isUseEmptyElementTags() {
        return useEmptyElementTags;
    }

    public void setUseEmptyElementTags(boolean useEmptyElementTags) {
        this.useEmptyElementTags = useEmptyElementTags;
    }

    public boolean isConvertFontTagToSpan() {
        return convertFontTagToSpan;
    }

    public void setConvertFontTagToSpan(boolean convertFontTagToSpan) {
        this.convertFontTagToSpan = convertFontTagToSpan;
    }

    public Map<String, String> getTagTransformations() {
        return tagTransformations;
    }

    public void setTagTransformations(Map<String, String> tagTransformations) {
        this.tagTransformations = tagTransformations;
    }

    public List<String> getIgnoredConfluenceMacros() {
        return ignoredConfluenceMacros;
    }

    public void setIgnoredConfluenceMacros(List<String> disabledConfluenceMacros) {
        this.ignoredConfluenceMacros = disabledConfluenceMacros;
    }

    public List<SynchronizedAttachment> getSynchronizedAttachments() {
        return synchronizedAttachments;
    }
    
    public void setSynchronizedAttachments(List<SynchronizedAttachment> synchronizedAttachments) {
        this.synchronizedAttachments = synchronizedAttachments;
    }

    public SyntaxHighlighterPlugin getSyntaxHighlighterPlugin() {
        return syntaxHighlighterPlugin;
    }
    
    public void setSyntaxHighlighterPlugin(SyntaxHighlighterPlugin syntaxHighlighterPlugin) {
        this.syntaxHighlighterPlugin = syntaxHighlighterPlugin;
    }

	public String getConfluenceRootUrl() {
		return confluenceRootUrl;
	}

	public void setConfluenceRootUrl(String confluenceRootUrl) {
		this.confluenceRootUrl = confluenceRootUrl;
	}

	public Map<String, String> getTagAttributes() {
		return tagAttributes;
	}

	public void setTagAttributes(Map<String, String> tagAttributes) {
		this.tagAttributes = tagAttributes;
	}

	public boolean isFormatHtml() {
		return formatHtml;
	}

	public void setFormatHtml(boolean formatHtml) {
		this.formatHtml = formatHtml;
	}

	public List<String> getRemoveEmptyTags() {
		return removeEmptyTags;
	}

	public void setRemoveEmptyTags(List<String> removeEmptyTags) {
		this.removeEmptyTags = removeEmptyTags;
	}

	public List<String> getStripTags() {
		return stripTags;
	}

	public void setStripTags(List<String> stripTags) {
		this.stripTags = stripTags;
	}

	public Map<String, String> getReplaceTags() {
		return replaceTags;
	}

	public void setReplaceTags(Map<String, String> replaceTags) {
		this.replaceTags = replaceTags;
	}

}