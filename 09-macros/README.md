# Macros

A macro is a Scala function that runs at compile time.

The job of macros is to transform *trees* - a macro takes a tree as input and returns a tree as output.

Terminology:

* Tree = a representation of a piece of Scala code, a.k.a. an Abstract Syntax Tree (AST)
* Expr (expression) = a typed Tree, i.e. a Tree plus a type

## Use cases

http://scalamacros.org/paperstalks/2013-07-17-WhatAreMacrosGoodFor.pdf

* Code generation
    * Materialization (e.g. play-json `Format`)
* Static checks
    * e.g. https://github.com/propensive/rapture-i18n
* Domain specific languages

## How to write a macro

```
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

def myMacro(c: blackbox.Context)(a: c.Tree): c.Tree = {
  ...
}

def doSomething(a: Int): String = macro myMacro
```

* Macros take a `Context`. All access to the macros API goes through there.
* You need to write a wrapper function around your macro. Clients call this - they cannot call the macro directly.

## How to call a macro

```
val foo = doSomething(123)
```

To the client it just looks like a normal function call. No indication that macro magic is happening behind the scenes.

Note that the macro and the client code cannot be compiled at the same time, i.e. you need to put the macro in a separate source folder, jar, etc.
