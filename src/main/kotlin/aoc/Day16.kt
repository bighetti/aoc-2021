package aoc

import java.math.BigInteger

class Day16(val input: List<String>) {

	var level = 0
	var literals = mutableMapOf<Int, MutableList<Long>>()
	var result = 0L
	var versionTotal = 0L

	private fun binaryToDecimal(input: String): Long {
		return input.toLong(2)
	}

	private fun calculateTotal(type: Int, values: List<Long>): Long {
		return when (type) {
			0 -> values.sum()
			1 -> {
				if (values.size == 1) {
					values[0]
				} else {
					var product = values[0]

					for (i in 1 until values.size) {
						product *= values[i]
					}

					product
				}
			}
			2 -> values.minOrNull() ?: 0
			3 -> values.maxOrNull() ?: 0
			5 -> if (values[0] > values[1]) 1 else 0
			6 -> if (values[0] < values[1]) 1 else 0
			7 -> if (values[0] == values[1]) 1 else 0
			else -> throw Exception("unexpected type: $type")
		}
	}

	private fun hexToBinary(input: String): String {
		return BigInteger(input, 16).toString(2)
	}

	private fun isNotEmpty(input: String): Boolean {
		return input.replace("0", "").isNotEmpty()
	}

	private fun readBinary(input: String): String {
		val version = binaryToDecimal(input.take(3))
		val type = binaryToDecimal(input.drop(3).take(3)).toInt()

		if (type != 4) {
			level++

			literals[level] = mutableListOf()
		}

		versionTotal += version

		val literal = (type == 4)
		var remaining = input.drop(6)

		remaining = if (literal) {
			readLiteral(remaining)
		} else {
			readOperator(remaining)
		}

		if (type != 4) {
			val total = calculateTotal(type, literals[level] ?: mutableListOf())

			if (level == 1) {
				result = total // final result
			} else {
				literals[level - 1]?.add(total)

				literals.remove(level)
			}

			level--
		}

		return remaining
	}

	private fun readLiteral(input: String): String {
		var lastPacket = false

		var data = ""
		var remaining = input

		while (!lastPacket) {
			lastPacket = (remaining.take(1) == "0")

			data += remaining.drop(1).take(4)
			remaining = remaining.drop(5)
		}

		// process the data
		val literal = binaryToDecimal(data)

		literals[level]?.add(literal)

		return remaining
	}

	private fun readOperator(input: String): String {
		val lengthType = input.take(1)
		var remaining = input.drop(1)

		if (lengthType == "0") {
			val length = binaryToDecimal(remaining.take(15)) // length

			remaining = readPacketsByLength(remaining, length)
		} else if (lengthType == "1") {
			val packets = binaryToDecimal(remaining.take(11)) // sub packets

			remaining = readPacketsByCount(remaining, packets)
		}

		return remaining
	}

	private fun readPacketsByCount(input: String, packetCount: Long): String {
		var remaining = input.drop(11)

		for (i in 1..packetCount) {
			remaining = readBinary(remaining)
		}

		return remaining
	}

	private fun readPacketsByLength(input: String, length: Long): String {
		var remaining = input.drop(15)

		val remainingSize = remaining.length - length

		while (remaining.length > remainingSize) {
			remaining = readBinary(remaining)
		}

		return remaining
	}

	fun part1() {
		var binary = hexToBinary(input[0])

		while (isNotEmpty(binary)) {
			binary = readBinary(binary)
		}

		print1(versionTotal)
	}

	fun part2() {
		var binary = hexToBinary(input[0])

		while (isNotEmpty(binary)) {
			binary = readBinary(binary)
		}

		print2(result)
	}

}

fun main() {

	val input = readInput("day16.txt")

	Day16(input).part1()
	Day16(input).part2()

}
