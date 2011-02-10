<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="portlet">
	<div class="portlet">
	    <xsl:if test="not(string(display-portlet-title)='1')">
			<h3 class="portlet-header">
				<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
	                	<span style="text-align: right;"><xsl:apply-templates select="portlet-buttons" /></span>
			</h3>
            </xsl:if>
	    <div class="portlet-content">
	        <xsl:apply-templates select="html-portlet" />
	    </div>
	</div>
</xsl:template>

<xsl:template match="html-portlet">
	<xsl:apply-templates select="html-portlet-content" />
</xsl:template>

<xsl:template match="html-portlet-content">
	<p><xsl:value-of disable-output-escaping="yes" select="." /></p>
</xsl:template>

<xsl:template match="portlet-buttons">
	<xsl:for-each select="modes/button">
		<a>
			<xsl:attribute name="href"><xsl:value-of select="@link"/></xsl:attribute>
			<img border="0">
				<xsl:attribute name="src"><xsl:value-of select="@image"/></xsl:attribute>
				<xsl:attribute name="src"><xsl:value-of select="@image"/></xsl:attribute>
			</img>
		</a>
	</xsl:for-each>
	<xsl:for-each select="states/button">
		<a>
			<xsl:attribute name="href"><xsl:value-of select="@link"/></xsl:attribute>
			<img border="0">
				<xsl:attribute name="src"><xsl:value-of select="@image"/></xsl:attribute>
			</img>
		</a>
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>

