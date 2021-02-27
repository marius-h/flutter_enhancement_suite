//package de.mariushoefler.flutter_enhancement_suite.templates;
//
//import com.intellij.codeInsight.lookup.Lookup;
//import com.intellij.codeInsight.lookup.LookupManager;
//import com.intellij.codeInsight.lookup.impl.LookupImpl;
//import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
//import com.intellij.codeInsight.template.impl.TemplateState;
//import com.intellij.codeInsight.template.impl.actions.ListTemplatesAction;
//import com.intellij.openapi.command.WriteCommandAction;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.util.Disposer;
//import com.intellij.openapi.util.io.FileUtil;
//import com.intellij.psi.codeStyle.CodeStyleManager;
//import com.intellij.testFramework.TestSourceBasedTestCase;
//import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
//import org.jetbrains.annotations.Nullable;
//import com.jetbrains.lang.dart.*;
//
//public class DartLiveTemplatesTest extends TestSourceBasedTestCase {
//    @Override
//    protected String getTestDataPath() {
//        //return DartTestUtils.BASE_TEST_DATA_PATH + getBasePath();
//        return "";
//    }
//
//    @Nullable
//    @Override
//    protected String getTestPath() {
//        return null;
//    }
//
//    @Override
//    private String getBasePath() {
//        return FileUtil.toSystemDependentName("/liveTemplates/");
//    }
//
//    public void expandTemplate(final Editor editor) {
//        final Project project = editor.getProject();
//        assertNotNull(project);
//        new ListTemplatesAction().actionPerformedImpl(project, editor);
//        final LookupImpl lookup = (LookupImpl) LookupManager.getActiveLookup(editor);
//        assertNotNull(lookup);
//        lookup.finishLookup(Lookup.NORMAL_SELECT_CHAR);
//        TemplateState template = TemplateManagerImpl.getTemplateState(editor);
//        if (template != null) {
//            Disposer.dispose(template);
//        }
//    }
//
//    private void doTest() {
//        doTest(getTestName(false) + ".dart");
//    }
//
//    private void doTest(String... files) {
//        myFixture.configureByFiles(files);
//        expandTemplate(myFixture.getEditor());
//        WriteCommandAction.runWriteCommandAction(null, () -> {
//            CodeStyleManager.getInstance(myFixture.getProject()).reformat(myFixture.getFile());
//        });
//        myFixture.getEditor().getSelectionModel().removeSelection();
//        myFixture.checkResultByFile(getTestName(false) + ".after.dart");
//    }
//
//    public void testItar1() {
//        doTest();
//    }
//
//    public void testItar2() {
//        doTest();
//    }
//
//    public void testIter() {
//        doTest();
//    }
//
//    public void testSout() {
//        doTest();
//    }
//
//    public void testSoutm() {
//        doTest();
//    }
//
//    public void testSoutm2() {
//        doTest();
//    }
//}