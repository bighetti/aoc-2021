package aoc

class Day02(val input: List<String>) {

	fun part1() {
		var depth = 0
		var horiz = 0

		input.forEach {
			val command = it.split(" ").first()
			val value = it.split(" ").last().toInt()

			when (command) {
				"forward" -> horiz += value
				"down" -> depth += value
				"up" -> depth -= value
			}
		}

		print1(depth * horiz)
	}

	fun part2() {
		var aim = 0
		var depth = 0
		var horiz = 0

		input.forEach {
			val command = it.split(" ").first()
			val value = it.split(" ").last().toInt()

			when (command) {
				"forward" -> {
					horiz += value
					depth += value * aim
				}
				"down" -> aim += value
				"up" -> aim -= value
			}
		}

		print2(depth * horiz)
	}

}

fun main() {

	val input = readInput("day02.txt")

	Day02(input).part1()
	Day02(input).part2()

}
