# TruffleErlang

TruffleErlang is an experimental implementation of the Erlang programming language built on GraalVM.

⚠️ This is a research prototype at an early stage.

## Introduction

Unlike most implementations of Erlang, we choose Core Erlang as the input language. Core Erlang is an intermediate representation between Erlang and BEAM bytecode. Its detailed grammar can found in [Core Erlang 1.0.3 language specification](https://www.it.uu.se/research/group/hipe/cerl/doc/core_erlang-1.0.3.pdf).

We make use of the Truffle language implementation framework to improve the runtime performance of Erlang. The principle of Truffle is elaborated in the PLDI'17 paper [Practical Partial Evaluation for High-Performance Dynamic Language Runtimes](https://chrisseaton.com/truffleruby/pldi17-truffle/pldi17-truffle.pdf).

Currently, a limited set of features is implemented for experimental purposes:

- supports common data types: integers, floats, atoms, binaries, tuples, lists, funs
- follows the Erlang conventions:
  - `[a,b]` is a shorthand for `[a|[b|[]]]`, just like `cons` in Lisp
  - `$char` is interpreted as an integer of its Unicode code point
  - `"string"` is interpreted as a list of their Unicode code points
  - `true` and `false` atoms are used to denote boolean values
- implements pattern matching with `case`
- performs tail call elimination to avoid stack overflow
- implements operators as built-in functions in the `erlang` module
  - arithmetic: `+` `-` `*` `/` `div` `rem`
  - logic: `not` `and` `or` `xor`
  - comparison: `==` `/=` `=<` `<` `>=` `>` `=:=` `=/=`
- supports `io:get_line` and `io:format` for standard I/O
- implements a half-baked actor model using Akka on JVM

## Usage

First, make sure you have installed [GraalVM](https://www.graalvm.org/downloads/) and set `$JAVA_HOME` to point to the directory of GraalVM.

Note that the input language of our interpreter is *Core Erlang*. In order to execute Erlang source code, you should first compile it to Core Erlang using `erlc`. Then you can just use Gradle to build and run our interpreter.

```
erlc +to_core source.erl
gradle run --args source.core
```

The `main/0` function in Core Eralng source code will be automatically executed.

## Codename: Jiro

You may find the codename "Jiro" everywhere in our source code. It is a wordplay: *Erlang* is homophonous with the Chinese word "二郎" while the same characters read *Jirō* in Japanese, which indicates that Jiro is another implementation of Erlang. By the way, Jiro can also be interpreted as a **J**ava **I**mplementation of E**r**lang/**O**TP.
