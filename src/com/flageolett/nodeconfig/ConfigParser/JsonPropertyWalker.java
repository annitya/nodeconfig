package com.flageolett.nodeconfig.ConfigParser;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.impl.JsonRecursiveElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class JsonPropertyWalker extends JsonRecursiveElementVisitor
{
    private final HashMap<String, JsonProperty> properties = new HashMap<>();

    public HashMap<String, JsonProperty> getProperties() { return properties; }

    List<LookupElement> getCompletions()
    {
        return properties
                .keySet()
                .stream()
                .map(LookupElementBuilder::create)
                .collect(Collectors.toList());
    }

    @Override
    public void visitProperty(@NotNull JsonProperty property)
    {
        super.visitProperty(property);

        ArrayDeque<JsonProperty> nameProperties = new ArrayDeque<>();
        nameProperties.add(property);

        JsonProperty parentProperty = PsiTreeUtil.getParentOfType(property, JsonProperty.class);

        while (parentProperty != null)
        {
            nameProperties.addFirst(parentProperty);
            parentProperty = PsiTreeUtil.getParentOfType(parentProperty, JsonProperty.class);
        }

        String qualifiedName = nameProperties
            .stream()
            .map(JsonProperty::getName)
            .collect(Collectors.joining("."));

        properties.put(qualifiedName, property);
    }
}
