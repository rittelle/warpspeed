import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.markup.*
import com.maddyhome.idea.vim.command.MappingMode
import com.maddyhome.idea.vim.extension.VimExtension
import com.maddyhome.idea.vim.extension.VimExtensionFacade
import com.maddyhome.idea.vim.extension.VimExtensionHandler
import com.maddyhome.idea.vim.helper.StringHelper
import java.awt.Font

class WarpSpeedExtension : VimExtension {
    private val markHandler = MarkHandler()

    override fun getName(): String = "warpspeed"

    override fun init() {
        VimExtensionFacade.putExtensionHandlerMapping(
            MappingMode.NXO,
            StringHelper.parseKeys("<Plug>(warpspeed-s)"),
            owner,
            WarpSpeedExtensionHandler(markHandler),
            false
        )
        VimExtensionFacade.putKeyMapping(
            MappingMode.NXO,
            StringHelper.parseKeys("gs"),
            owner,
            StringHelper.parseKeys("<Plug>(warpspeed-s)"),
            true
        )
    }

    private class WarpSpeedExtensionHandler(val markHandler: MarkHandler) : VimExtensionHandler {
        override fun execute(editor: Editor, context: DataContext) {
            markHandler.addMark(
                editor,
                editor.caretModel.primaryCaret.offset..editor.caretModel.primaryCaret.offset + 2, "?"
            )
        }
    }

    /**
     * A class to manage the marks shown when the plugin is used.
     *
     * A mark consists of an highlighlight of the matched text and a label showing the shortcut.
     */
    private class MarkHandler {
        private class Mark(val editor: Editor, val highlight: RangeHighlighter)

        private val labels = mutableSetOf<Mark>()

        fun addMark(editor: Editor, range: ClosedRange<Int>, label: String) {
            labels.add(
                Mark(
                    editor,
                    editor.markupModel.addRangeHighlighter(
                        range.start,
                        range.endInclusive,
                        HighlighterLayer.SELECTION,
                        getHighlightAttributes(editor),
                        HighlighterTargetArea.EXACT_RANGE
                    )
                )
            )
            // TODO: draw label after the highlighted text
        }

        private fun getHighlightAttributes(editor: Editor) = TextAttributes(
            null,
            EditorColors.TEXT_SEARCH_RESULT_ATTRIBUTES.defaultAttributes.backgroundColor,
            editor.colorsScheme.getColor(EditorColors.CARET_COLOR),
            EffectType.SEARCH_MATCH,
            Font.PLAIN
        )
    }
}