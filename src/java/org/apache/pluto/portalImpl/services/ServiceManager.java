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

 */
package org.apache.pluto.portalImpl.services;

import fr.paris.lutece.plugins.jsr168.pluto.LutecePlutoConstant;
import fr.paris.lutece.plugins.jsr168.pluto.exception.ContainerInitLutecePlutoException;
import fr.paris.lutece.plugins.jsr168.pluto.xml.ServiceXML;
import fr.paris.lutece.plugins.jsr168.pluto.xml.ServicesXML;
import fr.paris.lutece.plugins.jsr168.pluto.xml.XMLFactory;
import fr.paris.lutece.plugins.jsr168.pluto.xml.XMLFactoryException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.pluto.portalImpl.util.Properties;
import org.apache.pluto.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


/**
 * Manages the life-time of services registered during servlet startup.
 * A service has to derive from {@link ContainerService} and implement the
 * <CODE>init()</CODE> and <CODE>destroy()</CODE> methods as appropriate.
 *
 * <P>
 * By registering the service and its implementation in the file
 * <CODE>/config/services.properties</CODE>, the service will become
 * available to the portal engine. The format of the file is simple:
 *
 * <PRE>
 *   org.apache.pluto.portalImpl.services.log.LogService = org.apache.pluto.portalImpl.services.log.LogServicesImpl
 * </PRE>
 *
 * Each entry represents one service. The left-hand side is the abstract
 * service class, the right-hand side is the implementation of this service.
 * The services are initialized in the order of appearance.
 *
 * <P>
 * Each service can have its own configuration file, located in
 * <CODE>/config/services</CODE>. It has to have the name of either
 * implementation or abstract class of the service, without the
 * leading package name. For example, the service manager looks
 * for <CODE>LogServiceImpl.properties</CODE>. This allows a special
 * implementation to provide different configuration than the
 * general (abstract) service requires.
 *
 * <P>
 * If present, one of the services configuration files is loaded
 * and passed to the service as {@link org.apache.pluto.portalImpl.util.Properties}
 * object. Not providing a service configuration file is okay too,
 * in that case the properties are empty.
 *
 * @see ContainerService
 */
public class ServiceManager
{
    private static volatile boolean _bInitialized = false;
    final private static Map _mapServices = new HashMap(  );
    final private static List _lstServices = new LinkedList(  );

    /**
     ** Initializes all services specified in <CODE>services.properties</CODE>.
     ** By specifying a different implementation of the service the behaviour
     ** of the portal can be modified.
     **
     ** @param   aConfig
     **          the servlet configuration
     **
     ** @exception    Exception
     **               if loading <CODE>services.properties</CODE>
     **               or initializing any of its contained services fails
     **/
    public static void init( ServletConfig aConfig ) throws Exception
    {
        init( aConfig, AppPropertiesService.getProperty( LutecePlutoConstant.PROPERTY_FILE_SERVICES ) );
    }

    /**
     ** Initializes all services specified in <CODE>services.properties</CODE>.
     ** By specifying a different implementation of the service the behaviour
     ** of the portal can be modified.
     **
     ** @param   config
     **          the servlet configuration
     ** @param   serviceConfigFile
     **          The location of <CODE>services.properties</CODE> (relative to classpath)
     **
     ** @exception    Exception
     **               if loading <CODE>services.properties</CODE>
     **               or initializing any of its contained services fails
     **/
    public static void init( ServletConfig config, String serviceConfigFile )
        throws ContainerInitLutecePlutoException
    {
        if ( config == null )
        {
            throw new NullPointerException( "Parameter 'config' cannot be null." );
        }

        final ServletContext context = config.getServletContext(  );

        if ( context == null )
        {
            throw new NullPointerException( "config.getServletContext() cannot sennd a null value." );
        }

        // avoid duplicate initialization of services
        if ( _bInitialized )
        {
            return;
        }

        synchronized ( ServiceManager.class )
        {
            if ( !_bInitialized )
            {
                _bInitialized = true;
            }
            else
            {
                return;
            }
        }

        AppLogService.info( "Lutece/Pluto[ServiceManager] Loading services..." );

        final ServicesXML servicesXML;

        try
        {
            servicesXML = XMLFactory.loadServicesXML( context, serviceConfigFile );
        }
        catch ( XMLFactoryException e )
        {
            throw new ContainerInitLutecePlutoException( "ServiceManager: can't read services configuration (file '" +
                serviceConfigFile + "').", e );
        }

        int numAll = 0;
        int numSuccessful = 0;

        for ( Iterator it = servicesXML.getServices(  ).iterator(  ); it.hasNext(  ); )
        {
            final ServiceXML serviceXML = (ServiceXML) it.next(  );

            ++numAll;

            final String serviceBaseName = serviceXML.getServiceBase(  );

            // try to get hold of the base service
            final Class serviceBase;

            try
            {
                serviceBase = Class.forName( serviceBaseName );
            }
            catch ( ClassNotFoundException e )
            {
                AppLogService.info( "Lutece/Pluto[ServiceManager] can't find base class '" + serviceBaseName + "'." );

                continue;
            }

            final String serviceImplName = serviceXML.getImplementation(  );
            final Service service;

            try
            {
                final Class serviceImpl = Class.forName( serviceImplName );
                service = (Service) serviceImpl.newInstance(  );
            }
            catch ( ClassNotFoundException e )
            {
                AppLogService.info( "Lutece/Pluto[ServiceManager] can't find service implementation class '" +
                    serviceImplName + "'." );

                continue;
            }
            catch ( ClassCastException e )
            {
                AppLogService.info( "Lutece/Pluto[ServiceManager] class '" + serviceImplName +
                    "' isn't a service (base class must be '" + Service.class.getName(  ) + "')." );

                continue;
            }
            catch ( IllegalAccessException e )
            {
                AppLogService.info( 
                    "Lutece/Pluto[ServiceManager] no public access to empty constructor in service class '" +
                    serviceImplName + "'." );

                continue;
            }
            catch ( InstantiationException e )
            {
                AppLogService.info( "Lutece/Pluto[ServiceManager] can't instanciate service class '" + serviceImplName +
                    "'." );

                continue;
            }

            try
            {
                final Properties serviceProps = serviceXML.getProperties(  );

                AppLogService.info( "Lutece/Pluto[ServiceManager] " + StringUtils.nameOf( serviceBase ) +
                    " initializing..." );
                service.init( config, serviceProps );
                AppLogService.info( "Lutece/Pluto[ServiceManager] " + StringUtils.nameOf( serviceBase ) + " done." );
            }
            catch ( Exception e )
            {
                AppLogService.error( "Lutece/Pluto[ServiceManager] " + StringUtils.nameOf( serviceBase ) +
                    " exception occured (" + e.getMessage(  ) + ").", e );

                continue;
            }

            _mapServices.put( serviceBase, service );

            // build up list in reverse order for later destruction
            _lstServices.add( 0, service );

            ++numSuccessful;
        }

        if ( numSuccessful != numAll )
        {
            AppLogService.info( "Lutece/Pluto[ServiceManager] Services initialized (" + numSuccessful + "/" + numAll +
                " successful)." );
            throw new ContainerInitLutecePlutoException( "ServiceManager: Services initialized (" + numSuccessful +
                "/" + numAll + " successful)." );
        }
        else
        {
            AppLogService.info( "Lutece/Pluto[ServiceManager] Services initialized (" + numSuccessful + "/" + numAll +
                " successful)." );
        }
    }

