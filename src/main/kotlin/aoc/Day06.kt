package aoc

class Day06(val input: List<String>) {

	private fun increase(fish: MutableList<Int>, days: Int): Long {
		val fishMap = mutableMapOf<Int, Long>()

		for (value in 0..8) {
			fishMap[value] =  fish.filter { it == value}.size.toLong()
		}

		for (i in 1..days) {
			val cloneMap = fishMap.toMutableMap()

			for (value in 0..5) {
				fishMap[value] = cloneMap.getOrDefault(value + 1, 0)
			}

			// 0 and 7 both go to 6
			fishMap[6] = cloneMap.getOrDefault(0, 0) + cloneMap.getOrDefault(7, 0)
			fishMap[7] = cloneMap.getOrDefault(8, 0)
			fishMap[8] = cloneMap.getOrDefault(0, 0)
		}

		return fishMap.values.sum()
	}

	fun part1() {
		val fish = input[0].split(",").map { it.toInt() }.toMutableList()

		print1(increase(fish, 80))
	}

	fun part2() {
		val fish = input[0].split(",").map { it.toInt() }.toMutableList()

		print2(increase(fish, 256))
	}

}

fun main() {

	val input = readInput("day06.txt")

	Day06(input).part1()
	Day06(input).part2()

}
