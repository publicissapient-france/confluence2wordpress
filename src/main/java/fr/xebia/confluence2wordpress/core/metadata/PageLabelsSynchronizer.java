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
package fr.xebia.confluence2wordpress.core.metadata;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;

/**
 * @author Alexandre Dutra
 *
 */
public class PageLabelsSynchronizer {

    private LabelManager labelManager;
    
    public PageLabelsSynchronizer(LabelManager labelManager) {
        this.labelManager = labelManager;
    }


    public void tagNamesToPageLabels(ContentEntityObject page, Metadata metadata) {
        List<String> tagNames = metadata.getTagNames();
        for (String tagName : tagNames) {
            boolean found = false;
            List<Label> labels = page.getLabels();
            for (Label label : labels) {
                if(label.getName().equals(tagName)){
                    found = true;
                    break;
                }
            }
            if(!found){
                labelManager.addLabel(page, new Label(tagName));
            }
        }

        List<Label> deletedLabels = new ArrayList<Label>();
        for (Label label : page.getLabels()) {
            boolean found = false;
            for (String tagName : tagNames) {
                if(label.getName().equals(tagName)){
                    found = true;
                    break;
                }
            }
            if(!found){
                deletedLabels.add(label);
            }
        }
        if( ! deletedLabels.isEmpty()){
            labelManager.removeLabels(page, deletedLabels);
        }
    }
    
    
    public void pageLabelsToTagNames(ContentEntityObject page, Metadata metadata) {
        List<String> tagNames = metadata.getTagNames();
        List<Label> labels = page.getLabels();
        if(labels != null && ! labels.isEmpty()){
            if(tagNames == null){
                tagNames = new ArrayList<String>();
                metadata.setTagNames(tagNames);
            }
            for (Label label : labels) {
                if(! "wordpressmetadata".equalsIgnoreCase(label.getName()) && ! tagNames.contains(label.getName())){
                    tagNames.add(label.getName());
                }
            }
        }
        metadata.setTagNames(tagNames);
    }


}