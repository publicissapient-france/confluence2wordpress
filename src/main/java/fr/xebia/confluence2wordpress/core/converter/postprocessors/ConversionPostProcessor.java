package fr.xebia.confluence2wordpress.core.converter.postprocessors;

import org.htmlcleaner.TagNode;

import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;

public interface ConversionPostProcessor {

    public abstract String postProcess(String html, TagNode body, ConverterOptions options);

}