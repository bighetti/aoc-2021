package aoc

import kotlin.math.abs

class Day22Cube(val x: IntRange, val y: IntRange, val z: IntRange, var on: Boolean) {

	fun getTotalSize(): Long {
		val xSize = if (x.first == x.last) 1 else abs(x.last - x.first) + 1
		val ySize = if (y.first == y.last) 1 else abs(y.last - y.first) + 1
		val zSize = if (z.first == z.last) 1 else abs(z.last - z.first) + 1
		val multiply = if (on) 1 else -1

		return xSize.toLong() * ySize.toLong() * zSize.toLong() * multiply
	}

	fun intersect(other: Day22Cube, on: Boolean): Day22Cube? {
		return if (x.first > other.x.last || x.last < other.x.first || y.first > other.y.last || y.last < other.y.first || z.first > other.z.last || z.last < other.z.first) {
			null
		} else {
			Day22Cube(
				IntRange(listOf(x.first, other.x.first).maxOf { it }, listOf(x.last, other.x.last).minOf { it }),
				IntRange(listOf(y.first, other.y.first).maxOf { it }, listOf(y.last, other.y.last).minOf { it }),
				IntRange(listOf(z.first, other.z.first).maxOf { it }, listOf(z.last, other.z.last).minOf { it }),
				on
			)
		}
	}

	fun rangeAfter(range: IntRange, intersectRange: IntRange): IntRange? {
		if (range.last <= intersectRange.last) {
			return null
		}

		val rangeStart = intersectRange.last + 1
		val rangeEnd = range.last

		return IntRange(rangeStart, rangeEnd)
	}

	fun rangeBefore(range: IntRange, intersectRange: IntRange): IntRange? {
		if (range.first >= intersectRange.first) {
			return null
		}

		val rangeStart = range.first
		val rangeEnd = intersectRange.first - 1

		return IntRange(rangeStart, rangeEnd)
	}

	override fun toString() = "$x,$y,$z on = $on, size = ${getTotalSize()}"

}

class Day22(val input: List<String>) {

	private val cubeMap = mutableMapOf<String, Boolean>()

	private fun parse1(str: String, coordinate: String): IntRange? {
		val substring = str.split(" ").last().split(",").find { it.startsWith(coordinate) } ?: return null

		val low = substring.drop(2).split("..").first().toInt() // e.g. -48..6
		val high = substring.drop(2).split("..").last().toInt()

		if ((low < -50 && high < -50) || (low > 50 && high > 50)) {
			return null
		}

		return IntRange(low, high)
	}

	private fun parse2(str: String): Day22Cube {
		val isOn = str.startsWith("on ")
		val split = str.split(" ").last().split(",")

		val lowX = split[0].drop(2).split("..").first().toInt() // e.g. x=-48..6
		val highX = split[0].drop(2).split("..").last().toInt()
		val lowY = split[1].drop(2).split("..").first().toInt()
		val highY = split[1].drop(2).split("..").last().toInt()
		val lowZ = split[2].drop(2).split("..").first().toInt()
		val highZ = split[2].drop(2).split("..").last().toInt()

		val xRange = IntRange(lowX, highX)
		val yRange = IntRange(lowY, highY)
		val zRange = IntRange(lowZ, highZ)

		return Day22Cube(xRange, yRange, zRange, isOn)
	}

	private fun splitCube(oldCube: Day22Cube, newCube: Day22Cube): List<Day22Cube> {
		val intersect = newCube.intersect(oldCube, newCube.on) ?: return listOf(oldCube)
		val results = mutableListOf<Day22Cube>()

		val beforeX = oldCube.rangeBefore(oldCube.x, intersect.x)
		val beforeY = oldCube.rangeBefore(oldCube.y, intersect.y)
		val beforeZ = oldCube.rangeBefore(oldCube.z, intersect.z)

		val afterX = oldCube.rangeAfter(oldCube.x, intersect.x)
		val afterY = oldCube.rangeAfter(oldCube.y, intersect.y)
		val afterZ = oldCube.rangeAfter(oldCube.z, intersect.z)

		if (beforeX != null) {
			results.add(
				Day22Cube(beforeX, oldCube.y, oldCube.z, oldCube.on)
			)
		}

		if (beforeY != null) {
			results.add(
				Day22Cube(intersect.x, beforeY, oldCube.z, oldCube.on)
			)
		}

		if (beforeZ != null) {
			results.add(
				Day22Cube(intersect.x, intersect.y, beforeZ, oldCube.on)
			)
		}

		if (afterX != null) {
			results.add(
				Day22Cube(afterX, oldCube.y, oldCube.z, oldCube.on)
			)
		}

		if (afterY != null) {
			results.add(
				Day22Cube(intersect.x, afterY, oldCube.z, oldCube.on)
			)
		}

		if (afterZ != null) {
			results.add(
				Day22Cube(intersect.x, intersect.y, afterZ, oldCube.on)
			)
		}

		return results
	}

	fun part1() {
		input.forEach { str ->
			val isOn = str.startsWith("on ")
			val xRange = parse1(str, "x")
			val yRange = parse1(str, "y")
			val zRange = parse1(str, "z")

			if (xRange != null && yRange != null && zRange != null) {
				for (x in xRange) {
					for (y in yRange) {
						for (z in zRange) {
							cubeMap["$x,$y,$z"] = isOn
						}
					}
				}
			}
		}

		val cubesOn = cubeMap.filter { it.value }.size

		print1(cubesOn)
	}

	fun part2() {
		val cubes = input.map { str ->
			parse2(str)
		}

		val seen = mutableListOf<Day22Cube>()

		cubes.forEach { cube ->
			val toAdd = mutableListOf<Day22Cube>()

			seen.forEach { seenCube ->
				toAdd.addAll(splitCube(seenCube, cube))
			}

			seen.clear()
			seen.addAll(toAdd)

			if (cube.on) {
				seen.add(cube)
			}
		}

		val result = seen.sumOf { it.getTotalSize() }

		print2(result)
	}

}

fun main() {

	val input = readInput("day22.txt")

	Day22(input).part1()
	Day22(input).part2()

}
