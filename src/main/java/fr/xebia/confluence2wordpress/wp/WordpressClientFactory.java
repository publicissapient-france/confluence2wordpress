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
package fr.xebia.confluence2wordpress.wp;

import org.apache.commons.lang.StringUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class WordpressClientFactory {

    public WordpressClient newWordpressClient(WordpressConnectionProperties connectionProperties) {
        WordpressConnection wordpressConnection = new WordpressConnection(
            connectionProperties.getWordpressXmlRpcUrl(),
            connectionProperties.getWordpressUserName(),
            connectionProperties.getWordpressPassword(),
            connectionProperties.getWordpressBlogId());
        wordpressConnection.setProxyHost(connectionProperties.getProxyHost());
		wordpressConnection.setProxyPort(StringUtils.isEmpty(connectionProperties.getProxyPort()) ? null : Integer.decode(connectionProperties.getProxyHost()));
        return new WordpressClient(wordpressConnection);
    }

}