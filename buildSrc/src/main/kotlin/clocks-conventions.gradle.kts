import com.timgroup.gradle.ClocksExtension

plugins {
    `java-library`
    `maven-publish`
}

extensions.create<ClocksExtension>("clocks")

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
