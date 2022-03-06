package aoc

class Day10(val input: List<String>) {

	private val pairs = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')

	fun part1() {
		var result = 0

		input.forEach { str ->
			var closings = mutableListOf<Char>()
			val openings = mutableListOf<Char>()

			var error = false

			str.forEach { char ->
				if (!error) {
					if (pairs.values.contains(char)) { // is closing character
						if (char == closings.first()) { // remove from trackers
							closings.removeAt(0)
							openings.removeAt(openings.size - 1)
						} else {
							error = true

							when (char) {
								')' -> result += 3
								']' -> result += 57
								'}' -> result += 1197
								'>' -> result += 25137
							}
						}
					} else { // opening character
						closings.add(0, pairs[char] ?: throw Exception("unexpected character $char"))
						openings.add(char)
					}
				}
			}
		}

		print1(result)
	}

	fun part2() {
		val results = mutableListOf<Long>()

		input.forEach { str ->
			var closings = mutableListOf<Char>()
			val openings = mutableListOf<Char>()

			var error = false

			str.forEach { char ->
				if (!error) {
					if (pairs.values.contains(char)) { // is closing character
						if (char == closings.first()) { // remove from trackers
							closings.removeAt(0)
							openings.removeAt(openings.size - 1)
						} else {
							error = true // corrupted line - rather than incomplete line

							closings.clear()
							openings.clear()
						}
					} else { // opening character
						closings.add(0, pairs[char] ?: throw Exception("unexpected character $char"))
						openings.add(char)
					}
				}
			}

			var result = 0L

			closings.forEach { char ->
				var point = when (char) {
					')' -> 1
					']' -> 2
					'}' -> 3
					else -> 4 // '>'
				}

				result *= 5
				result += point
			}

			if (result > 0) {
				results.add(result)
			}
		}

		val middle = (results.size / 2)
		val middleResult = results.sorted()[middle]

		print2(middleResult)
	}

}

fun main() {

	val input = readInput("day10.txt")

	Day10(input).part1()
	Day10(input).part2()

}
