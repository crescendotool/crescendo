grammar VdmLink;

options {
  language = Java;
}

tokens{
  OUTPUT = 'vdm.outputs';
  INPUT = 'vdm.inputs';
  SHARED = 'vdm.sdp';
  EVENT = 'vdm.events';
}

@header {
package org.destecs.core.parsers.vdmlink;

}

@lexer::header{  
package org.destecs.core.parsers.vdmlink;
} 

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

ID  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;
    
COMMENT
    :   '--' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

start 
    : (link | intf)* EOF
    ;
    
link
    : ID '=' ID '.' ID
    ;

intf
    : OUTPUT '=' idList
    | INPUT '=' idList
    | SHARED '=' idList
    | EVENT '=' idList
    ;

idList 
    : ID (',' ID)*;
    


