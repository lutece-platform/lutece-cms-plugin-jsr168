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
package fr.paris.lutece.plugins.jsr168.pluto.services.log;

import fr.paris.lutece.plugins.jsr168.pluto.LutecePlutoConstant;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.logging.LogFactory;

import org.apache.pluto.portalImpl.services.log.LogService;
import org.apache.pluto.portalImpl.services.log.LoggerImpl;
import org.apache.pluto.portalImpl.util.Properties;
import org.apache.pluto.services.log.Logger;


/**
 * Log service linked to Lutece/Pluto
 * TODO : see if this class can be removed
 */
public class LogServiceImpl extends LogService
{
    private boolean _bIsDebugEnabled;
    private boolean _bIsInfoEnabled;
    private boolean _bIsWarnEnabled;
    private boolean _bIsErrorEnabled;

    /**
     * Initialize service with LogService
     *
     * @param aProperties Initialize properties
     */
    public void init( Properties aProperties )
    {
        _bIsDebugEnabled = aProperties.getBoolean( LutecePlutoConstant.CONFIG_SERVICES_LOG_DEBUG_ENABLED,
                LutecePlutoConstant.CONFIG_SERVICES_LOG_DEBUG_ENABLED_DEFAULT ).booleanValue(  );
        _bIsInfoEnabled = aProperties.getBoolean( LutecePlutoConstant.CONFIG_SERVICES_LOG_INFO_ENABLED,
                LutecePlutoConstant.CONFIG_SERVICES_LOG_INFO_ENABLED_DEFAULT ).booleanValue(  );
        _bIsWarnEnabled = aProperties.getBoolean( LutecePlutoConstant.CONFIG_SERVICES_LOG_WARN_ENABLED,
                LutecePlutoConstant.CONFIG_SERVICES_LOG_WARN_ENABLED_DEFAULT ).booleanValue(  );
        _bIsErrorEnabled = aProperties.getBoolean( LutecePlutoConstant.CONFIG_SERVICES_LOG_ERROR_ENABLED,
                LutecePlutoConstant.CONFIG_SERVICES_LOG_ERROR_ENABLED_DEFAULT ).booleanValue(  );
    }

    /**
     * @see org.apache.pluto.services.log.LogService#isDebugEnabled(java.lang.String)
     */
    public boolean isDebugEnabled( String aComponent )
    {
        return _bIsDebugEnabled;
    }

    /**
     * @see org.apache.pluto.services.log.LogService#isInfoEnabled(java.lang.String)
     */
    public boolean isInfoEnabled( String aComponent )
    {
        return _bIsInfoEnabled;
    }

    /**
     * @see org.apache.pluto.services.log.LogService#isWarnEnabled(java.lang.String)
     */
    public boolean isWarnEnabled( String aComponent )
    {
        return _bIsWarnEnabled;
    }

    /**
     * @see org.apache.pluto.services.log.LogService#isErrorEnabled(java.lang.String)
     */
    public boolean isErrorEnabled( String aComponent )
    {
        return _bIsErrorEnabled;
    }

    /**
     * @see org.apache.pluto.services.log.LogService#debug(java.lang.String, java.lang.String)
     */
    public void debug( String aComponent, String aMessage )
    {
        AppLogService.debug( aComponent + ": " + aMessage );
    }

    /**
     * @see org.apache.pluto.services.log.LogService#debug(java.lang.String, java.lang.String, java.lang.Throwable)
     */
    public void debug( String aComponent, String aMessage, Throwable aThrowable )
    {
        AppLogService.debug( aComponent + ": " + aMessage, aThrowable );
    }

    /**
     * @see org.apache.pluto.services.log.LogService#info(java.lang.String, java.lang.String)
     */
    public void info( String aComponent, String aMessage )
    {
        AppLogService.info( aComponent + ": " + aMessage );
    }

    /**
     * @see org.apache.pluto.services.log.LogService#warn(java.lang.String, java.lang.String)
     */
    public void warn( String aComponent, String aMessage )
    {
        AppLogService.info( aComponent + ": " + aMessage );
    }

    /**
     * @see org.apache.pluto.services.log.LogService#error(java.lang.String, java.lang.String, java.lang.Throwable)
     */
    public void error( String aComponent, String aMessage, Throwable aThrowable )
    {
        AppLogService.error( aComponent + ": " + aMessage, aThrowable );
    }

    /**
     * @see org.apache.pluto.services.log.LogService#error(java.lang.String, java.lang.Throwable)
     */
    public void error( String aComponent, Throwable aThrowable )
    {
        AppLogService.error( aComponent, aThrowable );
    }

    // FIXME
    public Logger getLogger( String component )
    {
        return new LoggerImpl( LogFactory.getLog( component ) );
    }

    //  FIXME
    public Logger getLogger( Class klass )
    {
        return new LoggerImpl( LogFactory.getLog( klass ) );
    }
}
