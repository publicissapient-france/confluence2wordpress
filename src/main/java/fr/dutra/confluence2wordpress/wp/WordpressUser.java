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
package fr.dutra.confluence2wordpress.wp;

import com.atlassian.gzipfilter.org.apache.commons.lang.StringUtils;



public class WordpressUser {

    private Integer id;

    private String login;

    private String niceName;

    private String displayName;

    private String firstName;

    private String lastName;

    private Integer level;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getLogin() {
        return login;
    }


    public void setLogin(String login) {
        this.login = login;
    }


    public String getNiceName() {
        return niceName;
    }


    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }


    public String getDisplayName() {
        return displayName;
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastNameName) {
        this.lastName = lastNameName;
    }


    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    //http://codex.wordpress.org/Roles_and_Capabilities#User_Level_to_Role_Conversion

    public boolean isSubscriber() {
        return level != null && level >= 0;
    }
    public boolean isContributor() {
        return level != null && level >= 1;
    }
    public boolean isAuthor() {
        return level != null && level >= 2;
    }
    public boolean isEditor() {
        return level != null && level >= 5;
    }
    public boolean isAdministrator() {
        return level != null && level >= 8;
    }

    public String getNiceUsername(){
        boolean hasFirstName = StringUtils.isNotEmpty(getFirstName());
        boolean hasLastName = StringUtils.isNotEmpty(getLastName());
        boolean hasFirstAndLastName = hasFirstName && hasLastName;
        boolean hasFirstOrLastName = hasFirstName || hasLastName;
        StringBuilder sb = new StringBuilder();
        if(hasLastName){
            sb.append(getLastName());
        }
        if(hasFirstAndLastName){
            sb.append(" ");
        }
        if(hasFirstName){
            sb.append(getFirstName());
        }
        if(hasFirstOrLastName){
            sb.append(" (");
        }
        sb.append(getLogin());
        if(hasFirstOrLastName){
            sb.append(")");
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format(
            "WordpressUser [id=%s, login=%s, level=%s, displayName=%s, niceName=%s, firstName=%s, lastName=%s]",
            id, login, level, displayName, niceName, firstName, lastName);
    }



}