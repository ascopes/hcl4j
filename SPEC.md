# Language Specification

This language specification is based upon 
[HCL 2.17](https://github.com/hashicorp/hcl/blob/v2.17.0/hclsyntax/spec.md).

The specification in this file is mostly defined using
[EBNF](https://en.m.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form)
notation. Some elements cannot be defined clearly using EBNF: these will be
defined in the prose. Any conflict between the prose and the
EBNF definition should be resolved by giving the prose precedence.

```
 a = ...   definition of production a
  a , b    production a followed by b
  a | b    production a or production b
  a - b    production a, except anything also matching b
    ;      termination
   "a"     the literal string "a"
   'a'     the literal string 'a'
  '\r'     literal ASCII carriage return
  '\n'     literal ASCII line feed
  '\\'     literal backslash
 ( ... )   a nested expression
  [ x ]    production x, 0 or 1 times
  { y }    production y, 1 or more times
(* xxx *)  a comment
```

## Encoding

HCL config files are expected to be UTF-8 encoded. These can be started with or
without a UTF-8 byte-order mark.

The UTF-8 byte-order mark comprises of three bytes:

    0xEF 0xBB 0xBF

## Lexical Analysis

Lexical analysis occurs using several lexer modes that are pushed and popped
on a stack. This enables changing the syntatic context for specific constructs
such as heredocs, nested templates, and comments.

The algorithm for retrieving the next token shall consider the topmost mode
on the stack.

### Config File Mode

Config file mode defines the following grammar for tokens.

#### Whitespace

```
whitespace = { "\t" | " " } ;
newline    = { "\n" | "\r\n" } ;
```

#### Identifiers

In this rule, `ID_Start` refers to a Unicode `ID_Start` symbol.
`ID_Continue` refers to a Unicode `ID_Continue` symbol.

```
identifier = ID_Start , [ { ID_Continue | '-'  } ] ;
```

#### Literals

```
digit = "0" | "1" | "2" | "3" | "4"
      | "5" | "6" | "7" | "8" | "9" ;

numeric literal = real literal | integer literal ;
integer literal = { digit } ;
fraction        = "." , { digit } ;
exponent        = ( "E" | "e" ) , [ "+" | "-" ] , { digit } ;
real literal    = { digit } , [ fraction ] , [ exponent ] ;
```

#### Arithmetic operators

```
plus   = "+" ;
minus  = "-" ;
star   = "*" ;
divide = "/" ;
modulo = "%" ;
```

#### Logic operators

```
and = "&&" ;
or  = "||" ;
not = "!" ;
```

#### Comparative operators

```
equal            = "==" ;
not equal        = "!=" ;
less             = "<" ;
less or equal    = "<=" ;
greater          = ">" ;
greater or equal = ">=" ;
```

#### Mode changing symbols

```
heredoc anchor = "<<" ;  (* pushes the heredoc header mode  *)
quote          = '"' ;   (* pushes the quoted template mode *)
left brace     = "{" ;   (* pushes a new config file mode   *)
right brace    = "}" ;   (* pops the current mode           *)
hash           = "#" ;   (* pushes the line comment mode    *)
double slash   = "//" ;  (* pushes the line comment mode    *)
slash star     = "/*" ;  (* pushes the inline comment mode  *)
```

#### Misc symbols

```
assign               = "=" ;
fat arrow            = "=>" ;
left parenthesis     = "(" ;
right parenthesis    = ")" ;
left square bracket  = "[" ;
right square bracket = "]" ;
question mark        = "?" ;
colon                = ":" ;
ellipsis             = "..." ;
dot                  = "." ;
```

### Inline comment mode

Inline comments consume content across zero or more lines.

```
inline comment         = "/*" , inline comment content , "*/" ;
inline comment content = (* any characters until '*/' is observed *) ;
```

### Line comment mode

Line comments are similar to inline comments, but are terminated by a line ending or the end 
of the file.

Allowing EOF is important here as it allows files to contain trailing line comments even if 
the file does not have a trailing new line.

```
line comment         = ( "#" | "//" ) , line comment content , ( newline | eof ) ;
line comment content = (* any characters until newline is observed *) ;
newline              = "\n" | "\r\n" ;
```

### Template mode

Template mode tokenizes nested expressions within template interpolations.

Template mode **extends** the **config file mode**, but adds 
an additonal rule that deals with closing template directives
and interpolation sequences. Outside of a template, this rule
is not a valid token.

The very first token may be a `~` to indicate whitespace trimming for leading whitespace.

The template is terminated by a closing brace, which may or may not be preceeded by a `~` to indicate trailing whitespace trimming.

If additional lexer modes are pushed, they take precedence over these two rules.

It is worth noting the implementation in this repository may or may not choose to handle leading `~` before pushing this mode. This is just documented here for simplicity.

```

trim                 = "~" ;
template termination = [ trim ], "}" ;
```

### Template file mode 

The template file mode will read characters until the end of the file.

```
interpolation start            = "${" ;          (* push the template mode *)
directive start                = "%{" ;          (* push the template mode *)

text = { "$${" | "%%{" | any other character } ;
```

It is worth noting that `$${` will result in a literal `${` within the text.
Likewise, `%%{` will result in a literal `%{`.

### Heredoc header mode

Heredoc header mode consumes the opening header to a heredoc. Any other
sequences are considered invalid.

```
trim marker = "-" ;
identifier  = ID_Start , [ { ID_Continue | "-" } ] ;
newline     = "\n" | "\r\n" ;
```

When a newline is encountered, the header is considered to have ended.
If an identifier has been cached, then the current mode is replaced with the
**heredoc mode**, which is given the cached identifier.

If there is no cached identifier, then this will be 
considered a syntax error by the parser and to prevent further
confusion, the current mode is popped.

### Heredoc template mode

The heredoc mode will read until the end of the file or the first occurance
of the `identifier` followed by a `newline`. This information is passed from 
the **heredoc header mode**.

As in other production groups, newlines are either `\n` or
`\r\n`.

When the identifier followed by a newline is read, the current
mode should be popped.

```
interpolation start            = "${" ;           (* push the template mode *)   
directive start                = "%{" ;           (* push the template mode *)

text = { "$${" | "%%{" | any other character } ;
```

It is worth noting that `$${` will result in a literal `${` within the text.
Likewise, `%%{` will result in a literal `%{`.

### Quoted template mode

```
quote                          = '"' ;               (* pop the current mode *)
interpolation start            = "${" ;              (* push the template mode *)
directive start                = "%{" ;              (* push the template mode *)


newline = "\n" | "\r\n" ;
text    = { escape | any other character - newline } ;

escape = "$${"
       | "%%{"
       | '\\"'
       | "\\\\"
       | "\\r"
       | "\\n"
       | "\\t"
       | "\\u" , hex , hex , hex , hex
       | "\\U" , hex , hex , hex , hex , hex , hex , hex , hex 
       ;

hex = "0" | "1" | "2" | "3" | "4"
    | "5" | "6" | "7" | "8" | "9" 
    | "A" | "B" | "C" | "D" | "E" | "F"
    | "a" | "b" | "c" | "d" | "e" | "f"
    ;
```

It is worth noting that:

- `$${` will result in a literal `${` within the text.
- `%%{` will result in a literal `%{`.
- `\\r` translates to an ASCII carriage return `\r`.
- `\\n` translates to an ASCII line feed `\n`.
- `\\t` translates to an ASCII horizontal tab `\t`.
- `\\"` translates to a double quote literal `"`.
- `\\\\` translates to a backslash `\`.
- `\\uXXXX` translates to a basic multilingual plane hexadecimal character code.
- `\\UXXXXXXXX` translates to a supplimentary plane hexadecimal character code.

Any `newline` token is an error and will result in the string being
popped.

