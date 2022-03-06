package aoc

import kotlin.math.floor

class Day24Values {

	var map = mutableMapOf(
		"w" to 0L,
		"x" to 0L,
		"y" to 0L,
		"z" to 0L
	)

	override fun toString() = "w: ${map["w"]}, x: ${map["x"]}, y: ${map["y"]}, z: ${map["y"]}"

}

class Day24(val input: List<String>) {

	var bestScore = Long.MAX_VALUE
	var bestNumber = 0L
	var minScores = mutableMapOf<Int, Long>() // position -> min score
	var maxScores = mutableMapOf<Int, Long>() // position -> max score
	val seen = mutableListOf<String>() // position-zValueIn

	fun add(values: Day24Values, field1: String, field2: String) {
		val value1 = values.map[field1]
		val value2 = getField2(values, field2)

		if (value1 != null) {
			values.map[field1] = value1 + value2
		}
	}

	fun divide(values: Day24Values, field1: String, field2: String) {
		val value1 = values.map[field1]
		val value2 = getField2(values, field2)

		if (value1 != null && value2 != 0L) {
			values.map[field1] = floor(value1.toDouble() / value2.toDouble()).toLong()
		}
	}

	fun equal(values: Day24Values, field1: String, field2: String) {
		val value1 = values.map[field1]
		val value2 = getField2(values, field2)

		if (value1 != null) {
			values.map[field1] = if (value1 == value2) 1 else 0
		}
	}

	fun input(values: Day24Values, field1: String, nextNumber: Long) {
		values.map[field1] = nextNumber
	}

	fun getField2(values: Day24Values, field2: String): Long {
		return when (field2) {
			"w" -> values.map[field2] ?: throw Exception("$field2 not found")
			"x" -> values.map[field2] ?: throw Exception("$field2 not found")
			"y" -> values.map[field2] ?: throw Exception("$field2 not found")
			"z" -> values.map[field2] ?: throw Exception("$field2 not found")
			else -> field2.toLong()
		}
	}

	fun groupInput(): MutableList<List<String>> {
		val result = mutableListOf<List<String>>()

		// group by 'inp' line
		val currentList = mutableListOf<String>()

		input.forEachIndexed { index, str ->
			if (str.startsWith("inp")) {
				if (currentList.isNotEmpty()) {
					result.add(currentList.toList())
					currentList.clear()
				}
			}

			currentList.add(str)

			if (index == input.size - 1) {
				result.add(currentList.toList())
				currentList.clear()
			}
		}

		return result
	}

	fun modulo(values: Day24Values, field1: String, field2: String) {
		val value1 = values.map[field1]
		val value2 = getField2(values, field2)

		if (value1 != null && value2 > 0) {
			values.map[field1] = if (value1 < 0) value1 else value1 % value2
		}
	}

	fun multiply(values: Day24Values, field1: String, field2: String) {
		val value1 = values.map[field1]
		val value2 = getField2(values, field2)

		if (value1 != null) {
			values.map[field1] = value1 * value2
		}
	}

	fun processInputGroup(inputs: List<String>, nextNumber: Long, zValue: Long): Long {
		val values = Day24Values().apply {
			map["z"] = zValue
		}

		inputs.forEach { str ->
			val split = str.split(" ")

			val command = split[0]
			val field1 = split[1]
			val field2 = split.getOrElse(2) { "" } // 'inp' does not have 3 fields

			when (command) {
				"add" -> add(values, field1, field2)
				"div" -> divide(values, field1, field2)
				"eql" -> equal(values, field1, field2)
				"inp" -> input(values, field1, nextNumber)
				"mod" -> modulo(values, field1, field2)
				"mul" -> multiply(values, field1, field2)
				else -> throw Exception("command not found: $command") // should not happen
			}
		}

		return values.map["z"] ?: throw Exception("z field not found") // should not happen
	}

	fun processLoopPart1(inputGrouped: List<List<String>>, fullNumber: String, pos: Int, zValue: Long, reverse: Boolean) {
		if (bestScore > 0L) {
			val list = if (reverse) listOf(1,2,3,4,5,6,7,8,9) else listOf(9,8,7,6,5,4,3,2,1)

			list.forEach { number ->
				val zResult = processLoopPart2(inputGrouped[pos], pos, number.toLong(), zValue)

				if (zResult != -1L) {
					if (pos == 13) {
						if (zResult == 0L) {
							bestScore = 0L
							bestNumber = "$fullNumber$number".toLong()

							println("found score 0 with number: $bestNumber")
						} else if (zResult < bestScore) {
							bestScore = zResult
							bestNumber = "$fullNumber$number".toLong()
						}
					} else {
						if (pos in listOf(4,7,9,10,11,12)) {
							// check if the result is too high relative to other scores
							// for some key stages where Z value can be multiplied by 1 or 26
							val min = minScores[pos] ?: -1

							if (zResult < min * 10) {
								processLoopPart1(inputGrouped, "$fullNumber$number", pos + 1, zResult, reverse)
							}
						} else {
							processLoopPart1(inputGrouped, "$fullNumber$number", pos + 1, zResult, reverse)
						}
					}
				}
			}
		}
	}

	fun processLoopPart2(inputs: List<String>, pos: Int, number: Long, zValue: Long): Long {
		if (seen.contains("$pos-${zValue}")) {
			return -1
		}

		val result = processInputGroup(inputs, number, zValue) // checks cache

		// update min + max
		val min = minScores[pos]
		val max = maxScores[pos]

		if (min == null || result < min) {
			minScores[pos] = result
		}

		if (max == null || result > max) {
			maxScores[pos] = result
		}

		return result
	}

	fun part1() {
		val start = System.currentTimeMillis()
		val inputGrouped = groupInput()

		processLoopPart1(inputGrouped, "", 0, 0L, false)

		val end = System.currentTimeMillis()

		println("time taken: ${(end - start) / 1000} seconds")
	}

	fun part2() {
		val start = System.currentTimeMillis()
		val inputGrouped = groupInput()

		processLoopPart1(inputGrouped, "", 0, 0L, true)

		val end = System.currentTimeMillis()

		println("time taken: ${(end - start) / 1000} seconds")
	}

}

fun main() {

	val input = readInput("day24.txt")

	Day24(input).part1()
	Day24(input).part2()

}
