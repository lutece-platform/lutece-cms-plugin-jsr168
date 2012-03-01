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

import fr.paris.lutece.plugins.jsr168.pluto.core.PortletURLProviderImpl;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.portalImpl.om.window.impl.PortletWindowImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Buttons list to be displayed in portlet title
 */
public class Buttons
{
    private static final String PROPERTIES_MODE_PREFIX = "portlet.jsr168.mode.";
    private static final String PROPERTIES_STATE_PREFIX = "portlet.jsr168.state.";
    private static final String PROPERTIES_IMAGE_SUFFIX = ".image";
    private final List _listModes;
    private final List _listStates;

    /**
     * Public default constructor
     */
    Buttons(  )
    {
        _listModes = new ArrayList(  );
        _listStates = new ArrayList(  );
    }

    /**
     * Initialize list by inspecting current defined parameters
     *
     * @param request Current HTTP request
     * @param response Current HTTP response (needed to encode URLs)
     * @param portletDef Portlet definition (needed to access modes and states list)
     * @param portletWindow Current portlet window
     */
    void init( HttpServletRequest request, HttpServletResponse response, PortletDefinition portletDef,
        PortletWindowImpl portletWindow )
    {
        ObjectID portletID = portletWindow.getId(  );

        ArrayList listModes = new ArrayList(  );

        String strMimeType = "text/html";
        ContentType contentTypeSet = portletDef.getContentTypeSet(  ).get( strMimeType );

        for ( Iterator itModes = contentTypeSet.getPortletModes(  ); itModes.hasNext(  ); )
        {
            listModes.add( itModes.next(  ) );
        }

        for ( Iterator itModesStr = listModes.iterator(  ); itModesStr.hasNext(  ); )
        {
            PortletMode mode = (PortletMode) itModesStr.next(  );

            if ( !portletWindow.getPortletMode(  ).equals( mode ) )
            {
                String strHelpRenderURL = PortletURLProviderImpl.getRenderURL( request, response, mode, null, portletID );
                String strHelpImagePath = AppPropertiesService.getProperty( PROPERTIES_MODE_PREFIX + mode +
                        PROPERTIES_IMAGE_SUFFIX );

                _listModes.add( new Button( strHelpRenderURL, strHelpImagePath ) );
            }
        }

        if ( !WindowState.MINIMIZED.equals( portletWindow.getWindowState(  ) ) )
        {
            String strHelpRenderURL = PortletURLProviderImpl.getRenderURL( request, response, null,
                    WindowState.MINIMIZED, portletID );
            String strHelpImagePath = AppPropertiesService.getProperty( PROPERTIES_STATE_PREFIX +
                    WindowState.MINIMIZED + PROPERTIES_IMAGE_SUFFIX );

            _listStates.add( new Button( strHelpRenderURL, strHelpImagePath ) );
        }

        // TODO il n'y a pas d'acces a la definition des etats (WindowState) en plus...
        if ( !WindowState.NORMAL.equals( portletWindow.getWindowState(  ) ) )
        {
            String strHelpRenderURL = PortletURLProviderImpl.getRenderURL( request, response, null, WindowState.NORMAL,
                    portletID );
            String strHelpImagePath = AppPropertiesService.getProperty( PROPERTIES_STATE_PREFIX + WindowState.NORMAL +
                    PROPERTIES_IMAGE_SUFFIX );

            _listStates.add( new Button( strHelpRenderURL, strHelpImagePath ) );
        }

        if ( !WindowState.MAXIMIZED.equals( portletWindow.getWindowState(  ) ) )
        {
            String strHelpRenderURL = PortletURLProviderImpl.getRenderURL( request, response, null,
                    WindowState.MAXIMIZED, portletID );
            String strHelpImagePath = AppPropertiesService.getProperty( PROPERTIES_STATE_PREFIX +
                    WindowState.MAXIMIZED + PROPERTIES_IMAGE_SUFFIX );

            _listStates.add( new Button( strHelpRenderURL, strHelpImagePath ) );
        }
    }

    /**
     * Return an iterator for defined buttons for modes
     *
     * @return an iterator for defined buttons for modes
     */
    public Iterator modes(  )
    {
        return _listModes.iterator(  );
    }

    /**
     * Return an iterator for defined buttons for states
     *
     * @return an iterator for defined buttons for states
     */
    public Iterator states(  )
    {
        return _listStates.iterator(  );
    }
}
