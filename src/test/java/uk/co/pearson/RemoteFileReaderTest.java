package uk.co.pearson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoteFileReaderTest {

    private static final String FIRST_LINE_ID = "1234";
    private static final String FIRST_LINE_POSTCODE = "WC33RE";
    private static final String FIRST_LINE_ADDRESS = "Test street";
    private static final String FIRST_LINE_CITY = "London";
    private static final String FIRST_LINE_OPEN_DATE = "11/11/2011";

    private static final String SECOND_LINE_ID = "5678";
    private static final String SECOND_LINE_POSTCODE = "WC33RD";
    private static final String SECOND_LINE_ADDRESS = "Another Test street";
    private static final String SECOND_LINE_CITY = "Manchester";
    private static final String SECOND_LINE_OPEN_DATE  = "11/11/2016";

    @Spy
    RemoteFileReader remoteFileReader = new RemoteFileReader();

    @Test(expected = IOException.class)
    public void readOnePassesExceptionWhenReaderThrowsIt() throws IOException {
        when(remoteFileReader.getBufferedReader()).thenThrow(IOException.class);
        remoteFileReader.readOneLineById(FIRST_LINE_ID);
    }

    @Test
    public void readAllPassesExceptionWhenReaderThrowsIt() throws IOException {
        when(remoteFileReader.getBufferedReader()).thenThrow(IOException.class);
        try {
            remoteFileReader.readAll();
            fail("Should throw IOException");
        }catch (IOException e){
            //Expected exception
        }
    }

    @Test
    public void returnEmptyStringWhenEmptyFile() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("".getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        when(remoteFileReader.getBufferedReader()).thenReturn(bufferedReader);
        assertEquals("", remoteFileReader.readOneLineById(FIRST_LINE_ID));
    }

    @Test
    public void returnsOneStoreWhenMatchIsFound() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(createStream()));
        when(remoteFileReader.getBufferedReader()).thenReturn(bufferedReader);

        String store = remoteFileReader.readOneLineById(FIRST_LINE_ID);
        assertEquals(createFirstLine(), store);
    }

    @Test
    public void returnsNullWhenNoMatchIsFound() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(createFirstLine().getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        when(remoteFileReader.getBufferedReader()).thenReturn(bufferedReader);

        String store = remoteFileReader.readOneLineById(SECOND_LINE_ID);
        assertEquals("", store);
    }

    @Test
    public void returnsFirstMatchWhenMoreThanOnePotentialMatch() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(createStreamWithInvalidSecondLine()));
        when(remoteFileReader.getBufferedReader()).thenReturn(bufferedReader);

        String store = remoteFileReader.readOneLineById(FIRST_LINE_ID);
        assertEquals(createFirstLine(), store);
    }

    @Test
    public void returnsAllButFirstLine() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(createStream()));
        when(remoteFileReader.getBufferedReader()).thenReturn(bufferedReader);

        List<String> stores = remoteFileReader.readAll();
        assertEquals(2, stores.size());
        assertEquals(createFirstLine(), stores.get(0));
        assertEquals(createSecondLine(), stores.get(1));
    }

    private InputStream createStream()
    {
        String content = createHeader() + "\n" + createFirstLine() + "\n" + createSecondLine();
        return new ByteArrayInputStream(content.getBytes());
    }

    private InputStream createStreamWithInvalidSecondLine()
    {
        String content = createHeader() + "\n" + createFirstLine() + "\n" + createInvalidLine();
        return new ByteArrayInputStream(content.getBytes());
    }

    private String createHeader()
    {
        return "Header!!";
    }

    private String createFirstLine()
    {
        return FIRST_LINE_ID + "," + FIRST_LINE_POSTCODE + "," + FIRST_LINE_CITY + "," + FIRST_LINE_ADDRESS + "," + FIRST_LINE_OPEN_DATE;
    }

    private String createInvalidLine()
    {
        return FIRST_LINE_ID;
    }

    private String createSecondLine()
    {
        return SECOND_LINE_ID + "," + SECOND_LINE_POSTCODE + "," + SECOND_LINE_CITY + "," + SECOND_LINE_ADDRESS + "," + SECOND_LINE_OPEN_DATE;
    }
}
