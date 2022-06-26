package com.flageolett.nodeconfig.Notifications;

import com.flageolett.nodeconfig.Utilities.TypeScriptStubLibrary;
import com.intellij.lang.javascript.library.JSLibraryManager;
import com.intellij.lang.javascript.library.JSLibraryMappings;
import com.intellij.lang.javascript.library.download.TypeScriptAllStubsFile;
import com.intellij.lang.javascript.library.download.TypeScriptDefinitionFilesRootsProvider;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.webcore.libraries.ui.download.DownloadableFileSetDescriptionWithUrl;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;

class Listener implements NotificationListener
{
    final private Project project;

    Listener(Project project)
    {
        this.project = project;
    }

    @Override
    public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event)
    {
        // The user clicked something... hide the balloon.
        notification.hideBalloon();
        boolean enable = event.getDescription().equals("1");

        if (!enable)
        {
            return;
        }

        DownloadableFileSetDescriptionWithUrl configLibrary = TypeScriptAllStubsFile
            .INSTANCE
            .getDownloadableFileSetDescription("config");

        if (!TypeScriptStubLibrary.HAS_LIBRARY)
        {
            TypeScriptDefinitionFilesRootsProvider
                .LibraryCreator
                .downloadTypesLibrary(project, configLibrary, null, TypeScriptStubLibrary::enable);
        }
        else if (!TypeScriptStubLibrary.LIBRARY_ENABLED)
        {
            enableLibrary(configLibrary);
        }
    }

    private void enableLibrary(DownloadableFileSetDescriptionWithUrl configLibrary)
    {
        String libraryName = TypeScriptStubLibrary.getNameForLibrary(configLibrary);
        JSLibraryManager libraryManager = JSLibraryManager.getInstance(project);

        JSLibraryMappings mappings = JSLibraryMappings.getInstance(project);
        mappings.associate(null, libraryName, false);

        libraryManager.commitChanges();
        LocalFileSystem.getInstance().refresh(true);

        TypeScriptStubLibrary.enable();
    }
}
