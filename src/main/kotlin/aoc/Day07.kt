package aoc

import kotlin.math.abs

class Day07(val input: List<String>) {

	private fun getFuelIncreasing(position: Int, crabPosition: Int): Int {
		val difference = abs(position - crabPosition)

		if (difference < 2) {
			return difference
		}

		return (1..difference).sum()
	}

	fun part1() {
		val values = input.first().split(",").map { it.toInt() }

		val fuels = mutableListOf<Int>()
		val max = values.maxOrNull() ?: 0
		val min = values.minOrNull() ?: 0

		(min..max).forEach { position ->
			val fuel = values.map { crabPosition ->
				abs(position - crabPosition)
			}

			fuels.add(fuel.sum())
		}

		print1(fuels.minOrNull() ?: "")
	}

	fun part2() {
		val values = input.first().split(",").map { it.toInt() }

		val fuels = mutableListOf<Int>()
		val max = values.maxOrNull() ?: 0
		val min = values.minOrNull() ?: 0

		(min..max).distinct().forEach { position ->
			val fuel = values.map {
				getFuelIncreasing(position, it)
			}

			fuels.add(fuel.sum())
		}

		print2(fuels.minOrNull() ?: "")
	}

}

fun main() {

	val input = readInput("day07.txt")

	Day07(input).part1()
	Day07(input).part2()

}
