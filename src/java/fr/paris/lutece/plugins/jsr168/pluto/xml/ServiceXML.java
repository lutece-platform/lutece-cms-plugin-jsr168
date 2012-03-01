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
package fr.paris.lutece.plugins.jsr168.pluto.xml;

import org.apache.pluto.portalImpl.util.Properties;


/**
 * Java bean to map the service configuration file<br>
 * Map a service definition
 */
public class ServiceXML
{
    private final String _strServiceBase;
    private final String _strImplementation;
    private final Properties _properties;

    /**
     * Default service definition constructor
     *
     * @param serviceBase The service base class where service must be registred
     * @param implementation The implementation class
     */
    ServiceXML( String serviceBase, String implementation )
    {
        _strServiceBase = serviceBase;
        _strImplementation = implementation;
        _properties = new Properties(  );
    }

    /**
     * Add a property to service definition<br>
     * Used to build the definition
     *
     * @param name Name of the property
     * @param value Value of the property
     */
    void addProperty( String name, String value )
    {
        _properties.add( name, value );
    }

    /**
     * Return the service base class where service must be registred
     *
     * @return the service base class where service must be registred
     */
    public String getServiceBase(  )
    {
        return _strServiceBase;
    }

    /**
     * Return the implementation class
     *
     * @return the implementation class
     */
    public String getImplementation(  )
    {
        return _strImplementation;
    }

    /**
     * Properties associated to the plugin
     *
     * @return Properties associated to the plugin
     */
    public Properties getProperties(  )
    {
        return _properties;
    }
}
