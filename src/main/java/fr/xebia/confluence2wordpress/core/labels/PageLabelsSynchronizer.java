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
package fr.xebia.confluence2wordpress.core.labels;

import com.atlassian.confluence.core.ContentEntityObject;

import fr.xebia.confluence2wordpress.core.metadata.Metadata;

public interface PageLabelsSynchronizer {

    void tagNamesToPageLabels(ContentEntityObject page, Metadata metadata);

    void pageLabelsToTagNames(ContentEntityObject page, Metadata metadata);

}