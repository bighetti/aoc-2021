package aoc

class Day25Grid {

	var progressRows = mutableMapOf<Int, MutableList<Char>>()
	var rows = mutableMapOf<Int, MutableList<Char>>()

	fun copyMap(from: MutableMap<Int, MutableList<Char>>, to: MutableMap<Int, MutableList<Char>>) {
		from.forEach { (key, value) ->
			to[key] = value.toMutableList()
		}
	}

	fun moveDown(): Int {
		var moved = 0

		copyMap(rows, progressRows) // use rows (map) to evaluate, make changes to progressRows (map)

		for (rowNumber in 0 until rows.size) {
			val row = rows[rowNumber]
			val progressRow = progressRows[rowNumber]

			if (row != null && progressRow != null) {
				val nextRow = if (rowNumber == rows.size - 1) rows[0] else rows[rowNumber + 1]
				val nextProgressRow = if (rowNumber == rows.size - 1) progressRows[0] else progressRows[rowNumber + 1]

				if (nextRow != null && nextProgressRow != null) {
					for (pos in 0 until row.size) {
						if (row[pos] == 'v') {
							if (nextRow[pos] == '.') {
								progressRow[pos] = '.'
								nextProgressRow[pos] = 'v'

								moved++
							}
						}
					}
				}
			}
		}

		copyMap(progressRows, rows) // apply changes

		return moved
	}

	fun moveRight(): Int {
		var moved = 0

		for (rowNumber in 0 until rows.size) {
			val row = rows[rowNumber]

			if (row != null) {
				val updatedRow = row.toMutableList() // make changes to temporary row

				for (pos in row.size - 1 downTo 0) {
					if (row[pos] == '>') {
						if (pos == row.size - 1) { // rollover to first column
							if (row[0] == '.') {
								updatedRow[0] = '>'
								updatedRow[pos] = '.'

								moved++
							}
						} else { // check next column
							if (row[pos + 1] == '.') {
								updatedRow[pos + 1] = '>'
								updatedRow[pos] = '.'

								moved++
							}
						}
					}
				}

				rows[rowNumber] = updatedRow // save temporary row back to the map
			}
		}

		return moved
	}

}

class Day25(val input: List<String>) {

	fun part1() {
		val grid = Day25Grid()

		input.forEachIndexed { index, str ->
			grid.progressRows[index] = str.toMutableList()
			grid.rows[index] = str.toMutableList()
		}

		var moveAmount = 1
		var moveCount = 0

		while (moveAmount > 0) {
			moveAmount = grid.moveRight() + grid.moveDown()

			moveCount++
		}

		print1(moveCount)
	}

}

fun main() {

	val input = readInput("day25.txt")

	Day25(input).part1()

}
