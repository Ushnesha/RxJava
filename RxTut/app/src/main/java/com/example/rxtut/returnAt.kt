package com.example.rxtut

fun main(args : Array<String>) {
    run loop@{
        listOf(1, 2, 3, 4, 5).forEach {
            if (it == 3) return@loop // non-local return from the lambda passed to run
            print("$it ")
        }
    }
    print(" done with nested loop")

    println()

    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return@forEach // local return to the caller of the lambda, i.e. the forEach loop
        print("$it ")
    }
    print(" done with implicit label")

    println()

    listOf(1, 2, 3, 4, 5).forEach lit@{
        if (it == 3) return@lit // local return to the caller of the lambda, i.e. the forEach loop
        print("$it ")
    }
    print(" done with explicit label")

    println()

    listOf(1, 2, 3, 4, 5).forEach(fun(value: Int) {
        if (value == 3) return  // local return to the caller of the anonymous fun, i.e. the forEach loop
        print("$value ")
    })
    print(" done with anonymous function")

    println()

    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return // non-local return directly to the caller of foo()
        print("$it ")
    }
    println("this point is unreachable")

    println()

    listOf(1, 2, 3, 4, 5).forEach(fun(value: Int) {
        if (value == 3) return  // local return to the caller of the anonymous fun, i.e. the forEach loop
        print("$value ")
    })
    print(" done with anonymous function")

}