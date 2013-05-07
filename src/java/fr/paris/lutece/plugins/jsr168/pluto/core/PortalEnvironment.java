/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Stock la reference de l'ensemble des ressources necessaires aux
 * conteneur de portlets.
 *
 * Un objet de type PortalEnvironment doit être initialisé et placé en
 * attribut dans la requête HTTP avant tout appel au conteneur de portlet.
 * Pour placer l'objet en requête appeler la méthode {@link #initPortalEnvironment()}.
 *
 * La méthode statique {@link #getPortalEnvironment(HttpServletRequest)} permet
 * d'obtenir la reference de l'environnement à partir de la requ�te HTTP.
 */
public class PortalEnvironment
{
    private final String _strPortletID;
    private final ServletConfig _config;
    private final HttpServletRequest _request;
    private final HttpServletResponse _response;

    /**
     * Initialize the environment for portlet resolution
     *
     * @param config Current <code>ServletConfig</code>
     * @param request Current <code>HttpServletRequest</code>
     * @param response Current <code>HttpServletResponse</code>
     * @param strPortletID Current portlet ID
     */
    public PortalEnvironment( ServletConfig config, HttpServletRequest request, HttpServletResponse response,
        String strPortletID )
    {
        _strPortletID = strPortletID;
        _config = config;
        _request = request;
        _response = response;
    }

    /**
     * Return current <code>ServletConfig</code>
     *
     * @return current <code>ServletConfig</code>
     */
    public ServletConfig getConfig(  )
    {
        return _config;
    }

    /**
     * Return current <code>HttpServletRequest</code>
     *
     * @return current <code>HttpServletRequest</code>
     */
    public HttpServletRequest getRequest(  )
    {
        return _request;
    }

    /**
     * Return current <code>HttpServletResponse</code>
     *
     * @return current <code>HttpServletResponse</code>
     */
    public HttpServletResponse getResponse(  )
    {
        return _response;
    }

    /**
     * Return current portlet id
     *
     * @return current portlet id
     */
    public String getPortletID(  )
    {
        return _strPortletID;
    }

    /**
     * Initialize the environment (put the environment into http request object)
     */
    public void initPortalEnvironment(  )
    {
        _request.setAttribute( LutecePlutoConstant.PLUTO_PORTAL_REQUEST_PORTALENV, this );
    }

    /**
     * Extract portlet environment from current http request
     *
     * @param request current http request
     * @return portlet environment extracted from current HTTP request
     */
    public static PortalEnvironment getPortalEnvironment( HttpServletRequest request )
    {
        return (PortalEnvironment) request.getAttribute( LutecePlutoConstant.PLUTO_PORTAL_REQUEST_PORTALENV );
    }
}
