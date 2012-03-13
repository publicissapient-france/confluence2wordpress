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
package fr.dutra.confluence2wordpress.wp;



public enum WordpressPostStatus {

    DRAFT   ("draft"  , false),
    PENDING ("pending", false),
    PRIVATE ("private", false),
    PUBLISH ("publish", true);
    
    private final String code;

    private final boolean publish;
    
    private WordpressPostStatus(String code, boolean publish) {
        this.code = code;
        this.publish = publish;
    }

    public String getCode() {
        return code;
    }

    public boolean isPublish() {
        return publish;
    }
    
    
}