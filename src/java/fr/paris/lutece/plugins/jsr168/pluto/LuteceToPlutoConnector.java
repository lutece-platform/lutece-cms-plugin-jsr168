/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import fr.paris.lutece.plugins.jsr168.pluto.core.PortalEnvironment;
import fr.paris.lutece.plugins.jsr168.pluto.core.PortalURL;
import fr.paris.lutece.plugins.jsr168.pluto.core.PortletContainerEnvironment;
import fr.paris.lutece.plugins.jsr168.pluto.servlet.ServletRequestImpl;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import org.apache.pluto.PortletContainer;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletApplicationDefinitionList;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.apache.pluto.om.window.PortletWindowCtrl;
import org.apache.pluto.portalImpl.core.PortletContainerFactory;
import org.apache.pluto.portalImpl.factory.FactoryAccess;
import org.apache.pluto.portalImpl.om.window.impl.PortletWindowImpl;
import org.apache.pluto.portalImpl.services.ServiceManager;
import org.apache.pluto.portalImpl.services.config.Config;
import org.apache.pluto.portalImpl.services.factorymanager.FactoryManager;
import org.apache.pluto.portalImpl.services.log.Log;
import org.apache.pluto.portalImpl.services.portletdefinitionregistry.PortletDefinitionRegistry;
import org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistry;

import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.portlet.PortletRequest;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Main access point for connect Lutece container with Pluto container
 */
public final class LuteceToPlutoConnector
{
    /**
     * The initialization isn't launched
     */
    public static final int STATE_INIT_NOTYET = 0;

    /**
     * The initialization is working
     */
    public static final int STATE_INIT_WORKING = 1;

    /**
     * Value greater than <code>STATE_INIT_DONE</code> means: initialization
     * is done, but this don't inform about success or faillure.
     */
    public static final int STATE_INIT_DONE = 2;

    /**
     * The initialization is done with success
     */
    public static final int STATE_INIT_DONE_OK = 3;

    /**
     * The initialization is done, but in faillure
     */
    public static final int STATE_INIT_DONE_KO = 4;

    /**
     * The init current state, possibles values are:<br>
     * <li><code>STATE_INIT_NOTYET</code>
     * <li><code>STATE_INIT_WORKING</code>
     * <li><code>STATE_INIT_DONE_OK</code>
     * <li><code>STATE_INIT_DONE_KO</code>
     */
    private static int _nStateInit = STATE_INIT_NOTYET;

    /**
     * Pluto's main container
     */
    private static PortletContainer _portletContainer;

    /**
     * Utility classes have no constructor
     */
    private LuteceToPlutoConnector(  )
    {
    }

    /**
     * Initialize Pluto container
     *
     * @param config The <code>ServletConfig</code> of the web application
     * @return <code>true</code> if all is ok
     */
    private static boolean init( ServletConfig config )
    {
        // Check if init isn't done
        if ( _nStateInit == STATE_INIT_NOTYET )
        {
            synchronized ( LuteceToPlutoConnector.class )
            {
                _nStateInit = STATE_INIT_WORKING;

                try
                {
                    // Init Lutece/Pluto connector: ServiceManager (services access for Pluto)
                    ServiceManager.init( config );
                    ServiceManager.postInit( config );

                    // Read Pluto's "unique container name" 
                    String strContainerNameParam = LutecePlutoConstant.CONFIG_SERVICES_PARAM_UNIQUE_CONTAINERNAME;
                    String strContainerNameParamDefault = LutecePlutoConstant.CONFIG_SERVICES_PARAM_UNIQUE_CONTAINERNAME_DEFAULT;
                    String strUniqueContainerName = Config.getParameters(  )
                                                          .getString( strContainerNameParam,
                            strContainerNameParamDefault );

                    // Initialize the environement 
                    PortletContainerEnvironment environment = new PortletContainerEnvironment(  );
                    environment.addContainerService( Log.getService(  ) );
                    environment.addContainerService( FactoryManager.getService(  ) );
                    environment.addContainerService( FactoryAccess.getInformationProviderContainerService(  ) );

                    // No special properties to transmit
                    Properties properties = new Properties(  );

                    _portletContainer = PortletContainerFactory.getPortletContainer(  );
                    _portletContainer.init( strUniqueContainerName, config, environment, properties );

                    if ( _portletContainer.isInitialized(  ) )
                    {
                        _nStateInit = STATE_INIT_DONE_OK;
                    }
                    else
                    {
                        _nStateInit = STATE_INIT_DONE_KO;
                    }
                }
                catch ( Throwable t )
                {
                    _nStateInit = STATE_INIT_DONE_KO;
                    AppLogService.error( AppPropertiesService.getProperty( LutecePlutoConstant.PROPERTY_LOG_PREFIX ) +
                        AppPropertiesService.getProperty( LutecePlutoConstant.PROPERTY_LOG_INITIALIZATION_FAIL ), t );

                    // TODO
                    // For initialization in servlets container (in function init(ServletConfig))
                    // the function will throw an UnavailableException(". . ."): the portlet 
                    // must become "unavailable" if permanent is set to true
                }
            }
        }

        // return true if init was a success
        return ( _nStateInit == STATE_INIT_DONE_OK );
    }

