package aoc

class Day18SnailPair(var level: Int) {

	var parent: Day18SnailPair? = null

	// either number or pair can be set on left (first) and right (second) side
	var firstNumber = -1
	var firstPair: Day18SnailPair? = null
	var secondNumber = -1
	var secondPair: Day18SnailPair? = null

	fun addNumber(char: Char) {
		// add to first available slot
		if (firstNumber == -1 && firstPair == null) {
			firstNumber = char.toString().toInt()
		} else {
			secondNumber = char.toString().toInt()
		}
	}

	fun addPair(pair: Day18SnailPair) {
		pair.parent = this

		// add to first available slot
		if (firstNumber == -1 && firstPair == null) {
			firstPair = pair
		} else {
			secondPair = pair
		}
	}

	private fun findLevelFive(pair: Day18SnailPair): Day18SnailPair? {
		if (pair.level == 5) {
			return pair
		}

		var result: Day18SnailPair? = null

		val firstPair = pair.firstPair
		val secondPair = pair.secondPair

		if (firstPair != null) {
			result = findLevelFive(firstPair)
		}

		if (result == null && secondPair != null) {
			result = findLevelFive(secondPair)
		}

		return result
	}

	private fun findValueTen(pair: Day18SnailPair): Day18SnailPair? {
		if (pair.firstNumber >= 10) {
			return pair
		}

		var result: Day18SnailPair? = null

		val firstPair = pair.firstPair
		val secondPair = pair.secondPair

		if (firstPair != null) {
			result = findValueTen(firstPair)
		}

		if (result == null && pair.secondNumber >= 10) {
			result = pair
		}

		if (result == null && secondPair != null) {
			result = findValueTen(secondPair)
		}

		return result
	}

	private fun increaseLeftNumber() {
		var parentPair = parent

		if (parentPair != null) {
			val parentFirstPair = parentPair.firstPair
			val parentSecondPair = parentPair.secondPair

			if (parentSecondPair == this) { // update the left object of same parent - easy part
				if (parentPair.firstNumber > -1) { // left object is a number
					parentPair.firstNumber += firstNumber
				} else if (parentFirstPair != null) { // left object is a pair - the right number is the closest value to the left
					if (parentFirstPair.secondNumber > -1) {
						parentFirstPair.secondNumber += firstNumber
					}
				}
			} else { // update the right side of previous array, if exists - hard part
				var searchPair = this

				// search upwards until the original object become the right pair
				// may never happen - parent becomes null instead
				while (parentPair != null && parentPair.secondPair != searchPair) {
					searchPair = parentPair
					parentPair = searchPair.parent
				}

				if (parentPair != null) {
					if (parentPair.firstNumber > -1) { // left object is a number
						parentPair.firstNumber += firstNumber
					} else {
						var lastPair = parentPair.firstPair

						// continue until reaching the last right (second) pair
						while (lastPair?.secondPair != null) {
							lastPair = lastPair.secondPair
						}

						if (lastPair != null) {
							lastPair.secondNumber += firstNumber
						}
					}
				}
			}
		}
	}

	fun increaseLevels(newParent: Day18SnailPair) {
		if (parent == null) {
			parent = newParent
		}

		level++

		firstPair?.increaseLevels(newParent)
		secondPair?.increaseLevels(newParent)
	}

	private fun increaseRightNumber() {
		var parentPair = parent

		if (parentPair != null) {
			val parentFirstPair = parentPair.firstPair
			val parentSecondPair = parentPair.secondPair

			if (parentFirstPair == this) { // update the right object of same parent - easy part
				if (parentPair.secondNumber > -1) {
					parentPair.secondNumber += secondNumber
				} else if (parentSecondPair != null) { // right object is a pair - the left number is the closest value to the right
					if (parentSecondPair.firstNumber > -1) {
						parentSecondPair.firstNumber += secondNumber
					}
				}
			} else { // update the left side of next array, if exists - hard part
				var searchPair = this

				// search upwards until the original object become the left pair
				// may never happen - parent becomes null instead
				while (parentPair != null && parentPair.firstPair != searchPair) {
					searchPair = parentPair
					parentPair = searchPair.parent
				}

				if (parentPair != null) {
					if (parentPair.secondNumber > -1) { // right object is a number
						parentPair.secondNumber += secondNumber
					} else {
						var nextPair = parentPair.secondPair

						// continue only until reaching a left (first) number
						while (nextPair != null) {
							if (nextPair.firstNumber > -1) {
								nextPair.firstNumber += secondNumber
								nextPair = null
							} else {
								nextPair = nextPair.firstPair
							}
						}
					}
				}
			}
		}
	}

