package aoc

import java.util.PriorityQueue
import kotlin.Comparator
import kotlin.math.abs

interface Day23Area {

	fun getAvailableDestinations(fromIndex: Int): List<Pair<Day23Area, Int>>
	fun getAvailableMovers(): List<Int>

	fun moveFrom(index: Int): Day23Pod
	fun moveTo(pod: Day23Pod, index: Int)

}

class Day23Move(val grid: Day23Grid, val from: Pair<Day23Area, Int>, val to: Pair<Day23Area, Int>) {

	override fun toString() = "from: $from, to: $to"

}

class Day23Pod(val name: String = "empty") {

	var moveCount = 0

	fun clone(): Day23Pod {
		val clone = Day23Pod(name)

		clone.moveCount = moveCount

		return clone
	}

	fun getScore(): Int {
		return when (name) {
			"A" -> moveCount
			"B" -> moveCount * 10
			"C" -> moveCount * 100
			else -> moveCount * 1000
		}
	}

	override fun toString() = name

}

class Day23Hallway(val rooms: Map<String, Day23Room>) : Day23Area {

	companion object {

		val BLOCKERS = mapOf( // blockers for moving from hallway index -> room name
			"0,A" to listOf(1),
			"1,A" to emptyList(),
			"3,A" to emptyList(),
			"5,A" to listOf(3),
			"7,A" to listOf(5, 3),
			"9,A" to listOf(7, 5, 3),
			"10,A" to listOf(9, 7, 5, 3),
			"0,B" to listOf(1, 3),
			"1,B" to listOf(3),
			"3,B" to emptyList(),
			"5,B" to emptyList(),
			"7,B" to listOf(5),
			"9,B" to listOf(7, 5),
			"10,B" to listOf(9, 7, 5),
			"0,C" to listOf(1, 3, 5),
			"1,C" to listOf(3, 5),
			"3,C" to listOf(5),
			"5,C" to emptyList(),
			"7,C" to emptyList(),
			"9,C" to listOf(7),
			"10,C" to listOf(9, 7),
			"0,D" to listOf(1, 3, 5, 7),
			"1,D" to listOf(3, 5, 7),
			"3,D" to listOf(5, 7),
			"5,D" to listOf(7),
			"7,D" to emptyList(),
			"9,D" to emptyList(),
			"10,D" to listOf(9)
		)

	}

	var spots = mutableMapOf<Int, Day23Pod>()

	init {
		rooms.values.forEach { it.hallway = this }

		spots[0] = Day23Pod()
		spots[1] = Day23Pod()
		// gap for Room A
		spots[3] = Day23Pod()
		// gap for Room B
		spots[5] = Day23Pod()
		// gap for Room C
		spots[7] = Day23Pod()
		// gap for Room D
		spots[9] = Day23Pod()
		spots[10] = Day23Pod()
	}

	fun clone(rooms: Map<String, Day23Room>): Day23Hallway {
		val clone = Day23Hallway(rooms)

		clone.spots = spots.toMutableMap()

		spots.keys.forEach { index ->
			val pod = clone.spots[index]

			if (pod != null) {
				clone.spots[index] = pod.clone()
			}
		}

		return clone
	}

	override fun getAvailableDestinations(fromIndex: Int): List<Pair<Day23Area, Int>> {
		// there should only be one option at this point - after getAvailableMovers removed invalid choices
		val pod = spots[fromIndex] ?: return emptyList()
		val room = rooms[pod.name] ?: return emptyList()

		val toIndex = room.getAvailableIndex() // next empty spot in the room

		return listOf(
			Pair(room, toIndex)
		)
	}

