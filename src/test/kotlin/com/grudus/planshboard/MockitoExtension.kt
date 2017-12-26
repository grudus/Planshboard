package com.grudus.planshboard

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestInstancePostProcessor
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import java.lang.reflect.Parameter

import org.mockito.Mockito.mock

/**
 * `MockitoExtension` showcases the [TestInstancePostProcessor]
 * and [ParameterResolver] extension APIs of JUnit 5 by providing
 * dependency injection support at the field level and at the method parameter
 * level via Mockito 2.x's [@Mock][Mock] annotation.
 *
 * @since 5.0
 */
class MockitoExtension : TestInstancePostProcessor, ParameterResolver {

    override fun postProcessTestInstance(testInstance: Any, context: ExtensionContext) {
        MockitoAnnotations.initMocks(testInstance)
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
            parameterContext.parameter.isAnnotationPresent(Mock::class.java)

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
            getMock(parameterContext.parameter, extensionContext)

    private fun getMock(parameter: Parameter, extensionContext: ExtensionContext): Any {
        val mockType: Class<*> = parameter.type
        val mocks = extensionContext.getStore(ExtensionContext.Namespace.create(MockitoExtension::class.java, mockType))
        val mockName: String? = getMockName(parameter)

        return if (mockName != null) {
            mocks.getOrComputeIfAbsent<String, Any>(mockName) { mock(mockType, mockName) }
        } else {
            mocks.getOrComputeIfAbsent<String, Any>(mockType.canonicalName) { mock(mockType) }
        }
    }

    private fun getMockName(parameter: Parameter): String? {
        val explicitMockName = parameter.getAnnotation(Mock::class.java).name.trim({ it <= ' ' })
        if (!explicitMockName.isEmpty()) {
            return explicitMockName
        } else if (parameter.isNamePresent) {
            return parameter.name
        }
        return null
    }

}