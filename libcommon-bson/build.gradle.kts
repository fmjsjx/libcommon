import java.io.StringWriter

plugins {
    id("libcommon.java-library-conventions")
    id("libcommon.kotlin-library-conventions")
    id("libcommon.publish-conventions")
}

ext["kotlin.stdlib.default.dependency"] = "false"

dependencies {
    implementation("org.slf4j:slf4j-api")
    implementation(project(":libcommon-util"))
    api("org.mongodb:bson")
    api("org.mongodb:mongodb-driver-core")
    compileOnlyApi(kotlin("stdlib"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl")
    testImplementation("io.mockk:mockk")
    testImplementation("io.kotest:kotest-assertions-core")
}

description = "libcommon/BSON"

val compileOnlyApiDeps: Provider<Set<String>> = provider {
    project.configurations.findByName("compileOnlyApi")?.dependencies?.map { it.name }?.toSet() ?: emptySet()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                withXml {
                    // Add <optional>true</optional> for all compileOnlyApi dependencies
                    val xmlText = asString().toString()
                    val document = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(xmlText.byteInputStream())

                    val dependencyElements = document.getElementsByTagName("dependency")
                    for (i in 0 until dependencyElements.length) {
                        val depElement = dependencyElements.item(i) as org.w3c.dom.Element
                        val artifactId = depElement.getElementsByTagName("artifactId").item(0)?.textContent

                        // Check if this dependency is a compileOnlyApi optional dependency declared by this module
                        if (artifactId != null && compileOnlyApiDeps.get().contains(artifactId)) {
                            // Defensive code: remove potential conflicting old optional node
                            val existingOptional = depElement.getElementsByTagName("optional").item(0)
                            if (existingOptional != null) depElement.removeChild(existingOptional)

                            // 1.【Precision Indentation】Get the last child node (usually a text node or whitespace before </dependency>)
                            // Append a standard "newline + 4 spaces" text node at the end to ensure alignment
                            val indentNode = document.createTextNode("  ")
                            depElement.appendChild(indentNode)

                            // 2. Create and append the standard <optional>true</optional> tag
                            val optionalNode = document.createElement("optional")
                            optionalNode.textContent = "true"
                            depElement.appendChild(optionalNode)

                            // 3. Complete the indentation before the closing tag (to keep the </dependency> indentation at 2 spaces)
                            val closingIndentNode = document.createTextNode("\n    ")
                            depElement.appendChild(closingIndentNode)
                        }
                    }
                    // 4.【Core Fix Point】Disable automatic INDENT, preserving all natural line breaks and formatting in the original POM
                    val transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer().apply {
                        setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "no")
                        setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "no")
                    }
                    val source = javax.xml.transform.dom.DOMSource(document)
                    val result = javax.xml.transform.stream.StreamResult(StringWriter())
                    transformer.transform(source, result)
                    // Write the modified perfect XML back to the Gradle buffer
                    asString().setLength(0)
                    asString().append(result.writer.toString())
                }
                name.set("libcommon/BSON")
                description.set("A set of some common useful libraries.")
                url.set("https://github.com/fmjsjx/libcommon")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("fmjsjx")
                        name.set("MJ Fang")
                        email.set("fmjsjx@163.com")
                        url.set("https://github.com/fmjsjx")
                        organization.set("fmjsjx")
                        organizationUrl.set("https://github.com/fmjsjx")
                    }
                }
                scm {
                    url.set("https://github.com/fmjsjx/libcommon")
                    connection.set("scm:git:https://github.com/fmjsjx/libcommon.git")
                    developerConnection.set("scm:git:https://github.com/fmjsjx/libcommon.git")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
