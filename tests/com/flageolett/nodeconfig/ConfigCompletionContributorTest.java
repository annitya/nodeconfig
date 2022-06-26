package com.flageolett.nodeconfig;

import com.flageolett.nodeconfig.Utilities.Es6Case;
import com.flageolett.nodeconfig.Utilities.TypeScriptStubLibrary;
import com.intellij.codeInsight.completion.CompletionType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigCompletionContributorTest extends Es6Case
{
    private final List<String> configCompletions = List.of(
            "auth_key",
            "database",
            "database.host",
            "database.host.uri",
            "database.name",
            "database.name.value",
            "user"
    );

    public void testJsCompletion()
    {
        myFixture.configureByFiles("configCompletions.js", "config/default.js");
        myFixture.completeBasic();
        verifyCompletions();
    }

    public void testNoJsCompletion()
    {
        myFixture.configureByFiles("configCompletions.js", "config/default.js");

        TypeScriptStubLibrary.PLUGIN_ENABLED = false;
        myFixture.completeBasic();

        verifyNoCompletions();
    }

    public void testJsonCompletion()
    {
        myFixture.configureByFiles("configCompletions.js", "config/default.json");
        myFixture.completeBasic();
        verifyCompletions();
    }

    public void testNoJsonCompletion()
    {
        myFixture.configureByFiles("configCompletions.js", "config/default.json");

        TypeScriptStubLibrary.PLUGIN_ENABLED = false;
        myFixture.completeBasic();

        verifyNoCompletions();
    }

    public void testExtendedCompletion()
    {
        myFixture.configureByFiles("configCompletions.js", "config/default.js");
        myFixture.complete(CompletionType.BASIC, 2);
        verifyNoCompletions();
    }

    public void testNoQuoteCompletion()
    {
        myFixture.configureByFiles("noQuoteConfigCompletions.js", "config/default.js");
        myFixture.completeBasic();
        verifyNoCompletions();
    }

    private void verifyCompletions()
    {
        List<String> strings = myFixture.getLookupElementStrings();

        assertThat(strings)
                .withFailMessage("Completions should not be null.")
                .isNotNull();

        assertThat(strings)
        .withFailMessage("7 completions should be available.")
                .hasSize(7);

        assertThat(strings)
                .withFailMessage("Completions should be fetched from config-files.")
                .isEqualTo(configCompletions);

    }

    private void verifyNoCompletions()
    {
        List<String> strings = myFixture.getLookupElementStrings();

        assertThat(strings)
            .withFailMessage("Completions should not be null.")
            .isNotNull();


        assertThat(strings)
            .withFailMessage("Should not return completions from config-file.")
            .isNotEqualTo(configCompletions);
    }
}
