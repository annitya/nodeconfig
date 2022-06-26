package com.flageolett.nodeconfig.Inspections;

import com.flageolett.nodeconfig.Utilities.TypeScriptStubLibrary;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.ecmascript6.psi.ES6FromClause;
import com.intellij.lang.ecmascript6.psi.ES6ImportDeclaration;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class ImportInspector extends JSElementVisitor
{
    static final String PROBLEM_DESCRIPTION = "Use has/get-methods instead.";

    private final ProblemsHolder holder;

    ImportInspector(ProblemsHolder holder)
    {
        this.holder = holder;
    }

    @Override
    public void visitES6ImportDeclaration(@NotNull ES6ImportDeclaration importDeclaration)
    {
        if (!TypeScriptStubLibrary.PLUGIN_ENABLED)
        {
            return;
        }

        String fromClauseText = Optional
            .ofNullable(importDeclaration.getFromClause())
            .map(ES6FromClause::getReferenceText)
            .map(StringUtil::stripQuotesAroundValue)
            .orElse("");

        if (!fromClauseText.equals("config"))
        {
            return;
        }

        boolean hasImportSpecifiers = importDeclaration
            .getImportSpecifiers()
            .length > 0;

        if (!hasImportSpecifiers)
        {
            return;
        }

        // Houston we have a problem.
        holder.registerProblem(importDeclaration, PROBLEM_DESCRIPTION);
    }
}
