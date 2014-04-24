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
package fr.paris.lutece.plugins.jsr168.pluto.core;

import org.apache.pluto.services.ContainerService;

import java.util.HashMap;
import java.util.Map;


/**
 * Lutece/Pluto portlet container environment (access point to service)
 */
public class PortletContainerEnvironment implements org.apache.pluto.services.PortletContainerEnvironment
{
    private Map _services;

    /**
     * Default constructor
     */
    public PortletContainerEnvironment(  )
    {
        _services = new HashMap(  );
    }

    /**
         * @see org.apache.pluto.services.PortletContainerEnvironment#getContainerService(java.lang.Class)
         */
    public ContainerService getContainerService( Class service )
    {
        return (ContainerService) _services.get( service );
    }

    /**
     * Add a container service interface
     *
     * @param service Service to add
     */
    public void addContainerService( ContainerService service )
    {
        Class serviceClass = service.getClass(  );

        while ( serviceClass != null )
        {
            final Class[] interfaces = serviceClass.getInterfaces(  );

            for ( int i = 0; i < interfaces.length; i++ )
            {
                final Class[] interfaces2 = interfaces[i].getInterfaces(  );

                for ( int ii = 0; ii < interfaces2.length; ii++ )
                {
                    if ( interfaces2[ii].equals( ContainerService.class ) )
                    {
                        _services.put( interfaces[i], service );
                    }
                }
            }

            serviceClass = serviceClass.getSuperclass(  );
        }
    }
}
