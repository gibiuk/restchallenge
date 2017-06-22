package uk.co.pearson;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/stores")
public class StoreController {

    StoreConverter storeConverter = new StoreConverter();
    RemoteFileReader remoteFileReader = new RemoteFileReader();

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Store>> getAllStores(
            @RequestParam(value = "sort", required = false) final String sort) {
        try
        {
            List<String> storeLines = remoteFileReader.readAll();
            if(storeLines.isEmpty())
            {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<Store> stores = storeConverter.convertAll(storeLines);
            if(stores.isEmpty())
            {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            sortResult(stores, sort);
            return new ResponseEntity<>(stores, HttpStatus.OK);
        }
        catch (IOException e)
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sortResult(List<Store> stores, String sort) {
        if("city".equalsIgnoreCase(sort))
        {
            Collections.sort(stores, (Store s1, Store s2) -> s1.getCity().compareTo(s2.getCity()));
        }

        if("opendate".equalsIgnoreCase(sort))
        {
            Collections.sort(stores, (Store s1, Store s2) -> stringToDate(s2.getOpenDate()).compareTo(stringToDate(s1.getOpenDate())));
        }
    }

    private Date stringToDate(String stringToConvert)
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return dateFormat.parse(stringToConvert);
        } catch (ParseException e) {
            return new Date(Long.MIN_VALUE);
        }
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Store> getStoreById(@PathVariable String storeId) {
        try {
            String lineFound = remoteFileReader.readOneLineById(storeId);
            if(lineFound.isEmpty())
            {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            Store store = storeConverter.convertStringToStore(remoteFileReader.readOneLineById(storeId));
            if(store == null)
            {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(store, HttpStatus.OK);
        }
        catch (IOException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Store> createStore() {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
