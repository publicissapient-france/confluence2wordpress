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
package fr.dutra.xebia.wiki2html.rpc;

import java.util.Hashtable;

import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.SecureRpc;

public interface Wiki2HtmlRpc extends SecureRpc {

    String convert(String authToken, String pageUrl, Hashtable<Object,Object> options) throws RemoteException;

}