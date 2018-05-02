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
package fr.paris.lutece.plugins.jsr168.pluto.services.factorymanager;

import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.pluto.factory.Factory;
import org.apache.pluto.portalImpl.services.factorymanager.FactoryManagerService;
import org.apache.pluto.portalImpl.util.Properties;
import org.apache.pluto.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;


/**
 * Manages the life-time of factories registered during container startup.
 * A service has to derive from {@link Factory} and implement the
 * <CODE>init()</CODE> and <CODE>destroy()</CODE> methods as appropriate.
 *
 * @see Factory
 */
public class FactoryManagerServiceImpl extends FactoryManagerService implements org.apache.pluto.services.factory.FactoryManagerService
{
    private Map _mapFactory = new HashMap(  );
    private List _lstFactory = new LinkedList(  );

    /**
     * Initializes all factories specified in the configuration beginning with 'factory.'.
     * By specifying a different implementation of the factory the behaviour
     * of the portlet container can be modified.
     *
     * @param config
     *          the servlet configuration
     * @param aProperties Defined properties for the factory manager
     * @exception    Exception
     *               if initializing any of the factories fails
     */
    public void init( ServletConfig config, Properties aProperties )
        throws Exception
    {
        logInfo( "FactoryManager: Loading factories..." );

        final Map factoryImpls = new HashMap(  );
        final Map factoryProps = new HashMap(  );
        final Iterator configNames = aProperties.keys(  );
        String lastFactoryInterfaceName = null;

        while ( configNames.hasNext(  ) )
        {
            final String configName = (String) configNames.next(  );

            // NOTE: it's not clean!
            // Properties must appear after definition of classes in the XML config file.  
            if ( ( lastFactoryInterfaceName != null ) && configName.startsWith( lastFactoryInterfaceName + "." ) )
            {
                String propertyName = configName.substring( lastFactoryInterfaceName.length(  ) + 1 );
                String propertyValue = aProperties.getString( configName );
                Map properties = (Map) factoryProps.get( lastFactoryInterfaceName );
                properties.put( propertyName, propertyValue );
            }
            else
            {
                String factoryInterfaceName = configName;
                String factoryImplName = aProperties.getString( configName );
                factoryImpls.put( factoryInterfaceName, factoryImplName );
                factoryProps.put( factoryInterfaceName, new HashMap(  ) );

                // remember interface name to get all properties
                lastFactoryInterfaceName = factoryInterfaceName;
            }
        }

        int numAll = 0;

        for ( Iterator iter = factoryImpls.entrySet(  ).iterator(  ); iter.hasNext(  ); )
        {
            Map.Entry implEntry = (Map.Entry) iter.next(  );
            String factoryInterfaceName = (String) implEntry.getKey(  );
            String factoryImplName = (String) implEntry.getValue(  );

            ++numAll;

            // try to get hold of the factory
            final Class factoryInterface;

            try
            {
                factoryInterface = Class.forName( factoryInterfaceName );
            }
            catch ( ClassNotFoundException e )
            {
                logError( "FactoryManager: A factory with name " + factoryInterfaceName + " cannot be found.", e );

                continue;
            }

            try
            {
                Class factoryImpl = Class.forName( factoryImplName );
                Factory factory = (Factory) factoryImpl.newInstance(  );
                Map props = (Map) factoryProps.get( factoryInterfaceName );

                logInfo( StringUtils.nameOf( factoryInterface ) + " initializing..." );
                factory.init( config, props );
                logInfo( StringUtils.nameOf( factoryInterface ) + " done." );

                _mapFactory.put( factoryInterface, factory );

                // build up list in reverse order for later destruction
                _lstFactory.add( 0, factory );
            }
            catch ( ClassNotFoundException e )
            {
                logError( "FactoryManager: A factory implementation with name " + factoryImplName +
                    " cannot be found.", e );
                throw e;
            }
            catch ( ClassCastException e )
            {
                logError( "FactoryManager: Factory implementation " + factoryImplName +
                    " is not a factory of the required type.", e );
                throw e;
            }
            catch ( InstantiationException e )
            {
                logError( "FactoryManager: Factory implementation " + factoryImplName + " cannot be instantiated.", e );
                throw e;
            }
            catch ( Exception e )
            {
                logError( "FactoryManager: An unidentified error occurred", e );
                throw e;
            }
        }

        logInfo( "FactoryManager: Factories initialized (" + numAll + " successful)." );
    }

    /**
     ** Destroys all services.
     **
     ** @param   config
     **          the servlet configuration
     **/
    public void destroy( ServletConfig config )
    {
        // destroy the services in reverse order
        for ( Iterator iterator = _lstFactory.iterator(  ); iterator.hasNext(  ); )
        {
            Factory factory = (Factory) iterator.next(  );

            try
            {
                factory.destroy(  );
            }
            catch ( Exception e )
            {
                logError( "FactoryManager: Factory couldn't be destroyed.", e );
            }
        }

        _mapFactory.clear(  );
        _lstFactory.clear(  );
    }

    /**
     ** Returns the service implementation for the given service class, or
     ** <code>null</code> if no such service is registered.
     **
     ** @param   theClass
     **          the service class
     **
     ** @return   the service implementation
     **/
    public Factory getFactory( Class theClass )
    {
        // at this state the services map is read-only,
        // therefore we can go without synchronization
        return ( (Factory) _mapFactory.get( theClass ) );
    }

    /**
     * Log <code>strMessage</code> as info
     *
     * @param strMessage The message to log
     */
    private void logInfo( String strMessage )
    {
        AppLogService.info( strMessage );
    }

    /**
     * Log <code>message</code> as error
     *
     * @param message The message to log
     * @param t The cause exception
     */
    private void logError( Object message, Throwable t )
    {
        AppLogService.error( message, t );
    }
}
