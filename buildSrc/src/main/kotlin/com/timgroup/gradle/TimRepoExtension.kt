package com.timgroup.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class TimRepoExtension @Inject constructor(objectFactory: ObjectFactory, providers: ProviderFactory) {
    val nexusRepoUrl: Provider<String> = providers.gradleProperty("repoUrl")
    val nexusRepoUsername: Provider<String> = providers.gradleProperty("repoUsername")
    val nexusRepoPassword: Provider<String> = providers.gradleProperty("repoPassword")
    val artifactId: Property<String> = objectFactory.property()
}