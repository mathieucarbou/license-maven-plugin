(:
 : Copyright (c)2005 Elsevier, Inc.
 :
 : Licensed under the Apache License, Version 2.0 (the "License");
 : you may not use this file except in compliance with the License.
 : You may obtain a copy of the License at
 :
 : http://www.apache.org/licenses/LICENSE-2.0
 :
 : Unless required by applicable law or agreed to in writing, software
 : distributed under the License is distributed on an "AS IS" BASIS,
 : WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 : See the License for the specific language governing permissions and
 : limitations under the License.
 :
 : The use of the Apache License does not indicate that this project is
 : affiliated with the Apache Software Foundation.
 :)

(:~ 
 :  This library module provides several functions that
 :  support interactions with the xqDoc web service.
 :  Both operations defined by the xqDoc web service can be
 :  accessed via the functions defined in this module ... this
 :  includes retrieving the xqDoc XML for predefined library
 :  modules (such as the XPath F&O, MarkLogic CTS, and MarkLogic
 :  XDMP modules) as well as generating xqDoc XML for user supplied
 :  XQuery.
 :  <p/>
 :  This is the version 1.0 interface for the xqDoc Web Service and
 :  maps to the version 1.0 WSDL for the xqDoc Web Service.
 :  <br/>
 :  <p/>
 :  Sample Invocations:
 :  <p/>
 :  Get the xqDoc XML for the May 2003 XPath F&O library module
 :  <p>
 :  <pre>
 :  import module "ml-xqdoc-ws-lib" at "/ml-xqdoc-ws-lib.xqy"
 :  <br/>
 :  declare namespace xqws="ml-xqdoc-ws-lib"
 :  <br/>
 :  xqws:get-xqdoc-module-xml((), $xqws:XPATHFO-MAY2003-URI,()) 
 :  </pre> 
 :  </p>
 :  Generate the xqDoc XML for the specified XQuery file.  No default namespace
 :  or predefined namespaces are specified.
 :  <p>
 :  <pre>
 :  import module "ml-xqdoc-ws-lib" at "/ml-xqdoc-ws-lib.xqy"
 :  <br/>
 :  declare namespace xqws="ml-xqdoc-ws-lib"
 :  <br/>
 :  xqws:get-xqdoc-xquery-xml((),(),(),(),(),
 :  <br/>
 :                          xdmp:document-get("ml-xqdoc-ws-lib.xqy", 
 :  <br/>
 :                                            &amp;lt;options xmlns="xdmp:document-get"&amp;gt;
 :  <br/>
 :                                            &amp;lt;format&amp;gt;text&amp;lt;/format&amp;gt;
 :  <br/>
 :                                            &amp;lt;/options&amp;gt;) )
 :  </pre>
 :  </p>
 :  Generate the xqDoc XML for the specified XQuery file.  Use the specified
 :  default function namespace and the mapping of prefixes and namespaces for
 :  cts and xdmp.  The default function namespace is not necessary if the XQuery
 :  module has already identified a 'default function namespace'.  The predefined
 :  namespace mappings will be required for MarkLogic if you want to enable linking 
 :  to xdmp and cts library modules (unless you explicitly declare these namespaces
 :  in the XQuery module).
 :  <p>
 :  <pre>
 :  import module "ml-xqdoc-ws-lib" at "/ml-xqdoc-ws-lib.xqy"
 :  <br/>
 :  declare namespace xqws="ml-xqdoc-ws-lib"
 :  <br/>
 :  xqws:get-xqdoc-xquery-xml((),(),(),
 :  <br/>
 :                          $xqws:XPATHFO-MAY2003-URI,
 :  <br/>
 :                          (xqws:build-prefix-ns-map($xqws:ML-XDMP-PREFIX,
 :  <br/>
 :                                                    $xqws:ML-XDMP-URI),
 :  <br/>
 :                           xqws:build-prefix-ns-map($xqws:ML-CTS-PREFIX,
 :  <br/>
 :                                                    $xqws:ML-CTS-URI)),
 :  <br/>
 :                          xdmp:document-get("ml-xqdoc-ws-lib.xqy", 
 :  <br/>
 :                                            &amp;lt;options xmlns="xdmp:document-get"&amp;gt;
 :  <br/>
 :                                            &amp;lt;format&amp;gt;text&amp;lt;/format&amp;gt;
 :  <br/>
 :                                            &amp;lt;/options&amp;gt;))
 :  </pre>
 :  </p>
 :  @author Darin McBeath
 :  @since October 18, 2005
 :  @version 1.0
 :)
module "ml-xqdoc-ws-lib"

import module "ml-ws-lib" at "/ml-ws-lib.xqy"

declare namespace ws="ml-ws-lib"

declare namespace xq="ml-xqdoc-ws-lib"

declare namespace types="http://schemas.xqdoc.org/xqdocService/types/v1_1"

