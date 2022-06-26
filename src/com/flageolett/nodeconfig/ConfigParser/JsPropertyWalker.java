package com.flageolett.nodeconfig.ConfigParser;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSQualifiedName;
import com.intellij.lang.javascript.psi.JSRecursiveWalkingElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class JsPropertyWalker extends JSRecursiveWalkingElementVisitor
{
    private final HashMap<String, JSProperty> properties = new HashMap<>();

    public HashMap<String, JSProperty> getProperties() { return properties; }

    List<LookupElement> getCompletions()
    {
        return properties
                .keySet()
                .stream()
                .map(LookupElementBuilder::create)
                .collect(Collectors.toList());
    }

    @Override
    public void visitJSProperty(@NotNull JSProperty property)
    {
        super.visitJSProperty(property);

        String name = property.getName();

        if (name == null) {
            return;
        }

        boolean hasParent = PsiTreeUtil.getParentOfType(property, JSProperty.class) != null;

        JSQualifiedName qualifiedName = property
                .getJSNamespace()
                .getQualifiedName();

        String baseName = qualifiedName != null ? qualifiedName.getName() : "";
        boolean isChildProperty = hasParent && !baseName.isEmpty();

        String completion = isChildProperty ? baseName + "." + name : name;

        properties.put(completion, property);
    }
}
