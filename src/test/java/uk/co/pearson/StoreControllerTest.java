package uk.co.pearson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StoreControllerTest {
    private static final String FIRST_LINE_ID = "1234";
    private static final String FIRST_LINE_POSTCODE = "WC33RE";
    private static final String FIRST_LINE_ADDRESS = "Test street";
    private static final String FIRST_LINE_CITY = "London";

    private static final String SECOND_LINE_ID = "9876";
    private static final String SECOND_LINE_POSTCODE = "WC33RD";
    private static final String SECOND_LINE_ADDRESS = "Another test street";
    private static final String SECOND_LINE_CITY = "Aberdeen";

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RemoteFileReader remoteFileReader;

    @InjectMocks
    private StoreController storeController = new StoreController();

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(storeController)
                .build();
    }

    @Test
    public void returnsAllTheStoresInJsonFormat() throws Exception {
        List<String> firstLine = new ArrayList<>();
        firstLine.add(createFirstLine());
        when(remoteFileReader.readAll()).thenReturn(firstLine);
        mockMvc.perform(get("/stores/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$[0].id").value(FIRST_LINE_ID))
                .andExpect(jsonPath("$[0].postCode").value(FIRST_LINE_POSTCODE))
                .andExpect(jsonPath("$[0].city").value(FIRST_LINE_CITY))
                .andExpect(jsonPath("$[0].address").value(FIRST_LINE_ADDRESS));
    }

    @Test
    public void returnsNoContentWhenNoStoreAtAllHasBeenFound() throws Exception {
        List<String> noLine = new ArrayList<>();
        when(remoteFileReader.readAll()).thenReturn(noLine);
        mockMvc.perform(get("/stores/all"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void returnsNoContentWhenImpossibleToConvertAllStores() throws Exception {
        List<String> firstLine = new ArrayList<>();
        firstLine.add(invalidString());
        when(remoteFileReader.readAll()).thenReturn(firstLine);
        mockMvc.perform(get("/stores/all"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void returnsInternalServerErrorWhenIOExceptionOccursForAllStores() throws Exception {
        when(remoteFileReader.readAll()).thenThrow(IOException.class);
        mockMvc.perform(get("/stores/all"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void returnsAllStoresOrderedByCity() throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add(createFirstLine());
        lines.add(createSecondLine());
        when(remoteFileReader.readAll()).thenReturn(lines);
        mockMvc.perform(get("/stores/all?sort=city"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$[0].city").value(SECOND_LINE_CITY))
                .andExpect(jsonPath("$[1].city").value(FIRST_LINE_CITY));
    }

    @Test
    public void returnsAllStoresOrderedByOpenDate() throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add(createFirstLine());
        lines.add(createSecondLine());
        when(remoteFileReader.readAll()).thenReturn(lines);
        mockMvc.perform(get("/stores/all?sort=opendate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$[0].openDate").value(createXDaysAgo(1)))
                .andExpect(jsonPath("$[1].openDate").value(createXDaysAgo(2)));
    }

    @Test
    public void returnsFoundStoreInJsonFormat() throws Exception {
        when(remoteFileReader.readOneLineById(FIRST_LINE_ID)).thenReturn(createFirstLine());
        mockMvc.perform(get("/stores/" + FIRST_LINE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$['id']").value(FIRST_LINE_ID))
                .andExpect(jsonPath("$['postCode']").value(FIRST_LINE_POSTCODE))
                .andExpect(jsonPath("$['city']").value(FIRST_LINE_CITY))
                .andExpect(jsonPath("$['address']").value(FIRST_LINE_ADDRESS));
    }

    @Test
    public void returnsNoContentWhenNoStoreHasBeenFound() throws Exception {
        String idToFind = "3456";
        when(remoteFileReader.readOneLineById(idToFind)).thenReturn("");
        mockMvc.perform(get("/stores/" + idToFind))
                .andExpect(status().isNoContent());
    }

    @Test
    public void returnsNoContentWhenImpossibleToConvertStore() throws Exception {
        String idToFind = "3456";
        List<String> firstLine = new ArrayList<>();
        firstLine.add(createFirstLine());
        when(remoteFileReader.readOneLineById(idToFind)).thenReturn(invalidString());
        mockMvc.perform(get("/stores/" + idToFind))
                .andExpect(status().isNoContent());
    }

    @Test
    public void returnsInternalServerErrorWhenIOExceptionOccurs() throws Exception {
        String idToFind = "3456";
        List<String> firstLine = new ArrayList<>();
        firstLine.add(createFirstLine());
        when(remoteFileReader.readOneLineById(idToFind)).thenThrow(IOException.class);
        mockMvc.perform(get("/stores/" + idToFind))
                .andExpect(status().isInternalServerError());
    }

    private String createFirstLine()
    {
        return FIRST_LINE_ID + "," + FIRST_LINE_POSTCODE + "," + FIRST_LINE_CITY + "," + FIRST_LINE_ADDRESS + "," + createXDaysAgo(1);
    }

    private String createSecondLine()
    {
        return SECOND_LINE_ID + "," + SECOND_LINE_POSTCODE + "," + SECOND_LINE_CITY + "," + SECOND_LINE_ADDRESS + "," + createXDaysAgo(2);
    }

    private String invalidString()
    {
        return FIRST_LINE_ID;
    }

    private String createXDaysAgo(long daysBackInTime)
    {
        Date date = new Date();
        long oneDay = daysBackInTime*1000*60*60*24;
        date.setTime( date.getTime() - oneDay );
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }
}
