pluginManagement {
    repositories {
        gradlePluginPortal()
        val repoUrl: String by settings
        maven(url = "$repoUrl/groups/public")
    }
}

rootProject.name = "clocks"

include("joda")
include("testing")
include("joda:testing")
