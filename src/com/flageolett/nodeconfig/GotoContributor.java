package com.flageolett.nodeconfig;

import com.flageolett.nodeconfig.ConfigParser.CompletionBuilder;
import com.flageolett.nodeconfig.ConfigParser.ConfigUtilities;
import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

final class GotoContributor implements ChooseByNameContributorEx
{
    @Override
    public void processNames(@NotNull Processor<? super String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter)
    {
        var project = Objects.requireNonNull(scope.getProject());
        var jsProperties = ConfigUtilities.getJsConfigFiles(project)
                .stream()
                .map(CompletionBuilder::getProperties)
                .map(list -> list.st)
                .flatMap(results -> results)
                .toList();

        var result = ConfigUtilities
                .getConfigFiles(project)
                .stream()
                .map(CompletionBuilder::getCompletions);


        List<String> propertyKeys = ContainerUtil.map(SimpleUtil.findProperties(project), SimpleProperty::getKey);
        ContainerUtil.process(propertyKeys, processor);
}

    @Override
    public void processElementsWithName(@NotNull String name, @NotNull Processor<? super NavigationItem> processor, @NotNull FindSymbolParameters parameters)
    {
        List<NavigationItem> properties = ContainerUtil.map(SimpleUtil.findProperties(parameters.getProject(), name), property -> (NavigationItem) property);
        ContainerUtil.process(properties, processor);
    }
}