    /**
     * TODO Not implemented: must be called (and coded) for shutdown Pluto
     * container.
     */
    public static void shutdown(  )
    {
    }

    /**
     * Render the portlet fragment
     *
     * @param nPortletID Lutece portlet ID
     * @param strPortletName JSR 168 portlet name (ID)
     * @return the portlet fragment page
     */
    public static String render( int nPortletID, String strPortletName )
    {
        // Restore main parameter from the ThreadLocal
        ServletConfig config = LocalVariables.getConfig(  );
        HttpServletRequest request = LocalVariables.getRequest(  );
        HttpServletResponse response = LocalVariables.getResponse(  );

        // Initialize pluto if isn't done
        if ( !init( config ) )
        {
            return AppPropertiesService.getProperty( LutecePlutoConstant.PROPERTY_PROBLEM_INITIALIZATION );
        }

        AppLogService.debug( "JSR168 / BEGIN Render porlet (lutece ID [" + nPortletID + "]; portlet name [" +
            strPortletName + "]" );

        // Retrieve the portlet window
        PlutoSession plutoSession = PlutoSession.findSession( request );
        PortletWindowImpl portletWindow = plutoSession.getPortletWindow( String.valueOf( nPortletID ) );

        // Wrap request and response
        HttpServletRequest luteceRequest = new ServletRequestImpl( request, portletWindow );
        LuteceHttpServletResponse luteceResponse = new LuteceHttpServletResponse( response );

        String strPortletEntityId = LutecePlutoConstant.WEBAPP + "." + strPortletName;

        ObjectID objectID = org.apache.pluto.portalImpl.util.ObjectID.createFromString( strPortletEntityId );
        PortletEntity portletEntity = PortletEntityRegistry.getPortletEntity( objectID );
        ( (PortletWindowCtrl) portletWindow ).setPortletEntity( portletEntity );

        try
        {
            PortalEnvironment portalEnvironment = new PortalEnvironment( config, luteceRequest, luteceResponse,
                    String.valueOf( nPortletID ) );

            // Needed since we want to avoid side effect in PortalEnvironment constructor
            portalEnvironment.initPortalEnvironment(  );

            _portletContainer.portletLoad( portletWindow, luteceRequest, luteceResponse );
            _portletContainer.renderPortlet( portletWindow, luteceRequest, luteceResponse );

            String strData = luteceResponse.getBufferString(  );

            return strData;
        }
        catch ( Throwable e )
        {
            AppLogService.error( AppPropertiesService.getProperty( LutecePlutoConstant.PROPERTY_LOG_PREFIX ) +
                " exception " + e.getClass(  ).getName(  ) + ": " + e.getMessage(  ), e );
        }

        return AppPropertiesService.getProperty( LutecePlutoConstant.PROPERTY_PROBLEM_RENDER );
    }

