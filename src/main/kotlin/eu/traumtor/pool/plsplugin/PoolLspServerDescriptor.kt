package eu.traumtor.pool.plsplugin

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.platform.lsp.api.lsWidget.LspServerWidgetItem
import com.intellij.ui.IconManager


internal class PoolLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
        if (file.extension == "pool") {
            serverStarter.ensureServerStarted(PoolLspServerDescriptor(project))
        }
    }
    override fun createLspServerWidgetItem(lspServer: LspServer, currentFile: VirtualFile?) =
        LspServerWidgetItem(
            lspServer,
            currentFile,
            IconManager.getInstance()
                .loadRasterizedIcon("icons/pool.svg", PoolLspServerSupportProvider::class.java.classLoader, 199994656, 2)
        )
}

private class PoolLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "POOL-LS") {
    override fun isSupportedFile(file: VirtualFile) = file.extension == "pool"
    override fun createCommandLine(): GeneralCommandLine {
        val gcl = GeneralCommandLine("pool-language-server")
        ModuleManager.getInstance(project).modules.forEach { module ->
            ModuleRootManager.getInstance(module).sourceRootUrls.forEach { url ->
                gcl.addParameters("--sourceUri", url)
            }
        }
        return gcl
    }
}
