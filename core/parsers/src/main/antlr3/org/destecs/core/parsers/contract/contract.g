grammar contract;

options{
	language=Java;
	output=AST;
}

tokens {
	CONTRACT = 'contract';
	//DESIGN_PARAMETERS = 'design_parameters';
	DESIGN_PARAMETER = 'design_parameter';
	//VARIABLES = 'variables';
	MONITORED = 'monitored';
	CONTROLLED = 'controlled';
	REAL = 'real';
	BOOL = 'bool';
	//EVENTS = 'events';
	EVENT = 'event';
	END = 'end';
	ASSIGN = ':=';
//	BODYDEF = 'BODYDEF';
//	TRUE = 'true';
//	FALSE = 'false';
}

@header {
package org.destecs.core.parsers.contract;
}

@lexer::header{  
package org.destecs.core.parsers.contract;
} 

@members {

}

BOOL_VAL 
	: 'true'
	| 'false'
	;

COMMENT
    :   '--' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    | 'design_parameters' {$channel=HIDDEN;}
    | 'variables' {$channel=HIDDEN;}
    | 'events' {$channel=HIDDEN;}
    ;
	
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;
    
	



    

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
    


contract 
	: CONTRACT ID body* END ID EOF -> CONTRACT ID+ ^(body)
	;
	
body 
	: parameters
	| variables
	| events
	;

parameters 
	: DESIGN_PARAMETER type ID ';' -> ^(ID type)
	;

variables 
	: kind type ID ASSIGN value ';' -> ^(ID kind type value)
	;

events	: EVENT ID ';' -> ID
	;	

type : REAL -> REAL
	| BOOL -> BOOL;

value 
	: FLOAT -> FLOAT
	| BOOL_VAL -> BOOL_VAL
	;

kind 	
	: MONITORED -> MONITORED
	| CONTROLLED -> CONTROLLED
	;
