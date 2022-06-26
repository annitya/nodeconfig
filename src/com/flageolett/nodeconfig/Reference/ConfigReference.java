package com.flageolett.nodeconfig.Reference;

import com.flageolett.nodeconfig.ConfigParser.ConfigUtilities;
import com.flageolett.nodeconfig.ConfigParser.JsPropertyWalker;
import com.flageolett.nodeconfig.ConfigParser.JsonPropertyWalker;
import com.intellij.json.psi.JsonProperty;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class ConfigReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference
{
    ConfigReference(@NotNull PsiElement element)
    {
        super(element);
    }

    @Nullable
    @Override
    public PsiElement resolve()
    {
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants()
    {
        return new Object[0];
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode)
    {
        JSLiteralExpression literalExpression;
        try
        {
            literalExpression = (JSLiteralExpression)myElement;
        }
        catch (Exception e)
        {
            return ResolveResult.EMPTY_ARRAY;
        }

        String text = literalExpression.getStringValue();
        Project project = literalExpression.getProject();

        List<PsiFile> jsConfigFiles = ConfigUtilities.getConfigFiles(project, "js");
        List<PsiFile> jsonConfigFiles = ConfigUtilities.getConfigFiles(project, "json");

        List<ResolveResult> references = new ArrayList<>();

        for (PsiFile file : jsConfigFiles)
        {
            JsPropertyWalker walker = new JsPropertyWalker();
            walker.visitFile(file);
            HashMap<String, JSProperty> properties = walker.getProperties();

            if (!properties.containsKey(text))
            {
                continue;
            }

            references.add(new JSResolveResult(properties.get(text)));
        }

        for (PsiFile file : jsonConfigFiles)
        {
            JsonPropertyWalker walker = new JsonPropertyWalker();
            walker.visitFile(file);
            HashMap<String, JsonProperty> properties = walker.getProperties();

            if (!properties.containsKey(text))
            {
                continue;
            }

            references.add(new JSResolveResult(properties.get(text)));
        }

        return references.toArray(new ResolveResult[0]);
    }
}
