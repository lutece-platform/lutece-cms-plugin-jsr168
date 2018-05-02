/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.jsr168.pluto.services.portletdefinitionregistry;

import fr.paris.lutece.plugins.jsr168.pluto.LutecePlutoConstant;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletApplicationDefinitionList;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.portalImpl.om.common.impl.DescriptionImpl;
import org.apache.pluto.portalImpl.om.common.impl.DescriptionSetImpl;
import org.apache.pluto.portalImpl.om.common.impl.DisplayNameImpl;
import org.apache.pluto.portalImpl.om.common.impl.DisplayNameSetImpl;
import org.apache.pluto.portalImpl.om.common.impl.LanguageSetImpl;
import org.apache.pluto.portalImpl.om.common.impl.ParameterImpl;
import org.apache.pluto.portalImpl.om.common.impl.PreferenceImpl;
import org.apache.pluto.portalImpl.om.common.impl.PreferenceSetImpl;
import org.apache.pluto.portalImpl.om.portlet.impl.ContentTypeImpl;
import org.apache.pluto.portalImpl.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.pluto.portalImpl.om.portlet.impl.PortletApplicationDefinitionListImpl;
import org.apache.pluto.portalImpl.om.portlet.impl.PortletDefinitionImpl;
import org.apache.pluto.portalImpl.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.pluto.portalImpl.services.log.Log;
import org.apache.pluto.portalImpl.services.portletdefinitionregistry.PortletDefinitionRegistryService;
import org.apache.pluto.portalImpl.util.Properties;
import org.apache.pluto.portalImpl.xml.XmlParser;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;

import org.w3c.dom.Document;

import org.xml.sax.InputSource;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


/**
 * A simple XML Castor file based implementation of the <code>PortletRegistryService</config>
 * <p>This store persit the PortletRegistry informations</p>
 */
public class PortletDefinitionRegistryServiceFileImpl extends PortletDefinitionRegistryService
{
    // web.xml ressource name (resolved in init() method)    
    private String _webXmlRessource;

    // portlet.xml ressource name (resolved in init() method)    
    private String _portletXmlRessource;

    // Servlet Context
    private ServletContext _servletContext;

    // Helper lists and hashtables to access the data as fast as possible
    // List containing all portlet applications available in the system
    private final PortletApplicationDefinitionListImpl _registry;
    private final Map _mapPortletsKeyObjectId;

    /**
     * Default constructor
     */
    public PortletDefinitionRegistryServiceFileImpl(  )
    {
        _registry = new PortletApplicationDefinitionListImpl(  );
        _mapPortletsKeyObjectId = new HashMap(  );
    }

    /**
         * @see org.apache.pluto.portalImpl.services.Service#init(javax.servlet.ServletConfig, org.apache.pluto.portalImpl.util.Properties)
         */
    public void init( final ServletConfig config, final Properties properties )
        throws Exception
    {
        _servletContext = config.getServletContext(  );

        // get web xml mapping file
        _webXmlRessource = properties.getString( LutecePlutoConstant.CONFIG_SERVICES_PORTLETDEF_WEBXML_RESSOURCE,
                LutecePlutoConstant.CONFIG_SERVICES_PORTLETDEF_WEBXML_RESSOURCE_DEFAULT );

        final String webMapping = properties.getString( LutecePlutoConstant.CONFIG_SERVICES_PORTLETDEF_WEBXML_MAPPING,
                LutecePlutoConstant.CONFIG_SERVICES_PORTLETDEF_WEBXML_MAPPING_DEFAULT );
        final InputStream webMappingStream = _servletContext.getResourceAsStream( webMapping );
        final InputSource webMappingSource = new InputSource( webMappingStream );
        final Mapping mappingWebXml = new Mapping(  );

        try
        {
            mappingWebXml.loadMapping( webMappingSource );
        }
        catch ( Exception e )
        {
            Log.error(  /*LutecePlutoConstant.LOG_CATEGORY,*/
                "Failed to load mapping file " + webMapping, e ); // FIXME
            throw e;
        }

        // get portlet xml mapping file
        _portletXmlRessource = properties.getString( LutecePlutoConstant.CONFIG_SERVICES_PORTLETDEF_PORTLETXML_RESSOURCE,
                LutecePlutoConstant.CONFIG_SERVICES_PORTLETDEF_PORTLETXML_RESSOURCE_DEFAULT );

        final String portletMapping = properties.getString( LutecePlutoConstant.CONFIG_SERVICES_PORTLETDEF_PORTLETXML_MAPPING,
                LutecePlutoConstant.CONFIG_SERVICES_PORTLETDEF_PORTLETXML_MAPPING_DEFAULT );
        final InputStream portletMappingStream = _servletContext.getResourceAsStream( portletMapping );
        final InputSource portletMappingSource = new InputSource( portletMappingStream );
        final Mapping mappingPortletXml = new Mapping(  );

        try
        {
            mappingPortletXml.loadMapping( portletMappingSource );
        }
        catch ( Exception e )
        {
            Log.error(  /*LutecePlutoConstant.LOG_CATEGORY,*/
                "Failed to load mapping file " + portletMapping, e ); // FIXME
            throw e;
        }

        load( mappingWebXml, mappingPortletXml );
        fill(  );
    }

