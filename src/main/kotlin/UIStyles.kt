import kotlinx.css.*
import styled.StyleSheet

object UIStyles : StyleSheet("WelcomeStyles", isStatic = true) {
    val textContainer by css {
        padding(5.px)

        backgroundColor = rgb(8, 97, 22)
        color = rgb(56, 246, 137)
    }

    val textInput by css {
        margin(vertical = 1.em)
        width = 30.em

        fontSize = 14.px
    }
} 
