package fr.xebia.confluence2wordpress.core.converter.preprocessors;

import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;

public interface PreProcessor {

    String preProcess(String wiki, ConverterOptions options);

}