	override fun getAvailableMovers(): List<Int> {
		// any spots that aren't empty
		// AND any spots (pods) which are blocked in front - the two edges only
		val firstPass = spots.filter {
			val blockedInFront =
				(it.key == 0 && spots[1]?.name != "empty") || (it.key == 10 && spots[9]?.name != "empty")

			it.value.name != "empty" && !blockedInFront
		}

		// any spots (pods) that aren't blocked from moving all the way to the destination room
		// AND destination room doesn't still have other pods that need to leave first
		val secondPass = firstPass.filter {
			val name = it.value.name
			val room = rooms[name]

			if (room == null) {
				false
			} else {
				val ready = room.isReadyForPod()

				val blocked = if (ready) {
					isPathBlocked(it.key, it.value)
				} else {
					true
				}

				ready && !blocked
			}
		}

		return secondPass.keys.sorted()
	}

	fun isMovePossible(indexes: List<Int>): Boolean {
		indexes.forEach {
			if (spots[it]?.name != "empty") {
				return false
			}
		}

		return true
	}

	fun isPathBlocked(currentPosIndex: Int, pod: Day23Pod): Boolean {
		val roomName = pod.name

		val blockerList = BLOCKERS["$currentPosIndex,$roomName"] ?: return true // should never be null

		if (blockerList.isEmpty()) {
			return false
		}

		blockerList.forEach { index ->
			if (spots[index]?.name != "empty") {
				return true
			}
		}

		return false
	}

	override fun moveFrom(index: Int): Day23Pod {
		val existingPod = spots[index] ?: Day23Pod() // should never be null

		spots[index] = Day23Pod() // put an empty pod in this place

		return existingPod
	}

	override fun moveTo(pod: Day23Pod, index: Int) {
		spots[index] = pod
	}

	override fun toString() = "hallway, spots: $spots"

}

class Day23Room(val name: String) : Day23Area {

	companion object {

		val BLOCKERS = mapOf( // blockers for moving from room index (0 = A) -> hallway index
			"0,0" to listOf(1, 0),
			"0,1" to listOf(1),
			"0,3" to listOf(3),
			"0,5" to listOf(3, 5),
			"0,7" to listOf(3, 5, 7),
			"0,9" to listOf(3, 5, 7, 9),
			"0,10" to listOf(3, 5, 7, 9, 10),
			"1,0" to listOf(3, 1, 0),
			"1,1" to listOf(3, 1),
			"1,3" to listOf(3),
			"1,5" to listOf(5),
			"1,7" to listOf(5, 7),
			"1,9" to listOf(5, 7, 9),
			"1,10" to listOf(5, 7, 9, 10),
			"2,0" to listOf(5, 3, 1, 0),
			"2,1" to listOf(5, 3, 1),
			"2,3" to listOf(5, 3),
			"2,5" to listOf(5),
			"2,7" to listOf(7),
			"2,9" to listOf(7, 9),
			"2,10" to listOf(7, 9, 10),
			"3,0" to listOf(7, 5, 3, 1, 0),
			"3,1" to listOf(7, 5, 3, 1),
			"3,3" to listOf(7, 5, 3),
			"3,5" to listOf(7, 5),
			"3,7" to listOf(7),
			"3,9" to listOf(9),
			"3,10" to listOf(9, 10)
		)

	}

	var spots = mutableMapOf<Int, Day23Pod>()

	lateinit var hallway: Day23Hallway

	fun clone(): Day23Room {
		val clone = Day23Room(name)

		clone.spots = spots.toMutableMap()

		for (index in 0 until clone.spots.size) {
			val pod = clone.spots[index]

			if (pod != null) {
				clone.spots[index] = pod.clone()
			}
		}

		return clone // hallway in this object needs to be set to a clone still
	}

	override fun getAvailableDestinations(fromIndex: Int): List<Pair<Day23Area, Int>> {
		// can only move to a hallway
		val roomIndex = getRoomIndex().toString()

		val blockersFiltered = BLOCKERS.filter { it.key.startsWith(roomIndex) }

		val results = mutableListOf<Pair<Day23Area, Int>>()

		blockersFiltered.forEach { (key, values) ->
			val destinationIndex = key.split(",").last().toInt()

			if (hallway.isMovePossible(values)) {
				results.add(Pair(hallway as Day23Area, destinationIndex))
			}
		}

		return results
	}

	fun getAvailableIndex(): Int {
		for (index in spots.size downTo 0) {
			if (spots[index]?.name == "empty") {
				return index
			}
		}

		return -1
	}

