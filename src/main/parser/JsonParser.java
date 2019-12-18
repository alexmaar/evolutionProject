package parser;

import parameters.Parameters;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonParser {
    private static final String path ="/Users/aleksandramarzec/javaProjects/src/main/parameters/somefile.json";

    public static Parameters parse (){
        Parameters parameters=null;

        try {
            final byte[] config = Files.readAllBytes(Paths.get(path));
            final String json = new String(config);
            final Gson gson = new Gson();
            parameters = gson.fromJson(json, Parameters.class);
            return parameters;
        } catch (JsonSyntaxException | IOException e) {
            System.err.println(e.getMessage());
        }
        return null;

    }

}
