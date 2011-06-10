/**
 * Copyright 2011 Alexandre Dutra
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.confluence2wordpress;

import java.io.IOException;
import java.util.Properties;

import junit.framework.Assert;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;
import com.atlassian.confluence.plugin.functest.JWebUnitConfluenceWebTester;
import com.atlassian.confluence.plugin.functest.TesterConfiguration;
import com.atlassian.confluence.plugin.functest.helper.PageHelper;
import com.atlassian.confluence.plugin.functest.helper.SpaceHelper;

public abstract class AbstractIT extends AbstractConfluencePluginWebTestCase {

    protected String spaceKey;

    protected long pageId;

    @Override
    protected JWebUnitConfluenceWebTester createConfluenceWebTester() {

        Properties props = new Properties();
        props.put("confluence.webapp.protocol", "http");
        props.put("confluence.webapp.host", "localhost");

        // this is deceiving: the func test library checks for the system properties
        // *before* checking in this properties file for these values, so these
        // properties are technically ignored
        props.put("confluence.webapp.port", Integer.parseInt( System.getProperty("http.port")));
        props.put("confluence.webapp.context.path", System.getProperty("context.path"));

        props.put("confluence.auth.admin.username", "admin");
        props.put("confluence.auth.admin.password", "admin");

        //TODO site backup zip
        //props.put("confluence.data.export", "src/test/resources/site-export.zip");

        TesterConfiguration conf;
        try {
            conf = new TesterConfiguration(props);
        } catch (IOException ioe) {
            Assert.fail("Unable to create tester: " + ioe.getMessage());
            return null;
        }

        JWebUnitConfluenceWebTester tester = new JWebUnitConfluenceWebTester(conf);

        tester.getTestContext().setBaseUrl(tester.getBaseUrl());
        tester.setScriptingEnabled(false);

        return tester;
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createTestSpace();
        createTestPage(spaceKey);
    }


    @Override
    protected void tearDown() throws Exception {
        getPageHelper(pageId).delete();
        getSpaceHelper(spaceKey).delete();
        super.tearDown();
    }


    protected void createTestSpace() {
        SpaceHelper spaceHelper = getSpaceHelper();
        spaceKey = "test" + System.currentTimeMillis();
        spaceHelper.setKey(spaceKey);
        spaceHelper.setName("test");
        spaceHelper.create();
    }

    protected void createTestPage(String spaceKey) {
        PageHelper pageHelper = getPageHelper();
        pageHelper.setTitle("test-title" + System.currentTimeMillis());
        pageHelper.setSpaceKey(spaceKey);
        pageHelper.setContent("test-content");
        pageHelper.create();
        pageId = pageHelper.getId();
    }
}
