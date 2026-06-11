import com.timgroup.gradle.ClocksExtension
import com.timgroup.gradle.TimRepoExtension

plugins {
    `java-library`
    `maven-publish`
}

extensions.create<TimRepoExtension>("timgroupRepo")
extensions.create<ClocksExtension>("clocks")

group = "com.timgroup"
if (System.getenv("BUILD_NUMBER") != null) version = "1.0.${System.getenv("BUILD_NUMBER")}"

tasks.withType<Jar>().configureEach {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "TIM Group Ltd"
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.isIncremental = true
    options.isDeprecation = true
    options.compilerArgs.add("-parameters")
}

repositories {
    mavenCentral()
}

the<JavaPluginExtension>().apply {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

afterEvaluate {
    val clocks: ClocksExtension by project.extensions
    val javaModuleName = clocks.javaModuleName
    if (javaModuleName.isPresent) {
        tasks.named<Jar>("jar") {
            manifest {
                attributes(
                    "Automatic-Module-Name" to javaModuleName.get()
                )
            }
        }
    }
}

publishing {
    repositories {
        val timgroupRepo: TimRepoExtension by project.extensions
        if (timgroupRepo.nexusRepoUrl.isPresent) {
            maven(url = "${timgroupRepo.nexusRepoUrl.get()}/repositories/yd-release-candidates") {
                name = "nexus"
                credentials {
                    username = timgroupRepo.nexusRepoUsername.get()
                    password = timgroupRepo.nexusRepoPassword.get()
                }
                isAllowInsecureProtocol = true
            }
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            val timgroupRepo: TimRepoExtension by project.extensions
            register<MavenPublication>("mavenJava") {
                this.artifactId = when {
                    timgroupRepo.artifactId.isPresent -> timgroupRepo.artifactId.get()
                    project == rootProject -> project.name
                    else -> rootProject.name + project.path.replace(':', '-')
                }
                from(components["java"])
                val pomName: String? by project
                pom {
                    name.set(pomName ?: project.name)
                    description.set(project.description)
                    url.set("http://github.com/tim-group/clocks")
                    licenses {
                        license {
                            name.set("The BSD 2-Clause License")
                            url.set("http://opensource.org/licenses/BSD-2-Clause")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("steve.haslam@timgroup.com")
                            name.set("Steve Haslam")
                            email.set("steve.haslam@timgroup.com")
                        }
                    }
                }
            }
        }
    }
}
