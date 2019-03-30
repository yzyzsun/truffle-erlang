// Derived from the Core Erlang 1.0.3 language specification

grammar CoreErlang;

annotatedModule
    : module
    | '(' module '-|' '[' (constant (',' constant)*)? ']' ')'
    ;

module
    : 'module' ATOM moduleHeader moduleBody 'end'
    ;

moduleHeader
    : exports attributes
    ;

exports
    : '[' (functionName (',' functionName)*)? ']'
    ;

functionName
    : ATOM '/' INTEGER
    ;

attributes
    : 'attributes' '[' (moduleAttribute (',' moduleAttribute)*)? ']'
    ;

moduleAttribute
    : ATOM '=' constant
    ;

moduleBody
    : functionDefinition*
    ;

functionDefinition
    : annotatedFunctionName '=' annotatedFun
    ;

annotatedFunctionName
    : functionName
    | '(' functionName '-|' '[' (constant (',' constant)*)? ']' ')'
    ;

annotatedFun
    : fun
    | '(' fun '-|' '[' (constant (',' constant)*)? ']' ')'
    ;

constant
    : atomicLiteral
    | '{' (constant (',' constant)*)? '}'
    | '[' constant (',' constant)* ']'
    | '[' constant (',' constant)* '|' constant ']'
    ;

atomicLiteral
    : INTEGER
    | FLOAT
    | ATOM
    | nil
    | CHAR
    | STRING
    ;

nil
    : '[' ']'
    ;

annotatedVariable
    : VARIABLE_NAME
    | '(' VARIABLE_NAME '-|' '[' (constant (',' constant)*)? ']' ')'
    ;

annotatedPattern
    : annotatedVariable
    | pattern
    | '(' pattern '-|' '[' (constant (',' constant)*)? ']' ')'
    ;

pattern
    : atomicLiteral
    | '{' (annotatedPattern (',' annotatedPattern)*)? '}'
    | '[' annotatedPattern (',' annotatedPattern)* ']'
    | '[' annotatedPattern (',' annotatedPattern)* '|' annotatedPattern ']'
    | '#' '{' (bitstringPattern (',' bitstringPattern)*)? '}' '#'
    | annotatedVariable '=' annotatedPattern
    ;

bitstringPattern
    : '#' '<' annotatedPattern '>' '(' (expression (',' expression)*)? ')'
    ;

expression
    : annotatedValueList
    | annotatedSingleExpression
    ;

annotatedValueList
    : valueList
    | '(' valueList '-|' '[' (constant (',' constant)*)? ']' ')'
    ;

valueList
    : '<' (annotatedSingleExpression (',' annotatedSingleExpression)*)? '>'
    ;

annotatedSingleExpression
    : singleExpression
    | '(' singleExpression '-|' '[' (constant (',' constant)*)? ']' ')'
    ;

singleExpression
    : atomicLiteral
    | VARIABLE_NAME
    | functionName
    | tuple
    | list
    | binary
    | let
    | case_
    | fun
    | letrec
    | application
    | interModuleCall
    | primOpCall
    | try_
    | receive
    | sequencing
    | catch_
    ;

tuple
    : '{' (expression (',' expression)*)? '}'
    ;

list
    : '[' expression (',' expression)* ']'
    | '[' expression (',' expression)* '|' expression ']'
    ;

binary
    : '#' '{' (bitstring (',' bitstring)*)? '}' '#'
    ;

bitstring
    : '#' '<' expression '>' '(' (expression (',' expression)*)? ')'
    ;

let
    : 'let' variables '=' expression 'in' expression
    ;

variables
    : annotatedVariable
    | '<' (annotatedVariable (',' annotatedVariable)*)? '>'
    ;

case_
    : 'case' expression 'of' annotatedClause+ 'end'
    ;

annotatedClause
    : clause
    | '(' clause '-|' '[' (constant (',' constant)*)? ']' ')'
    ;

clause
    : patterns guard '->' expression
    ;

patterns
    : annotatedPattern
    | '<' (annotatedPattern (',' annotatedPattern)*)? '>'
    ;

guard
    : 'when' expression
    ;

fun
    : 'fun' '(' (annotatedVariable (',' annotatedVariable)*)? ')' '->' expression
    ;

letrec
    : 'letrec' functionDefinition* 'in' expression
    ;

application
    : 'apply' expression '(' (expression (',' expression)*)? ')'
    ;

interModuleCall
    : 'call' expression ':' expression '(' (expression (',' expression)*)? ')'
    ;

primOpCall
    : 'primop' ATOM '(' (expression (',' expression)*)? ')'
    ;

try_
    : 'try' expression 'of' variables '->' expression 'catch' variables '->' expression
    ;

receive
    : 'receive' annotatedClause* timeout
    ;

timeout
    : 'after' expression '->' expression
    ;

sequencing
    : 'do' expression expression
    ;

catch_
    : 'catch' expression
    ;

INTEGER       : SIGN? DIGIT+;
FLOAT         : SIGN? DIGIT+ '.' DIGIT+ (('E' | 'e') SIGN? DIGIT+)?;
ATOM          : '\'' (~[\r\n\u0000-\u001f\\'] | ESCAPE)* '\'';
CHAR          : '$' (~[\r\n\u0000-\u001f \\] | ESCAPE);
STRING        : '"' (~[\r\n\u0000-\u001f\\"] | ESCAPE)* '"';
VARIABLE_NAME : (UPPERCASE | ('_' NAME_CHAR)) NAME_CHAR*;

fragment SIGN        : '+' | '-';
fragment DIGIT       : [0-9];
fragment UPPERCASE   : [A-Z\u00c0-\u00d6\u00d8-\u00de];
fragment LOWERCASE   : [a-z\u00df-\u00f6\u00f8-\u00ff];
fragment NAME_CHAR   : UPPERCASE | LOWERCASE | DIGIT | '@' | '_';
fragment ESCAPE      : '\\' (OCTAL | ('^' CTRL_CHAR) | ESCAPE_CHAR);
fragment OCTAL_DIGIT : [0-7];
fragment OCTAL       : OCTAL_DIGIT (OCTAL_DIGIT OCTAL_DIGIT?)?;
fragment CTRL_CHAR   : [\u0040-\u005f];
fragment ESCAPE_CHAR : [bdefnrstv"'\\];

WHITESPACE : [ \t\r\n] -> skip;
COMMENT : '%' ~[\r\n]* -> skip;
