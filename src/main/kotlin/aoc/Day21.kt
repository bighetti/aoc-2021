package aoc

class Day21Game(var playerPos1: Int, var playerPos2: Int) {

	var instances = 1L

	var finished = false
	var isPlayer1 = true

	var playerScore1 = 0
	var playerScore2 = 0

	override fun equals(other: Any?): Boolean {
		if (other == null || other !is Day21Game) {
			return false
		}

		return isPlayer1 == other.isPlayer1
				&& playerPos1 == other.playerPos1
				&& playerScore1 == other.playerScore1
				&& playerPos2 == other.playerPos2
				&& playerScore2 == other.playerScore2
	}

	override fun hashCode(): Int {
		var result = isPlayer1.hashCode()

		result = 31 * result + playerPos1
		result = 31 * result + playerScore1
		result = 31 * result + playerPos2
		result = 31 * result + playerScore2

		return result
	}

}

class Day21Score(one: Int, two: Int, three: Int) {

	val total = one + two + three

}

class Day21(val input: List<String>) {

	var diceNumber = 0
	var iterations = 0
	var rollCount = 0

	var playerWin1 = 0L
	var playerWin2 = 0L

	private fun createGameQuantum(originalGame: Day21Game, score: Day21Score, scoreInstances: Int): Day21Game {
		val result = Day21Game(originalGame.playerPos1, originalGame.playerPos2).apply {
			instances = originalGame.instances * scoreInstances
			isPlayer1 = originalGame.isPlayer1
			playerScore1 = originalGame.playerScore1
			playerScore2 = originalGame.playerScore2
		}

		// also run the game
		if (result.isPlayer1) {
			result.playerPos1 = updatePosition(result.playerPos1, score.total)
			result.playerScore1 += result.playerPos1
		} else {
			result.playerPos2 = updatePosition(result.playerPos2, score.total)
			result.playerScore2 += result.playerPos2
		}

		result.isPlayer1 = !result.isPlayer1

		if (result.playerScore1 >= 21 || result.playerScore2 >= 21) {
			result.finished = true
		}

		return result
	}

	private fun getNext(): Int {
		if (diceNumber == 0 || diceNumber == 100) {
			diceNumber = 1
		} else {
			diceNumber++
		}

		rollCount++

		return diceNumber
	}

	private fun roll(game: Day21Game) {
		val score = getNext() + getNext() + getNext()

		if (game.isPlayer1) {
			game.playerPos1 = updatePosition(game.playerPos1, score)
			game.playerScore1 += game.playerPos1
		} else {
			game.playerPos2 = updatePosition(game.playerPos2, score)
			game.playerScore2 += game.playerPos2
		}

		game.isPlayer1 = !game.isPlayer1
	}

	private fun rollQuantum(games: List<Day21Game>, scores: Map<Int, List<Day21Score>>): List<Day21Game> {
		val newGames = mutableListOf<Day21Game>()

		games.forEach { game ->
			scores.values.forEach { scoreList -> // scores grouped by total from 3 rolls
				val score = scoreList.first()
				val newGame = createGameQuantum(game, score, scoreList.size) // play the game

				val index = newGames.indexOf(newGame) // find a game with the same values

				if (index == -1) {
					newGames.add(newGame)
				} else {
					newGames[index].instances += newGame.instances
				}
			}
		}

		// find games which just finished
		newGames.filter { it.finished }.forEach {
			if (it.playerScore1 >= 21) { // winning score always 21
				playerWin1 += it.instances
			} else {
				playerWin2 += it.instances
			}
		}

		iterations++

		// repeat with any game that have not finished yet
		return newGames.filter { !it.finished }.toMutableList()
	}

	private fun updatePosition(startingPosition: Int, score: Int): Int {
		val newPosition = (startingPosition + score).toString()

		return if (newPosition.endsWith("0")) {
			10
		} else {
			newPosition.takeLast(1).toInt()
		}
	}

	fun part1() {
		val pos1 = input[0].replace("Player 1 starting position: ", "").toInt()
		val pos2 = input[1].replace("Player 2 starting position: ", "").toInt()

		val game = Day21Game(pos1, pos2)
		val winningScore = 1000

		while (game.playerScore1 < winningScore && game.playerScore2 < winningScore) {
			roll(game)
		}

		val losingScore = listOf(game.playerScore1, game.playerScore2).minOf { it }

		print1(losingScore * rollCount)
	}

	fun part2() {
		val pos1 = input[0].replace("Player 1 starting position: ", "").toInt()
		val pos2 = input[1].replace("Player 2 starting position: ", "").toInt()

		var games = listOf(Day21Game(pos1, pos2))
		val scores = mutableListOf<Day21Score>() // 27 scenarios

		for (first in 1..3) {
			for (second in 1..3) {
				for (third in 1..3) {
					scores.add(Day21Score(first, second, third))
				}
			}
		}

		val scoresGrouped = scores.groupBy { it.total }

		while (games.isNotEmpty()) {
			games = rollQuantum(games, scoresGrouped)
		}

		print2("player 1 wins: $playerWin1")
		print2("player 2 wins: $playerWin2")
	}

}

fun main() {

	val input = readInput("day21.txt")

	Day21(input).part1()
	Day21(input).part2()

}
