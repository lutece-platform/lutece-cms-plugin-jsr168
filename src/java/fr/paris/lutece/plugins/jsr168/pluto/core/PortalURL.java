/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.jsr168.pluto.core;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.portalImpl.om.window.impl.PortletWindowImpl;

import java.net.URLEncoder;

import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import javax.servlet.http.HttpServletRequest;


/**
 * Define
 */
public class PortalURL
{
    /**
     * Prefix all parameters
     */
    public static final String PREFIX = "_";

    /**
     * Portlet ID request parameter name
     */
    private static final String PORTLET_ID = "pid";

    /**
     * Portlet mode request parameter name
     */
    private static final String MODE = "md";

    /**
     * Window state request parameter name
     */
    private static final String STATE = "st";

    /**
     * Indicate an URL action (request parameter name)
     */
    private static final String ACTION = "ac";

    /**
     * Indicate if it's a portal action URL
     */
    private final boolean _bAction;

    /**
     * Portlet mode associate to this portal URL
     */
    private final PortletMode _portletMode;

    /**
     * Portlet state associate to this portal URL
     */
    private final WindowState _windowState;

    /**
     * Construct a PortletURL instance<br>
     *
     * TODO need to extract render parameters?
     *
     * @param request A HTTP request who define action, mode and state
     */
    public PortalURL( HttpServletRequest request )
    {
        _bAction = extractAction( request );
        _portletMode = extractMode( request );
        _windowState = extractState( request );
    }

    /**
     * Return <code>true</code> to indicate an action URL.
     *
     * @return <code>true</code> to indicate an action URL
     */
    public boolean isAction(  )
    {
        return _bAction;
    }

    /**
     * Return the portlet mode associate to this URL.
     *
     * @return portlet mode associate to this URL
     */
    public PortletMode getPortletMode(  )
    {
        return _portletMode;
    }

    /**
     * Return the window state associate to this URL.
     *
     * @return window state associate to this URL
     */
    public WindowState getWindowState(  )
    {
        return _windowState;
    }

    /**
     * Fill a portlet window with state and mode values
     *
     * @param portletWindow The portlet window to fill with defined state and mode
     */
    public void fillWindowPortlet( PortletWindowImpl portletWindow )
    {
        if ( getPortletMode(  ) != null )
        {
            portletWindow.setPortletMode( getPortletMode(  ) );
        }

        if ( getWindowState(  ) != null )
        {
            portletWindow.setWindowState( getWindowState(  ) );
        }
    }

    /**
     * Build URL parameters list
     *
     * @param portletID Portlet ID of the portlet target
     * @param bAction Indicate an action URL (vs a render URL)
     * @param portletMode Current portlet mode
     * @param windowState Current portlet window state
     * @param parameters Map of parameters (the map must be typed &lt;String, String&gt; or &lt;String, String[]&gt;)
     * @return The URL parameters fragment<br>
     *                 for example:
     *                 <li>Portlet ID: 15</li>
     *                 <li>Ask for an action URL</li>
     *                 <li>Parameters: value=&quot;295&quot;, action=&quot;set&quot;, field=&quot;day&quot;</li><br>
     *                 will return the string&nbsp;: &quot;_pid=15&amp;_ac=1&amp;value=295&amp;action=set&amp;field=day&quot;
     */
    public static String buildParams( ObjectID portletID, boolean bAction, PortletMode portletMode,
        WindowState windowState, Map parameters )
    {
        StringBuffer buf = new StringBuffer(  );
        String sep = "";

        String portletIdParam = buildPortletIDParam( portletID );

        if ( ( portletIdParam != null ) && ( portletIdParam.length(  ) > 0 ) )
        {
            buf.append( sep ).append( portletIdParam );
            sep = "&";
        }

        String actionParam = buildActionParam( bAction );

        if ( ( actionParam != null ) && ( actionParam.length(  ) > 0 ) )
        {
            buf.append( sep ).append( actionParam );
            sep = "&";
        }

        String modeParam = buildModeParam( portletMode );

        if ( ( modeParam != null ) && ( modeParam.length(  ) > 0 ) )
        {
            buf.append( sep ).append( modeParam );
            sep = "&";
        }

        String stateParam = buildStateParam( windowState );

        if ( ( stateParam != null ) && ( stateParam.length(  ) > 0 ) )
        {
            buf.append( sep ).append( stateParam );
            sep = "&";
        }

        String renderParams = buildParameterParam( parameters );

        if ( ( renderParams != null ) && ( renderParams.length(  ) > 0 ) )
        {
            buf.append( sep ).append( renderParams );
            sep = "&";
        }

        return buf.toString(  );
    }

    /**
     * Extract the porlet ID from an HTTP request
     *
     * @param servletRequest The HTTP request
     * @return the porlet ID defined by an HTTP request
     */
    public static String extractPortletId( HttpServletRequest servletRequest )
    {
        return servletRequest.getParameter( getPortletIdKey(  ) );
    }

    /**
     * Indicate if an HTTP request is an action
     *
     * @param servletRequest The HTTP request
     * @return <code>true</code> if the HTTP request is a portlet action request
     */
    public static boolean isActionURL( HttpServletRequest servletRequest )
    {
        final String actionParam = servletRequest.getParameter( getActionKey(  ) );

        return ( actionParam != null );
    }

