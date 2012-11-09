/**
 * Copyright 2011-2012 Alexandre Dutra
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
package fr.dutra.confluence2wordpress.core.sync;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.util.LabelUtil;

import fr.dutra.confluence2wordpress.core.metadata.Metadata;

/**
 * @author Alexandre Dutra
 */
public class DefaultPageLabelsSynchronizer implements PageLabelsSynchronizer {

    private static final String WORDPRESSMETADATA = "wordpressmetadata";
    
    private LabelManager labelManager;

    public DefaultPageLabelsSynchronizer(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    @Override
    public void tagNamesToPageLabels(Labelable page, Metadata metadata) {
        List<String> tagNames = metadata.getTagNames();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                boolean found = false;
                List<Label> labels = page.getLabels();
                String sanitized = sanitize(tagName);
                for (Label label : labels) {
                    if (label.getName().equals(sanitized)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    LabelUtil.addLabel(sanitized, labelManager, page);
                }
            }
        }
        List<Label> deletedLabels = new ArrayList<Label>();
        for (Label label : page.getLabels()) {
            if(!WORDPRESSMETADATA.equals(label.getName())){
                boolean found = false;
                if (tagNames != null) {
                    for (String tagName : tagNames) {
                        if (label.getName().equals(sanitize(tagName))) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    deletedLabels.add(label);
                }
            }
        }
        if (!deletedLabels.isEmpty()) {
            labelManager.removeLabels(page, deletedLabels);
        }
    }

    @Override
    public void pageLabelsToTagNames(Labelable page, Metadata metadata) {
        List<String> tagNames = metadata.getTagNames();
        List<Label> labels = page.getLabels();
        if (labels != null && !labels.isEmpty()) {
            if (tagNames == null) {
                tagNames = new ArrayList<String>();
                metadata.setTagNames(tagNames);
            }
            for (Label label : labels) {
                if (!WORDPRESSMETADATA.equals(label.getName()) && ! contains(label.getName(), tagNames)) {
                    tagNames.add(label.getName());
                }
            }
        }
        metadata.setTagNames(tagNames);
    }

    private String sanitize(String tagName) {
        return tagName.replaceAll("[^A-Za-z0-9-_]", "").toLowerCase();
    }

    private boolean contains(String labelName, List<String> tagNames){
        for (String tagName : tagNames) {
            if(labelName.equals(sanitize(tagName))){
                return true;
            }
        }
        return false;
    }

}