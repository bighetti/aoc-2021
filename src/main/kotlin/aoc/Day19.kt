package aoc

import kotlin.math.abs

class Day19Beacon(var x: Int, var y: Int, var z: Int) {

	override fun toString() = "$x,$y,$z"

}

class Day19Distance(var x: Int, var y: Int, var z: Int) {

	override fun toString() = "$x,$y,$z"

}

class Day19Scanner(val name: String) {

	var beacons = mutableListOf<Day19Beacon>()

	override fun toString() = name

}

class Day19(val input: List<String>) {

	val directions = listOf(1, -1)

	val positions = listOf(
		listOf("x", "y", "z"),
		listOf("x", "z", "y"),
		listOf("y", "x", "z"),
		listOf("z", "x", "y"),
		listOf("y", "z", "x"),
		listOf("z", "y", "x")
	)

	var beaconPositions = mutableSetOf<String>()
	val distances = mutableListOf<Day19Distance>()
	var scanners = mutableListOf<Day19Scanner>()

	private fun adjustScanner(scanner1: Day19Scanner,
							  scanner2: Day19Scanner,
							  diffX: Int,
							  multiplyX: Int,
							  diffY: Int,
							  multiplyY: Int,
							  diffZ: Int,
							  multiplyZ: Int,
							  resultPos: List<String>) {
		scanner2.beacons.forEach {
			val origX = it.x
			val origY = it.y
			val origZ = it.z

			val newX = if (resultPos.indexOf("x") == 0) {
				origX * multiplyX + diffX
			} else if (resultPos.indexOf("x") == 1) {
				origY * multiplyX + diffX
			} else {
				origZ * multiplyX + diffX
			}

			val newY = if (resultPos.indexOf("y") == 0) {
				origX * multiplyY + diffY
			} else if (resultPos.indexOf("y") == 1) {
				origY * multiplyY + diffY
			} else {
				origZ * multiplyY + diffY
			}

			val newZ = if (resultPos.indexOf("z") == 0) {
				origX * multiplyZ + diffZ
			} else if (resultPos.indexOf("z") == 1) {
				origY * multiplyZ + diffZ
			} else {
				origZ * multiplyZ + diffZ
			}

			it.x = newX
			it.y = newY
			it.z = newZ
		}

		scanner1.beacons.addAll(scanner2.beacons)
		scanner2.beacons.clear()
	}

	fun findBeaconPositions() {
		scanners.forEach { scanner ->
			scanner.beacons.forEach { beacon ->
				beaconPositions.add("${beacon.x},${beacon.y},${beacon.z}")
			}
		}
	}

	private fun findOverlap(beacons1: List<Day19Beacon>, scanner1: Day19Scanner, scanner2: Day19Scanner): List<Day19Beacon> {
		val mapOverlap = mutableMapOf<String, MutableList<Pair<Day19Beacon, Day19Beacon>>>()

		var resultMultiplyX = 0
		var resultMultiplyY = 0
		var resultMultiplyZ = 0
		var resultX = 0
		var resultY = 0
		var resultZ = 0
		var resultPos = emptyList<String>()

		beacons1.forEach { beacon1 ->
			val x1 = beacon1.x
			val y1 = beacon1.y
			val z1 = beacon1.z

			scanner2.beacons.forEach { beacon2 ->
				directions.forEach { xMultiply ->
					directions.forEach { yMultiply ->
						directions.forEach { zMultiply ->
							positions.forEach { pos ->
								val x2 = getValue(beacon2, xMultiply, pos.indexOf("x"))
								val y2 = getValue(beacon2, yMultiply, pos.indexOf("y"))
								val z2 = getValue(beacon2, zMultiply, pos.indexOf("z"))

								val diffX = (x1 - x2)
								val diffY = (y1 - y2)
								val diffZ = (z1 - z2)

								val key = "$diffX,$diffY,$diffZ"
								val pair = Pair(beacon1, beacon2)

								val list = mapOverlap.getOrPut(key) { mutableListOf() }

								list.add(pair)

								if (list.size == 12) {
									resultMultiplyX = xMultiply
									resultMultiplyY = yMultiply
									resultMultiplyZ = zMultiply
									resultPos = pos
									resultX = diffX
									resultY = diffY
									resultZ = diffZ

									// record the distance between the two scanners for part 2
									distances.add(Day19Distance(diffX, diffY, diffZ))
								}
							}
						}
					}
				}
			}
		}

		if (resultX != 0) {
			val result = scanner2.beacons.toList()

			adjustScanner(scanner1, scanner2, resultX, resultMultiplyX, resultY, resultMultiplyY, resultZ, resultMultiplyZ, resultPos)

			return result
		}

		return emptyList()
	}

	private fun findOverlaps() {
		val first = scanners[0]

		val beaconLists = mutableListOf(
			first.beacons.toList()
		)

		while (beaconLists.isNotEmpty()) {
			val beacons = beaconLists.first()

			// try to match this set of beacons with all other scanners
			for (i in 1 until scanners.size) {
				if (scanners[i].beacons.isNotEmpty()) {
					val results = findOverlap(beacons, first, scanners[i])

					// the result is the modified beacons from the 'other' scanner
					// these can be checked next against all scanners
					if (results.isNotEmpty()) {
						beaconLists.add(results)
					}
				}
			}

			beaconLists.removeAt(0)
		}
	}

	private fun findMaxDistance(): Int {
		val results = mutableListOf<Int>()

		distances.forEach { distance1 ->
			distances.forEach { distance2 ->
				val diffX = abs(distance1.x - distance2.x)
				val diffY = abs(distance1.y - distance2.y)
				val diffZ = abs(distance1.z - distance2.z)

				results.add(diffX + diffY + diffZ)
			}
		}

		return results.maxOf { it }
	}

	private fun getValue(beacon: Day19Beacon, multiply: Int, position: Int): Int {
		val result = when (position) {
			0 -> beacon.x
			1 -> beacon.y
			else -> beacon.z
		}

		return result * multiply
	}

	private fun parseInput() {
		// ignore empty lines
		input.filter { it.isNotEmpty() }.forEach {
			if (it.contains("scanner")) {
				val name = it.filter { char -> char.isDigit() }

				scanners.add(Day19Scanner(name))
			} else {
				val split = it.split(",")

				scanners.last().beacons.add(
					Day19Beacon(split[0].toInt(), split[1].toInt(), split[2].toInt())
				)
			}
		}
	}

	fun part1() {
		parseInput()

		findOverlaps()
		findBeaconPositions()

		print1(beaconPositions.size)
	}

	fun part2() {
		parseInput()

		findOverlaps() // also records distances
		findBeaconPositions()

		print2(findMaxDistance())
	}

}

fun main() {

	val input = readInput("day19.txt")

	Day19(input).part1()
	Day19(input).part2()

}
