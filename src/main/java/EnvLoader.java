import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class EnvLoader {
    Path envFile = Paths.get("/.env");

    Object getEnvVar(String var) throws IOException {
        var props = new Properties();
        try(var inputStream = Files.newInputStream(envFile)){
            props.load(inputStream);
        }

        return props.get(var);

    }

}