    /**
     * Calls post init for all services
     *
     * @param   aConfig
     *         the servlet configuration
     **/
    public static void postInit( ServletConfig aConfig )
        throws ContainerInitLutecePlutoException
    {
        // avoid duplicate destruction of services
        if ( !_bInitialized )
        {
            return;
        }

        /*
        synchronized (ServiceManager.class)
        {
            if (_bInitialized)
            {
                 _bInitialized = false;
            }
            else
            {
                return;
            }
        }
        */

        // post init all services
        int numSuccessful = 0;
        final int numAll = _lstServices.size(  );

        for ( Iterator iterator = _lstServices.iterator(  ); iterator.hasNext(  ); )
        {
            final Service service = (Service) iterator.next(  );

            try
            {
                service.postInit( aConfig );
                ++numSuccessful;
            }
            catch ( Exception e )
            {
                AppLogService.error( "Lutece/Pluto[ServiceManager] exception occured for '" +
                    service.getClass(  ).getName(  ) + "' postInit phase (" + e.getMessage(  ) + ").", e );
            }
        }

        if ( numSuccessful != numAll )
        {
            AppLogService.info( "Lutece/Pluto[ServiceManager] Services post-initialized (" + numSuccessful + "/" +
                numAll + " successful)." );
            throw new ContainerInitLutecePlutoException( "ServiceManager: Services post-initialized (" + numSuccessful +
                "/" + numAll + " successful)." );
        }
        else
        {
            AppLogService.info( "Lutece/Pluto[ServiceManager] Services post-initialized (" + numSuccessful + "/" +
                numAll + " successful)." );
        }
    }

    /**
     ** Destroys all services.
     **
     ** @param   aConfig
     **          the servlet configuration
     **/
    public static void destroy( ServletConfig aConfig )
    {
        // avoid duplicate destruction of services
        if ( !_bInitialized )
        {
            return;
        }

        synchronized ( ServiceManager.class )
        {
            if ( _bInitialized )
            {
                _bInitialized = false;
            }
            else
            {
                return;
            }
        }

        ServletContext context = null;

        if ( aConfig != null )
        {
            context = aConfig.getServletContext(  );
        }

        // destroy the services in reverse order
        for ( Iterator iterator = _lstServices.iterator(  ); iterator.hasNext(  ); )
        {
            Service service = (Service) iterator.next(  );

            try
            {
                service.destroy( aConfig );
            }
            catch ( Exception e )
            {
                AppLogService.error( "Lutece/Pluto[ServiceManager] Service '" + service.getClass(  ).getName(  ) +
                    "' couldn't be destroyed.", e );
            }
        }

        _lstServices.clear(  );
        _mapServices.clear(  );
    }

    /**
     ** Returns the service implementation for the given service class, or
     ** <CODE>null</CODE> if no such service is registered.
     **
     ** @param   aClass
     **          the service class
     **
     ** @return   the service implementation
     **/
    public static Service getService( Class aClass )
    {
        // at this state the services map is read-only,
        // therefore we can go without synchronization
        return ( (Service) _mapServices.get( aClass ) );
    }
}
