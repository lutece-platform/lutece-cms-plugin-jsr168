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
package fr.paris.lutece.plugins.jsr168.pluto.core;

import fr.paris.lutece.plugins.jsr168.pluto.LutecePlutoConstant;

import org.apache.pluto.portalImpl.services.config.Config;
import org.apache.pluto.services.information.PortalContextProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;


/**
 * Context provider
 */
public class PortalContextProviderImpl implements PortalContextProvider
{
    /** Portal information */
    private String _strInfo;

    /** supported portlet modes by this portal */
    private List _listModes;

    /** supported window states by this portal */
    private List _listStates;

    /** portal properties */
    private Map _properties;

    /**
     * Default constructor: initialize all members with default
     * portlet container values
     */
    public PortalContextProviderImpl(  )
    {
        // these are the minimum modes that the portal needs to support
        _listModes = getDefaultModes(  );

        // these are the minimum states that the portal needs to support
        _listStates = getDefaultStates(  );

        // set info
        _strInfo = "Lutece1.1/Pluto1.0";

        _properties = new HashMap(  );
    }

    /**
         * @see org.apache.pluto.services.information.PortalContextProvider#getProperty(java.lang.String)
         */
    public String getProperty( String name )
    {
        if ( name == null )
        {
            throw new IllegalArgumentException( "Property name == null" );
        }

        return (String) _properties.get( name );
    }

    /**
         * @see org.apache.pluto.services.information.PortalContextProvider#getPropertyNames()
         */
    public Collection getPropertyNames(  )
    {
        return _properties.keySet(  );
    }

    /**
         * @see org.apache.pluto.services.information.PortalContextProvider#getSupportedPortletModes()
         */
    public Collection getSupportedPortletModes(  )
    {
        return _listModes;
    }

    /**
         * @see org.apache.pluto.services.information.PortalContextProvider#getSupportedWindowStates()
         */
    public Collection getSupportedWindowStates(  )
    {
        return _listStates;
    }

    /**
         * @see org.apache.pluto.services.information.PortalContextProvider#getPortalInfo()
         */
    public String getPortalInfo(  )
    {
        return _strInfo;
    }

    /**
     * Return default portlet supported modes
     *
     * @return default portlet supported modes
     */
    private List getDefaultModes(  )
    {
        List m = new ArrayList(  );

        String[] supportedModes = Config.getParameters(  )
                                        .getStrings( LutecePlutoConstant.CONFIG_SERVICES_PARAM_SUPPORTED_PORTLETMODE );

        for ( int i = 0; i < supportedModes.length; i++ )
        {
            m.add( new PortletMode( supportedModes[i].toString(  ).toLowerCase(  ) ) );
        }

        return m;
    }

    /**
    * Return default portlet supported states
    *
    * @return default portlet supported states
     */
    private List getDefaultStates(  )
    {
        List s = new ArrayList(  );

        String[] supportedStates = Config.getParameters(  )
                                         .getStrings( LutecePlutoConstant.CONFIG_SERVICES_PARAM_SUPPORTED_WINDOWSTATE );

        for ( int i = 0; i < supportedStates.length; i++ )
        {
            s.add( new WindowState( supportedStates[i].toString(  ).toLowerCase(  ) ) );
        }

        return s;
    }

    /**
     * reset all values to default portlet modes and window states;
     * delete all properties and set the given portlet information
     * as portlet info string.
     *
     * @param strPortalInfo  portal information string that will be returned
     *                    by the <code>getPortalInfo</code> call.
     */
    private void reset( String strPortalInfo )
    {
        _strInfo = new String( strPortalInfo );

        // these are the minimum modes that the portal needs to support
        _listModes = getDefaultModes(  );

        // these are the minimum states that the portal needs to support
        _listStates = getDefaultStates(  );

        _properties.clear(  );
    }
}
