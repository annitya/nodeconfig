package com.flageolett.nodeconfig.Reference;

import com.flageolett.nodeconfig.Utilities.TypeScriptStubLibrary;
import com.intellij.lang.ecmascript6.psi.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class ChainingProvider extends PsiReferenceProvider
{
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext)
    {
        if (!TypeScriptStubLibrary.PLUGIN_ENABLED)
        {
            return PsiReference.EMPTY_ARRAY;
        }

        ES6ImportDeclaration[] importDeclarations = PsiTreeUtil.getChildrenOfType(psiElement.getContainingFile(), ES6ImportDeclaration.class);

        if (importDeclarations == null || importDeclarations.length == 0)
        {
            return PsiReference.EMPTY_ARRAY;
        }

        ES6ImportDeclaration configImport = null;

        for (ES6ImportDeclaration importDeclaration : importDeclarations)
        {
            ES6FromClause fromClause = importDeclaration.getFromClause();

            if (fromClause == null)
            {
                continue;
            }

            String referenceText = Optional
                .ofNullable(fromClause.getReferenceText())
                .orElse("");

            if (StringUtil.stripQuotesAroundValue(referenceText).equals("config"))
            {
                configImport = importDeclaration;
                break;
            }
        }

        if (configImport == null)
        {
            return PsiReference.EMPTY_ARRAY;
        }

        ES6ImportedBinding[] importedBindings = configImport.getImportedBindings();

        if (importedBindings.length != 1)
        {
            return PsiReference.EMPTY_ARRAY;
        }

        ES6ImportedBinding importedBinding = importedBindings[0];

        String configVariableName = Optional
            .ofNullable(importedBinding.getText())
            .orElse("");

        if (configVariableName.length() == 0)
        {
            return PsiReference.EMPTY_ARRAY;
        }

        VariableVisitor variableVisitor = new VariableVisitor(importedBinding);
        variableVisitor.visitFile(psiElement.getContainingFile());

        HashMap<String, ConfigVariable> configVariables = variableVisitor.getConfigVariables();

        configVariables.forEach((s, configVariable) -> configVariable.tag(configVariables));

        List<PsiElement> configPsiElements = configVariables
            .values()
            .stream()
            .map(ConfigVariable::getVariable)
            .collect(Collectors.toList());

        /*
         * 1. Resolve chained access - const db = config.get('db'); db.get('username');
         *      - Gather all JSVariable.
         *      - Attempt to build chain.
         *          - Find root.
         *              - Establish chain.
         *      - Use said chain to determine if a variable is actually a config-object.
         *      - Test the hell out of it.
         *
         * 2. Any part of the chain should resolve to the TypeScript-library.
         * 3. The completion-contributor should also respect the chain.
         */

        return PsiReference.EMPTY_ARRAY;
    }
}
