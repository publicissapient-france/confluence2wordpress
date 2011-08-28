curl -d "<methodCall> \
     <methodName>metaWeblog.newPost</methodName> \
        <params> \
           <param> \
           <value><int>0</int></value> \
           <value><string>admin</string></value> \
           <value><string>admin</string></value> \
              <value><string>revue-de-presse-xebia-2011-30</string></value> \
           </param> \
        </params> \
  </methodCall>" http://localhost/~alexandre/wordpress/xmlrpc.php