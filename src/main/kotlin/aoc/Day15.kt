package aoc

import java.util.PriorityQueue

class Day15Point(val pair: Pair<Int, Int>, val cost: Int) : Comparable<Day15Point> {

	override fun compareTo(other: Day15Point) = cost - other.cost

	fun neighbors() = listOf(
		Pair(pair.first, pair.second + 1),
		Pair(pair.first, pair.second - 1),
		Pair(pair.first + 1, pair.second),
		Pair(pair.first - 1, pair.second)
	)

}

class Day15(val input: List<String>) {

	private fun addLine(input: String, increment: Int): String {
		var output = ""

		input.forEach { char ->
			var newValue = char.toString().toInt() + increment

			if (newValue > 9) {
				newValue -= 9
			}

			output += newValue
		}

		return output
	}

	private fun expandLine(input: String): String {
		var output = input

		for (i in 1..4) { // repeat 4 times
			input.forEach { char ->
				var newValue = char.toString().toInt() + i

				if (newValue > 9) {
					newValue -= 9
				}

				output += newValue
			}
		}

		return output
	}

	private fun findShortestPath(input: List<String>): Int {
		val queue = PriorityQueue<Day15Point>()
		var result = Integer.MAX_VALUE
		val visited = mutableListOf<String>()

		val finish = Pair(input.size - 1, input.size - 1)

		queue.add(Day15Point(Pair(0, 0), 0))

		while (queue.isNotEmpty()) {
			val point = queue.poll()

			if (point.pair == finish && point.cost < result) {
				result = point.cost
			}

			if (!visited.contains(point.pair.toString())) {
				visited.add(point.pair.toString())

				point.neighbors()
					.filter { // exclude invalid
						it.first in (0..finish.first) && it.second in (0..finish.second) && it.first + it.second != 0
					}
					.forEach {
						val value = input[it.first][it.second].toString().toInt() // value from grid

						val cost = point.cost + value

						if (cost < result && !visited.contains(it.toString())) { // ignore costs already greater than result
							queue.offer(Day15Point(it, cost))
						}
					}
			}
		}

		return result
	}

	private fun increaseInput(input: List<String>): List<String> {
		val result = mutableListOf<String>()

		// expand existing rows
		input.forEach { str ->
			var output = expandLine(str)

			result.add(output)
		}

		// add more rows - repeat 4 times
		for (i in 1..4) {
			input.forEach { str ->
				var newString = addLine(str, i)
				var output = expandLine(newString)

				result.add(output)
			}
		}

		return result
	}

	fun part1() {
		val start = System.currentTimeMillis()
		var result = findShortestPath(input)

		print1(result)

		val end = System.currentTimeMillis()

		println("time taken: ${(end - start) / 1000} seconds")
	}

	fun part2() {
		val input = increaseInput(input)
		val start = System.currentTimeMillis()

		var result = findShortestPath(input)

		print2(result)

		val end = System.currentTimeMillis()

		println("time taken: ${(end - start) / 1000} seconds")
	}

}

fun main() {

	val input = readInput("day15.txt")

	Day15(input).part1()
	Day15(input).part2()

}
