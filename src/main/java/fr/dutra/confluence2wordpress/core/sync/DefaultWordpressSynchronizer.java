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
package fr.dutra.confluence2wordpress.core.sync;

import java.util.List;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.setup.settings.SettingsManager;

import fr.dutra.confluence2wordpress.core.converter.ConversionException;
import fr.dutra.confluence2wordpress.core.converter.Converter;
import fr.dutra.confluence2wordpress.core.converter.ConverterOptions;
import fr.dutra.confluence2wordpress.core.metadata.Metadata;
import fr.dutra.confluence2wordpress.core.settings.PluginSettingsManager;
import fr.dutra.confluence2wordpress.wp.WordpressClient;
import fr.dutra.confluence2wordpress.wp.WordpressPost;
import fr.dutra.confluence2wordpress.wp.WordpressXmlRpcException;

public class DefaultWordpressSynchronizer implements WordpressSynchronizer {

	private SettingsManager settingsManager;

	private Converter converter;

	private AttachmentsSynchronizer attachmentsSynchronizer;

	private PluginSettingsManager pluginSettingsManager;

	public DefaultWordpressSynchronizer(SettingsManager settingsManager, Converter converter, AttachmentsSynchronizer attachmentsSynchronizer, PluginSettingsManager pluginSettingsManager) {
		this.settingsManager = settingsManager;
		this.converter = converter;
		this.attachmentsSynchronizer = attachmentsSynchronizer;
		this.pluginSettingsManager = pluginSettingsManager;
	}

	public Metadata synchronize(ContentEntityObject page, Metadata metadata) throws SynchronizationException, WordpressXmlRpcException, ConversionException {
		ConverterOptions options = createConversionOptions(page, metadata);
		List<SynchronizedAttachment> synchronizedAttachments = attachmentsSynchronizer.synchronizeAttachments(page, metadata);
		options.setSynchronizedAttachments(synchronizedAttachments);
		WordpressPost post = metadata.createPost();
		String postBody = converter.convert(page, options);
		post.setBody(postBody);
		WordpressClient client = pluginSettingsManager.getWordpressClient();
		post = client.post(post);
		metadata.updateFromPost(post);
		return metadata;
	}

	public String preview(ContentEntityObject page, Metadata metadata) throws ConversionException {
		ConverterOptions options = createConversionOptions(page, metadata);
		return converter.convert(page, options);
	}

	private ConverterOptions createConversionOptions(ContentEntityObject page, Metadata metadata) {
		ConverterOptions options = new ConverterOptions();
		options.setPageTitle(metadata.getPageTitle());
		options.setIgnoredConfluenceMacros(metadata.getIgnoredConfluenceMacros());
		options.setSyntaxHighlighterPlugin(pluginSettingsManager.getWordpressSyntaxHighlighterPluginAsEnum());
		options.setTagAttributes(metadata.getTagAttributes());
		String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
		options.setConfluenceRootUrl(baseUrl);
		options.setFormatHtml(metadata.isFormatHtml());
		return options;
	}

}
