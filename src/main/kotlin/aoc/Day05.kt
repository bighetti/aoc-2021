package aoc

class Day05(val input: List<String>) {

	private fun updateGrid(grid: MutableMap<String, Int>, start: String, end: String, diagonal: Boolean) {
		var x = start.split(",").first().toInt()
		var y = start.split(",").last().toInt()

		val endX = end.split(",").first().toInt()
		val endY = end.split(",").last().toInt()

		if (!diagonal && x != endX && y != endY) {
			return // ignore diagonal lines
		}

		grid[start] = grid.getOrDefault(start, 0) + 1

		while (x != endX || y != endY) {
			if (x != endX) {
				if (x < endX) {
					x++
				} else {
					x--
				}
			}

			if (y != endY) {
				if (y < endY) {
					y++
				} else {
					y--
				}
			}

			grid["$x,$y"] = grid.getOrDefault("$x,$y", 0) + 1
		}
	}

	fun part1() {
		val grid = mutableMapOf<String, Int>() // x,y -> count

		input.forEach {
			val start = it.split(" -> ").first()
			val end = it.split(" -> ").last()

			updateGrid(grid, start, end, false) // no diagonal lines
		}

		val result = grid.values.filter { it >= 2 }.size

		print1(result)
	}

	fun part2() {
		val grid = mutableMapOf<String, Int>() // x,y -> count

		input.forEach {
			val start = it.split(" -> ").first()
			val end = it.split(" -> ").last()

			updateGrid(grid, start, end, true) // diagonal lines
		}

		val result = grid.values.filter { it >= 2 }.size

		print2(result)
	}

}

fun main() {

	val input = readInput("day05.txt")
	// val inputSample = readInput("day05.sample.txt")

	Day05(input).part1()
	// Day05(inputSample).part1()

	Day05(input).part2()

}
