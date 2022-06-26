package com.flageolett.nodeconfig.Reference;

import com.intellij.lang.ecmascript6.psi.ES6ImportedBinding;
import com.intellij.lang.javascript.psi.*;

import java.util.HashMap;

public class VariableVisitor extends JSRecursiveWalkingElementVisitor
{
    private final HashMap<String, ConfigVariable> configVariables = new HashMap<>();

    VariableVisitor(ES6ImportedBinding importedConfigBinding)
    {
        ConfigVariable root = new ConfigVariable(importedConfigBinding);
        configVariables.put(root.getAssignedFromName(), root);
    }

    @Override
    public void visitJSVariable(JSVariable variable)
    {
        super.visitJSVariable(variable);

        ConfigVariable configVariable = new ConfigVariable(variable);

        if (configVariable.isValid())
        {
            configVariables.put(configVariable.getAssignedFromName(), configVariable);
        }
    }

    HashMap<String, ConfigVariable> getConfigVariables() { return configVariables; }
}
