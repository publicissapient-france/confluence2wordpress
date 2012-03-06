package fr.xebia.confluence2wordpress.core.sync;

import java.util.List;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.setup.settings.SettingsManager;

import fr.xebia.confluence2wordpress.core.converter.ConversionException;
import fr.xebia.confluence2wordpress.core.converter.Converter;
import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;
import fr.xebia.confluence2wordpress.core.metadata.Metadata;
import fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager;
import fr.xebia.confluence2wordpress.wp.WordpressClient;
import fr.xebia.confluence2wordpress.wp.WordpressPost;
import fr.xebia.confluence2wordpress.wp.WordpressXmlRpcException;

public class DefaultWordpressSynchronizer implements WordpressSynchronizer {

	private SettingsManager settingsManager;

	private Converter converter;

	private AttachmentsSynchronizer attachmentsSynchronizer;

	private PluginSettingsManager pluginSettingsManager;

	public DefaultWordpressSynchronizer(SettingsManager settingsManager, Converter converter, AttachmentsSynchronizer attachmentsSynchronizer, PluginSettingsManager pluginSettingsManager) {
		super();
		this.settingsManager = settingsManager;
		this.converter = converter;
		this.attachmentsSynchronizer = attachmentsSynchronizer;
		this.pluginSettingsManager = pluginSettingsManager;
	}

	public Metadata synchronize(ContentEntityObject page, Metadata metadata) throws SynchronizationException, WordpressXmlRpcException, ConversionException {
		ConverterOptions options = createConversionOptions(metadata);
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
		ConverterOptions options = createConversionOptions(metadata);
		return converter.convert(page, options);
	}

	private ConverterOptions createConversionOptions(Metadata metadata) {
		ConverterOptions options = new ConverterOptions();
		options.setPageTitle(metadata.getPageTitle());
		options.setIgnoredConfluenceMacros(metadata.getIgnoredConfluenceMacros());
		options.setOptimizeForRDP(metadata.isOptimizeForRDP());
		options.setSyntaxHighlighterPlugin(pluginSettingsManager.getWordpressSyntaxHighlighterPluginAsEnum());
		options.setTagAttributes(metadata.getTagAttributes());
		String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
		options.setConfluenceRootUrl(baseUrl);
		options.setFormatHtml(metadata.isFormatHtml());
		return options;
	}

}
