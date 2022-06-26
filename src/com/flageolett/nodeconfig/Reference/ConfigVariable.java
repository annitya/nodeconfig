package com.flageolett.nodeconfig.Reference;

import com.intellij.lang.ecmascript6.psi.ES6ImportedBinding;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.JSVariable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ConfigVariable
{
    public static final Key<String> CONFIG_CHAIN_KEY = Key.create("NodeConfigChain");

    // The config-object stored in a variable.
    private final PsiElement variable;
    // What is the name of this variable?
    private final String variableName;
    // What part of the configuration does this variable point to?
    private String configNodeName;
    // Where did this variable come from?
    private String assignedFromName;

    ConfigVariable(JSVariable variable)
    {
        this.variable = variable;
        variableName = variable.getName();

        JSExpression initializer = variable.getInitializer();
        JSReferenceExpression referenceExpression = PsiTreeUtil.getChildOfType(initializer, JSReferenceExpression.class);

        if (referenceExpression == null)
        {
            return;
        }

        JSExpression qualifier = referenceExpression.getQualifier();

        if (qualifier == null)
        {
            return;
        }

        assignedFromName = qualifier.getText();

        JSCallExpression callExpression;
        try
        {
            callExpression = (JSCallExpression)initializer;
        }
        catch (Exception e)
        {
            return;
        }

        JSExpression[] arguments = callExpression.getArguments();

        if (arguments.length != 1)
        {
            return;
        }

        String configNodeName = StringUtil.stripQuotesAroundValue(arguments[0].getText());
        this.configNodeName = StringUtil.stripQuotesAroundValue(configNodeName);
    }

    // Constructor for the root-element.
    ConfigVariable(ES6ImportedBinding importedBinding)
    {
        variable = importedBinding;
        variableName = importedBinding.getText();
        configNodeName = "config";
    }

    boolean isValid()
    {
        boolean sourceIsKnown = assignedFromName != null;
        boolean isRoot = !(variable instanceof ES6ImportedBinding);

        if (!sourceIsKnown && !isRoot)
        {
            return false;
        }

        return variable != null && variableName != null && configNodeName != null && assignedFromName != null;
    }

    // Sets the userdata which the CompletionContributor will need later.
    void tag(HashMap<String, ConfigVariable> configVariables)
    {
        if (assignedFromName == null)
        {
            return;
        }

        List<String> configNodeNames = new ArrayList<>();
        ConfigVariable current = configVariables.get(assignedFromName);

        while (current != null)
        {
            configNodeNames.add(current.configNodeName);
            current = configVariables.get(current.assignedFromName);
        }

        Collections.reverse(configNodeNames);
        String qualifiedName = StringUtil.join(configNodeNames, ".");

        if (qualifiedName.length() > 0)
        {
            variable.putCopyableUserData(CONFIG_CHAIN_KEY, qualifiedName);
        }
    }

    PsiElement getVariable() { return variable; }

    String getAssignedFromName() { return assignedFromName; }
}
