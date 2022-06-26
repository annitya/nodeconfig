package com.flageolett.nodeconfig.Utilities;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter;
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreterManager;
import com.intellij.javascript.nodejs.npm.NpmUtil;
import com.intellij.javascript.nodejs.util.NodePackage;
import com.intellij.lang.javascript.buildTools.npm.rc.NpmCommand;
import com.intellij.lang.javascript.library.JSLibraryManager;
import com.intellij.lang.javascript.library.JSLibraryMappings;
import com.intellij.lang.javascript.library.download.TypeScriptAllStubsFile;
import com.intellij.lang.typescript.library.download.TypeScriptDefinitionFilesDirectory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.RootsChangeRescanningInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.util.ArrayUtil;
import com.intellij.util.download.DownloadableFileSetDescription;
import com.intellij.webcore.libraries.ScriptingLibraryModel;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;

class NodeConfigProjectDescriptor extends LightProjectDescriptor
{
    @Override
    protected VirtualFile createSourceRoot(@NotNull Module module, String srcPath)
    {
        VirtualFile dummyRoot = VirtualFileManager
                .getInstance()
                .findFileByUrl("temp:///");

        if (dummyRoot != null) {
            this.createContentEntry(module, dummyRoot);
        }

        return super.createSourceRoot(module, srcPath);
    }

    @Override
    public void setUpProject(@NotNull Project project, @NotNull SetupHandler handler) throws Exception
    {
        super.setUpProject(project, handler);

        String globalTypesTargetPath = TypeScriptDefinitionFilesDirectory.getGlobalTypesTopDirectory();
        File globalTypesTargetDirectory = new File(globalTypesTargetPath);

        File nodeModulesDir = new File(globalTypesTargetDirectory, "node_modules");

        DownloadableFileSetDescription description = TypeScriptAllStubsFile.INSTANCE.getDownloadableFileSetDescription("config");

        if (nodeModulesDir.exists())
        {
            FileUtil.delete(nodeModulesDir);
        }

        FileUtil.createDirectory(nodeModulesDir);

        NodeJsLocalInterpreter interpreter = NodeJsLocalInterpreterManager
            .getInstance()
            .detectMostRelevant();

        NodePackage npmPackage = NodePackage.findDefaultPackage(project, "npm", interpreter);

        if (interpreter == null || npmPackage == null)
        {
            return;
        }

        GeneralCommandLine commandLine = NpmUtil.createNpmCommandLine(nodeModulesDir, interpreter, npmPackage, NpmCommand.INSTALL, Collections.emptyList());
        CapturingProcessHandler processHandler = new CapturingProcessHandler(commandLine);
        processHandler.runProcess();

        updateLibraries(project, description);
    }

    private static void updateLibraries(Project project, DownloadableFileSetDescription description)
    {
        ApplicationManager
            .getApplication()
            .runWriteAction(() ->
            {
                String libraryName = TypeScriptStubLibrary.getNameForLibrary(description);
                JSLibraryManager libraryManager = JSLibraryManager.getInstance(project);
                libraryManager.createLibrary(libraryName, VirtualFile.EMPTY_ARRAY, VirtualFile.EMPTY_ARRAY, ArrayUtil.EMPTY_STRING_ARRAY, ScriptingLibraryModel.LibraryLevel.GLOBAL, true);
                libraryManager.commitChanges(RootsChangeRescanningInfo.TOTAL_RESCAN);
                JSLibraryMappings mappings = JSLibraryMappings.getInstance(project);
                mappings.associate(null, libraryName, false);
                LocalFileSystem.getInstance().refresh(false);
            });
    }
}
