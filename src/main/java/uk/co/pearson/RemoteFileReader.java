package uk.co.pearson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoteFileReader {

    public List<String> readAll() throws IOException {
        return getLines().collect(Collectors.toList());
    }

    public String readOneLineById(String id) throws IOException {
        for (String line : getLines().collect(Collectors.toList())) {
            if(line.split(",")[0].equals(id))
            {
                return line;
            }
        }
        return "";
    }

    protected BufferedReader getBufferedReader() throws IOException {
        URL url = new URL("https://raw.githubusercontent.com/pearsonpmcuk/codingchallenge/master/stores.csv");
        return new BufferedReader(new InputStreamReader(url.openStream()));
    }

    private Stream<String> getLines() throws IOException {
        BufferedReader bufferedReader = getBufferedReader();
        return bufferedReader.lines().skip(1);
    }
}
