# Jiro

Jiro is a high-performance implementation of the Erlang virtual machine built on GraalVM.

⚠️ This is an experimental project at a very early stage.

## Features

Unlike most implementations of Erlang VM, Jiro chooses *Core Erlang* as its source language. Core Erlang is an intermediate representation between Erlang and BEAM bytecode. Its detailed grammar can found in [Core Erlang 1.0.3 language specification](https://www.it.uu.se/research/group/hipe/cerl/doc/core_erlang-1.0.3.pdf).

Jiro makes use of the Truffle language implementation framework to improve the runtime performance of Erlang. The principle of Truffle is elaborated in the PLDI'17 paper [Practical Partial Evaluation for High-Performance Dynamic Language Runtimes](https://chrisseaton.com/truffleruby/pldi17-truffle/pldi17-truffle.pdf).

Currently, Jiro is expected to support the following features:

- [ ] fully compatible with Core Erlang 1.0.3
- [ ] supports common data types: integers, floats, atoms, binaries, tuples, lists
- [ ] supports first-class `fun` which can capture its surrounding environment
- [ ] follows the Erlang conventions:
  - `[a,b]` is a shorthand for `[a|[b|[]]]`, just like `cons` in Lisp
  - `$char` is interpreted as an integer of its Unicode code point
  - `"string"` is interpreted as a list of their Unicode code points
  - `true` and `false` atoms are used to denote boolean values
- [ ] respects block-level scopes with `let` expressions
- [ ] implements pattern matching properly with `case` and `receive`
- [ ] performs tail call elimination to reduce overheads
- [ ] supports exceptions and their handling
- [ ] implements operators as built-in functions in the `erlang` module
  - arithmetic: `+` `-` `*` `/` `div` `rem`
  - logic: `not` `and` `or` `xor`
  - comparison: `==` `/=` `=<` `<` `>=` `>` `=:=` `=/=`
- [ ] supports common built-in functions in the `erlang` module
- [ ] supports `io:get_line` and `io:format` for standard I/O
- [ ] implements the actor model using Akka on JVM

## Usage

First, make sure you have installed [GraalVM](https://www.graalvm.org/downloads/) and set `$JAVA_HOME` to point to the directory of GraalVM.

Note that Jiro takes Core Erlang as the input language. In order to execute Erlang source code, you should first compile it to Core Erlang using `erlc`. Then you can just use Gradle to build and run Jiro.

```
erlc +to_core source.erl
gradle run --args source.core
```

Jiro will execute the `main/0` function found in Core Eralng source code.

## Why this name?

*Erlang* is homophonous with the Chinese word "二郎" while the same characters read *Jirō* in Japanese. I use this wordplay to indicate that Jiro is another implementation of Erlang.

In addition, Jiro can also be interpreted as a **J**ava **I**mplementation of E**r**lang/**O**TP.
