package com.flageolett.nodeconfig.Utilities;

import com.flageolett.nodeconfig.ConfigParser.ConfigUtilities;
import com.flageolett.nodeconfig.Notifications.Library;
import com.intellij.lang.javascript.library.JSLibraryManager;
import com.intellij.lang.javascript.library.download.TypeScriptAllStubsFile;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.util.download.DownloadableFileSetDescription;
import com.intellij.webcore.libraries.ScriptingLibraryModel;
import org.jetbrains.annotations.NotNull;

public class TypeScriptStubLibrary implements StartupActivity, DumbAware
{
    // The plugin will do nothing if this variable is false.
    public static Boolean PLUGIN_ENABLED = false;
    // Is the library-files installed?
    public static Boolean HAS_LIBRARY = false;
    // Is the library enabled?
    public static Boolean LIBRARY_ENABLED = false;

    public static void enable()
    {
        HAS_LIBRARY = true;
        LIBRARY_ENABLED = true;
        PLUGIN_ENABLED = true;
    }

    public static String getNameForLibrary(DownloadableFileSetDescription description)
    {
        return description.getVersionString() + "/" + description.getName();
    }

    @Override
    public void runActivity(@NotNull Project project)
    {
        ApplicationManager
            .getApplication()
            .runReadAction(() -> checkIsNodeConfigProject(project));
    }

    private void checkIsNodeConfigProject(Project project)
    {
        boolean hasConfigFiles = ConfigUtilities
            .getConfigFiles(project)
            .size() > 0;

        // No supported config-files found. All bets are off.
        if (!hasConfigFiles)
        {
            return;
        }

        DownloadableFileSetDescription description = TypeScriptAllStubsFile
            .INSTANCE
            .getDownloadableFileSetDescription("config");

        String libraryName = getNameForLibrary(description);

        JSLibraryManager libraryManager = JSLibraryManager.getInstance(project);
        LibraryTable libraryTable = libraryManager.getLibraryTable(ScriptingLibraryModel.LibraryLevel.GLOBAL);

        // Is the library installed?
        HAS_LIBRARY = libraryTable.getLibraryByName(libraryName) != null;
        // Is the library enabled?
        LIBRARY_ENABLED = libraryManager
            .getLibraryMappings()
            .getMappingsByLibraryName(libraryName)
            .size() > 0;

        PLUGIN_ENABLED = HAS_LIBRARY && LIBRARY_ENABLED;

        // The library is either not installed or not enabled. Ask user permission to fix it.
        if (!PLUGIN_ENABLED)
        {
            Library.send(project);
        }
    }
}
