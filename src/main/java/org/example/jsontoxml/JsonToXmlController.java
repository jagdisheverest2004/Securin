package org.example.jsontoxml;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsonToXmlController {

    @PostMapping(value = "/convert/json-to-xml", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String convertJsonToXml(@RequestBody String jsonString) {
        return JsonToXmlConverter.convert(jsonString);
    }
}