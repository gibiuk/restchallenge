package uk.co.pearson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StoreConverter {

    private static final String DELIMITER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    public Store convertStringToStore(String storeString)
    {
        if (storeString == null || "".equals(storeString))
        {
            System.out.println("Cannot convert an empty or null string");
            return null;
        }

        String[] splitString = storeString.split(DELIMITER, -1);

        Store store = new Store();
        if(splitString.length == 5) {
            store.setId(splitString[0]);
            store.setPostCode(splitString[1]);
            store.setCity(splitString[2]);
            store.setAddress(splitString[3]);
            store.setOpenDate(splitString[4]);
            store.setDaysSinceOpen(calculateDaysSinceOpening(splitString[4]));
        }
        else
        {
            System.out.println("The string has been split in " + splitString.length + " substring. Is should be 5. Check yur data.");
            return null;
        }

        return store;
    }

    public List<Store> convertAll(List<String> strings)
    {
        List<Store> stores = new ArrayList<>();
        for (String string : strings) {
            if(convertStringToStore(string) != null)
            {
                stores.add(convertStringToStore(string));
            }
        }
        return stores;
    }

    private String calculateDaysSinceOpening(String stringOpenDate)
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date openDate = dateFormat.parse(stringOpenDate);
            return String.valueOf(getDifferenceBetweenDateAndTodayInDays(openDate));
        } catch (ParseException e) {
            System.out.println("The passed date: " + stringOpenDate + " is not of the format dd/MM/yyyy");
            return null;
        }
    }

    public static long getDifferenceBetweenDateAndTodayInDays(Date date) {
        long differenceInMilliseconds = new Date().getTime() - date.getTime();
        return TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);
    }
}