	override fun getAvailableMovers(): List<Int> {
		var checking = true
		var index = 0
		val result = mutableListOf<Int>()

		while (checking && index < spots.size) {
			val spot = spots[index]

			if (spot?.name == "empty") {
				index++ // continue to next
			} else {
				checking = false

				// names don't match
				// OR names match but other incorrect names below
				if (spot?.name != name || !isCorrectNamesOnly()) {
					result.add(index)
				}
			}
		}

		return result
	}

	fun getRoomIndex(): Int {
		return when (name) {
			"A" -> 0
			"B" -> 1
			"C" -> 2
			else -> 3
		}
	}

	fun isComplete(): Boolean {
		return spots.values.filter { it.name == name }.size == spots.size
	}

	fun isCorrectNamesOnly(): Boolean {
		// a combination of empty + correct name
		return spots.values.find { it.name != name && it.name != "empty" } == null
	}

	fun isReadyForPod(): Boolean {
		if (spots[0]?.name != "empty") {
			return false // all spots taken
		}

		if (spots[spots.size - 1]?.name == "empty") {
			return true // all spots empty
		}

		val incorrectPods = spots.values.find { it.name != "empty" && it.name != name }

		return incorrectPods == null // some pods need to be moved out
	}

	override fun moveFrom(index: Int): Day23Pod {
		val existingPod = spots[index] ?: Day23Pod() // should never be null

		spots[index] = Day23Pod() // replace with empty pod

		return existingPod
	}

	override fun moveTo(pod: Day23Pod, index: Int) {
		spots[index] = pod
	}

	override fun toString() = "name: $name, spots: $spots}"

}

class Day23Grid(
	line1: List<String> = emptyList(),
	line2: List<String> = emptyList(),
	line3: List<String> = emptyList(),
	line4: List<String> = emptyList()
) {

	var roomA = Day23Room("A")
	var roomB = Day23Room("B")
	var roomC = Day23Room("C")
	var roomD = Day23Room("D")

	var rooms = mapOf("A" to roomA, "B" to roomB, "C" to roomC, "D" to roomD)

	var hallway = Day23Hallway(rooms)

	init {
		listOf(line1, line2, line3, line4).forEachIndexed { index, line ->
			if (line.isNotEmpty()) {
				roomA.spots[index] = Day23Pod(line[0])
				roomB.spots[index] = Day23Pod(line[1])
				roomC.spots[index] = Day23Pod(line[2])
				roomD.spots[index] = Day23Pod(line[3])
			}
		}
	}

	fun clone(): Day23Grid {
		val clone = Day23Grid()

		clone.roomA = roomA.clone()
		clone.roomB = roomB.clone()
		clone.roomC = roomC.clone()
		clone.roomD = roomD.clone()

		clone.rooms = mapOf("A" to clone.roomA, "B" to clone.roomB, "C" to clone.roomC, "D" to clone.roomD)

		clone.hallway = hallway.clone(clone.rooms)

		return clone
	}

	fun getMoveDistance(move: Day23Move): Int {
		val fromArea = move.from.first
		val fromIndex = move.from.second

		val toArea = move.to.first
		val toIndex = move.to.second

		if (fromArea is Day23Hallway) {
			val toColumn = when ((toArea as Day23Room).name) {
				"A" -> 2
				"B" -> 4
				"C" -> 6
				else -> 8
			}

			val hallwayMove = abs(toColumn - fromIndex)
			val roomMove = toIndex + 1

			return hallwayMove + roomMove
		} else {
			val fromColumn = when ((fromArea as Day23Room).name) {
				"A" -> 2
				"B" -> 4
				"C" -> 6
				else -> 8
			}

			val roomMove = fromIndex + 1
			val hallwayMove = abs(toIndex - fromColumn)

			return hallwayMove + roomMove
		}
	}

	fun getPossibleMoves(): MutableList<Day23Move> {
		val results = mutableListOf<Day23Move>()

		val startingPositions = mutableListOf<Pair<Day23Area, Int>>()

		hallway.getAvailableMovers().forEach { index ->
			startingPositions.add(Pair(hallway, index)) // may be multiple options for hallway
		}

		// move from hallway whenever available
		if (startingPositions.isEmpty()) {
			rooms.values.forEach { room ->
				val indexes = room.getAvailableMovers()

				if (indexes.isNotEmpty()) {
					startingPositions.add(Pair(room, indexes.first())) // only 1 option for a room
				}
			}
		}

		startingPositions.forEach { (area, index) ->
			val destinations = area.getAvailableDestinations(index)

			destinations.forEach { to ->
				val clone = clone() // clone for each move

				val cloneFromArea = when (area) {
					is Day23Hallway -> clone.hallway
					is Day23Room -> clone.rooms[area.name] ?: throw Exception("should not happen")
					else -> throw Exception("should not happen")
				}

				val cloneToArea = when (val toArea = to.first) {
					is Day23Hallway -> clone.hallway
					is Day23Room -> clone.rooms[toArea.name] ?: throw Exception("should not happen")
					else -> throw Exception("should not happen")
				}

				val cloneFrom = Pair(cloneFromArea, index)
				val cloneTo = Pair(cloneToArea, to.second)

				results.add(Day23Move(clone, cloneFrom, cloneTo))
			}
		}

		return results
	}

	fun getCurrentState(): String {
		return "$hallway - $rooms"
	}

	fun getScore(): Int {
		var result = 0

		rooms.values.forEach { room ->
			room.spots.values.forEach { pod ->
				result += pod.getScore()
			}
		}

		return result
	}

	fun isComplete(): Boolean {
		return roomA.isComplete() && roomB.isComplete() && roomC.isComplete() && roomD.isComplete()
	}

	fun move(move: Day23Move) {
		val fromArea = move.from.first
		val toArea = move.to.first

		val pod = fromArea.moveFrom(move.from.second)

		pod.moveCount += getMoveDistance(move)

		toArea.moveTo(pod, move.to.second)
	}

}

