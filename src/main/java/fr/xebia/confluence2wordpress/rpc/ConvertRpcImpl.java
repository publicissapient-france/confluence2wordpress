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
package fr.xebia.confluence2wordpress.rpc;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.rpc.AuthenticationFailedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import fr.xebia.confluence2wordpress.core.Converter;
import fr.xebia.confluence2wordpress.core.ConverterOptions;
import fr.xebia.confluence2wordpress.core.PageLocator;

/**
 * There is a test XML-RPC method that already does a similar function:
 * @see com.atlassian.confluence.plugin.functest.module.xmlrpc.rendering.RenderingHelperService#convertMarkupToXhtml(String, String, String)
 * 
 * @author Alexandre Dutra
 */
public class ConvertRpcImpl implements ConvertRpc {

    /**
     * @see "http://confluence.atlassian.com/display/CONFDEV/RPC+Module"
     */
    private TransactionTemplate transactionTemplate;

    private PageLocator pageLocator;

    protected Converter converter;

    public void setPageManager(PageManager pageManager) {
        this.pageLocator = new PageLocator(pageManager);
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.converter = new Converter(wikiStyleRenderer);
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * Fake implementation of login method. The actual invocation is handled by the framework.
     */
    public String login(String username, String password) throws AuthenticationFailedException, RemoteException {
        return null;
    }

    /**
     * Fake implementation of logout method. The actual invocation is handled by the framework.
     */
    public boolean logout(String token) throws RemoteException {
        return false;
    }

    /**
     * @inheritdoc
     */
    public String convert(final String token, final String pageUrl, final Hashtable<Object,Object> properties) throws RemoteException {
        final ConverterOptions options = getWiki2HtmlConverterOptions(properties);
        String html = transactionTemplate.execute(new TransactionCallback<String>(){
            public String doInTransaction() {
                Page page = pageLocator.findPageByIdOrUrl(pageUrl);
                return converter.convert(page, options);
            }});
        return html;
    }


    protected ConverterOptions getWiki2HtmlConverterOptions(final Hashtable<Object,Object> properties) throws RemoteException {
        ConverterOptions options;
        try {
            options = new ConverterOptions(properties);
        } catch (IllegalAccessException e) {
            throw new RemoteException(e);
        } catch (InvocationTargetException e) {
            throw new RemoteException(e);
        } catch (NoSuchMethodException e) {
            throw new RemoteException(e);
        }
        return options;
    }


}