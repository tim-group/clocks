description = "Joda-Clock implementations for use in testing"
extra["javaModuleName"] = "com.timgroup.clocks.joda.testing"

dependencies {
    api("junit:junit:4.12")
    api(project(":joda"))
    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest-core:1.3")
    testImplementation("org.hamcrest:hamcrest-library:1.3")
}
