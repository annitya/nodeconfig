package com.flageolett.nodeconfig.ConfigParser;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CompletionBuilder
{
    public static List<LookupElement> getCompletions(PsiFile file)
    {
        String extension = Optional
            .ofNullable(file.getVirtualFile())
            .map(VirtualFile::getExtension)
            .orElse("");

        switch (extension)
        {
            case "js":
                JsPropertyWalker jsWalker = new JsPropertyWalker();
                jsWalker.visitFile(file);

                return jsWalker.getCompletions();
            case "json":
                JsonPropertyWalker jsonWalker = new JsonPropertyWalker();
                jsonWalker.visitFile(file);

                return jsonWalker.getCompletions();
            default:
                return Collections.emptyList();
        }
    }
}