    /**
     * Realise an action (render or action)
     *
     * @param nPortletId Lutece portlet ID
     * @param strPortletName JSR 168 portlet name (ID)
     * @return <code>true</code> for a normal processing, <code>false</code> for the need to send a redirect to the client.
     */
    public static boolean request( int nPortletId, String strPortletName )
    {
        // Restore main parameters from the ThreadLocal
        HttpServletRequest request = LocalVariables.getRequest(  );

        if ( PortalURL.isActionURL( request ) )
        {
            return LuteceToPlutoConnector.requestAction( nPortletId, strPortletName );
        }

        return LuteceToPlutoConnector.requestRender( nPortletId );
    }

    /**
     * Return titles list of defined portlets
     *
     * @return titles list of defined portlets
     */
    public static ReferenceList getPortletTitles(  )
    {
        // Restore main parameter from the ThreadLocal
        ServletConfig config = LocalVariables.getConfig(  );
        HttpServletRequest request = LocalVariables.getRequest(  );

        ReferenceList result = new ReferenceList(  );

        if ( !init( config ) )
        {
            return result;
        }

        String webapp = LutecePlutoConstant.WEBAPP;
        ObjectID webappObjectID = org.apache.pluto.portalImpl.util.ObjectID.createFromString( webapp );
        PortletApplicationDefinitionList applicationDefinitionList = PortletDefinitionRegistry.getPortletApplicationDefinitionList(  );
        PortletApplicationDefinition applicationDefinition = applicationDefinitionList.get( webappObjectID );
        PortletDefinitionList portletDefinitionList = applicationDefinition.getPortletDefinitionList(  );

        Iterator it = portletDefinitionList.iterator(  );

        while ( it.hasNext(  ) )
        {
            PortletDefinition portletDefinition = (PortletDefinition) it.next(  );
            String strName = portletDefinition.getName(  );

            DisplayName displayNameObj = portletDefinition.getDisplayName( request.getLocale(  ) );

            if ( displayNameObj == null )
            {
                displayNameObj = portletDefinition.getDisplayName( Locale.ENGLISH );
            }

            String strDisplayName;

            if ( displayNameObj != null )
            {
                strDisplayName = displayNameObj.getDisplayName(  );
            }
            else
            {
                strDisplayName = strName;
            }

            ReferenceItem refItem = new ReferenceItem(  );
            refItem.setCode( strName );
            refItem.setName( strDisplayName );
            result.add( refItem );
        }

        return result;
    }

    /**
     * Return list of buttons that must be displayed on the portlet titlebar.
     *
     * @param nPortletID Lutece portlet ID
     * @param strPortletName JSR 168 portlet name (ID)
     * @return Return button defined for this
     */
    public static Buttons getButtons( int nPortletID, String strPortletName )
    {
        // Restore main parameter from the ThreadLocal
        ServletConfig config = LocalVariables.getConfig(  );
        HttpServletRequest request = LocalVariables.getRequest(  );
        HttpServletResponse response = LocalVariables.getResponse(  );

        final Buttons buttons = new Buttons(  );

        if ( init( config ) )
        {
            // Retrieve the portlet window
            PlutoSession plutoSession = PlutoSession.findSession( request );
            PortletWindowImpl portletWindow = plutoSession.getPortletWindow( String.valueOf( nPortletID ) );

            String strPortletEntityId = LutecePlutoConstant.WEBAPP + "." + strPortletName;
            ObjectID objectID = org.apache.pluto.portalImpl.util.ObjectID.createFromString( strPortletEntityId );
            PortletEntity portletEntity = PortletEntityRegistry.getPortletEntity( objectID );
            ( (PortletWindowCtrl) portletWindow ).setPortletEntity( portletEntity );

            buttons.init( request, response, portletEntity.getPortletDefinition(  ), portletWindow );
        }

        return buttons;
    }

