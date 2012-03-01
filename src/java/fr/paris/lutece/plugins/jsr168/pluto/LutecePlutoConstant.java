/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.jsr168.pluto;


/**
 * Defined constants for Lutece/Pluto connector (JSR 168 plugin)
 */
public class LutecePlutoConstant
{
    /////////////////////////////////////////////////////////////////////////
    // Properties defined in "conf/plugins/jsr168.properties" configuration file 
    public static final String PROPERTY_FILE_SERVICES = "portlet.jsr168.file.services";
    public static final String PROPERTY_PROBLEM_INITIALIZATION = "portlet.jsr168.message.problem.initialization";
    public static final String PROPERTY_PROBLEM_RENDER = "portlet.jsr168.message.problem.render";
    public static final String PROPERTY_LOG_PREFIX = "portlet.jsr168.log.prefix";
    public static final String PROPERTY_LOG_INITIALIZATION_FAIL = "portlet.jsr168.log.initializationFail";

    /////////////////////////////////////////////////////////////////////////
    // Properties defined in "services.xml" configuration file 
    // Properties for base service "org.apache.pluto.portalImpl.services.config.ConfigService"
    public static final String CONFIG_SERVICES_PARAM_HOST_NAME = "host.name";
    public static final String CONFIG_SERVICES_PARAM_HOST_NAME_DEFAULT = "localhost";
    public static final String CONFIG_SERVICES_PARAM_HOST_PORT_HTTP = "host.port.http";
    public static final String CONFIG_SERVICES_PARAM_HOST_PORT_HTTP_DEFAULT = "80";
    public static final String CONFIG_SERVICES_PARAM_HOST_PORT_HTTPS = "host.port.https";
    public static final String CONFIG_SERVICES_PARAM_HOST_PORT_HTTPS_DEFAULT = "443";
    public static final String CONFIG_SERVICES_PARAM_UNIQUE_CONTAINERNAME = "portletcontainer.uniquename";
    public static final String CONFIG_SERVICES_PARAM_UNIQUE_CONTAINERNAME_DEFAULT = "lutece2Pluto";
    public static final String CONFIG_SERVICES_PARAM_SUPPORTED_PORTLETMODE = "supported.portletmode";
    public static final String CONFIG_SERVICES_PARAM_SUPPORTED_WINDOWSTATE = "supported.windowstate";

    // Properties for log service "org.apache.pluto.portalImpl.services.log.LogService"
    public static final String CONFIG_SERVICES_LOG_DEBUG_ENABLED = "debug.enable";
    public static final Boolean CONFIG_SERVICES_LOG_DEBUG_ENABLED_DEFAULT = Boolean.FALSE;
    public static final String CONFIG_SERVICES_LOG_INFO_ENABLED = "info.enable";
    public static final Boolean CONFIG_SERVICES_LOG_INFO_ENABLED_DEFAULT = Boolean.FALSE;
    public static final String CONFIG_SERVICES_LOG_WARN_ENABLED = "warn.enable";
    public static final Boolean CONFIG_SERVICES_LOG_WARN_ENABLED_DEFAULT = Boolean.FALSE;
    public static final String CONFIG_SERVICES_LOG_ERROR_ENABLED = "error.enable";
    public static final Boolean CONFIG_SERVICES_LOG_ERROR_ENABLED_DEFAULT = Boolean.FALSE;

    // Properties for web & portlet mapping service "org.apache.pluto.portalImpl.services.portletdefinitionregistry.PortletDefinitionRegistryService"
    // Mapping for access and read web.xml deploiement descriptor   
    public static final String CONFIG_SERVICES_PORTLETDEF_WEBXML_MAPPING = "web.mapping";
    public static final String CONFIG_SERVICES_PORTLETDEF_WEBXML_MAPPING_DEFAULT = "WEB-INF/pluto/mapping/servletdefinitionmapping.xml";
    public static final String CONFIG_SERVICES_PORTLETDEF_WEBXML_RESSOURCE = "web.ressource";
    public static final String CONFIG_SERVICES_PORTLETDEF_WEBXML_RESSOURCE_DEFAULT = "WEB-INF/web.xml";

    // Mapping for access and read portlet.xml deploiement descriptor   
    public static final String CONFIG_SERVICES_PORTLETDEF_PORTLETXML_MAPPING = "portlet.mapping";
    public static final String CONFIG_SERVICES_PORTLETDEF_PORTLETXML_MAPPING_DEFAULT = "WEB-INF/pluto/mapping/portletdefinitionmapping.xml";
    public static final String CONFIG_SERVICES_PORTLETDEF_PORTLETXML_RESSOURCE = "portlet.ressource";
    public static final String CONFIG_SERVICES_PORTLETDEF_PORTLETXML_RESSOURCE_DEFAULT = "WEB-INF/portlet.xml";

    // Properties for entity mapping service "org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService"  
    public static final String CONFIG_SERVICES_ENTITYXML_MAPPING = "mapping";
    public static final String CONFIG_SERVICES_ENTITYXML_MAPPING_DEFAULT = "WEB-INF/pluto/mapping/portletentitymapping.xml";
    public static final String CONFIG_SERVICES_ENTITYXML_RESSOURCE = "ressource";
    public static final String CONFIG_SERVICES_ENTITYXML_RESSOURCE_DEFAULT = "WEB-INF/pluto/portletentityregistry.xml";

    /////////////////////////////////////////////////////////////////////////
    // Properties used in Lutece/Pluto connector
    public static final String LUTECEPLUTO_SESSION_PORTLET = "fr.paris.lutece.plugins.jsr168";

    /////////////////////////////////////////////////////////////////////////
    // Properties used in Pluto portal modifications
    public static final String PLUTO_PORTAL_REQUEST_PORTALENV = "fr.paris.lutece.plugins.jsr168.pluto.PortalEnvironment";

    /////////////////////////////////////////////////////////////////////////
    // WEBAPP is used to workaround Pluto search of application web
    // This constant, web application name, must be used in entity XML file. 
    public static final String WEBAPP = "lutece"; // XXX
    public static final String LOG_CATEGORY = "fr.paris.lutece.plugins.jsr168";
    public static final String URL_JSR168_ACTION = "/jsp/site/plugins/jsr168/DoAction.jsp?";
    public static final String URL_LUTECE_PORTAL = "/jsp/site/Portal.jsp?";
}
