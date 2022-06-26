package com.flageolett.nodeconfig.Inspections;

import com.flageolett.nodeconfig.Utilities.Es6Case;
import com.flageolett.nodeconfig.Utilities.TypeScriptStubLibrary;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ImportInspectorTest extends Es6Case
{
    public void testBestPractices()
    {
        myFixture.configureByFile("bestPractices.js");
        myFixture.enableInspections(new BestPractice());

        List<HighlightInfo> highlightInfos = myFixture.doHighlighting();

        assertThat(highlightInfos)
                .withFailMessage("There should be two highlights available.")
                .hasSize(2);

        HighlightInfo bestPracticeHighlight = highlightInfos.getFirst();
        String bestPracticeReason = "The first highlight should be the best-practive inspection.";

        assertThat(bestPracticeHighlight.getDescription())
                .withFailMessage("The first highlight should be the best-practive inspection.")
                .isEqualTo(ImportInspector.PROBLEM_DESCRIPTION);
    }

    public void testNoInspection()
    {
        TypeScriptStubLibrary.PLUGIN_ENABLED = false;

        myFixture.configureByFile("bestPractices.js");
        myFixture.enableInspections(new BestPractice());

        verifyNoInspection();
    }

    public void testDifferentModuleImport()
    {
        myFixture.configureByFile("bestPracticesNotConfigModule.js");
        myFixture.enableInspections(new BestPractice());
        verifyNoInspection();
    }

    public void testNoImports()
    {
        myFixture.configureByFile("bestPracticesNoImports.js");
        myFixture.enableInspections(new BestPractice());
        verifyNoInspection();
    }

    private void verifyNoInspection()
    {
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting();


        assertThat(highlightInfos)
                .withFailMessage("There should only be one highlight available.")
                .hasSize(1);


        HighlightInfo highlightInfo = highlightInfos.getFirst();

        assertThat(highlightInfo.getDescription())
                .withFailMessage("The best-practice inspection should not be triggered.")
                        .isNotEqualTo(ImportInspector.PROBLEM_DESCRIPTION);
    }
}
