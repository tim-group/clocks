plugins {
    id("com.timgroup.jarmangit") version "1.1.113"
}

allprojects {
    group = "com.timgroup"
    if (System.getenv("BUILD_NUMBER") != null) version = "1.0.${System.getenv("BUILD_NUMBER")}"
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.timgroup.jarmangit")
    apply(plugin = "maven-publish")

    tasks.withType<Jar> {
        manifest {
            attributes(
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version,
                    "Implementation-Vendor" to "TIM Group Ltd"
            )
        }
    }

    tasks.withType<JavaCompile> {
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    afterEvaluate {
        val javaModuleName: String? by project
        if (javaModuleName != null) {
            tasks.named<Jar>("jar") {
                manifest {
                    attributes(
                            "Automatic-Module-Name" to javaModuleName
                    )
                }
            }
        }
    }

    the<PublishingExtension>().apply {
        repositories {
            val repoUrl: String? by project
            if (repoUrl != null) {
                val repoUsername: String by project
                val repoPassword: String by project
                maven(url = "${repoUrl}/repositories/yd-release-candidates") {
                    credentials {
                        username = repoUsername
                        password = repoPassword
                    }
                }
            }
        }
        publications {
            val artifactId: String? by project.ext
            register<MavenPublication>("mavenJava") {
                this.artifactId = when {
                    artifactId != null -> artifactId
                    project == rootProject -> project.name
                    else -> rootProject.name + project.path.replace(':', '-')
                }
                from(components["java"])
                addPomElements {
                    val pomName: String? by project
                    addElement("name", pomName ?: project.name)
                    addElement("description", project.description)
                    addElement("url", "http://github.com/tim-group/clocks")
                    addElement("licenses") {
                        addElement("license") {
                            addElement("name", "The BSD 2-Clause License")
                            addElement("url", "http://opensource.org/licenses/BSD-2-Clause")
                            addElement("distribution", "repo")
                        }
                    }
                    addElement("developers") {
                        addElement("developer") {
                            addElement("id", "steve.haslam@timgroup.com")
                            addElement("name", "Steve Haslam")
                            addElement("email", "steve.haslam@timgroup.com")
                        }
                    }
                }
            }
        }
    }
}
