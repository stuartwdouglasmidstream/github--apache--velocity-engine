/* Generated By:JJTree: Do not edit this line. ASTDirective.java */

/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */


/**
 * This class is responsible for handling the pluggable
 * directives in VTL. ex.  #foreach()
 * 
 * Please look at the Parser.jjt file which is
 * what controls the generation of this class.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: ASTDirective.java,v 1.9 2000/11/19 23:16:07 geirm Exp $ 
*/

package org.apache.velocity.runtime.parser.node;

import java.io.Writer;
import java.io.IOException;

import org.apache.velocity.Context;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.Parse;
import org.apache.velocity.runtime.parser.*;
import org.apache.velocity.runtime.Runtime;

public class ASTDirective extends SimpleNode
{
    private Directive directive;
    private String strDirectiveName_ = "";
    private boolean isDirective;

    int iParseDepth_ = 0;

    public ASTDirective(int id)
    {
        super(id);
    }

    public ASTDirective(Parser p, int id)
    {
        super(p, id);
    }


    /** Accept the visitor. **/
    public Object jjtAccept(ParserVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }
    
    public Object init(Context context, Object data) throws Exception
    {
        if (parser.isDirective( strDirectiveName_ ))
        {
            isDirective = true;
            
            directive = (Directive) parser.getDirective( strDirectiveName_ )
                .getClass().newInstance();
            
            /*
             *  we need to treat #parse differently, alas
             */
            if (strDirectiveName_.equals("parse"))
                ( (Parse) directive).setParseDepth( iParseDepth_ );
            
            directive.init(context,this);
        }          
        else if (Runtime.isVelocimacro( strDirectiveName_  )) 
        {
            /*
             *  we seem to be a Velocimacro.
             */

            isDirective = true;
            directive = (Directive) Runtime.getVelocimacro( strDirectiveName_ );
            directive.init( context, this );
        } 
        else
        {
            isDirective = false;
        }            
    
        return data;
    }

    public boolean render(Context context, Writer writer)
        throws IOException
    {
     
        /*
         *  normal processing
         */

        if (isDirective)
        {           
            directive.render(context, writer, this);
        }
        else
        {
            writer.write( "#" +   strDirectiveName_);
        }

        return true;
    }

    /**
     *   Sets the directive name.  Used by the parser.  This keeps us from having to 
     *   dig it out of the token stream and gives the parse the change to override.
     */
    public void setDirectiveName( String str )
    {
        strDirectiveName_ = str;
        return;
    }

    /**
     *  Gets the name of this directive.
     */
    public String getDirectiveName()
    {
        return strDirectiveName_;
    }

    /**
     *  Sets the parse depth for recursion limitataion
     */
    public void setParserDepth( int i)
    {
        iParseDepth_ = i;
    }
}




