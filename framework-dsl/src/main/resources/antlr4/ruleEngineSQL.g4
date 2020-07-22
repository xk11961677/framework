grammar ruleEngineSQL;

select
    : SELECT subscribeAttr+ FROM WHERE filterAttrs+
    ;

subscribeAttr
    : attr+
    ;

attr
    : ID FIELD ',';

filterAttrs
    : filterAttr+ OP FIELD;

filterAttr
    : ID FIELD ;

ID  : [a-zA-Z0-9]+'#' ;

FIELD :  [a-zA-Z0-9]+ ;

SELECT: 'select' ;

FROM : 'from' ;

WHERE : 'where' ;

OP : '>'
   | '<'
   | '='
   ;

WS  : [ \t\r\n]+ -> skip ;    // toss out whitespace