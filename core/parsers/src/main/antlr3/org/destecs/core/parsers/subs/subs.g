grammar subs;

options {
  language = Java;
}

@header {
package org.destecs.core.parsers.subs;


}

@lexer::header{  
package org.destecs.core.parsers.subs;
}  

fragment LETTER : ('a'..'z' | 'A'..'Z') ;
fragment DIGIT : '0'..'9';
IDENT: LETTER (LETTER | DIGIT)*;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

start : sub subs;

sub : IDENT '/' IDENT;

subs : 
     | ',' sub subs
     ;
