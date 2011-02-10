<%@ page import="fr.paris.lutece.portal.web.LocalVariables" 
%><%@ page import="fr.paris.lutece.portal.web.constants.Parameters" 
%><%@ page errorPage="../../ErrorPagePortal.jsp" 
%><jsp:useBean id="jsr168Portlet" scope="session" class="fr.paris.lutece.plugins.jsr168.web.portlet.Jsr168PortletJspBean" 
/><%
	// Attention: no text must out from this page, all definition must be joined

	try 
	{
		LocalVariables.setLocal( config, request, response );

		if ( ! jsr168Portlet.realiseAction( request ) ) 
		{
			if ( request.getParameter(Parameters.PAGE_ID) != null ) 
			{
				response.sendRedirect( "../../Portal.jsp?" + Parameters.PAGE_ID + "=" + request.getParameter( Parameters.PAGE_ID ) );
			} 
			else 
			{
				response.sendRedirect( "../../Portal.jsp" );
			}
		}
	} 
	finally 
	{
		LocalVariables.setLocal( null, null, null );
	}
%>