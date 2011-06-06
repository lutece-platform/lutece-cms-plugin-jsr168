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
package fr.paris.lutece.plugins.jsr168.pluto.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;


/**
 * XML Handler for reading <code>services.xml</code>.
 */
class ServicesXMLHandler extends DefaultHandler
{
    private final Stack _stack;
    private final ServicesXML _servicesXml;

    /**
     * Default constructor
     */
    ServicesXMLHandler(  )
    {
        _stack = new Stack(  );
        _servicesXml = new ServicesXML(  );
    }

    /**
     * Return the services definition readed
     *
     * @return the services definition readed
     */
    ServicesXML getServicesXML(  )
    {
        return _servicesXml;
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {
        if ( "service".equals( qName ) )
        {
            _stack.pop(  );
        }
        else if ( !"services".equals( qName ) && !"properties".equals( qName ) && !"property".equals( qName ) )
        {
            throw new SAXException( "End Element '" + qName + "' non géré!" );
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement( final String uri, final String localName, final String qName, final Attributes attributes )
        throws SAXException
    {
        if ( "service".equals( qName ) )
        {
            final String serviceBase = attributes.getValue( "serviceBase" );
            final String implementation = attributes.getValue( "implementation" );

            final ServiceXML serviceXml = new ServiceXML( serviceBase, implementation );
            _servicesXml.addService( serviceXml );
            _stack.push( serviceXml );
        }
        else if ( "property".equals( qName ) )
        {
            final String name = attributes.getValue( "name" );
            final String value = attributes.getValue( "value" );

            final ServiceXML serviceXml = (ServiceXML) _stack.peek(  );
            serviceXml.addProperty( name, value );
        }
        else if ( !"services".equals( qName ) && !"properties".equals( qName ) )
        {
            throw new SAXException( "Start Element '" + qName + "' unknown!" );
        }
    }
}
