description = "JSR310 clock implementations for use in testing"
extra["javaModuleName"] = "com.timgroup.clocks.testing"

dependencies {
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
}
