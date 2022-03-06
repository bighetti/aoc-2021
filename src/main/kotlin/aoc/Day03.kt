package aoc

class Day03(val input: List<String>) {

	private fun binaryToDecimal(str: String): Int {
		return str.toInt(2)
	}

	fun findChar(remainingInput: List<String>, pos: Int, mostCommon: Boolean): Char {
		if (remainingInput.size == 1) {
			return remainingInput[0][pos] // just return the character
		}

		val count = remainingInput.filter { it[pos] == '0' }.size

		return if (count > (remainingInput.size / 2)) {
			if (mostCommon) '0' else '1'
		} else {
			if (mostCommon) '1' else '0'
		}
	}

	fun part1() {
		val majority = input.size / 2

		var gamma = ""
		var epsilon = ""

		for (index in 0 until input[0].length) {
			val count = input.filter { it[index] == '0' }.size

			if (count > majority) { // 0 is the majority
				gamma = gamma.plus("0")
				epsilon = epsilon.plus("1")
			} else {
				gamma = gamma.plus("1")
				epsilon = epsilon.plus("0")
			}
		}

		print1(binaryToDecimal(gamma) * binaryToDecimal(epsilon))
	}

	fun part2() {
		var common = input
		var notCommon = input

		for (index in 0 until input[0].length) {
			val commonChar = findChar(common, index, true)
			val notCommonChar = findChar(notCommon, index, false)

			common = common.filter { it[index] == commonChar }
			notCommon = notCommon.filter { it[index] == notCommonChar }
		}

		print2(binaryToDecimal(common[0]) * binaryToDecimal(notCommon[0]))
	}

}

fun main() {

	val input = readInput("day03.txt")

	Day03(input).part1()
	Day03(input).part2()

}
