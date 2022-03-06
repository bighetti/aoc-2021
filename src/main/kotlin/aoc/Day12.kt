package aoc

class Day12Cave(var path: String) {

	override fun toString() = path

}

class Day12(val input: List<String>) {

	/**
	 * Find all the next steps for the last cave added to the path.
	 */
	fun findPaths(caves: MutableList<Day12Cave>, cave: Day12Cave, duplicateSmall: Boolean) {
		val name = cave.path.split("-").last()
		val path = cave.path // to be appended with next cave in each possible route

		val inputWithCaveName = input.filter { it.contains(name) && !it.contains("start") }

		inputWithCaveName.forEach { str ->
			val cave1 = str.split("-").first()
			val cave2 = str.split("-").last()

			val otherCaveName = if (cave1 == name) cave2 else cave1

			if (!isCaveAlreadyAdded(cave, otherCaveName, duplicateSmall)) {
				val newCave = Day12Cave(path.plus("-$otherCaveName"))

				if (otherCaveName == "end") {
					caves.add(newCave)
				} else {
					findPaths(caves, newCave, duplicateSmall)
				}
			}
		}
	}

	fun isCaveAlreadyAdded(cave: Day12Cave, otherName: String, duplicateSmall: Boolean): Boolean {
		// rule only applies to 'small' caves
		if (!duplicateSmall) {
			return otherName == otherName.lowercase() && cave.path.contains(otherName)
		}

		// find the amount of 'small' caves vs. amount of unique 'small' cases
		// should either be equal or -1
		val lower = cave.path.split("-").filter { it == it.lowercase() }
		val lowerDistinct = lower.distinct()

		return otherName == otherName.lowercase() && cave.path.contains(otherName) && lowerDistinct.size < lower.size
	}

	private fun parseInput(): List<Day12Cave> {
		val caves = mutableListOf<Day12Cave>()
		val inputWithStart = input.filter { it.contains("start") }

		inputWithStart.forEach { str ->
			val cave1 = str.split("-").first()
			val cave2 = str.split("-").last()

			val nextName = if (cave1 == "start") cave2 else cave1

			caves.add(
				Day12Cave("start-$nextName")
			)
		}

		return caves
	}

	fun part1() {
		val results = mutableListOf<Day12Cave>()
		val startCaves = parseInput()

		startCaves.forEach { cave ->
			findPaths(results, cave, false) // allow no duplicate 'small' caves
		}

		print1(results.size)
	}

	fun part2() {
		val results = mutableListOf<Day12Cave>()
		val startCaves = parseInput()

		startCaves.forEach { cave -> // concurrent modification
			findPaths(results, cave, true) // allow one duplicate 'small' cave
		}

		print2(results.size)
	}

}

fun main() {

	val input = readInput("day12.txt")

	Day12(input).part1()
	Day12(input).part2()

}
