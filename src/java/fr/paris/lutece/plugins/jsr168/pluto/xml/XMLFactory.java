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
package fr.paris.lutece.plugins.jsr168.pluto.xml;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Services configuration factory reader
 */
public final class XMLFactory
{
    /**
     * Utility classes have no constructor
     */
    private XMLFactory(  )
    {
    }

    /**
     * Read <code>services.xml</code> configuration file and return it.
     *
     * @param context Servlet context associated to the web application
     * @param strRessource The <code>services.xml</code> ressource path
     * @return Services configuration bean
     * @throws XMLFactoryException For any error when reading configuration file
     */
    public static ServicesXML loadServicesXML( ServletContext context, String strRessource )
        throws XMLFactoryException
    {
        try
        {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance(  );
            parserFactory.setValidating( false );

            SAXParser parser = parserFactory.newSAXParser(  );
            ServicesXMLHandler servicesXMLHandler = new ServicesXMLHandler(  );
            InputStream fis = context.getResourceAsStream( strRessource );
            InputStream bis = new BufferedInputStream( fis );
            parser.parse( bis, servicesXMLHandler );

            return servicesXMLHandler.getServicesXML(  );
        }
        catch ( ParserConfigurationException e )
        {
            throw new XMLFactoryException( e );
        }
        catch ( SAXParseException e )
        {
            throw new XMLFactoryException( e );
        }
        catch ( SAXException e )
        {
            throw new XMLFactoryException( e );
        }
        catch ( IOException e )
        {
            throw new XMLFactoryException( e );
        }
    }
}
