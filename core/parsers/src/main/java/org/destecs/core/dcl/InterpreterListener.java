package org.destecs.core.dcl;

import org.antlr.runtime.Token;

/** How to response to messages and errors from interpreter */
public interface InterpreterListener {
    public void info(String msg);
    public void error(String msg);
    public void error(String msg, Exception e);
    public void error(String msg, Token t);
}
