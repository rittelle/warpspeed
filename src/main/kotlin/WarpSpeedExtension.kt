import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.maddyhome.idea.vim.command.MappingMode
import com.maddyhome.idea.vim.extension.VimExtension
import com.maddyhome.idea.vim.extension.VimExtensionFacade
import com.maddyhome.idea.vim.extension.VimExtensionHandler
import com.maddyhome.idea.vim.helper.StringHelper

class WarpSpeedExtension : VimExtension {
    override fun getName(): String = "warpspeed"

    override fun init() {
        VimExtensionFacade.putExtensionHandlerMapping(
            MappingMode.NXO,
            StringHelper.parseKeys("<Plug>(warpspeed-s)"),
            owner,
            WarpSpeedExtensionHandler(),
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

    private class WarpSpeedExtensionHandler : VimExtensionHandler {
        override fun execute(editor: Editor, context: DataContext) {
            TODO("Not yet implemented")
        }
    }
}