package com.flageolett.nodeconfig.ConfigParser;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigUtilities
{
    public static List<PsiFile> getConfigFiles(Project project, String extension)
    {
        VirtualFile[] sourceRoots = ProjectRootManager
            .getInstance(project)
            .getContentRoots();

        List<VirtualFile> configDirectories = Arrays
            .stream(sourceRoots)
            .map(sourceRoot -> sourceRoot.findChild("config"))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        List<VirtualFile> allConfigFiles = new ArrayList<>();

        configDirectories
            .stream()
            .map(VirtualFile::getChildren)
            .map(Arrays::stream)
            .forEach(children -> children.forEach(allConfigFiles::add));

        return allConfigFiles
            .stream()
            .map(configFile -> PsiManager.getInstance(project).findFile(configFile))
            .filter(Objects::nonNull)
            .filter(psiFile -> psiFile.getName().endsWith(extension))
            .collect(Collectors.toList());
    }
}
