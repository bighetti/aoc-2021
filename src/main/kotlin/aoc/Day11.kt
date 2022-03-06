package aoc

import kotlin.math.sqrt

class Day11Octopus(val line: Int, val pos: Int, var value: Int) {

	private val adjacent = mutableListOf<Day11Octopus>()
	var flashes = mutableListOf<Int>() // step number when the flash occurred

	fun addOctopus(octopus: Day11Octopus) {
		adjacent.add(octopus)
	}

	fun increaseValue(step: Int) {
		if (!flashes.contains(step)) { // only flash once per step
			value++

			if (value > 9) {
				value = 0

				flashes.add(step)

				adjacent.forEach {
					it.increaseValue(step)
				}
			}
		}
	}

}

class Day11(val input: List<String>) {

	private fun addAdjacent(octopusList: List<Day11Octopus>) {
		// find the grid boundary, i.e. the maximum line / pos values
		val boundary = sqrt(octopusList.size.toDouble()).toInt()

		octopusList.forEach { octopus ->
			val positions = listOf(
				Pair(octopus.line - 1, octopus.pos - 1), // line above
				Pair(octopus.line - 1, octopus.pos),
				Pair(octopus.line - 1, octopus.pos + 1),
				Pair(octopus.line, octopus.pos - 1), // line equal
				Pair(octopus.line, octopus.pos + 1),
				Pair(octopus.line + 1, octopus.pos - 1), // line below
				Pair(octopus.line + 1, octopus.pos),
				Pair(octopus.line + 1, octopus.pos + 1)
			)

			val filtered = positions.filter { (k, v) ->
				k > -1 && k < boundary && v > -1 && v < boundary
			}

			filtered.forEach { (k, v) ->
				val adjacent = octopusList.find { it.line == k && it.pos == v }

				if (adjacent != null) { // should not occur
					octopus.addOctopus(adjacent)
				}
			}
		}
	}

	private fun parse(): List<Day11Octopus> {
		val lineLength = input.getOrElse(0) { "" }.length
		val octopusList = mutableListOf<Day11Octopus>()

		for (line in input.indices) {
			for (pos in 0 until lineLength) {
				val value = input[line][pos].toString().toInt()

				octopusList.add(Day11Octopus(line, pos, value))
			}
		}

		addAdjacent(octopusList)

		return octopusList
	}

	fun part1() {
		val octopusList = parse()

		for (step in 1..100) {
			octopusList.forEach {
				it.increaseValue(step)
			}
		}

		print1(octopusList.sumOf { it.flashes.size })
	}

	fun part2() {
		val octopusList = parse()

		var flashCount = 0
		var step = 0

		while (flashCount != 100) {
			step++ // step count

			octopusList.forEach {
				it.increaseValue(step)
			}

			flashCount = octopusList.filter { it.flashes.contains(step) }.size
		}

		print2(step)
	}

}

fun main() {

	val input = readInput("day11.txt")

	Day11(input).part1()
	Day11(input).part2()

}
