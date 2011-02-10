<%@ page import="fr.paris.lutece.portal.web.LocalVariables" %>
<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="jsr168Portlet" scope="session" class="fr.paris.lutece.plugins.jsr168.web.portlet.Jsr168PortletJspBean" />

<% jsr168Portlet.init( request, jsr168Portlet.RIGHT_MANAGE_ADMIN_SITE ); %>

<% 
   try { 
		LocalVariables.setLocal( config, request, response );
%>
	<%= jsr168Portlet.getCreate( request ) %>
<% 
   } finally { 
		LocalVariables.setLocal( null, null, null );
   }
%>
<%@ include file="../../AdminFooter.jsp" %>