    /**
     * Realise an action
     *
     * @param nPortletID Lutece portlet ID
     * @param strPortletName JSR 168 portlet name (ID)
     * @return <code>true</code> for a normal processing, <code>false</code> for the need to send a redirect to the client.
     */
    private static boolean requestAction( int nPortletID, String strPortletName )
    {
        // Restore main parameters from the ThreadLocal
        ServletConfig config = LocalVariables.getConfig(  );
        HttpServletRequest request = LocalVariables.getRequest(  );
        HttpServletResponse response = LocalVariables.getResponse(  );

        if ( !init( config ) )
        {
            return false;
        }

        AppLogService.debug( "JSR168 / BEGIN Action porlet (lutece ID [" + nPortletID + "]; portlet name [" +
            strPortletName + "]" );

        // Map all Lutece user info
        prepareUserInfo( request );

        // Retrieve the portlet window
        PlutoSession plutoSession = PlutoSession.findSession( request );
        PortletWindowImpl portletWindow = plutoSession.getPortletWindow( String.valueOf( nPortletID ) );

        PortalURL portalURL = new PortalURL( request );

        portalURL.fillWindowPortlet( portletWindow );

        String strPortletEntityId = LutecePlutoConstant.WEBAPP + "." + strPortletName;
        ObjectID objectID = org.apache.pluto.portalImpl.util.ObjectID.createFromString( strPortletEntityId );
        PortletEntity portletEntity = PortletEntityRegistry.getPortletEntity( objectID );
        ( (PortletWindowCtrl) portletWindow ).setPortletEntity( portletEntity );

        HttpServletRequest luteceRequest = new ServletRequestImpl( request, portletWindow );
        LuteceHttpServletResponse luteceResponse = new LuteceHttpServletResponse( response );

        try
        {
            PortalEnvironment portalEnvironment = new PortalEnvironment( config, luteceRequest, luteceResponse,
                    String.valueOf( nPortletID ) );

            // Needed since we want to avoid side effect in PortalEnvironment constructor
            portalEnvironment.initPortalEnvironment(  );

            _portletContainer.portletLoad( portletWindow, luteceRequest, luteceResponse );
            _portletContainer.processPortletAction( portletWindow, luteceRequest, luteceResponse );
        }
        catch ( Throwable e )
        {
            AppLogService.error( AppPropertiesService.getProperty( LutecePlutoConstant.PROPERTY_LOG_PREFIX ) +
                " exception " + e.getClass(  ).getName(  ) + ": " + e.getMessage(  ), e );
        }

        return true;
    }

    /**
     * Realise a render action (change mode and state)
     *
     * @param nPortletID Lutece portlet ID
     * @return <code>true</code> for a normal processing, <code>false</code> for the need to send a redirect to the client.
     */
    private static boolean requestRender( int nPortletID )
    {
        // Restore main parameters from the ThreadLocal
        ServletConfig config = LocalVariables.getConfig(  );
        HttpServletRequest request = LocalVariables.getRequest(  );

        if ( !init( config ) )
        {
            return false;
        }

        // Map all Lutece user info
        prepareUserInfo( request );

        // Retrieve the portlet window
        PlutoSession plutoSession = PlutoSession.findSession( request );
        PortletWindowImpl portletWindow = plutoSession.getPortletWindow( String.valueOf( nPortletID ) );

        PortalURL portalURL = new PortalURL( request );

        portalURL.fillWindowPortlet( portletWindow );

        return false;
    }

    /**
     * Initialize user informations for current portlet
     *
     * @param request The HTTP request
     */
    private static void prepareUserInfo( HttpServletRequest request )
    {
        LuteceUser luteceUser = SecurityService.getInstance(  ).getRegisteredUser( request );

        if ( luteceUser != null )
        {
            Map mapUserInfos = luteceUser.getUserInfos(  );
            Map cstMapUserInfos = Collections.unmodifiableMap( mapUserInfos );
            request.setAttribute( PortletRequest.USER_INFO, cstMapUserInfos );
        }
        else
        {
            // User not identified, getAttribute on "user info" must return null 
            request.removeAttribute( PortletRequest.USER_INFO );
        }
    }
}
