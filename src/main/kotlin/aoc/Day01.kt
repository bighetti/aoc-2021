package aoc

class Day01(val input: List<String>) {

	fun part1() {
		var count = 0

		for (index in 1 until input.size) {
			val current = input[index].toInt()
			val previous = input[index - 1].toInt()

			if (current > previous) {
				count++
			}
		}

		print1(count)
	}

	fun part2() {
		var count = 0

		for (index in 3 until input.size) {
			val current = input.subList(index - 2, index + 1).sumOf { it.toInt() }
			val previous = input.subList(index - 3, index).sumOf { it.toInt() }

			if (current > previous) {
				count++
			}
		}

		print2(count)
	}

}

fun main() {

	val input = readInput("day01.txt")

	Day01(input).part1()
	Day01(input).part2()

}
