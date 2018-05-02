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
package fr.paris.lutece.plugins.jsr168.pluto;

import fr.paris.lutece.portal.service.util.AppLogService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 * Wrap <code>HttpServletResponse</code> for Lutece processing:
 * We can't output direct flow to response.
 * We must keep all output, and give to {@link fr.paris.lutece.plugins.jsr168.pluto.LuteceToPlutoConnector}
 * for return it for render.
 */
public class LuteceHttpServletResponse extends HttpServletResponseWrapper
{
    private ServletOutputStream _servletOutputStream;
    private PrintWriter _printWriter;
    private ByteArrayOutputStream _buffer;

    /**
     * Initialize buffer and stream of the instance for capture
     * chars flow.
     *
     * @param response the real (parent) <code>HttpServletResponse</code> instance
     */
    public LuteceHttpServletResponse( HttpServletResponse response )
    {
        super( response );

        _buffer = new ByteArrayOutputStream(  );
        _servletOutputStream = new ServletOutputStream(  )
                {
                    public void write( int b )
                    {
                        _buffer.write( b );
                    }
                };
        _printWriter = new PrintWriter( _servletOutputStream );
    }

    /**
     * Return the content of the buffer
     *
     * @return the content of the buffer
     */
    public String getBufferString(  )
    {
        flushBuffer(  );

        try
        {
            return _buffer.toString( "ISO-8859-1" );
        }
        catch ( UnsupportedEncodingException e )
        {
            // UTF-8 est un encoding standard de JAVA
            AppLogService.error( e.getMessage(  ), e );
            throw new RuntimeException( e.getMessage(  ) );
        }
    }

    /**
     * Overridden: flush local buffer instead "real" buffer (in parent).
     *
     * @see javax.servlet.ServletResponse#flushBuffer()
     */
    public void flushBuffer(  )
    {
        // super.flushBuffer(  );
        _printWriter.flush(  );

        try
        {
            _servletOutputStream.flush(  );
        }
        catch ( IOException e )
        {
            // Nothing to do
            AppLogService.error( "Unreachable catch case", e );
        }
    }

    /**
     * Overridden: return local <code>OutputStream</code>.
     *
     * @see javax.servlet.ServletResponse#getOutputStream()
     */
    public ServletOutputStream getOutputStream(  )
    {
        // return super.getOutputStream(  );
        return _servletOutputStream;
    }

    /**
     * Overridden: return local <code>PrintWriter</code>.
     *
     * @see javax.servlet.ServletResponse#getWriter()
     */
    public PrintWriter getWriter(  )
    {
        // return super.getWriter(  );
        return _printWriter;
    }

    /**
     * Overridden: flush buffer and clear content
     *
     * @see javax.servlet.ServletResponse#reset()
     */
    public void reset(  )
    {
        // return super.reset(  );
        flushBuffer(  );
        _buffer.reset(  );
    }

    /**
     * Overridden: flush buffer and clear content
     *
     * @see javax.servlet.ServletResponse#resetBuffer()
     */
    public void resetBuffer(  )
    {
        // return super.resetBuffer(  );
        throw new UnsupportedOperationException( "resetBuffer n'est pas supporte!" );
    }

    /**
     * Overridden: unsupported operation
     *
     * @see HttpServletResponse#sendError(int)
     */
    public void sendError( int nHttpErrorCode )
    {
        // return super.sendError( nHttpErrorCode );
        throw new UnsupportedOperationException( "sendError n'est pas supporte!" );
    }

    /**
     * Overridden: unsupported operation
     *
     * @see HttpServletResponse#sendError(int, java.lang.String)
     */
    public void sendError( int arg0, String arg1 ) throws IOException
    {
        throw new UnsupportedOperationException( "sendError n'est pas supporte!" );
    }

    /**
     * Overridden: unsupported operation
     *
     * @see HttpServletResponse#sendRedirect(java.lang.String)
     */
    public void sendRedirect( String arg0 ) throws IOException
    {
        throw new UnsupportedOperationException( "sendRedirect n'est pas supporte!" );
    }

    /**
     * Overridden: empty operation
     *
     * @see javax.servlet.ServletResponse#setBufferSize(int)
     */
    public void setBufferSize( int nSize )
    {
        // super.setBufferSize( nSize );
    }

    /**
     * Overridden: unsupported operation
     *
     * @see javax.servlet.ServletResponse#setContentLength(int)
     */
    public void setContentLength( int nContentLength )
    {
        // super.setContentType( nContentLength );
        throw new UnsupportedOperationException( "setContentLength n'est pas supporte!" );
    }

    /**
     * Overridden: empty operation
     *
     * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
     */
    public void setContentType( String strContentType )
    {
        // super.setContentType( strContentType );
    }

    /**
     * Overridden: unsupported operation
     *
     * @see HttpServletResponse#setStatus(int)
     */
    public void setStatus( int nHttpStatus )
    {
        throw new UnsupportedOperationException( "setStatus n'est pas supporte!" );
    }

    /**
     * Overridden: unsupported operation
     *
     * @see HttpServletResponse#setStatus(int, java.lang.String)
     */
    public void setStatus( int arg0, String arg1 )
    {
        throw new UnsupportedOperationException( "setStatus n'est pas supporte!" );
    }

    /**
     * Overridden: empty operation (warn logged)
     *
     * @see javax.servlet.ServletResponseWrapper#setResponse(javax.servlet.ServletResponse)
     */
    public void setResponse( ServletResponse response )
    {
        AppLogService.info( "Plugin JSR 168: Try to redefine a response object!" );
    }

    /**
     * Overridden: return local buffer size
     *
     * @see javax.servlet.ServletResponse#getBufferSize()
     */
    public int getBufferSize(  )
    {
        // return super.getBufferSize(  );
        return _buffer.size(  );
    }

    /**
     * Overridden: proxy operation (info logged)
     *
     * @see javax.servlet.ServletResponseWrapper#getResponse()
     */
    public ServletResponse getResponse(  )
    {
        ServletResponse response = super.getResponse(  );

        AppLogService.debug( "Plugin JSR 168: getResponse() asked (response class is " +
            response.getClass(  ).getName(  ) + ")." );

        return response;
    }

    /**
     * Overridden: always "true" operation (info logged)
     *
     * @see javax.servlet.ServletResponse#isCommitted()
     */
    public boolean isCommitted(  )
    {
        boolean comitted = super.isCommitted(  );

        AppLogService.debug( "Plugin JSR 168: isCommited asked (commited: " + comitted + ")." );

        return true;
    }
}
