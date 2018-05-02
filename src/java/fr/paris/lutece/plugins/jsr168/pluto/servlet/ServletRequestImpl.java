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
package fr.paris.lutece.plugins.jsr168.pluto.servlet;

import fr.paris.lutece.plugins.jsr168.pluto.core.PortalURL;

import org.apache.pluto.portalImpl.om.window.impl.PortletWindowImpl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * Http servlet wrapper
 */
public class ServletRequestImpl extends HttpServletRequestWrapper
{
    private final Map _mapParameters;

    /**
     * Initialize HTTP servlet wrapper
     *
     * @param servletRequest Real servlet wrapper
     * @param window current portlet window
     */
    public ServletRequestImpl( HttpServletRequest servletRequest, PortletWindowImpl window )
    {
        super( servletRequest );

        //get control params
        Map portletParameters = new HashMap(  );

        portletParameters.putAll( window.getRenderParameters(  ) );

        // Get only parameter targetted to portlet 
        String pid = PortalURL.extractPortletId( (HttpServletRequest) servletRequest );
        String wid = window.getId(  ).toString(  );

        if ( wid.equals( pid ) )
        {
            for ( Enumeration parameters = super.getParameterNames(  ); parameters.hasMoreElements(  ); )
            {
                String paramName = (String) parameters.nextElement(  );
                String[] values = (String[]) portletParameters.get( paramName );
                String[] paramValues = (String[]) super.getParameterValues( paramName );

                final String[] finalValues;

                if ( values != null )
                {
                    finalValues = new String[paramValues.length + values.length];
                    System.arraycopy( paramValues, 0, finalValues, 0, paramValues.length );
                    System.arraycopy( values, 0, finalValues, paramValues.length, values.length );
                }
                else
                {
                    finalValues = paramValues;
                }

                portletParameters.put( paramName, paramValues );
            }
        }

        _mapParameters = Collections.unmodifiableMap( portletParameters );
    }

    /**
         * @see javax.servlet.ServletRequest#getContentType()
         */
    public String getContentType(  )
    {
        String contentType = "text/html";

        if ( getCharacterEncoding(  ) != null )
        {
            contentType += ( ";" + getCharacterEncoding(  ) );
        }

        return contentType;
    }

    /**
         * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
         */
    public String getParameter( String paramName )
    {
        final String[] values = (String[]) _mapParameters.get( paramName );

        if ( values != null )
        {
            return values[0];
        }

        return null;
    }

    /**
         * @see javax.servlet.ServletRequest#getParameterMap()
         */
    public Map getParameterMap(  )
    {
        return _mapParameters;
    }

    /**
         * @see javax.servlet.ServletRequest#getParameterNames()
         */
    public Enumeration getParameterNames(  )
    {
        return Collections.enumeration( _mapParameters.keySet(  ) );
    }

    /**
         * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
         */
    public String[] getParameterValues( String name )
    {
        return (String[]) _mapParameters.get( name );
    }
}
