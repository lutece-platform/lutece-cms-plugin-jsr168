/*
 * Copyright (c) 2002-2014, Mairie de Paris
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


/**
 * Exception throwed by XML Factory utility
 */
public class XMLFactoryException extends Exception
{
    private final Exception _cause;

    /**
     * Construct the exception with a description message
     *
         * @param strMessage The description message of the exception
         */
    public XMLFactoryException( String strMessage )
    {
        this( strMessage, null );
    }

    /**
     * Construct the exception with a description message
     * and a cause (source exception)
     *
         * @param strMessage The description message of the exception
         * @param cause The cause of this exception
         */
    public XMLFactoryException( String strMessage, Exception cause )
    {
        super( strMessage );
        _cause = cause;
    }

    /**
     * Construct the exception with the cause (source exception).
     * The message associated with this exception will be the message
     * of <code>cause</code> exception.
     *
         * @param cause The cause of this exception
         */
    public XMLFactoryException( Exception cause )
    {
        super( cause.getMessage(  ) );
        _cause = cause;
    }

    /**
     * Return the cause of exception (or <code>null</code> if no cause
     * was specified)
     *
         * @return the cause of exception (or <code>null</code> if no cause was specified)
         */
    public Exception getCause(  )
    {
        return _cause;
    }
}
