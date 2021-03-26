import react.dom.render
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.TITLE

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            child(ScansioniserUI::class) {
                attrs {
                    input = ""
                }
            }
        }
    }
}
