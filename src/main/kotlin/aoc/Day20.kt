package aoc

class Day20(val input: List<String>) {

	private var inputImageLines = mutableListOf<String>()
	private var outputImageLines = mutableListOf<String>()

	private fun binaryToDecimal(str: String): Int {
		return str.toInt(2)
	}

	private fun expand(loopCount: Int) {
		val size = inputImageLines.size // current size of a grid row - always a square

		// expand each row
		for (i in 0 until size) {
			inputImageLines[i] = ".".repeat(loopCount) + inputImageLines[i] + ".".repeat(loopCount)
		}

		// expand above and below
		for (i in 0 until loopCount) {
			inputImageLines.add(0, ".".repeat(size + (loopCount * 2)))
			inputImageLines.add(".".repeat(size + (loopCount * 2)))
		}

		outputImageLines = inputImageLines.toMutableList()
	}

	private fun findOutputPixel(algorithm: String, str: String): String {
		val index = stringToDecimal(str)

		// find the value from the algorithm (by position) to insert into the new output grid
		return algorithm[index].toString()
	}

	private fun findSurroundingString(x: Int, y: Int): String {
		// get the eight characters surrounding a position - plus the position itself
		val row1 = "${getInputChar(x-1, y-1)}${getInputChar(x, y-1)}${getInputChar(x+1, y-1)}"
		val row2 = "${getInputChar(x-1, y)}${getInputChar(x, y)}${getInputChar(x+1, y)}"
		val row3 = "${getInputChar(x-1, y+1)}${getInputChar(x, y+1)}${getInputChar(x+1, y+1)}"

		return "$row1$row2$row3"
	}

	private fun generateOutput(algorithm: String) {
		inputImageLines = outputImageLines.toMutableList() // use the output as the new input

		for (x in 0 until inputImageLines.size) { // always a square
			for (y in 0 until inputImageLines.size) {
				val inputString = findSurroundingString(x, y)
				val outputPixel = findOutputPixel(algorithm, inputString)

				outputImageLines[y] = outputImageLines[y].replaceRange(x, x+1, outputPixel)
			}
		}
	}

	private fun getInputChar(x: Int, y: Int): String {
		if (x < 0 || x >= inputImageLines.size || y < 0 || y >= inputImageLines.size) {
			return inputImageLines[0][0].toString() // the default is not always a dot
		}

		return inputImageLines[y][x].toString()
	}

	private fun getPixelResult(): Int {
		return outputImageLines.sumOf { str ->
			str.toList().filter { it == '#' }.size
		}
	}

	private fun stringToDecimal(str: String): Int {
		val updated = str.replace(".", "0").replace("#", "1")

		return binaryToDecimal(updated)
	}

	fun part1() {
		val algorithm = input[0]
		val loops = 2

		inputImageLines = input.drop(2).toMutableList()
		outputImageLines = inputImageLines.toMutableList()

		expand(loops + 1)

		for (i in 0 until loops) {
			generateOutput(algorithm)
		}

		print1(getPixelResult())
	}

	fun part2() {
		val algorithm = input[0]
		val loops = 50

		inputImageLines = input.drop(2).toMutableList()
		outputImageLines = inputImageLines.toMutableList()

		expand(loops + 1)

		for (i in 0 until loops) {
			generateOutput(algorithm)
		}

		print2(getPixelResult())
	}

}

fun main() {

	val input = readInput("day20.txt")

	Day20(input).part1()
	Day20(input).part2()

}
