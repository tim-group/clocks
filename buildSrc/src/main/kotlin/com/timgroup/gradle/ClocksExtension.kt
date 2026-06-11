package com.timgroup.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class ClocksExtension @Inject constructor(objectFactory: ObjectFactory) {
    val javaModuleName: Property<String> = objectFactory.property()
}
