package aoc

class Day08(val input: List<String>) {

	private fun containsAll(str: String, positions: Map<Int, String>, vararg search: Int): Boolean {
		if (str.length != search.size) {
			return false
		}

		val list = str.map { it.toString() }
		val searchStrings = search.map { positions[it] ?: "" }

		return list.containsAll(searchStrings)
	}

	private fun findPosition1(words: List<String>): String {
		val length2 = words.find { it.length == 2 }
		val length3 = words.find { it.length == 3 }

		if (length2 == null || length3 == null) {
			throw Exception("could not determine position 1")
		}

		return reduceString(length3, length2)
	}

	private fun findPosition2(words: List<String>): String {
		val length2 = words.find { it.length == 2 }
		val length4 = words.find { it.length == 4 }

		if (length2 == null || length4 == null) {
			throw Exception("could not determine position 2")
		}

		val reduced = reduceString(length4, length2) // position 2 (B) and 4 (D) in some order

		words.forEach {
			// strings of length 5 always contain 4 (D) but only some contain 2 (B)
			if (it.length == 5) {
				if (!it.contains(reduced.first())) {
					return reduced.first().toString()
				} else if (!it.contains(reduced.last())) {
					return reduced.last().toString()
				}
			}
		}

		throw Exception("could not determine position 2")
	}

	private fun findPosition3(words: List<String>): String {
		val length2 = words.find { it.length == 2 }
		val length3 = words.find { it.length == 3 }
		val length4 = words.find { it.length == 4 }

		if (length2 == null || length3 == null || length4 == null) {
			throw Exception("could not determine position 3")
		}

		words.forEach {
			// strings of length 6 always contain 1 (A) and 6 (F) but only some contain 3 (C)
			if (it.length == 6) {
				val reduced = reduceString(it, length3)

				if (reduced.length == 4) {
					return reduceString(length3, it)
				}
			}
		}

		throw Exception("could not determine position 3")
	}

	private fun findPosition4(words: List<String>, positions: Map<Int, String>): String {
		val length2 = words.find { it.length == 2 }
		val length4 = words.find { it.length == 4 }
		val position2 = positions[2]

		if (length2 == null || length4 == null || position2 == null) {
			throw Exception("could not determine position 4")
		}

		// strings of length 4 always contain 2 (B) and 4 (D) in addition to those in length 2
		// position 2 is already known
		return reduceString(length4, "$length2$position2")
	}

	private fun findPosition5(positions: Map<Int, String>): String {
		val position1 = positions[1]
		val position2 = positions[2]
		val position3 = positions[3]
		val position4 = positions[4]
		val position6 = positions[6]
		val position7 = positions[7]

		val length6 = "$position1$position2$position3$position4$position6$position7"
		val length7 = "abcdefg"

		// the only position remaining is 5 (E)
		return reduceString(length7, length6)
	}

	private fun findPosition6(words: List<String>, positions: Map<Int, String>): String {
		val position2 = positions[2]
		val position3 = positions[3]
		val position4 = positions[4]

		val length3 = "$position2$position3$position4"
		val length4 = words.find { it.length == 4 } ?: throw Exception("could not determine position 6")

		// strings of length 4 always contain 2 (B), 3 (C), 4 (D), and 6 (F)
		// position 2, 3, and 4 are already known
		return reduceString(length4, length3)
	}

	private fun findPosition7(words: List<String>, positions: Map<Int, String>): String {
		val position1 = positions[1]
		val position2 = positions[2]
		val position3 = positions[3]
		val position4 = positions[4]
		val position6 = positions[6]

		val length5 = "$position1$position2$position3$position4$position6"

		words.forEach {
			if (it.length == 6) {
				val reduced = reduceString(it, length5)

				// strings of length 6 always contain 1 (A), 2 (B), 6 (F), and 7 (G)
				// one form also contains 3 (C) and 4 (D)
				// position 1, 2, 3, 4, and 6 are already known
				if (reduced.length == 1) {
					return reduced
				}
			}
		}

		throw Exception("could not determine position 7")
	}

	private fun findValue(str: String, positions: Map<Int, String>): String {
		return when {
			// length based searches
			str.length == 2 -> "1"
			str.length == 3 -> "7"
			str.length == 4 -> "4"
			str.length == 7 -> "8"
			// character based searches
			containsAll(str, positions, 1, 2, 3, 5, 6, 7) -> "0"
			containsAll(str, positions, 1, 3, 4, 5, 7) -> "2"
			containsAll(str, positions, 1, 3, 4, 6, 7) -> "3"
			containsAll(str, positions, 1, 2, 4, 6, 7) -> "5"
			containsAll(str, positions, 1, 2, 4, 5, 6, 7) -> "6"
			containsAll(str, positions, 1, 2, 3, 4, 6, 7) -> "9"
			// should not happen
			else -> throw Exception("no patterns matched for $str")
		}
	}

	private fun reduceString(first: String, second: String): String {
		val list = first.toMutableList().subtract(second.toSet())

		return list.joinToString("")
	}

	fun part1() {
		val filtered = input.map {
			val outputString = it.split(" | ").last()

			outputString.split(" ").filter { str ->
				str.length == 2 || str.length == 3 || str.length == 4 || str.length == 7
			}
		}

		print1(filtered.flatten().size)
	}

	fun part2() {
		var count = 0

		input.forEach {
			val positions = mutableMapOf<Int, String>()

			val outputWords = it.split(" | ").last().split(" ")
			val words = it.replace(" | ", " ").split(" ")

			positions[1] = findPosition1(words)
			positions[2] = findPosition2(words)
			positions[3] = findPosition3(words)
			positions[4] = findPosition4(words, positions)
			positions[6] = findPosition6(words, positions)
			positions[7] = findPosition7(words, positions)
			positions[5] = findPosition5(positions)

			val strings = outputWords.map { str ->
				findValue(str, positions)
			}

			count += strings.joinToString("").toInt()
		}

		print2(count)
	}

}

fun main() {

	val input = readInput("day08.txt")

	Day08(input).part1()
	Day08(input).part2()

}
