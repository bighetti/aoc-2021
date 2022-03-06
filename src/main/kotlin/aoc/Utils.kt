package aoc

fun print1(result: Any) {
    println("part 1 result: $result")
}

fun print2(result: Any) {
    println("part 2 result: $result")
}

fun readInput(name: String) = object {}.javaClass.getResource("/$name").readText().lines()
