package fr.xebia.confluence2wordpress.core.sync;

import java.net.MalformedURLException;
import java.net.URL;
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

    public void setSettingsManager(SettingsManager settingsManager) {
		this.settingsManager = settingsManager;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}

	public void setAttachmentsSynchronizer(AttachmentsSynchronizer attachmentsSynchronizer) {
		this.attachmentsSynchronizer = attachmentsSynchronizer;
	}

	public void setPluginSettingsManager(PluginSettingsManager pluginSettingsManager) {
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
    
	private ConverterOptions createConversionOptions(Metadata metadata){
		ConverterOptions options = new ConverterOptions();
        options.setPageTitle(metadata.getPageTitle());
        options.setIgnoredConfluenceMacros(metadata.getIgnoredConfluenceMacros());
        options.setOptimizeForRDP(metadata.isOptimizeForRDP());
        options.setSyntaxHighlighterPlugin(pluginSettingsManager.getWordpressSyntaxHighlighterPluginAsEnum());
        String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
        try {
			options.setConfluenceRootUrl(new URL(baseUrl));
        } catch (MalformedURLException e) {
        }
        options.setTagAttributes(metadata.getTagAttributes());
		return options;
	}
	
}