(:~ Default target for the xqDoc web service. :)
define variable $xq:DEFAULT-SOAP-ENDPOINT {"http://webservices.xqdoc.org/xqdocWS/services/xqdocServicePort_V1_1" }

(:~ Default xqDoc XML version to generate. :)
define variable $xq:DEFAULT-XQDOC-VERSION {"1.0" }

(:~ Default W3C XQuery specification to use when processing the XQuery file. :)
define variable $xq:DEFAULT-XQUERY-SPEC-VERSION {"http://www.w3.org/TR/2003/WD-xquery-20030502/" }

(:~ May 2003 W3C XQuery specification. :)
define variable $xq:MAY2003-XQUERY-SPEC-VERSION {"http://www.w3.org/TR/2003/WD-xquery-20030502/" }

(:~ Nov 2003 W3C XQuery specification. :)
define variable $xq:NOV2003-XQUERY-SPEC-VERSION {"http://www.w3.org/TR/2003/WD-xquery-20031112/" }

(:~ Oct 2004 XQuery specification. :)
define variable $xq:OCT2004-XQUERY-SPEC-VERSION {"http://www.w3.org/TR/2004/WD-xquery-20041029/" }

(:~ URI for the pre-built xqDoc XML for the XPath F&O library module for May 2003. :)
define variable $xq:XPATHFO-MAY2003-URI {"http://www.w3.org/2003/05/xpath-functions" } 

(:~ URI for the pre-built xqDoc XML for the XPath F&O library module for Nov 2003. :)
define variable $xq:XPATHFO-NOV2003-URI {"http://www.w3.org/2003/11/xpath-functions" }

(:~ URI for the pre-built xqDoc XML for the XPath F&O library module for Oct 2004. :)
define variable $xq:XPATHFO-OCT2004-URI {"http://www.w3.org/2004/10/xpath-functions" } 

(:~ URI for MarkLogic xdmp built-ins (used in namespace-prefix mappings). :)
define variable $xq:ML-XDMP-URI {"http://marklogic.com/xdmp" } 

(:~ Prefix for MarkLogic xdmp built-ins (used in namespace-prefix mappings). :)
define variable $xq:ML-XDMP-PREFIX {"xdmp" } 

(:~ URI for MarkLogic cts built-ins (used in namespace-prefix mappings).  :)
define variable $xq:ML-CTS-URI {"http://marklogic.com/cts" } 

(:~ Prefix for MarkLogic cts built-ins (used in namespace-prefix mappings). :)
define variable $xq:ML-CTS-PREFIX {"cts" } 

(:~ URI for the pre-built xqDoc XML for the MarkLogic XDMP library module (version 2.2). :)
define variable $xq:ML-XDMP-2.2-URI {"http://marklogic.com/xdmp/2.2/" } 

(:~ URI for the pre-built xqDoc XML for the MarkLogic XDMP library module (version 3.0). :)
define variable $xq:ML-XDMP-3.0-URI {"http://marklogic.com/xdmp/3.0/" } 

(:~ URI for the pre-built xqDoc XML for the MarkLogic CTS library module (version 2.2). :)
define variable $xq:ML-CTS-2.2-URI {"http://marklogic.com/cts/2.2/" } 

(:~ URI for the pre-built xqDoc XML for the MarkLogic CTS library module (version 3.0). :)
define variable $xq:ML-CTS-3.0-URI {"http://marklogic.com/cts/3.0/" }

(:~ URI for the default function namespace (May 2003 XPath Functions and Operators) :)
define variable $xq:DEFAULT-FUNCTION-NAMESPACE { "http://www.w3.org/2003/05/xpath-functions" }

default function namespace="http://www.w3.org/2003/05/xpath-functions" 


(:~ 
 :  This function is the 'public' interface that will be invoked from a 'main' module
 :  to get the xqDoc XML for the specified library module.  Under the covers, this function will 
 :  construct the xqDoc payload, invoke the xqDoc web service, and process the response.
 :  If any problems occur, an error will be raised.
 :   
 :  @param $endpoint The endpoint for the xqDoc web service
 :  @param $module-uri The URI for the library module
 :  @param $xqdoc-version The version of xqDoc XML to generate
 : 
 :  @return The xqDoc XML.
 :
 :  @error Problems encountered while retrieving the xqDoc XML for the specified module URI.
 :)