    /**
     * Build URL parameters fragment that contain the &quot;portlet ID&quot;
     *
     * @param portletID Portlet ID of the portlet target
     * @return The URL fragment that contain portlet ID
     */
    private static String buildPortletIDParam( ObjectID portletID )
    {
        if ( portletID != null )
        {
            return getPortletIdKey(  ) + "=" + portletID;
        }

        return null;
    }

    /**
     * Build URL parameters fragment that contain the indication of &quot;action portlet request&quot;
     *
     * @param bAction Indicate an action URL (vs a render URL)
     * @return The URL fragment that contain action identification
     */
    private static String buildActionParam( final boolean bAction )
    {
        if ( bAction )
        {
            return getActionKey(  ) + "=" + 1;
        }

        return null;
    }

    /**
     * Indicate if an HTTP request is an action portlet request
     *
     * @param servletRequest The HTTP request
     * @return <code>true</code> if the request is an action portlet request
     */
    private static boolean extractAction( HttpServletRequest servletRequest )
    {
        String actionStr = servletRequest.getParameter( getActionKey(  ) );

        return ( actionStr != null );
    }

    /**
     * Build URL parameters fragment that contain the &quot;portlet mode&quot;
     *
     * @param portletMode Portlet mode to encode
     * @return The URL fragment that contain the encoded portlet mode
     */
    private static String buildModeParam( PortletMode portletMode )
    {
        if ( portletMode != null )
        {
            return getModeKey(  ) + "=" + portletMode;
        }

        return null;
    }

    /**
     * Extract portlet mode from an HTTP request
     *
     * @param servletRequest The HTTP request
     * @return The portlet mode contained in HTTP request (<code>null</code> if no
     * portlet mode is in URL)
     */
    private static PortletMode extractMode( HttpServletRequest servletRequest )
    {
        String mode = servletRequest.getParameter( getModeKey(  ) );

        if ( mode != null )
        {
            return new PortletMode( mode );
        }

        return null;
    }

    /**
    * Build URL parameters fragment that contain the &quot;window state&quot;
    *
    * @param windowState Portlet mode to encode
    * @return The URL fragment that contain the encoded window state
     */
    private static String buildStateParam( WindowState windowState )
    {
        if ( windowState != null )
        {
            return getStateKey(  ) + "=" + windowState;
        }

        return null;
    }

    /**
     * Extract window state from an HTTP request
     *
     * @param servletRequest The HTTP request
     * @return The window state contained in HTTP request (<code>null</code> if no
     * window state is in URL)
     */
    private static WindowState extractState( HttpServletRequest servletRequest )
    {
        String state = servletRequest.getParameter( getStateKey(  ) );

        if ( state != null )
        {
            return new WindowState( state );
        }

        return null;
    }

    /**
     * Build URL parameters for parameters contained in <code>renderParams</code>
     *
     * @param mapRenderParams The list of parameters
     * @return URL of parameters list
     */
    private static String buildParameterParam( Map mapRenderParams )
    {
        String sep = "";
        StringBuffer buf = new StringBuffer(  );
        Iterator entriesIt = mapRenderParams.entrySet(  ).iterator(  );

        while ( entriesIt.hasNext(  ) )
        {
            Map.Entry entry = (Map.Entry) entriesIt.next(  );
            Object key = entry.getKey(  );
            Object val = entry.getValue(  );

            // si val est null tant pis, c'est a PortletWindowImpl
            // d'assumer (ou de deleguer) le controle des parametres!
            // idem si la valeur n'est pas de type String[].
            // Vivement J2SE 1.5!
            if ( ( key == null ) || !( key instanceof String ) )
            {
                continue;
            }

            if ( val == null )
            {
                continue;
            }

            final String encKey = URLEncoder.encode( (String) key );

            // TODO key is encoded... reading the key will be safe to decode the key (JSR 168 PLT.7.1) 
            if ( val instanceof String )
            {
                String vals = (String) val;
                String encVal = URLEncoder.encode( (String) vals );
                buf.append( sep ).append( encKey ).append( '=' ).append( encVal );
                sep = "&";
            }
            else if ( val instanceof String[] )
            {
                String[] vals = (String[]) val;

                for ( int i = 0; i < vals.length; ++i )
                {
                    String encVal = URLEncoder.encode( (String) vals[i] );
                    buf.append( sep ).append( encKey ).append( '=' ).append( encVal );
                    sep = "&";
                }
            }
        }

        return buf.toString(  );
    }

    /**
     * Return the action URL parameter name
     *
     * @return the action URL parameter name
     */
    private static String getActionKey(  )
    {
        return PREFIX + ACTION;
    }

    /**
     * Return the portlet ID URL parameter name
     *
     * @return the portlet ID URL parameter name
     */
    private static String getPortletIdKey(  )
    {
        return PREFIX + PORTLET_ID;
    }

    /**
     * Return the portlet mode URL parameter name
     *
     * @return the portlet mode URL parameter name
     */
    private static String getModeKey(  )
    {
        return PREFIX + MODE;
    }

    /**
     * Return the window state URL parameter name
     *
     * @return the window state URL parameter name
     */
    private static String getStateKey(  )
    {
        return PREFIX + STATE;
    }
}