class Day23(val input: List<String>) {

	var completeScores = mutableListOf<Int>()
	var seen = mutableSetOf<String>()

	private fun simulate(originalGrid: Day23Grid) {
		val comparator: Comparator<Day23Grid> = compareBy { it.getScore() }
		val grids = PriorityQueue(comparator)

		grids.add(originalGrid)

		while (grids.isNotEmpty()) {
			val grid = grids.remove()
			val gridCurrentState = grid.getCurrentState()
			var moves = emptyList<Day23Move>()

			// check if grid is complete
			// also check if grid has already been simulated from this position
			if (grid.isComplete()) {
				completeScores.add(grid.getScore())
			} else if (!seen.contains(gridCurrentState)) {
				moves = grid.getPossibleMoves() // could be no possible moves left
			}

			moves.forEach { move ->
				move.grid.move(move) // use the cloned grid

				if (completeScores.isEmpty() || completeScores.minOf { it } > move.grid.getScore()) {
					grids.add(move.grid)
				}
			}

			seen.add(gridCurrentState)
		}
	}

	fun part1() {
		val line1 = input[2].trim().split("#").filter { it.isNotEmpty() }
		val line2 = input[5].trim().split("#").filter { it.isNotEmpty() }

		val grid = Day23Grid(line1, line2)

		simulate(grid)

		print1(completeScores.minOfOrNull { it } ?: 0)
	}

	fun part2() {
		val line1 = input[2].trim().split("#").filter { it.isNotEmpty() }
		val line2 = input[3].trim().split("#").filter { it.isNotEmpty() }
		val line3 = input[4].trim().split("#").filter { it.isNotEmpty() }
		val line4 = input[5].trim().split("#").filter { it.isNotEmpty() }

		val grid = Day23Grid(line1, line2, line3, line4)

		simulate(grid)

		print2(completeScores.minOfOrNull { it } ?: 0)
	}

}

fun main() {

	val input = readInput("day23.txt")

	Day23(input).part1()
	Day23(input).part2()

}
