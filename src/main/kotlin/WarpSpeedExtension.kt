import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.markup.*
import com.intellij.ui.JBColor
import com.maddyhome.idea.vim.command.MappingMode
import com.maddyhome.idea.vim.extension.VimExtension
import com.maddyhome.idea.vim.extension.VimExtensionFacade
import com.maddyhome.idea.vim.extension.VimExtensionHandler
import com.maddyhome.idea.vim.helper.StringHelper
import java.awt.Font
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import javax.swing.JComponent

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
        private class Mark(val editor: Editor, val highlight: RangeHighlighter, val label: String) : JComponent() {
            init {
                val contentComponent = editor.contentComponent
                contentComponent.add(this)
                setBounds(0, 0, contentComponent.width, contentComponent.height)
            }

            private fun getRectangle(): Rectangle {
                val startXY = editor.offsetToXY(highlight.startOffset)
                val endXY = editor.offsetToXY(highlight.endOffset)
                return Rectangle(
                    startXY.x,
                    startXY.y,
                    endXY.x - startXY.x,
                    endXY.y - startXY.y + 20
                )
            }

            private fun drawBackground(g: Graphics, rect: Rectangle) {
                val radius = 5
                g.color = JBColor.RED
                g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, radius, radius)
            }

            private fun drawText(g: Graphics, point: Point, text: String) {
                g.color = JBColor.GREEN
                g.font = Font("Arial", Font.BOLD, 20)
                g.drawString(text, point.x, point.y)
            }

            override fun paint(g: Graphics) {
                super.paint(g)
                val rect = getRectangle()
                drawBackground(g, rect)
                drawText(g, Point(rect.x, rect.y), label)
            }
        }

        private val labels = mutableSetOf<Mark>()

        fun addMark(editor: Editor, range: ClosedRange<Int>, label: String) {
            val mark = Mark(
                editor,
                editor.markupModel.addRangeHighlighter(
                    range.start,
                    range.endInclusive,
                    HighlighterLayer.SELECTION,
                    getHighlightAttributes(editor),
                    HighlighterTargetArea.EXACT_RANGE
                ),
                label
            )
            labels.add(mark)
            mark.repaint()
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