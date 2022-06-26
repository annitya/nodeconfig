package com.flageolett.nodeconfig.Utilities;

import com.intellij.lang.javascript.dialects.JSLanguageLevel;
import com.intellij.lang.javascript.settings.JSRootConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

abstract public class Es6Case extends BasePlatformTestCase
{
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        Project project = myFixture.getProject();

        JSRootConfiguration
            .getInstance(project)
            .storeLanguageLevelAndUpdateCaches(JSLanguageLevel.getLevelForJSX());

        // Enable plugin before tests are run.
        TypeScriptStubLibrary.PLUGIN_ENABLED = true;
    }

    @Override
    protected String getTestDataPath() { return "testData"; }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor()
    {
        return new NodeConfigProjectDescriptor();
    }
}
