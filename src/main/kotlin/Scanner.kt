object Scanner {
    fun scan(input: String, meter: Meter = Meter.UNKNOWN): Collection<Vowel> {
        val line = input.toLowerCase()
        val result = findVowels(line)
            .findElisions(line)
            .markDiphthongsLong(line)
            .markLongByPosition(line)

        meter.applyTo(result.filterNot { it.isElided })

        inferVowelStresses(result.filterNot { it.isElided }, meter)

        return result
    }

    private fun findVowels(input: String): List<Vowel> {
        val foundDiphthongs = diphthongs.findAll(input)
        val foundSingleVowels = singleVowels.findAll(input)

        val result = foundSingleVowels
            .filterNot { vowel -> foundDiphthongs.any { vowel.range.overlaps(it.range) } }
            .map { Vowel(it.range) }
            .toMutableList()

        result.addAll(foundDiphthongs.map { Vowel(it.range) })
        return result.sortedBy { it.position.first }
    }

    private fun List<Vowel>.findElisions(input: String): List<Vowel> {
        elisions.findAll(input).forEach { result ->
            this.find { result.range.contains(it.position.last) }?.isElided = true
        }
        return this
    }

    private fun List<Vowel>.markDiphthongsLong(input: String): List<Vowel> {
        this.forEach { if (it.position.first != it.position.last) it.stress = Stress.STRESSED }
        return this
    }

    private fun List<Vowel>.markLongByPosition(input: String): List<Vowel> {
        longByPosition.findAll(input).forEach { result ->
            this.find { result.range.contains(it.position.last)}?.stress = Stress.STRESSED
        }
        return this
    }

    private tailrec fun inferVowelStresses(vowels: List<Vowel>, meter: Meter, fullVowels: List<Vowel> = vowels, vowelsStartsFrom: Int = 0, feet: Sequence<Foot> = Foot.values().asSequence()) {
        val foot = feet
            .filter { meter.allowedFeet.contains(it) }
            .filter { it.size == vowels.size }
            .filter { it.matches(vowels) }
            .firstOrNull()
        if (foot != null) {
            foot.applyTo(vowels)
            if (fullVowels != vowels && fullVowels.size > vowels.size+vowelsStartsFrom)
                inferVowelStresses(fullVowels.subList(vowels.size+vowelsStartsFrom, fullVowels.size), meter, fullVowels, vowels.size+vowelsStartsFrom, feet)
        } else if (vowels.size > 1) {
            inferVowelStresses(vowels.subList(0, vowels.lastIndex), meter, fullVowels, vowelsStartsFrom, feet)
        }
    }

    private val singleVowels = Regex("[aeiou]")
    private val diphthongs = Regex("([ao]e)|(ei)|([ae]u)")
    private val elisions = Regex("${singleVowels.pattern}m? h?${singleVowels.pattern}")
    private val longByPosition = Regex("${singleVowels.pattern} ?([zx]|([bcdfgjklmnpqrst] ?[bcdfgjkmnpqst])|([klmnqrs] ?r)|([bcdfgjpt] r)|([bcdjklmnqrst] ?l)|([fgp] l))")

    private fun IntRange.overlaps(other: IntRange): Boolean {
        return this.first <= other.last && this.last >= other.first
    }
}

class Vowel(val position: IntRange) {
    var isElided = false
    var stress = Stress.UNKNOWN
}

enum class Stress {
    STRESSED,
    UNSTRESSED,
    UNKNOWN;

    fun matches(other: Stress): Boolean {
        return if (this == UNKNOWN || other == UNKNOWN)
            true
        else
            this == other
    }
}

enum class Foot(private val stresses: List<Stress>): List<Stress> by stresses {
    DACTYL(listOf(Stress.STRESSED, Stress.UNSTRESSED, Stress.UNSTRESSED)),
    SPONDEE(listOf(Stress.STRESSED, Stress.STRESSED)),
    FLOATING(listOf(Stress.STRESSED));

    fun matches(vowels: List<Vowel>): Boolean {
        for ((i, vowel) in vowels.withIndex()) if (!vowel.stress.matches(this[i])) return false
        return true
    }

    fun applyTo(vowels: List<Vowel>): List<Vowel> {
        vowels.zip(this).forEach { (vowel, stress) -> vowel.stress = stress }
        return vowels
    }
}

enum class Meter(private val feet: List<Foot?>, public val allowedFeet: Collection<Foot>): List<Foot?> by feet {
    DACTYLIC_HEXAMETER(listOf(null, null, null, null, Foot.DACTYL, Foot.SPONDEE), setOf(Foot.DACTYL, Foot.SPONDEE)),
    DACTYLIC_PENTAMETER(listOf(null, null, Foot.FLOATING, Foot.DACTYL, Foot.DACTYL, Foot.FLOATING), setOf(Foot.DACTYL, Foot.SPONDEE)),
    UNKNOWN(listOf(), Foot.values().toList());

    fun applyTo(vowels: List<Vowel>): List<Vowel> {
        var i = 0
        for (foot in feet.reversed()) if (foot != null) {
            if (vowels.lastIndex-i-foot.size < 0) return vowels
            foot.applyTo(vowels.subList(vowels.size-i-foot.size, vowels.size-i))
            i += foot.size
        }
        return vowels
    }
}
