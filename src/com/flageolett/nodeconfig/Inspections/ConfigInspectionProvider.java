package com.flageolett.nodeconfig.Inspections;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.codeInspection.LocalInspectionTool;
import org.jetbrains.annotations.NotNull;

public class ConfigInspectionProvider implements InspectionToolProvider
{
    @NotNull
    @Override
    public Class<? extends LocalInspectionTool> [] getInspectionClasses()
    {
        return new Class[] {BestPractice.class};
    }
}
