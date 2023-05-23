grammar Grammaire;

options {
	language = Java;
}

@header {
import Expression.*;
import Statement.*;
import Boolean.*;
import Util.*;
import Number.*;}

@members {
AbstractClass left;
AbstractClass right;
AbstractClass argument;
float b;
int i;
int id;
int lab;	
Numbers xmin, xmax;
String nom;	
AbstractClassB cond;
StatementList thenBlock;
StatementList elseBlock;
StatementList body;
AbstractClass prec ;
Environment env;}

expression returns [AbstractClass result]
:
	  left = expression MULT right = expression
	{
			 assert($left.result != null);  
			 assert($right.result != null); 
			 AbstractClass.newLabDecrease();
			$result = new Multiply($left.result, $right.result);
			
	 }
	
	 | left = expression MULT SHARP CONSTI right = expression
	{
			 assert($left.result != null);  
			 assert($right.result != null); 
			 AbstractClass.newLabDecrease();
			 int i = Integer.parseInt($CONSTI.text);
			 $result = new Multiply($left.result, $right.result, i);
			
	 }
		 		                           
    | left = expression DIV right = expression
	{
			 assert($left.result != null);  
			 assert($right.result != null); 
			 AbstractClass.newLabDecrease();
			$result = new Divide($left.result, $right.result);
			
			}
			
	    | left = expression DIV SHARP CONSTI right = expression
	{
			 assert($left.result != null);  
			 assert($right.result != null); 
			 AbstractClass.newLabDecrease();
			 int i = Integer.parseInt($CONSTI.text); 
			$result = new Divide($left.result, $right.result, i);
			
			}
	
	| left = expression PLUS right = expression
	{ 
			 assert($left.result != null);  
			 assert($right.result != null);
			AbstractClass.newLabDecrease();
			$result = new Add($left.result,$right.result);
			
			}

	| left = expression PLUS SHARP CONSTI right = expression
	{ 
			 assert($left.result != null);  
			 assert($right.result != null);
			AbstractClass.newLabDecrease();
		    int i = Integer.parseInt($CONSTI.text); 
			$result = new Add($left.result,$right.result, i);
			
			}

	| left = expression MINUS right = expression
	{
			 assert($left.result != null);  
			 assert($right.result != null); 
			 AbstractClass.newLabDecrease();
			$result = new Subtract($left.result,$right.result);}
			
			
	| left = expression MINUS SHARP CONSTI right = expression
	{ 
			 assert($left.result != null);  
			 assert($right.result != null);
			AbstractClass.newLabDecrease();
		    int i = Integer.parseInt($CONSTI.text); 
			$result = new Subtract($left.result,$right.result, i);
			
			}		

	| LPAREN argument = expression RPAREN
	{
			 assert($argument.result != null);  
			$result = $argument.result; }
			
	| SINUS LPAREN argument = expression RPAREN 
	{
		    assert($argument.result != null);  
			$result = new Sin($argument.result); }
			

	| SINUS LPAREN argument = expression RPAREN SHARP CONSTI
	
	{       assert($argument.result != null); 
		    int i = Integer.parseInt($CONSTI.text); 
			$result = new Sin($argument.result, i); }
	| COSINUS LPAREN argument = expression RPAREN
	{
			 assert($argument.result != null);  
			$result = new Cos($argument.result); }
	| TAN LPAREN argument = expression RPAREN
	{
			 assert($argument.result != null);  
			$result = new Tan($argument.result); }
	| ASIN LPAREN argument = expression RPAREN
	{
			 assert($argument.result != null);  
			$result = new ArcSin($argument.result); }		

	| ACOS LPAREN argument = expression RPAREN
	{
			 assert($argument.result != null);  
			$result = new ArcCos($argument.result); }
	| ATAN LPAREN argument = expression RPAREN
	{
			 assert($argument.result != null);  
			$result = new ArcTan($argument.result); }
			
	| EXPONENT LPAREN argument = expression RPAREN
	{
			 assert($argument.result != null);  
			$result = new Exponent($argument.result); }
    | LOG LPAREN argument = expression RPAREN
	{
			 assert($argument.result != null);  
			$result = new Log($argument.result); }
		

	| SQRT LPAREN argument = expression RPAREN
	{
			 assert($argument.result != null); 
			$result = new Sqrt($argument.result);
			
					 		                    }
	|(
		id = IDENTIFIER
	)
	{$result = new Variable( $id.getText());}

	|
	(
		id = IDENTIFIER INF e = expression SUP
	)
	{$result = new ExprTabElement( $id.getText(),$e.result) ; }

	| CONSTF
	{ 
			lab = AbstractClass.newLab();    	 	
  			Numbers r = new FloatNumbers(Float.parseFloat($CONSTF.text),lab);
    		 $result=  new Intervalle(r,r,lab);
    }

	| CONSTF SHARP CONSTI
	{ 
			lab = AbstractClass.newLab();    	 	
  			Numbers r = new FloatNumbers(Float.parseFloat($CONSTF.text),lab);
  			int i = Integer.parseInt($CONSTI.text);
  			$result=  new Intervalle(r, r, i,lab);//recuperer le  chiffre apres le sharp
    }

	| CONSTI
	{ 
     	    
         lab=AbstractClass.newLab();
         Numbers ii = new IntNumbers(Integer.parseInt($CONSTI.text),lab);
          $result=  new Intervalle(ii, ii,lab);
    }

	| INF xmin = baseexpr VIRG xmax = baseexpr SUP
	{
    	
    	AbstractClass.newLabDecrease2();
    	lab =AbstractClass.newLab();
    	Numbers xmin = new FloatNumbers(Float.parseFloat($xmin.text),lab);
    	Numbers xmax = new FloatNumbers(Float.parseFloat($xmax.text),lab);    
        assert($xmin.result != null);  
	    assert($xmax.result != null);    
       $result=  new Intervalle(xmin, xmax,lab);
    
    	}

