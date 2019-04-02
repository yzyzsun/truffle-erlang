/*
    Adapted from the Core Erlang 1.0.3 language specification.
    Optional annotations are removed for simplicity.
*/
grammar CoreErlang;

module
    : 'module' ATOM exports attributes functionDefinition* 'end'
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

functionDefinition
    : functionName '=' fun
    ;

constant
    : atomicLiteral # literalConstant
    | '{' (constant (',' constant)*)? '}' # tupleConstant
    | '[' constant (',' constant)* ']' # listConstant
    | '[' constant (',' constant)* '|' constant ']' # consConstant
    ;

atomicLiteral
    : INTEGER # integer
    | FLOAT   # float
    | CHAR    # char
    | STRING  # string
    | ATOM    # atom
    | '[' ']' # nil
    ;

fun
    : 'fun' '(' (VARIABLE_NAME (',' VARIABLE_NAME)*)? ')' '->' expression
    ;

expression
    : singleExpression
    | '<' (singleExpression (',' singleExpression)*)? '>'
    ;

singleExpression
    : VARIABLE_NAME # variable
    | atomicLiteral # literal
    | functionName # fname
    | fun # f
    | '{' (expression (',' expression)*)? '}' # tuple
    | '[' expression (',' expression)* ']' # list
    | '[' expression (',' expression)* '|' expression ']' # cons
    | '#' '{' (bitstring (',' bitstring)*)? '}' '#' # binary
    | 'let' variables '=' expression 'in' expression # let
    | 'case' expression 'of' clause+ 'end' # case
    | 'letrec' functionDefinition* 'in' expression # letrec
    | 'apply' expression '(' (expression (',' expression)*)? ')' # application
    | 'call' expression ':' expression '(' (expression (',' expression)*)? ')' # interModuleCall
    | 'primop' ATOM '(' (expression (',' expression)*)? ')' # primOpCall
    | 'try' expression 'of' variables '->' expression 'catch' variables '->' expression # try
    | 'receive' clause* 'after' expression '->' expression # receive
    | 'do' expression expression # sequencing
    | 'catch' expression # catching
    ;

bitstring
    : '#' '<' expression '>' '(' (expression (',' expression)*)? ')'
    ;

variables
    : VARIABLE_NAME
    | '<' (VARIABLE_NAME (',' VARIABLE_NAME)*)? '>'
    ;

clause
    : patterns 'when' expression '->' expression
    ;

patterns
    : pattern
    | '<' (pattern (',' pattern)*)? '>'
    ;

pattern
    : VARIABLE_NAME # variablePattern
    | atomicLiteral # literalPattern
    | '{' (pattern (',' pattern)*)? '}' # tuplePattern
    | '[' pattern (',' pattern)* ']' # listPattern
    | '[' pattern (',' pattern)* '|' pattern ']' # consPattern
    | '#' '{' (bitstringPattern (',' bitstringPattern)*)? '}' '#' # binaryPattern
    | VARIABLE_NAME '=' pattern # aliasPattern
    ;

bitstringPattern
    : '#' '<' pattern '>' '(' (expression (',' expression)*)? ')'
    ;

INTEGER       : SIGN? DIGIT+;
FLOAT         : SIGN? DIGIT+ '.' DIGIT+ (('E' | 'e') SIGN? DIGIT+)?;
CHAR          : '$' (~[\u0000-\u001f \\] | ESCAPE);
STRING        : '"' (~'"' | '\\"')* '"';
ATOM          : '\'' (~'\'' | '\\\'')* '\'';
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
