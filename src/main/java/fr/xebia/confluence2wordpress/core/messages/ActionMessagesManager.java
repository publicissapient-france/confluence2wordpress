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
package fr.xebia.confluence2wordpress.core.messages;

import java.util.List;

import com.atlassian.confluence.core.ConfluenceActionSupport;


public class ActionMessagesManager {

    private static final String ACTION_ERRORS_KEY = "C2W_ACTION_ERRORS";
    
    private static final String ACTION_MESSAGES_KEY = "C2W_ACTION_MESSAGES";
    
    @SuppressWarnings("unchecked")
    public void storeActionErrorsAndMessagesInSession(ConfluenceActionSupport page) {
        page.getSession().put(ACTION_ERRORS_KEY, page.getActionErrors());
        page.getSession().put(ACTION_MESSAGES_KEY, page.getActionMessages());
    }
    
    @SuppressWarnings("unchecked")
    public void restoreActionErrorsAndMessagesFromSession(ConfluenceActionSupport page) {
        List<String> errors = (List<String>) page.getSession().get(ACTION_ERRORS_KEY);
        if(errors != null){
            for (String error : errors) {
                page.addActionError(error);
            }
            page.getSession().put(ACTION_ERRORS_KEY, null);
        }
        List<String> messages = (List<String>) page.getSession().get(ACTION_MESSAGES_KEY);
        if(messages != null){
            for (String message : messages) {
                page.addActionMessage(message);
            }
            page.getSession().put(ACTION_MESSAGES_KEY, null);
        }
    }
        
}
