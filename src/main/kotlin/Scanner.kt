object Scanner {
    fun scan(input: String): Collection<Vowel> {
        val line = input.toLowerCase()
        return findVowels(line)
            .findElisions(line)
            .markDiphthongsLong(line)
            .markLongByPosition(line)
    }

    private fun findVowels(input: String): Set<Vowel> {
        val foundDiphthongs = diphthongs.findAll(input)
        val foundSingleVowels = singleVowels.findAll(input)

        val result = foundSingleVowels
            .filterNot { vowel -> foundDiphthongs.any { vowel.range.overlaps(it.range) } }
            .map { Vowel(it.range) }
            .toMutableSet()

        result.addAll(foundDiphthongs.map { Vowel(it.range) })
        return result
    }

    private fun Collection<Vowel>.findElisions(input: String): Collection<Vowel> {
        elisions.findAll(input).forEach { result ->
            this.find { result.range.contains(it.position.last) }?.isElided = true
        }
        return this
    }

    private fun Collection<Vowel>.markDiphthongsLong(input: String): Collection<Vowel> {
        this.forEach { if (it.position.first != it.position.last) it.isLong = true }
        return this
    }

    private fun Collection<Vowel>.markLongByPosition(input: String): Collection<Vowel> {
        longByPosition.findAll(input).forEach { result ->
            this.find { result.range.contains(it.position.last)}?.isLong = true
        }
        return this
    }

    class Vowel(val position: IntRange) {
        var isElided = false
        var isLong = false
    }

    private val singleVowels = Regex("[aeiou]")
    private val diphthongs = Regex("([ao]e)|(ei)|([ae]u)")
    private val elisions = Regex("${singleVowels.pattern}m? h?${singleVowels.pattern}")
    private val longByPosition = Regex("${singleVowels.pattern} ?([zx]|([bcdfgjklmnpqrst] ?[bcdfgjkmnpqst])|([klmnqrs] ?r)|([bcdfgjpt] r)|([bcdjklmnqrst] ?l)|([fgp] l))")

    private fun IntRange.overlaps(other: IntRange): Boolean {
        return this.first <= other.last && this.last >= other.first
    }
}