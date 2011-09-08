package fr.xebia.confluence2wordpress.core.labels;

import com.atlassian.confluence.core.ContentEntityObject;

import fr.xebia.confluence2wordpress.core.metadata.Metadata;

public interface PageLabelsSynchronizer {

    void tagNamesToPageLabels(ContentEntityObject page, Metadata metadata);

    void pageLabelsToTagNames(ContentEntityObject page, Metadata metadata);

}