;






baseexpr returns [AbstractClass result ]
:
	(
		id = IDENTIFIER
	)
	{$result = new Variable( $id.getText());}

	|
	(
		id = IDENTIFIER INF e = expression SUP
	)
	{$result = new ExprTabElement( $id.getText(),$e.result) ; }

	| CONSTF
	{ 
			lab = AbstractClass.newLab();    	 	
  			Numbers r = new FloatNumbers(Float.parseFloat($CONSTF.text),lab);
    		 $result=  new Intervalle(r, r,lab);
    }
    | CONSTF SHARP  CONSTI
	{ 
			lab = AbstractClass.newLab();    	 	
  			Numbers r = new FloatNumbers(Float.parseFloat($CONSTF.text),lab);
    		 $result=  new Intervalle(r, r,lab);
    }
	| CONSTI
	{ 
     	    
         lab=AbstractClass.newLab();
         Numbers ii = new IntNumbers(Integer.parseInt($CONSTI.text),lab);
          $result=  new Intervalle(ii, ii,lab);
    }
   
	| INF xmin = baseexpr VIRG xmax = baseexpr SUP
	{
    	
    	AbstractClass.newLabDecrease2();
    	lab =AbstractClass.newLab();
    	Numbers xmin = new FloatNumbers(Float.parseFloat($xmin.text),lab);
    	Numbers xmax = new FloatNumbers(Float.parseFloat($xmax.text),lab);    
        assert($xmin.result != null);  
	    assert($xmax.result != null);    
       $result=  new Intervalle(xmin, xmax,lab);
    
    	}

;

statementList returns [StatementList st]
:
	(
		s1 = statement
	) PV
	(
		s2 = statementList
	)
	{
    
    	
    	$st = new StatementList($s1.result,$s2.st);
    }

	|
	(
		s1 = statement
	) PV
	{
    	    	
    	$st = new StatementList($s1.result);
    }

;

statement returns [AbstractClassS result]
:
	(
		id = IDENTIFIER ASSIGN right = expression
	)
	{$result = new Assign($right.result,$id.text); }

	|
	(
		IF LPAREN condition = boolexp RPAREN LBRACKET thenBlock = statementList
		RBRACKET
		(
			ELSE LBRACKET elseBlock = statementList RBRACKET
		)
	)
	{
      		
      		  assert($condition.text != null);
              assert($thenBlock.text != null);
              assert($elseBlock.text != null);
             
      		$result = new IfThenElse($condition.result, $thenBlock.st, $elseBlock.st);
      	}

	|
	(
		IF LPAREN condition = boolexp RPAREN LBRACKET thenBlock = statementList
		RBRACKET
	)
	{
      		
      		  assert($condition.text != null);
              assert($thenBlock.text != null);
            
      		$result = new IfThenElse($condition.result, $thenBlock.st);
      	}

	|
	(
		WHILE LPAREN condition = boolexp RPAREN LBRACKET body = statementList
		RBRACKET
	)
	{     	 				
   
     $result = new While($condition.result, $body.st);}

	|
	(
		FOR LPAREN begin = statementList end = boolexp PV inc = statementList RPAREN
	) LBRACKET body = statementList RBRACKET
	{    
    	StatementList inc2 = new StatementList($inc.st);
    	StatementList body2 = new StatementList($body.st);
    	body2.add(inc2);
    	StatementList res = new StatementList($begin.st);
    	StatementList w = new StatementList( new While($end.result, body2));
    	res.add(w);
    $result = res;}

	|
	(
		CREATEARRAY LPAREN name = IDENTIFIER VIRG size = CONSTI RPAREN
	)
	{
   	$result = new CreateArray($name.text,Integer.parseInt($size.text)) ;
    
    }

	|
	|
	(
		id = IDENTIFIER INF index = expression SUP ASSIGN e = expression
	)
	{
           $result = new AssignTabElement($e.result,$id.getText(),$index.result) ;}

	|
	(
		REQUIREACCURACY LPAREN name = IDENTIFIER VIRG prec = baseexpr RPAREN
	)
	{
     
     	Numbers ii = $prec.result.xmin;
     	int i =ii.getIntValue() ;
//      int pp = prec.getInf().getIntValue();       	
  	  $result = new RequireAccuracy($name.text, i); }

