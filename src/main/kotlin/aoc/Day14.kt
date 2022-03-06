package aoc

class Day14(val input: List<String>) {

	fun part1() {
		val instructions = input.drop(2)
		var template = input.first()

		for (i in 1..10) { // 10 iterations of all instructions
			val replacements = mutableMapOf<String, String>()

			instructions.forEach {
				val key = it.split(" -> ").first()
				val insert = it.split(" -> ").last()

				replacements[key] = insert
			}

			val templateList = template.toList()
			val templateListSize = templateList.size
			val templateBuilder = StringBuilder()

			templateList.forEachIndexed { index, char ->
				templateBuilder.append(char)

				if (index < templateListSize - 1) {
					val replace = replacements["$char${templateList[index + 1]}"]

					if (replace != null) {
						templateBuilder.append(replace)
					}
				}
			}

			template = templateBuilder.toString()
		}

		val grouped = template.toList().groupBy { it }

		val max = grouped.values.maxOf { it.size }
		val min = grouped.values.minOf { it.size }

		print1(max - min)
	}

	fun part2() {
		val instructions = input.drop(2)
		var template = input.first()

		var occurs = mutableMapOf<String, Long>()
		var replacements = mutableMapOf<String, String>()

		var firstChar = template.take(1) // never changes
		var lastChar = template.takeLast(1) // never changes

		instructions.forEach {
			val key = it.split(" -> ").first()
			val insert = it.split(" -> ").last()

			// find how many times each instruction occurs in the template (left value)
			var count = 0L
			var index = template.indexOf(key)

			while (index > -1) {
				index = template.indexOf(key, index + 1)

				count++
			}

			occurs[key] = count
			replacements[key] = insert
		}

		for (i in 1..40) { // 40 iterations of all instructions
			val occursClone = occurs.toMap()

			replacements.keys.forEach { key ->
				occurs[key] = 0 // clear values in occurs map
			}

			replacements.forEach { (k, v) ->
				val value = occursClone[k]

				// update how many times each instruction will occur in the new template with the extra character added
				// first character + replacement character
				// AND replacement character + second character
				if (value != null && value > 0) {
					occurs["${k[0]}$v"] = (occurs["${k[0]}$v"] ?: 0) + value // include any that already exist
					occurs["$v${k[1]}"] = (occurs["$v${k[1]}"] ?: 0) + value // include any that already exist
				}
			}
		}

		val elements = listOf("B", "C", "F", "H", "K", "O", "N", "P", "S", "V") // all possible characters

		val elementCounts = elements.map { char ->
			val count = occurs.filter { (k, _) -> k[0] == char[0] }.map { it.value }.sum() +
				occurs.filter { (k, _) -> k[1] == char[0] }.map { it.value }.sum()

			var countDivided = count / 2

			if (char == firstChar) {
				countDivided++
			}

			if (char == lastChar) {
				countDivided++
			}

			countDivided
		}

		val max = elementCounts.maxOf { it }
		val min = elementCounts.minOf { it }

		print2(max - min)
	}

}

fun main() {

	val input = readInput("day14.txt")

	Day14(input).part1()
	Day14(input).part2()

}
