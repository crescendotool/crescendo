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
    : (link | interface)* EOF
    ;
    
link
    : ID '=' ID '.' ID
    ;

interface
    : OUTPUT '=' idList
    | INPUT '=' idList
    | SHARED '=' idList
    | EVENT '=' idList
    ;

idList 
    : ID (',' ID)*;
    


