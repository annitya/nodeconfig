package com.flageolett.nodeconfig.Inspections;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.inspections.JSInspection;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

class BestPractice extends JSInspection
{
    @NotNull
    @Override
    protected PsiElementVisitor createVisitor(@NotNull ProblemsHolder problemsHolder, @NotNull LocalInspectionToolSession localInspectionToolSession)
    {
        return new ImportInspector(problemsHolder);
    }

    @NotNull
    @Override
    public String @NotNull [] getGroupPath()
    {
        return new String[]{"JavaScript", "NodeConfig"};
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName()
    {
        return "Use best practices.";
    }
}