	fun reduce(): Boolean {
		val levelFive = findLevelFive(this)

		if (levelFive != null) {
			levelFive.increaseLeftNumber()
			levelFive.increaseRightNumber()

			levelFive.parent?.removePair(levelFive)

			return true
		}

		val valueTen = findValueTen(this)

		if (valueTen != null) {
			val firstValue: Int
			val nextLevel = valueTen.level + 1
			val secondValue: Int

			if (valueTen.firstNumber >= 10) {
				firstValue = valueTen.firstNumber / 2

				secondValue = if (firstValue * 2 == valueTen.firstNumber) {
					firstValue
				} else {
					firstValue + 1
				}

				valueTen.firstNumber = -1
				valueTen.firstPair = Day18SnailPair(nextLevel).apply {
					firstNumber = firstValue
					parent = valueTen
					secondNumber = secondValue
				}
			} else {
				firstValue = valueTen.secondNumber / 2

				secondValue = if (firstValue * 2 == valueTen.secondNumber) {
					firstValue
				} else {
					firstValue + 1
				}

				valueTen.secondNumber = -1
				valueTen.secondPair = Day18SnailPair(nextLevel).apply {
					firstNumber = firstValue
					parent = valueTen
					secondNumber = secondValue
				}
			}

			return true
		}

		return false
	}

	private fun removePair(childPair: Day18SnailPair) {
		if (firstPair == childPair) {
			firstNumber = 0
			firstPair = null
		} else {
			secondNumber = 0
			secondPair = null
		}
	}

	override fun toString(): String {
		val firstString = if (firstPair == null) {
			if (firstNumber == -1) "" else "$firstNumber,"
		} else {
			"$firstPair,"
		}

		val secondString = if (secondPair == null) {
			if (secondNumber == -1) "" else secondNumber
		} else {
			secondPair.toString()
		}

		return "[$firstString$secondString]"
	}
}

class Day18(val input: List<String>) {

	private fun combine(lines: MutableList<Day18SnailPair>) {
		if (lines.size == 1) {
			return
		}

		val result = Day18SnailPair(1).apply {
			firstPair = lines[0]
			secondPair = lines[1]
		}

		lines[0].increaseLevels(result)
		lines[1].increaseLevels(result)

		lines[0] = result

		lines.removeAt(1)
	}

	private fun findAllPairs(pair: Day18SnailPair, result: MutableList<Day18SnailPair>) {
		result.add(pair)

		val first = pair.firstPair
		val second = pair.secondPair

		if (first != null) {
			findAllPairs(first, result)
		}

		if (second != null) {
			findAllPairs(second, result)
		}
	}

	private fun findMagnitude(pair: Day18SnailPair): Int {
		val allPairs = mutableListOf<Day18SnailPair>()

		findAllPairs(pair, allPairs) // find all pairs inside the outermost pair

		for (currentLevel in 4 downTo 1) { // collapse pairs into numbers - magnitudes
			allPairs.filter { it.level == currentLevel }.forEach {
				val magnitude = (3 * it.firstNumber) + (2 * it.secondNumber)

				if (it.parent?.firstPair == it) {
					it.parent?.firstNumber = magnitude
					it.parent?.firstPair = null
				} else {
					it.parent?.secondNumber = magnitude
					it.parent?.secondPair = null
				}
			}
		}

		return (3 * pair.firstNumber) + (2 * pair.secondNumber)
	}

	private fun parseInput(inputs: List<String>): MutableList<Day18SnailPair> {
		val results = mutableListOf<Day18SnailPair>()

		inputs.forEach { str ->
			var currentLevel = 0
			val map = mutableMapOf<Int, Day18SnailPair>() // level -> SnailPair - temporary map

			str.forEach {
				if (it == '[') { // found start of [] group
					val pair = Day18SnailPair(++currentLevel)

					map[currentLevel] = pair

					if (currentLevel == 1) {
						results.add(pair)
					} else {
						val parentPair = map[currentLevel - 1]

						parentPair?.addPair(pair)
					}
				} else if (it == ']') { // found end of [] group
					map.remove(currentLevel--)
				} else if (it != ',') { // found a number - could be left (first) or right (second)
					val pair = map[currentLevel]

					pair?.addNumber(it) // add number to pair
				}
			}
		}

		return results
	}

	fun part1() {
		val lines = parseInput(input)
		var lineSize = lines.size

		while (lineSize > 1) {
			var reduced = true

			// continue reducing
			while (reduced) {
				reduced = lines[0].reduce()
			}

			lineSize = lines.size

			// add the second line to the first line after reducing is complete
			combine(lines)
		}

		print1(findMagnitude(lines.first()))
	}

	fun part2() {
		val magnitudes = mutableListOf<Int>()

		// compare every line to every other line, also in reverse order
		for (lineOne in input.indices) {
			for (lineTwo in input.indices) {
				if (lineOne != lineTwo) { // don't compare to self
					val inputs = listOf(input[lineOne], input[lineTwo])

					val lines = parseInput(inputs)

					combine(lines)

					var reduced = true

					while (reduced) {
						reduced = lines[0].reduce()
					}

					magnitudes.add(findMagnitude(lines[0]))
				}
			}
		}

		print2(magnitudes.maxOf { it })
	}

}

fun main() {

	val input = readInput("day18.txt")

	Day18(input).part1()
	Day18(input).part2()

}
