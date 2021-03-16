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
                    setState(
                        UIState(input = (event.target as HTMLInputElement).value, output = "AAAA")
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
