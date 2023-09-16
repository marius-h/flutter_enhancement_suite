package de.mariushoefler.flutterenhancementsuite.codeInsight.hints

import com.intellij.codeInsight.hints.HintInfo
import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.intellij.util.containers.ContainerUtil
import com.jetbrains.lang.dart.DartLanguage
import com.jetbrains.lang.dart.ide.info.DartFunctionDescription
import com.jetbrains.lang.dart.psi.DartArguments
import com.jetbrains.lang.dart.psi.DartCallExpression
import com.jetbrains.lang.dart.psi.DartComponent
import com.jetbrains.lang.dart.psi.DartComponentName
import com.jetbrains.lang.dart.psi.DartNewExpression
import com.jetbrains.lang.dart.util.DartResolveUtil

/**
 * This class is responsible for providing parameter hints for Dart.
 *
 * It is used to show the parameter names of a method.
 *
 * @author Marius Höfler
 * @since v1.7.0
 */
@Suppress("UnstableApiUsage")
class DartInlayParameterHintsProvider : InlayParameterHintsProvider {
    override fun getDefaultBlackList(): MutableSet<String> {
        return mutableSetOf(
            "dart.core", "(fn)", "(a)", "(a, b)"
        )
    }

    override fun getBlackListDependencyLanguage(): Language = DartLanguage.INSTANCE

    override fun getParameterHints(element: PsiElement): List<InlayInfo> {
        val arguments = when (element) {
            is DartCallExpression -> element.childrenOfType<DartArguments>().first()
            is DartNewExpression -> element.arguments
            else -> null
        } ?: return emptyList()
        val expressionList = arguments.argumentList?.expressionList ?: return emptyList()
        val functionDescription = getFunctionDescription(element)
        val parameterNames = functionDescription?.parameters?.map { it.text } ?: return emptyList()

        return expressionList.mapIndexedNotNull { index, expression ->
            val offset = expression.textOffset
            if (index >= parameterNames.size) return@mapIndexedNotNull null
            val parameterName = parameterNames[index].functionName()
            if (parameterName == expression.text) return@mapIndexedNotNull null
            return@mapIndexedNotNull InlayInfo(parameterName, offset)
        }
    }

    override fun getHintInfo(element: PsiElement): HintInfo? {
        return getFunctionDescription(element)?.let { getMethodInfo(it) }
    }

    private fun getMethodInfo(functionDescription: DartFunctionDescription): HintInfo.MethodInfo {
        val parameterNames = functionDescription.parameters.map { it.text.functionName() }
        return HintInfo.MethodInfo(functionDescription.name, parameterNames)
    }

    private fun getFunctionDescription(element: PsiElement) = when (element) {
        is DartCallExpression -> DartFunctionDescription.tryGetDescription(element)
        is DartNewExpression -> {
            val type = element.type
            val classResolveResult = DartResolveUtil.resolveClassByType(type)
            val referenceExpressions = element.referenceExpressionList
            val psiElement =
                if (referenceExpressions.isEmpty() && type != null) type.referenceExpression else ContainerUtil.getLastItem(
                    referenceExpressions
                )
            val target = psiElement?.resolve()
            if (target is DartComponentName)
                DartFunctionDescription.createDescription(
                    target.parent as DartComponent, classResolveResult
                ) else null
        }

        else -> null
    }

    private fun String.functionName(): String {
        if (split("").size == 1) return this
        return split("(").first().replace(Regex("[^a-zA-Z\\s]"), "").split(" ").last()
    }
}
