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
package fr.paris.lutece.plugins.jsr168.web.portlet;

import fr.paris.lutece.plugins.jsr168.business.portlet.Jsr168Portlet;
import fr.paris.lutece.plugins.jsr168.business.portlet.Jsr168PortletHome;
import fr.paris.lutece.plugins.jsr168.pluto.LuteceToPlutoConnector;
import fr.paris.lutece.plugins.jsr168.pluto.core.PortalURL;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage Jsr 168 Portlet features
 */
public class Jsr168PortletJspBean extends PortletJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

    //  Rights
    public static final String RIGHT_MANAGE_ADMIN_SITE = "CORE_ADMIN_SITE";
    private static final String ADMIN_SITE = "../../site/AdminSite.jsp";
    private static final String MESSAGE_MANDATORY_PORTLET_TITLE = "../../Message.jsp?message=mandatory.portlet.title";
    private static final String PARAM_PAGE_ID = "page_id";
    private static final String PARAM_PORTLET_ID = "portlet_id";
    private static final String PARAM_PORTLET_TYPE_ID = "portlet_type_id";
    private static final String PARAM_JSR168_NAME = "jsr168_name";
    private static final String BOOKMARK_JSR168_TITLE_COMBO = "@jsr168_title_combo@";

    //Markers
    private static final String MARK_PORTLET_LIST = "portlet_list";
    private static final String MARK_PORTLET_ID = "default_portlet_id";

    // Templates
    private static final String TEMPLATE_COMBO_PORTLETS = "admin/plugins/jsr168/portlet/combo_portletsJSR.html";
    private static final String COMBO_PORTLETS_LIST = "@combo_portletsJSR@";

    /**
     * Creates a new Jsr168PortletJspBean object.
     */
    public Jsr168PortletJspBean(  )
    {
    }

    /**
     * Returns the properties prefix used for jsr168 portlet and defined in lutece.properties file
     *
     * @return the value of the property prefix
     */
    public String getPropertiesPrefix(  )
    {
        return "portlet.jsr168";
    }

    /**
     * Returns the Jsr 168 Portlet form of creation
     *
     * @param request The current Http request
     * @return the html code of the jsr 168 portlet form
     */
    public String getCreate( HttpServletRequest request )
    {
        System.out.println( "in getCreate" );

        String strPageId = request.getParameter( PARAM_PAGE_ID );
        String strPortletTypeId = request.getParameter( PARAM_PORTLET_TYPE_ID );
        HtmlTemplate template = getCreateTemplate( strPageId, strPortletTypeId );

        System.out.println( "Template before :" );
        System.out.println( template.getHtml(  ) );

        ReferenceList portletsList = LuteceToPlutoConnector.getPortletTitles(  );

        String strHtmlCombo = getPortletIndexCombo( portletsList, "" );
        template.substitute( BOOKMARK_JSR168_TITLE_COMBO, strHtmlCombo );
        System.out.println( "out getCreate" );
        System.out.println( "Template after :" );
        System.out.println( template.getHtml(  ) );

        return template.getHtml(  );
    }

    /**
     * Returns the Jsr 168 Portlet form for update
     *
     * @param request The current Http request
     * @return the html code of the jsr 168 portlet form
     */
    public String getModify( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAM_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        Jsr168Portlet portlet = (Jsr168Portlet) PortletHome.findByPrimaryKey( nPortletId );
        HtmlTemplate template = getModifyTemplate( portlet );

        ReferenceList portletsList = LuteceToPlutoConnector.getPortletTitles(  );

        String strHtmlCombo = getPortletIndexCombo( portletsList, portlet.getJsr168Name(  ) );
        template.substitute( BOOKMARK_JSR168_TITLE_COMBO, strHtmlCombo );

        return template.getHtml(  );
    }

    /**
     * Processes the creation form of a new jsr 168 portlet
     *
     * @param request The current Http request
     * @return The jsp URL which displays the view of the created jsr 168 portlet
     */
    public String doCreate( HttpServletRequest request )
    {
        Jsr168Portlet portlet = new Jsr168Portlet(  );

        // Recovers portlet common attributes
        setPortletCommonData( request, portlet );

        // mandatory field
        String strName = portlet.getName(  );

        if ( ( strName == null ) || "".equals( strName.trim(  ) ) )
        {
            return MESSAGE_MANDATORY_PORTLET_TITLE;
        }

        // Recovers portlet specific attributes
        String strPageId = request.getParameter( PARAM_PAGE_ID );
        int nPageId = Integer.parseInt( strPageId );
        portlet.setPageId( nPageId );

        // html code cleaning
        String strContent = request.getParameter( PARAM_JSR168_NAME );
        portlet.setJsr168Name( strContent );

        // creates the portlet
        Jsr168PortletHome.getInstance(  ).create( portlet );

        // displays the page with the new portlet
        return ADMIN_SITE + "?" + PARAM_PAGE_ID + "=" + portlet.getPageId(  );
    }

    /**
     * Processes the update form of the jsr 168 portlet whose identifier is in the http request
     *
     * @param request The current Http request
     * @return The jsp URL which displays the view of the updated jsr 168 portlet
     */
    public String doModify( HttpServletRequest request )
    {
        // recovers portlet attributes
        String strPortletId = request.getParameter( PARAM_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        Jsr168Portlet portlet = (Jsr168Portlet) PortletHome.findByPrimaryKey( nPortletId );

        // recovers portlet common attributes
        setPortletCommonData( request, portlet );

        // mandatory field
        String strName = portlet.getName(  );

        if ( ( strName == null ) || "".equals( strName.trim(  ) ) )
        {
            return MESSAGE_MANDATORY_PORTLET_TITLE;
        }

        // html code cleaning
        String strContent = request.getParameter( PARAM_JSR168_NAME );
        portlet.setJsr168Name( strContent );

        // updates the portlet
        portlet.update(  );

        // displays the page with the portlet updated
        return ADMIN_SITE + "?" + PARAM_PAGE_ID + "=" + portlet.getPageId(  );
    }

    /**
    * Call {@link LuteceToPlutoConnector} method targeted by the request: <code>action</code>
    * or <code>render</code>
    *
    * @param request The current Http request
    * @return Indicate if this action has generated a fragment,
    *         <code>true</code> no redirect to send,
    *         <code>false</code> for a send redirect required (for <code>action</code> request)
     */
    public boolean realiseAction( HttpServletRequest request )
    {
        final String strPortletId = PortalURL.extractPortletId( request );

        final int nPortletId = Integer.parseInt( strPortletId );
        final Jsr168Portlet portlet = (Jsr168Portlet) PortletHome.findByPrimaryKey( nPortletId );

        return LuteceToPlutoConnector.request( nPortletId, portlet.getJsr168Name(  ) );
    }

    /**
     * Return the feed listing depending on rights
     * @param listFeeds list of available rss feeds
     * @param strDefaultFeedId The id of the feed used in the context
     * @return The html code of the combo
     */
    String getPortletIndexCombo( ReferenceList listPortlets, String strDefaultPortletId )
    {
        HashMap model = new HashMap(  );
        model.put( MARK_PORTLET_LIST, listPortlets );
        model.put( MARK_PORTLET_ID, strDefaultPortletId );

        HtmlTemplate templateCombo = AppTemplateService.getTemplate( TEMPLATE_COMBO_PORTLETS, getLocale(  ), model );

        return templateCombo.getHtml(  );
    }
}
