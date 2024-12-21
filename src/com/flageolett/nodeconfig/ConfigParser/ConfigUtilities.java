package com.flageolett.nodeconfig.ConfigParser;

import com.intellij.json.psi.JsonFile;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigUtilities
{
    public static List<PsiFile> getConfigFiles(Project project)
    {
        List<PsiFile> jsConfigFiles = getConfigFiles(project, "js");
        List<PsiFile> jsonConfigFiles = getConfigFiles(project, "json");

        return Stream
            .concat(jsConfigFiles.stream(), jsonConfigFiles.stream())
            .collect(Collectors.toList());
    }

    public static List<JSFile> getJsConfigFiles(Project project) {
        List<PsiFile> configFiles = getConfigFiles(project, "js");
        return configFiles.stream().map(file -> (JSFile)file).toList();
    }

    public static List<JsonFile> getJsonConfigFiles(Project project) {
        List<PsiFile> configFiles = getConfigFiles(project, "json");
        return configFiles.stream().map(file -> (JsonFile)file).toList();
    }

    public static List<PsiFile> getConfigFiles(Project project, String extension)
    {
        VirtualFile[] sourceRoots = ProjectRootManager
            .getInstance(project)
            .getContentRoots();

        List<VirtualFile> configDirectories = Arrays
            .stream(sourceRoots)
            .map(sourceRoot -> sourceRoot.findFileByRelativePath("config"))
            .filter(Objects::nonNull)
            .toList();

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
