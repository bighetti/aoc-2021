package aoc

class Day04Grid(input: List<String>) {

	val rows: List<List<Int>>
	val cols: List<List<Int>>

	init {
		rows = input.map { str ->
			str.trim().replace("  ", " ").split(" ").map { num -> num.toInt() }
		}

		cols = List(rows.size) { index ->
			rows.map { it[index] } // get one value from each row
		}
	}

	fun getScore(numbersCalled: List<Int>): Int {
		val number = numbersCalled.last()
		val sum = rows.flatten().filter { !numbersCalled.contains(it) }.sum()

		return number * sum
	}

	fun isComplete(numbersCalled: List<Int>): Boolean {
		// 5 matching numbers in a col or row vs. the numbers called
		return rows.plus(cols).find { it.intersect(numbersCalled.toSet()).size == 5 } != null
	}

}

class Day04(val input: List<String>) {

	fun part1() {
		val boardGroups = input.drop(1).filter { it.isNotEmpty() }.chunked(5)
		val numbersToBeCalled = input.first().split(",").map { it.toInt() }

		val grids = boardGroups.map { Day04Grid(it) }.toMutableList()

		numbersToBeCalled.forEachIndexed { index, _ ->
			val numbersCalled = numbersToBeCalled.take(index + 1)
			val completeGrid = grids.find { it.isComplete(numbersCalled) }

			if (completeGrid != null) {
				print1(completeGrid.getScore(numbersCalled))
				return // no need to continue
			}
		}
	}

	fun part2() {
		val boardGroups = input.drop(1).filter { it.isNotEmpty() }.chunked(5)
		val numbersToBeCalled = input.first().split(",").map { it.toInt() }

		val completedGrids = mutableListOf<Day04Grid>()
		val grids = boardGroups.map { Day04Grid(it) }.toMutableList()

		numbersToBeCalled.forEachIndexed { index, _ ->
			val numbersCalled = numbersToBeCalled.take(index + 1)

			grids.forEach { grid ->
				if (grid.isComplete(numbersCalled)) {
					completedGrids.add(grid)
				}

				if (completedGrids.size == boardGroups.size) {
					val lastGrid = completedGrids.last()

					print2(lastGrid.getScore(numbersCalled))
				}
			}

			grids.removeAll(completedGrids) // remove any new completed grids
		}
	}

}

fun main() {

	val input = readInput("day04.txt")
	// val inputSample = readInput("day04.sample.txt")

	Day04(input).part1()
	// Day04(inputSample).part1()

	Day04(input).part2()

}
