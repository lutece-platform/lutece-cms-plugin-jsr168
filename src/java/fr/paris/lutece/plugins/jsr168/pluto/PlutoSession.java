/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.pluto.portalImpl.om.window.impl.PortletWindowImpl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Unique class present in {@link javax.servlet.http.HttpSession} instance
 * associated to user.
 */
public final class PlutoSession
{
    /**
     * Portlets Window defined for the user (a portlet window
     * contains the definition of the portlet, current state,
     * current mode...)
     */
    private final Map _mapPortletWindow;

    /**
     * Default private constructor, PlutoSession is build
     * by {@link #findSession(HttpServletRequest)} call.
     */
    private PlutoSession(  )
    {
        _mapPortletWindow = new HashMap(  );
    }

    /**
     * Return a portlet window associated to a portlet ID.
     *
     * @param strPortletId The portlet ID (Lutece ID) of the portlet window asked
     * @return The portlet window associated to this servlet
     */
    public synchronized PortletWindowImpl getPortletWindow( String strPortletId )
    {
        PortletWindowImpl portletWindow = (PortletWindowImpl) _mapPortletWindow.get( strPortletId );

        if ( portletWindow == null )
        {
            portletWindow = new PortletWindowImpl( strPortletId );
            _mapPortletWindow.put( strPortletId, portletWindow );
        }

        AppLogService.debug( "JSR168 / BEGIN Portlet Window acceded (lutece ID [" + strPortletId + "]; " +
            portletWindow.getRenderParameters(  ) );

        return portletWindow;
    }

    /**
     * Return the current Pluto session (for current user request)
     *
     * @param request Current user HTTP resquest
     * @return The <code>PlutoSession</code> associated with this session
     */
    public static PlutoSession findSession( final HttpServletRequest request )
    {
        HttpSession session = request.getSession(  );

        synchronized ( session )
        {
            PlutoSession plutoSession = (PlutoSession) session.getAttribute( LutecePlutoConstant.LUTECEPLUTO_SESSION_PORTLET );

            if ( plutoSession == null )
            {
                plutoSession = new PlutoSession(  );
                session.setAttribute( LutecePlutoConstant.LUTECEPLUTO_SESSION_PORTLET, plutoSession );
            }

            return plutoSession;
        }
    }
}
