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
/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Pluto", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package fr.paris.lutece.plugins.jsr168.pluto.services.portletentityregistry;

import fr.paris.lutece.plugins.jsr168.pluto.LutecePlutoConstant;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletApplicationEntityList;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.portalImpl.om.entity.impl.PortletApplicationEntityListImpl;
import org.apache.pluto.portalImpl.services.log.Log;
import org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService;
import org.apache.pluto.portalImpl.util.Properties;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


/**
 * A simple XML Castor file based implementation of the <code>PortletEntityRegistryService</config>
 * <p>This store persit the PortletEntityRegistry informations</p>
 */
public class PortletEntityRegistryServiceFileImpl extends PortletEntityRegistryService
{
    // Mapping
    private Mapping _mapping;

    // Servlet Context
    private ServletContext _servletContext;

    // Registry
    private PortletApplicationEntityListImpl _registry;

    // Entity ressource name (resolved in init() method)    
    private String _strEntityRessource;

    // Helper lists and hashtables to access the data as fast as possible
    private final Map _mapPortletEntitiesKeyObjectID;

    /**
     * Default constructor
     */
    public PortletEntityRegistryServiceFileImpl(  )
    {
        _mapPortletEntitiesKeyObjectID = new HashMap(  );
    }

    /**
         * @see org.apache.pluto.portalImpl.services.Service#init(javax.servlet.ServletConfig, org.apache.pluto.portalImpl.util.Properties)
         */
    public void init( final ServletConfig servletConfig, final Properties properties )
        throws Exception
    {
        _servletContext = servletConfig.getServletContext(  );

        _strEntityRessource = properties.getString( LutecePlutoConstant.CONFIG_SERVICES_ENTITYXML_RESSOURCE,
                LutecePlutoConstant.CONFIG_SERVICES_ENTITYXML_RESSOURCE_DEFAULT );

        final String entityMapping = properties.getString( LutecePlutoConstant.CONFIG_SERVICES_ENTITYXML_MAPPING,
                LutecePlutoConstant.CONFIG_SERVICES_ENTITYXML_MAPPING_DEFAULT );
        final InputStream entityMappingStream = _servletContext.getResourceAsStream( entityMapping );
        final InputSource entityMappingSource = new InputSource( entityMappingStream );

        _mapping = new Mapping(  );

        try
        {
            _mapping.loadMapping( entityMappingSource );
        }
        catch ( Exception e )
        {
            Log.error(  /*LutecePlutoConstant.LOG_CATEGORY,*/
                "Failed to load mapping file " + _mapping, e ); // FIXME
            throw e;
        }

        load(  );
    }

    /**
         * @see org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService#getPortletApplicationEntityList()
         */
    public PortletApplicationEntityList getPortletApplicationEntityList(  )
    {
        return _registry;
    }

    /**
         * @see org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService#getPortletEntity(org.apache.pluto.om.common.ObjectID)
         */
    public PortletEntity getPortletEntity( ObjectID id )
    {
        return (PortletEntity) _mapPortletEntitiesKeyObjectID.get( id.toString(  ) );
    }

    /**
         * @see org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService#store()
         */
    public void store(  ) throws IOException
    {
        String filename = _strEntityRessource;

        File f = new File( filename );

        if ( !f.isAbsolute(  ) )
        {
            filename = _servletContext.getRealPath( filename );
        }

        FileWriter writer = new FileWriter( filename );

        try
        {
            final Marshaller marshaller = new Marshaller( writer );

            marshaller.setMapping( _mapping );

            _registry.preStore( null );

            marshaller.marshal( _registry );

            _registry.postStore( null );
        }
        catch ( final MappingException e )
        {
            Log.error( LutecePlutoConstant.LOG_CATEGORY, e );
            throw new IOException( e.toString(  ) );
        }
        catch ( final ValidationException e )
        {
            Log.error( LutecePlutoConstant.LOG_CATEGORY, e );
            throw new IOException( e.toString(  ) );
        }
        catch ( final MarshalException e )
        {
            Log.error( LutecePlutoConstant.LOG_CATEGORY, e );
            throw new IOException( e.toString(  ) );
        }
        catch ( final Exception e )
        {
            Log.error( LutecePlutoConstant.LOG_CATEGORY, e );
            throw new IOException( e.toString(  ) );
        }
    }

    /**
         * @see org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService#load()
         */
    public void load(  ) throws IOException
    {
        internalLoad(  );

        if ( Log.isDebugEnabled(  /*LutecePlutoConstant.LOG_CATEGORY*/
             ) )
        {
            Log.debug(  /*LutecePlutoConstant.LOG_CATEGORY,*/
                "Dumping complete object model description as it is read from the xml file..." ); // FIXME
            Log.debug(  /*LutecePlutoConstant.LOG_CATEGORY,*/
                _registry.toString(  ) ); // FIXME
        }

        fill(  );
    }

    /**
         * @see org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService#refresh(org.apache.pluto.om.entity.PortletEntity)
         */
    public void refresh( PortletEntity portletEntity )
    {
        _mapPortletEntitiesKeyObjectID.put( portletEntity.getId(  ).toString(  ), portletEntity );
    }

    /**
     * Load portlet entity file
     *
     * @throws IOException For file access exception or any other error
     */
    private void internalLoad(  ) throws IOException
    {
        final String filename = _strEntityRessource;
        final InputStream fileStream = _servletContext.getResourceAsStream( filename );
        final InputSource fileSource = new InputSource( fileStream );

        try
        {
            final Unmarshaller unmarshaller = new Unmarshaller( _mapping );
            _registry = (PortletApplicationEntityListImpl) unmarshaller.unmarshal( fileSource );
            _registry.postLoad( null );
            _registry.preBuild( null );
            _registry.postBuild( null );
        }
        catch ( MappingException e )
        {
            Log.error( LutecePlutoConstant.LOG_CATEGORY, e );
            throw new IOException( e.toString(  ) );
        }
        catch ( ValidationException e )
        {
            Log.error( LutecePlutoConstant.LOG_CATEGORY, e );
            throw new IOException( e.toString(  ) );
        }
        catch ( MarshalException e )
        {
            Log.error( LutecePlutoConstant.LOG_CATEGORY, e );
            throw new IOException( e.toString(  ) );
        }
        catch ( Exception e )
        {
            Log.error( LutecePlutoConstant.LOG_CATEGORY, e );
            throw new IOException( e.toString(  ) );
        }
    }

    /**
     * Agregate all entities definitions (only one for Lutece/Pluto)
     */
    private void fill(  )
    {
        final Iterator iterator = _registry.iterator(  );

        while ( iterator.hasNext(  ) )
        {
            final PortletApplicationEntity appInst = (PortletApplicationEntity) iterator.next(  );

            // fill portletEntitiesKeyObjectID
            final Iterator portlets = appInst.getPortletEntityList(  ).iterator(  );

            while ( portlets.hasNext(  ) )
            {
                final PortletEntity portletInst = (PortletEntity) portlets.next(  );
                _mapPortletEntitiesKeyObjectID.put( portletInst.getId(  ).toString(  ), portletInst );
            }
        }
    }

    /**
     * Return portlet entities defined for this web application
     *
     * @return portlet entities defined for this web application
     */
    public Map getPortletEntities(  )
    {
        return _mapPortletEntitiesKeyObjectID;
    }
}
