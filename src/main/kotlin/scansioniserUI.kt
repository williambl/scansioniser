import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledInput

external interface UIProps : RProps {
    var input: String
}

data class UIState(val input: String, val meter: Meter, val output: String? = null) : RState

@JsExport
class ScansioniserUI(props: UIProps) : RComponent<UIProps, UIState>(props) {

    init {
        state = UIState(props.input, Meter.UNKNOWN)
    }

    override fun RBuilder.render() {
        fieldSet {
            legend { +"Meter:" }

            Meter.values().map {
                div {
                    input {
                        attrs {
                            type = InputType.radio
                            value = it.name
                            id = it.name
                            name = "meter"
                            onChangeFunction = { event ->
                                val meter = (event.target as HTMLInputElement).value
                                setState({ s -> updateOutput(s.input, Meter.valueOf(meter))})
                            }
                        }
                    }

                    label {
                        +it.name
                    }
                }
            }
        }

        styledInput {
            css {
                UIStyles.textInput
            }
            attrs {
                type = InputType.text
                value = state.input
                onChangeFunction = { event ->
                    val input = (event.target as HTMLInputElement).value
                    setState({ s -> updateOutput(input, s.meter)})
                }
            }
        }
        styledDiv {
            css {
                UIStyles.textContainer
            }
            +(state.output ?: "")
        }
    }

    private fun updateOutput(input: String, meter: Meter): UIState {
        val output = input.toMutableList()
        var offset = 0
        for (vowel in Scanner.scan(input, meter)) {
            if (vowel.isElided) {
                output.add(vowel.position.last + 1 + offset, ')')
                output.add(vowel.position.first + offset, '(')
                offset += 2
            }
            else when(vowel.stress) {
                Stress.STRESSED -> {
                    for (position in vowel.position) {
                        offset++
                        output.add(position + offset, '\u0304')
                    }
                }
                Stress.UNSTRESSED -> {
                    for (position in vowel.position) {
                        offset++
                        output.add(position + offset, '\u0306')
                    }
                }
                Stress.UNKNOWN -> {}
            }
        }
        return UIState(input = input, meter = meter, output = output.toTypedArray().joinToString(separator = ""))
    }
}
