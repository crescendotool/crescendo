grammar subs;

options {
  language = Java;
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
