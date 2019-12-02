import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.delegateClosureOf

fun MavenPublication.addPomElements(action: groovy.util.NodeBuilder.() -> Unit) {
    pom.withXml {
        val lastNode = asNode().children().last() as groovy.util.Node
        lastNode.plus(delegateClosureOf<groovy.util.NodeBuilder> {
            this.action()
        })
    }
}

fun groovy.util.NodeBuilder.addElement(name: String, value: String?) {
    invokeMethod(name, value)
}

fun groovy.util.NodeBuilder.addElement(name: String, builder: groovy.util.NodeBuilder.() -> Unit) {
    invokeMethod(name, delegateClosureOf<groovy.util.NodeBuilder> {
        this.builder()
    })
}
