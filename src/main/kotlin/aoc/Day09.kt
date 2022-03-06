package aoc

class Day09Point(val row: Int, val pos: Int, val value: Int, private val left: Int, private val right: Int, private val up: Int, private val down: Int) {

	var seen = false

	fun findBasinSize(points: List<Day09Point>): Int {
		if (value == 9) {
			return 0
		}

		val result = mutableListOf<Day09Point>()

		findSurroundingPoints(this, points, result)

		return result.size + 1 // include self
	}

	private fun findSurroundingPoints(point: Day09Point, points: List<Day09Point>, result: MutableList<Day09Point>) {
		point.seen = true

		if (isValid(point.row - 1, point.pos)) { // up
			val found = points.find {
				!it.seen && it.value != 9 && it.row == (point.row - 1) && it.pos == (point.pos) && !result.contains(it)
			}

			if (found != null) {
				result.add(found)
				findSurroundingPoints(found, points, result)
			}
		}

		if (isValid(point.row + 1, point.pos)) { // down
			val found = points.find {
				!it.seen && it.value != 9 && it.row == (point.row + 1) && it.pos == (point.pos) && !result.contains(it)
			}

			if (found != null) {
				result.add(found)
				findSurroundingPoints(found, points, result)
			}
		}

		if (isValid(point.row, point.pos - 1)) { // left
			val found = points.find {
				!it.seen && it.value != 9 && it.row == (point.row) && it.pos == (point.pos - 1) && !result.contains(it)
			}

			if (found != null) {

				result.add(found)
				findSurroundingPoints( found, points, result)
			}
		}

		if (isValid(point.row, point.pos + 1)) { // right
			val found = points.find {
				!it.seen && it.value != 9 && it.row == (point.row) && it.pos == (point.pos + 1) && !result.contains(it)
			}

			if (found != null) {
				result.add(found)
				findSurroundingPoints(found, points, result)
			}
		}
	}

	fun isLowPoint(): Boolean {
		return value < left && value < right && value < up && value < down
	}

	private fun isValid(aRow: Int, aPos: Int): Boolean {
		return aRow in 0..99 && aPos in 0..99
	}

}

class Day09(val input: List<String>) {

	private fun createPoints(input: List<String>): MutableList<Day09Point> {
		val points = mutableListOf<Day09Point>()

		input.forEachIndexed { row, str ->
			val strDown = input.getOrNull(row + 1)
			val strUp = input.getOrNull(row - 1)

			str.forEachIndexed { pos, c ->
				val value = c.toString().toInt()

				val valueLeft = str.getOrElse(pos - 1) { '9' }.toString().toInt()
				val valueRight = str.getOrElse(pos + 1) { '9' }.toString().toInt()

				val valueDown = if (strDown == null) 9 else strDown[pos].toString().toInt()
				val valueUp = if (strUp == null) 9 else strUp[pos].toString().toInt()

				points.add(
					Day09Point(row, pos, value, valueLeft, valueRight, valueUp, valueDown)
				)
			}
		}

		return points
	}

	fun part1() {
		val points = createPoints(input)
		val filtered = points.filter { it.isLowPoint() }
		val filteredSum = filtered.sumOf { it.value + 1 }

		print1(filteredSum)
	}

	fun part2() {
		val points = createPoints(input)
		val sizes = mutableSetOf<Int>()

		points.forEach {
			val size = it.findBasinSize(points)

			sizes.add(size)
		}

		val sorted = sizes.sortedByDescending { it }
		val result = sorted[0] * sorted[1] * sorted[2]

		print2(result)
	}

}

fun main() {

	val input = readInput("day09.txt")

	Day09(input).part1()
	Day09(input).part2()

}
