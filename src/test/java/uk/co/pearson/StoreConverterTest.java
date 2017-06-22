package uk.co.pearson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.pearson.Store;
import uk.co.pearson.StoreConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class StoreConverterTest {

    private static final String FIRST_ID = "1234";
    private static final String FIRST_POSTCODE = "WC33RE";
    private static final String FIRST_ADDRESS = "Test street";
    private static final String FIRST_CITY = "London";

    private static final String SECOND_ID = "5678";
    private static final String SECOND_POSTCODE = "WC33RD";
    private static final String SECOND_ADDRESS = "\"Another, Test street\"";
    private static final String SECOND_CITY = "London";
    private static final String SECOND_OPEN_DATE = "11/11/2011";

    StoreConverter storeConverter = new StoreConverter();

    @Test
    public void returnsNullIfEmptyParameter()
    {
        assertNull(storeConverter.convertStringToStore(""));
    }

    @Test
    public void returnsNullIfNullParameter()
    {
        assertNull(storeConverter.convertStringToStore(null));
    }

    @Test
    public void returnsNullIfStringHasIncorrectNumberOfField()
    {
        assertNull(storeConverter.convertStringToStore(createFirstLineWithoutOpenDate()));
    }

    @Test
    public void convertStringToStoreCorrectly() throws Exception {
        Store store = storeConverter.convertStringToStore(createFirstLine());
        verifyFirstStore(store);
    }

    @Test
    public void convertTwoStringsToStoresCorrectly() throws Exception {
        List<String> strings = new ArrayList<>();
        strings.add(createFirstLine());
        strings.add(createSecondLine());
        List<Store> stores = storeConverter.convertAll(strings);

        assertEquals(2, stores.size());
        verifyFirstStore(stores.get(0));
        verifySecondStore(stores.get(1));
    }

    @Test
    public void returnsEmptyListIfNonConvertableItemIsPassed()
    {
        List<String> strings = new ArrayList<>();
        strings.add(createFirstLineWithoutOpenDate());
        List<Store> stores = storeConverter.convertAll(strings);

        assertTrue(stores.isEmpty());
    }

    @Test
    public void returnsOnlyConvertableItems()
    {
        List<String> strings = new ArrayList<>();
        strings.add(createFirstLine());
        strings.add(createFirstLineWithoutOpenDate());
        List<Store> stores = storeConverter.convertAll(strings);

        assertEquals(1, stores.size());
        verifyFirstStore(stores.get(0));
    }

    @Test
    public void calculationSinceOpeningReturnsNullWhenEmptyOpenDate()
    {
        Store store = storeConverter.convertStringToStore(createFirstLineWithEmptyOpenDate());
        assertNull(null, store.getDaysSinceOpen());
    }

    @Test
    public void calculationSinceOpeningReturnsNullWhenNonDateOpenDate()
    {
        Store store = storeConverter.convertStringToStore(createFirstLineWithNonDateOpenDate());
        assertNull(null, store.getDaysSinceOpen());
    }

    @Test
    public void calculatesDaysSinceOpeningCorrectly() throws Exception {
        Store store = storeConverter.convertStringToStore(createFirstLine());
        assertEquals("1", store.getDaysSinceOpen());
    }

    private String createFirstLine()
    {
        return FIRST_ID + "," + FIRST_POSTCODE + "," + FIRST_CITY + "," + FIRST_ADDRESS + "," + createYesterday();
    }

    private String createSecondLine()
    {
        return SECOND_ID + "," + SECOND_POSTCODE + "," + SECOND_CITY + "," + SECOND_ADDRESS + "," + SECOND_OPEN_DATE;
    }

    private String createFirstLineWithoutOpenDate()
    {
        return FIRST_ID + "," + FIRST_POSTCODE + "," + FIRST_CITY + "," + FIRST_ADDRESS;
    }

    private String createFirstLineWithEmptyOpenDate()
    {
        return FIRST_ID + "," + FIRST_POSTCODE + "," + FIRST_CITY + "," + FIRST_ADDRESS + ",";
    }

    private String createFirstLineWithNonDateOpenDate()
    {
        return FIRST_ID + "," + FIRST_POSTCODE + "," + FIRST_CITY + "," + FIRST_ADDRESS + "," + FIRST_CITY;
    }

    private String createYesterday()
    {
        Date date = new Date();
        long oneDay = 1000*60*60*24;
        date.setTime( date.getTime() - oneDay );
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    private void verifyFirstStore(Store store) {
        assertEquals(FIRST_ID, store.getId());
        assertEquals(FIRST_POSTCODE, store.getPostCode());
        assertEquals(FIRST_CITY, store.getCity());
        assertEquals(FIRST_ADDRESS, store.getAddress());
        assertEquals(createYesterday(), store.getOpenDate());
    }

    private void verifySecondStore(Store store) {
        assertEquals(SECOND_ID, store.getId());
        assertEquals(SECOND_POSTCODE, store.getPostCode());
        assertEquals(SECOND_CITY, store.getCity());
        assertEquals(SECOND_ADDRESS, store.getAddress());
        assertEquals(SECOND_OPEN_DATE, store.getOpenDate());
    }
}
