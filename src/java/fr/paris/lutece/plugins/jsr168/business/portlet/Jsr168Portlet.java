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
package fr.paris.lutece.plugins.jsr168.business.portlet;

import fr.paris.lutece.plugins.jsr168.pluto.Button;
import fr.paris.lutece.plugins.jsr168.pluto.Buttons;
import fr.paris.lutece.plugins.jsr168.pluto.LuteceToPlutoConnector;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents business objects HtmlPortlet
 */
public class Jsr168Portlet extends Portlet
{
    private static final String TAG_HTML_PORTLET = "html-portlet";
    private static final String TAG_HTML_PORTLET_CONTENT = "html-portlet-content";
    private static final String XML_ELEMENT_PORTLET_BUTTONS = "portlet-buttons";
    private static final String XML_ELEMENT_PORTLET_BUTTONS_MODES = "modes";
    private static final String XML_ELEMENT_PORTLET_BUTTONS_STATES = "states";

    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    private String _strJsr168Name;

    /**
     * Sets the identifier of the portlet type to value specified
     */
    public Jsr168Portlet(  )
    {
        setPortletTypeId( Jsr168PortletHome.getInstance(  ).getPortletTypeId(  ) );
    }

    /**
     * Sets the name of the jsr 168 portlet
     *
     * @param strJsr168Name the name of the jsr 168 portlet
     */
    public void setJsr168Name( String strJsr168Name )
    {
        _strJsr168Name = strJsr168Name;
    }

    /**
     * Returns the name of the jsr 168 name
     *
     * @return the name of the Jsr 168 portlet
     */
    public String getJsr168Name(  )
    {
        return _strJsr168Name;
    }

    /**
     * Add the common tags to all the portlets to the XML document
     *
     * @param sbPortlet The string buffer which contains the XML content of this portlet
     * @return The XML content of this portlet encapsulated by the common tags
     */
    protected String addPortletTags( StringBuffer sbPortlet )
    {
        StringBuffer sbXml = new StringBuffer(  );
        XmlUtil.beginElement( sbXml, TAG_PORTLET );
        XmlUtil.addElementHtml( sbXml, TAG_PORTLET_NAME, getName(  ) );
        XmlUtil.addElement( sbXml, TAG_PORTLET_ID, getId(  ) );
        XmlUtil.addElement( sbXml, TAG_PAGE_ID, getPageId(  ) );
        addPortletButtons( sbXml );
        sbXml.append( sbPortlet );
        XmlUtil.endElement( sbXml, TAG_PORTLET );

        return sbXml.toString(  );
    }

    /**
     * Removes the current instance of the HtmlPortlet object
     */
    public void remove(  )
    {
        Jsr168PortletHome.getInstance(  ).remove( this );
    }

    /*
            Method to add when portlets can follow the server life-cycle.
            This method must be called when the server shutdown.
            It calls "destroy" method of JSR168 portlets. JSR 168 Portlets
            can release ressources: database connexions, transactions, open files...
    
            public void destroy()
            {
                    try
                    {
                            PortletContainerFactory.
                                    getPortletContainer().
                                            shutdown();
    
                            // destroy all services
    
                            ServiceManager.destroy (getServletConfig ());
    
                            System.gc ();
                    }
                    catch (Throwable t)
                    {
                            log ("Destruction failed!", t);
                    }
            }
    */

    /**
     * Returns the Xml code of the HTML portlet without Xml header
     *
     * @param request Current user HTTP Request
     * @return the Xml code of the HTML portlet content
     */
    public String getXml( HttpServletRequest request )
    {
        StringBuffer sbXml = new StringBuffer(  );
        XmlUtil.beginElement( sbXml, TAG_HTML_PORTLET );
        XmlUtil.addElementHtml( sbXml, TAG_HTML_PORTLET_CONTENT,
            LuteceToPlutoConnector.render( getId(  ), getJsr168Name(  ) ) );
        XmlUtil.endElement( sbXml, TAG_HTML_PORTLET );

        return addPortletTags( sbXml );
    }

    /**
     * Returns the Xml code of the HTML portlet with Xml header
     *
     * @param request Current user HTTP Request
     * @return the Xml code of the HTML portlet
     */
    public String getXmlDocument( HttpServletRequest request )
    {
        return XmlUtil.getXmlHeader(  ) + getXml( request );
    }

    /**
     * Updates the current instance of the HtmlPortlet object
     */
    public void update(  )
    {
        Jsr168PortletHome.getInstance(  ).update( this );
    }

    /**
     * Add buttons associate with this portlet.<br>
     *
     * Side-effect: for perfomance, this method append text in the buffer
     * argument <code>bufXml</code>
     *
     * @param sbXml Buffer of current portlet xml fragment (side-effect on parameter:
     *     append text in the buffer)
     */
    private void addPortletButtons( StringBuffer sbXml )
    {
        // Get defined buttons for this portlet
        Buttons buttons = LuteceToPlutoConnector.getButtons( getId(  ), getJsr168Name(  ) );

        XmlUtil.beginElement( sbXml, XML_ELEMENT_PORTLET_BUTTONS );

        // Add "modes" buttons
        XmlUtil.beginElement( sbXml, XML_ELEMENT_PORTLET_BUTTONS_MODES );

        for ( Iterator it = buttons.modes(  ); it.hasNext(  ); )
        {
            Button button = (Button) it.next(  );
            sbXml.append( "<button link=\"" ).append( protectAmp( button.getUrlRender(  ) ) ).append( "\" image=\"" )
                 .append( button.getImagePath(  ) ).append( "\"/>" );
        }

        XmlUtil.endElement( sbXml, XML_ELEMENT_PORTLET_BUTTONS_MODES );

        // Add "states" buttons
        XmlUtil.beginElement( sbXml, XML_ELEMENT_PORTLET_BUTTONS_STATES );

        for ( Iterator it = buttons.states(  ); it.hasNext(  ); )
        {
            Button button = (Button) it.next(  );
            sbXml.append( "<button link=\"" ).append( protectAmp( button.getUrlRender(  ) ) ).append( "\" image=\"" )
                 .append( button.getImagePath(  ) ).append( "\"/>" );
        }

        XmlUtil.endElement( sbXml, XML_ELEMENT_PORTLET_BUTTONS_STATES );

        XmlUtil.endElement( sbXml, XML_ELEMENT_PORTLET_BUTTONS );
    }

    /**
     * Optimized function for protecting the character &quot;&amp;&quot;
     * by replacing it with an &quot;&amp;amp;&quot;.
     *
     * @param strSrc The string to analyse
     * @return The string with character replaced
     */
    private String protectAmp( String strSrc )
    {
        StringBuffer buf = new StringBuffer( strSrc.length(  ) );

        for ( StringTokenizer tokenizer = new StringTokenizer( strSrc, "&" ); tokenizer.hasMoreElements(  ); )
        {
            String token = tokenizer.nextToken(  );
            buf.append( token );

            if ( tokenizer.hasMoreElements(  ) )
            {
                buf.append( "&amp;" );
            }
        }

        return buf.toString(  );
    }
}
