<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="jsr168Portlet" scope="session" class="fr.paris.lutece.plugins.jsr168.web.portlet.Jsr168PortletJspBean" />

<%
    jsr168Portlet.init( request, jsr168Portlet.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect(  jsr168Portlet.doModify( request )  );
%>
