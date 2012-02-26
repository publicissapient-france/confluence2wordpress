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
package fr.xebia.confluence2wordpress.core.converter.preprocessors;


import static com.atlassian.confluence.content.render.xhtml.XhtmlConstants.*;
import java.io.StringReader;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.pages.ContentNode;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionReplacer;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import fr.xebia.confluence2wordpress.core.converter.ConversionException;
import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;


public class AttachmentsPreprocessor implements PreProcessor {

    private final XhtmlContent xhtmlUtils;
    
    private final ConversionContext conversionContext;
    
    public AttachmentsPreprocessor(XhtmlContent xhtmlUtils, ConversionContext conversionContext) {
        super();
        this.xhtmlUtils = xhtmlUtils;
        this.conversionContext = conversionContext;
    }

    @Override
    public String preProcess(String storage, final ConverterOptions options) throws ConversionException {
    	return storage;
    }

}

