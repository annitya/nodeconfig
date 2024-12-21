package com.flageolett.nodeconfig.ConfigParser;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.json.psi.JsonFile;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CompletionBuilder
{
    public static List<LookupElement> getCompletions(JSFile file)
    {
        JsPropertyWalker jsWalker = new JsPropertyWalker();
        jsWalker.visitFile(file);

        return jsWalker.getCompletions();
    }

    public static List<LookupElement> getCompletions(JsonFile file)
    {

        JsonPropertyWalker jsonWalker = new JsonPropertyWalker();
        jsonWalker.visitFile(file);

        return jsonWalker.getCompletions();

    }

    public static HashMap<String, PsiElement> getProperties(PsiFile file)
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

                return jsWalker.getProperties();
            case "json":
                JsonPropertyWalker jsonWalker = new JsonPropertyWalker();
                jsonWalker.visitFile(file);

                return jsonWalker.getProperties();
            default:
                return Collections.emptyList();
        }
    }
}
