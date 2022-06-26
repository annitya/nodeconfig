package com.flageolett.nodeconfig;

import com.flageolett.nodeconfig.ConfigParser.CompletionBuilder;
import com.flageolett.nodeconfig.ConfigParser.ConfigUtilities;
import com.flageolett.nodeconfig.Utilities.TypeScriptStubLibrary;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunctionSignature;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConfigCompletionContributor extends CompletionContributor
{
    private static Boolean isGetOrHasMethod(PsiElement element)
    {
        String methodName = Optional
                .ofNullable(PsiTreeUtil.getParentOfType(element, JSCallExpression.class))
                .map(JSCallExpression::getMethodExpression)
                .map(PsiElement::getReference)
                .map(PsiReference::resolve)
                .filter(psiElement -> psiElement instanceof TypeScriptFunctionSignature)
                .map(TypeScriptFunctionSignature.class::cast)
                .map(NavigationItem::getName)
                .orElse("");

        return methodName.equals("has") || methodName.equals("get");
    }

    private static Boolean isWithinQuotes(PsiElement element)
    {
        return Optional
            .ofNullable(PsiTreeUtil.getParentOfType(element, JSLiteralExpression.class))
            .map(JSLiteralExpression::isQuotedLiteral)
            .orElse(false);
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
    {
        if (!TypeScriptStubLibrary.PLUGIN_ENABLED)
        {
            return;
        }

        // User requested regular completions, don't interfere!
        if (parameters.isExtendedCompletion())
        {
            return;
        }

        PsiElement currentElement = parameters.getPosition();

        if (!isWithinQuotes(currentElement) || !isGetOrHasMethod(currentElement))
        {
            return;
        }

        // We are in the right spot, only display config-completions.
        result.stopHere();

        ConfigUtilities
            .getConfigFiles(currentElement.getProject())
            .stream()
            .map(CompletionBuilder::getCompletions)
            .forEach(result::addAllElements);
    }
}
