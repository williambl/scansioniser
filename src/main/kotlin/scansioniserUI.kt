import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.css
import styled.styledDiv
import styled.styledInput

external interface UIProps : RProps {
    var input: String
}

data class UIState(val input: String, val output: String? = null) : RState

@JsExport
class ScansioniserUI(props: UIProps) : RComponent<UIProps, UIState>(props) {

    init {
        state = UIState(props.input)
    }

    override fun RBuilder.render() {
        styledInput {
            css {
                +UIStyles.textInput
            }
            attrs {
                type = InputType.text
                value = state.input
                onChangeFunction = { event ->
                    val input = (event.target as HTMLInputElement).value
                    val output = input.toMutableList()
                    var offset = 0
                    for (vowel in Scanner.scan(input)) {
                        if (vowel.isElided) {
                            output.add(vowel.position.last + 1 + offset, ')')
                            output.add(vowel.position.first + offset, '(')
                            offset += 2
                        }
                        if (vowel.isLong) {
                            output.add(vowel.position.last + 1 + offset, '_')
                            output.add(vowel.position.first + offset, '_')
                            offset += 2
                        }
                    }
                    setState(
                        UIState(input = input, output = output.toTypedArray().joinToString(separator = ""))
                    )
                }
            }
        }
        styledDiv {
            css {
                +UIStyles.textContainer
            }
            if (state.output != null)
                +"${state.output}"
        }
    }
}