define function xq:get-xqdoc-module-xml($endpoint as xs:string?,
                                        $module-uri as xs:string,
                                        $xqdoc-version as xs:string?)
                                        as element()
{
  let $payload := <types:getModule>
                  <types:getModuleReqPayload>
                  <types:module>{$module-uri}</types:module>
                  {
                  if (exists($xqdoc-version)) then
                    <types:xqdocVersion>{$xqdoc-version}</types:xqdocVersion>
                  else
                    <types:xqdocVersion>{$xq:DEFAULT-XQDOC-VERSION}</types:xqdocVersion>
                  }
                  </types:getModuleReqPayload>
                  </types:getModule>

  let $soap-endpoint := if (exists($endpoint)) then
                         $endpoint
                       else
					     $xq:DEFAULT-SOAP-ENDPOINT

  let $response := ws:invoke-web-service($soap-endpoint, 
                                         "getModule",
                                         $payload,
						                 ()) 
  
  return
    if (exists($response)) then
      if (exists($response//types:error/types:errorText)) then
	    error($response//types:error/types:errorText)
	  else
	    (xdmp:unquote(data($response//types:xqdoc)))/*
	else
	  error("Problems processing the request.")
}


(:~ 
 :  This function is the 'public' interface that will be invoked from a 'main' module
 :  to get the xqDoc XML for some XQuery.  Under the covers, this function will construct
 :  the xqDoc payload, invoke the xqDoc web service, and process the response.  If any
 :  problems occur, an error will be raised.
 :
 :  @param $endpoint The endpoint for the xqDoc web service
 :  @param $spec-version The version of the W3C spec to use when processing the XQuery 
 :  @param $xqdoc-version The version of xqDoc XML to generate
 :  @param $dfn The URI for the default function namespace
 :  @param $pfx-ns The mapping of prefixes and namespaces
 :  @param $xquery The XQuery to process and generate xqDoc XML
 :  @param $name The name for the XQuery module
 : 
 :  @return The xqDoc XML.
 :
 :  @error Problems encountered while generating the xqDoc XML.
 :)
define function xq:get-xqdoc-xquery-xml($endpoint as xs:string?,
                                       $spec-version as xs:string?,
                                       $xqdoc-version as xs:string?,
						   $dfn as xs:string?,
                                       $pfx-ns as element()*,
					         $xquery as xs:string,
                                       $name as xs:string)
						   as element()
	
{


  let $default-predefined-namespaces :=  (xq:build-prefix-ns-map($xq:ML-XDMP-PREFIX,
                                                                 $xq:ML-XDMP-URI),
                                          xq:build-prefix-ns-map($xq:ML-CTS-PREFIX,
                                                                 $xq:ML-CTS-URI))
                                                  
  let $payload := <types:generateXqdocXml>
                  <types:generateXqdocXmlReqPayload>
                  {
                  if (exists($spec-version)) then
                    <types:xqueryVersion>{$spec-version}</types:xqueryVersion>
                  else
                    <types:xqueryVersion>{$xq:DEFAULT-XQUERY-SPEC-VERSION}</types:xqueryVersion>
                  }
                  <types:moduleName>{$name}</types:moduleName>
                  {
                  if (exists($xqdoc-version)) then
                    <types:xqdocVersion>{$xqdoc-version}</types:xqdocVersion>
                  else
                    <types:xqdocVersion>{$xq:DEFAULT-XQDOC-VERSION}</types:xqdocVersion>
                  }
                  {
                  if (exists($dfn)) then
                    <types:defaultNamespace>{$dfn}</types:defaultNamespace>
                  else
                    <types:defaultNamespace>{$xq:DEFAULT-FUNCTION-NAMESPACE}</types:defaultNamespace>
                  }
                  {
				  if (exists($pfx-ns)) then
                    $pfx-ns
				  else
				    $default-predefined-namespaces					 
                  }
                  <types:xquery>{$xquery}</types:xquery>
                  </types:generateXqdocXmlReqPayload>
                  </types:generateXqdocXml>

  let $soap-endpoint := if (exists($endpoint)) then
                         $endpoint
                       else
					     $xq:DEFAULT-SOAP-ENDPOINT

  let $response := ws:invoke-web-service($soap-endpoint,
                                         "generateXqdocXml",
						                 $payload,
						                 ())
  
  return

    if (exists($response)) then
      if (exists($response//types:error/types:errorText)) then
	    error($response//types:error/types:errorText)
	  else
	    (xdmp:unquote(data($response//types:xqdocXml)))/*
	else
	  error("Problems processing the request.")

}

(:~ 
 :  This function is invoked to create the structure necessary to associate
 :  a prefix with a namespace.  This is needed in situations where a namespace
 :  and prefix association is not required by an implementation (for certain
 :  prefixes) because the implementation has an underlying default.  For MarkLogic,
 :  this would include the xdmp and cts prefixes.
 :   
 :  @param $pfx
 :  @param $ns
 : 
 :  @return A 'map' for the prefix and the namespace.
 :)
define function xq:build-prefix-ns-map($pfx as xs:string,
                                       $ns as xs:string)
						   as element()
{
  <types:predefinedNamespace>
    <types:prefix>{$pfx}</types:prefix>
    <types:uri>{$ns}</types:uri>
  </types:predefinedNamespace>
}
