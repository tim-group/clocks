description = "JSR310 clock implementations for use in testing"
extra["javaModuleName"] = "com.timgroup.clocks.testing"

dependencies {
    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest-core:1.3")
    testImplementation("org.hamcrest:hamcrest-library:1.3")
}