;

boolexp returns [AbstractClassB result]
:
	(
		left = expression EQ right = expression
	)
	{
          	 assert($left.result != null);  
			 assert($right.result != null);   
	
			 $result = new Equal($left.result, $right.result);}

	|
	(
		left = expression GT right = expression
	)
	{
			 assert($left.result != null);  
			 assert($right.result != null);   
			 
			$result = new Greater($left.result, $right.result);}

	|
	(
		left = expression GE right = expression
	)
	{ 
			 assert($left.result != null);  
			 assert($right.result != null);   
			 
			$result = new GreaterEqual($left.result,  $right.result);}

	|
	(
		left = expression LT right = expression
	)
	{
			 assert($left.result != null);  
			 assert($right.result != null);  
			  
			$result =new Less($left.result, $right.result);}

	|
	(
		left = expression NEQ right = expression
	)
	{
			 assert($left.result != null);  
			 assert($right.result != null);  
			  
			$result =new NotEqual($left.result, $right.result);}
	|
	(
		left = expression LE right = expression
	)
	{
	
	    	 assert($left.result != null);  
			 assert($right.result != null);  
			 
	    	$result = new LessEqual(left, right);}

;

//============================================================================//
// LEXER
//============================================================================//
// Keywords

INTERVALLE
:
	(
		'0' .. '9'
		| MINUS '0' .. '9'
	)+ '.'
	(
		'0' .. '9'
		| MINUS '0' .. '9'
	)+ ','
	(
		'0' .. '9'
		| MINUS '0' .. '9'
	)+ '.'
	(
		'0' .. '9'
		| MINUS '0' .. '9'
	)+
;

CONSTF
:
	(
		'0' .. '9'
		| MINUS '0' .. '9'
	)+ '.'
	(
		'0' .. '9'
		| MINUS '0' .. '9'
	)+
;

CONSTI
:
	(
		'0' .. '9'
		| MINUS '0' .. '9'
	)+
;

RANGERANDOMFLOAT
:
	(
		'0' .. '9'
	)
;

REQUIREACCURACY
:
	'require_nsb'
;

SQRT
:
	'sqrt'
;

SINUS
:
	'sin'
;
EXPONENT
:
	'exp'
;
COSINUS
:
'cos'
;
TAN
:
'tan'
;
ASIN
:
	'asin'
;
ACOS
:
	'acos'
;

ATAN
:
	'atan'
;

LOG
:
	'log'
;

CREATEARRAY
:
	'create_array'
;

DIV
:
	'/'
;

MULT
:
	'*'
;

MINUS
:
	'-'
;

PLUS
:
	'+'
;

VIRG
:
	','
;

SHARP
:
	'#'
;

TRUE
:
	'true'
;

FALSE
:
	'false'
;

GT
:
	'>'
;

AND
:
	'&&'
;

OR
:
	'||'
;

GE
:
	'>='
;

LT
:
	'<'
;

LE
:
	'<='
;

EQ
:
	'=='
;

NEQ
:
	'!='
	| '<>'
;

NOT
:
	'!'
;

ABS
:
	'abs'
;

PV
:
	';'
;

ASSIGN
:
	'='
;

IF
:
	'if'
;

ELSE
:
	'else'
;

WHILE
:
	'while'
;

FOR
:
	'for'
;

LPAREN
:
	'('
;

RPAREN
:
	')'
;

LBRACKET
:
	'{'
;

INF
:
	'['
;

SUP
:
	']'
;

RBRACKET
:
	'}'
;

NEWLINE
:
	(
		'\n'
		| '\r'
	)+ -> skip
;

END
:
	EOF
; //

WS
:
	(
		'\t'
		| ' '
	)+ -> skip
;

COMMENT
:
	'/*' .*? '*/' -> skip
;

LINE_COMMENT
:
	'//' ~[\r\n]* -> skip
;

IDENTIFIER
:
	(
		'a' .. 'z'
		| 'A' .. 'Z'
	)
	(
		'a' .. 'z'
		| 'A' .. 'Z'
		| '0' .. '9'
		| '_'
	)*
;
	

	
	
	