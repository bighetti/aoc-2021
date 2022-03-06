package aoc

class Day17(val input: List<String>) {

	var absoluteMaxY = -1000
	var absoluteStartX = 0
	var absoluteStartY = 0
	var targetMaxX = 0
	var targetMaxY = 0
	var targetMinX = 0
	var targetMinY = 0
	var resultCount = 0

	private fun parse() {
		val line = input.first().replace("target area: x=", "").replace(", y=", "..")

		val x1 = line.split("..")[0].toInt()
		val x2 = line.split("..")[1].toInt()
		val y1 = line.split("..")[2].toInt()
		val y2 = line.split("..")[3].toInt()

		targetMinX = listOf(x1, x2).minOf { it }
		targetMaxX = listOf(x1, x2).maxOf { it }
		targetMinY = listOf(y1, y2).minOf { it }
		targetMaxY = listOf(y1, y2).maxOf { it }
	}

	private fun simulate(startX: Int, startY: Int) {
		var maxY = 0
		var posX = 0
		var posY = 0
		var velocityX = startX
		var velocityY = startY

		var targetReached = false

		while (posX < targetMaxX && posY > targetMinY && !targetReached) {
			posX += velocityX
			posY += velocityY

			velocityY -= 1

			if (posY > maxY) {
				maxY = posY // highest Y position seen for this path
			}

			if (velocityX > 0) {
				velocityX -= 1
			} else if (velocityX < 0) {
				velocityX += 1 // never happens
			}

			if (posX in targetMinX..targetMaxX && posY >= targetMinY && posY <= targetMaxY) {
				targetReached = true
				resultCount++
			}
		}

		if (targetReached && maxY > absoluteMaxY) {
			absoluteMaxY = maxY

			absoluteStartX = startX
			absoluteStartY = startY
		}
	}

	fun part1() {
		parse()

		for (x in 1..targetMaxX) {
			for (y in 500 downTo targetMinY) {
				simulate(x, y)
			}
		}

		print1(absoluteMaxY)
	}

	fun part2() {
		parse()

		for (x in 1..targetMaxX) {
			for (y in 500 downTo targetMinY) {
				simulate(x, y)
			}
		}

		print2(resultCount)
	}

}

fun main() {

	val input = readInput("day17.txt")
	// val inputSample = readInput("day17.sample.txt")

	Day17(input).part1()
	// Day17(inputSample).part1()

	Day17(input).part2()

}