    /**
         * @see org.apache.pluto.portalImpl.services.portletdefinitionregistry.PortletDefinitionRegistryService#getPortletApplicationDefinitionList()
         */
    public PortletApplicationDefinitionList getPortletApplicationDefinitionList(  )
    {
        return _registry;
    }

    /**
         * @see org.apache.pluto.portalImpl.services.portletdefinitionregistry.PortletDefinitionRegistryService#getPortletDefinition(org.apache.pluto.om.common.ObjectID)
         */
    public PortletDefinition getPortletDefinition( final ObjectID id )
    {
        return (PortletDefinition) _mapPortletsKeyObjectId.get( id );
    }

    /**
     * Load <code>web.xml</code> and <code>portlet.xml</code> deploiement descriptor.
     *
     * @param mappingWebXml castor mapping for read <code>web.xml</code>
     * @param mappingPortletXml castor mapping for read <code>portlet.xml</code>
     * @throws Exception for any exception
     */
    private void load( final Mapping mappingWebXml, final Mapping mappingPortletXml )
        throws Exception
    {
        final String webModule = LutecePlutoConstant.WEBAPP;

        Unmarshaller unmarshaller;

        if ( Log.isDebugEnabled(  /*LutecePlutoConstant.LOG_CATEGORY */
             ) )
        {
            Log.debug(  /*LutecePlutoConstant.LOG_CATEGORY,*/
                "Loading the portlet applications XML file..." ); // FIXME
        }

        final InputStream portletXmlIS = _servletContext.getResourceAsStream( _portletXmlRessource );
        final Document portletDocument = XmlParser.parsePortletXml( portletXmlIS );
        unmarshaller = new Unmarshaller( mappingPortletXml );
        unmarshaller.setIgnoreExtraElements( true );
        unmarshaller.setIgnoreExtraAttributes( true );

        final PortletApplicationDefinitionImpl portletApp = (PortletApplicationDefinitionImpl) unmarshaller.unmarshal( portletDocument );

        final InputStream webXmlIS = _servletContext.getResourceAsStream( _webXmlRessource );
        final Document webDocument = XmlParser.parseWebXml( webXmlIS );
        unmarshaller = new Unmarshaller( mappingWebXml );
        unmarshaller.setIgnoreExtraElements( true );
        unmarshaller.setIgnoreExtraAttributes( true );

        final WebApplicationDefinitionImpl webApp = (WebApplicationDefinitionImpl) unmarshaller.unmarshal( webDocument );

        final Vector<Object> structure = new Vector<Object>(  );
        structure.add( portletApp );
        structure.add( "/" + webModule );
        webApp.postLoad( structure );

        // refill structure with necessary information
        webApp.preBuild( structure );
        webApp.postBuild( structure );

        Log.debug(  /*LutecePlutoConstant.LOG_CATEGORY,*/
            webApp.toString(  ) ); // FIXME
        Log.debug(  /*LutecePlutoConstant.LOG_CATEGORY,*/
            portletApp.toString(  ) ); // FIXME

        _registry.add( portletApp );

        if ( Log.isDebugEnabled(  /*LutecePlutoConstant.LOG_CATEGORY */
             ) )
        {
            Log.debug(  /*LutecePlutoConstant.LOG_CATEGORY,*/
                "Dumping content of web.xml..." ); // FIXME
            Log.debug(  /*LutecePlutoConstant.LOG_CATEGORY,*/
                webApp.toString(  ) ); // FIXME

            Log.debug(  /* LutecePlutoConstant.LOG_CATEGORY,*/
                "Dumping content of portlet.xml..." ); // FIXME
            Log.debug(  /*LutecePlutoConstant.LOG_CATEGORY,*/
                portletApp.toString(  ) ); // FIXME
        }
    }

    /**
     * Find all portlet definition from <code>portlet.xml</code> descriptors.
     */
    private void fill(  )
    {
        Iterator iterator = _registry.iterator(  );

        while ( iterator.hasNext(  ) )
        {
            PortletApplicationDefinition papp = (PortletApplicationDefinition) iterator.next(  );

            // fill portletsKeyObjectId
            Iterator portlets = papp.getPortletDefinitionList(  ).iterator(  );

            while ( portlets.hasNext(  ) )
            {
                PortletDefinition portlet = (PortletDefinition) portlets.next(  );
                _mapPortletsKeyObjectId.put( portlet.getId(  ), portlet );
            }
        }
    }
}
