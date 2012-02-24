package fr.xebia.confluence2wordpress.core.converter;

import com.atlassian.confluence.core.ContentEntityObject;

public interface Converter {

	String convert(ContentEntityObject page, ConverterOptions options) throws ConversionException;

}