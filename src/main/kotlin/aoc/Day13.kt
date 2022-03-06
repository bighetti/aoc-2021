package aoc

class Day13(val input: List<String>) {

	private fun fold(map: MutableMap<Int, MutableSet<Int>>, instruction: String) {
		val direction = instruction.replace("fold along ", "").split("=").first()
		val position = instruction.replace("fold along ", "").split("=").last().toInt()

		val mapCopy = map.toMutableMap()

		mapCopy.forEach { (key, values) ->
			if (direction == "x") {
				val mapValues = map[key]

				values.toList().forEach { value ->
					if (value >= position) {
						val newValue = (position * 2) - value

						mapValues?.add(newValue)
						mapValues?.remove(value)
					}
				}
			} else if (direction == "y") {
				val mapRows = map.filter { it.key >= position }

				mapRows.keys.forEach { key ->
					val newRow = (position * 2) - key

					var mapValues = map.getOrPut(newRow) { mutableSetOf() }

					map[key]?.forEach {
						mapValues.add(it)
					}

					map.remove(key)
				}
			}
		}
	}

	fun part1() {
		val resultMap = mutableMapOf<Int, MutableSet<Int>>()

		val dots = input.filter { it.isNotEmpty() && !it.contains("fold") }
		val instructions = input.filter { it.contains("fold") }

		dots.forEach {
			val col = it.split(",").first().toInt()
			val row = it.split(",").last().toInt()

			var rowValues = resultMap.getOrPut(row) { mutableSetOf() }

			rowValues.add(col)
		}

		fold(resultMap, instructions.first())

		val result = resultMap.values.sumOf { it.size }

		print1(result)
	}

	fun part2() {
		val resultMap = mutableMapOf<Int, MutableSet<Int>>()

		val dots = input.filter { it.isNotEmpty() && !it.contains("fold") }
		val instructions = input.filter { it.contains("fold") }

		dots.forEach {
			val col = it.split(",").first().toInt()
			val row = it.split(",").last().toInt()

			var rowValues = resultMap.getOrPut(row) { mutableSetOf() }

			rowValues.add(col)
		}

		instructions.forEach {
			fold(resultMap, it)
		}

		val maxCol = resultMap.values.flatten().maxOrNull() ?: 0
		val maxRow = resultMap.keys.maxOrNull() ?: 0

		for (row in 0..maxRow) {
			for (col in 0..maxCol) {
				val values = resultMap[row]

				if (values != null && values?.contains(col)) {
					print("#")
				} else if (resultMap.values.find { it.contains(col) } == null) { // blank in every row
					print("    ")
				} else {
					print(" ")
				}
			}

			println("")
		}
	}

}

fun main() {

	val input = readInput("day13.txt")

	Day13(input).part1()
	Day13(input).part2()

}
