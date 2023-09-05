import io.github.cdimascio.dotenv.dotenv
import org.gradle.api.Project

fun loadSecret(project: Project, key: String): String = dotenv {
    ignoreIfMissing = true
    directory = project.layout.projectDirectory.asFile.path
}[key]
