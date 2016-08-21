package models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class Extension extends ObjectModel {

    @FieldMapper("info.firstName")
    @FieldMapperEx("info.firstName")
    private String firstName;

    @FieldMapper("info.lastName")
    @FieldMapperEx("info.lastName")
    private String lastName;

    @FieldMapper("info.directNumbers.number")
    @FieldMapperEx(value = "info.directNumbers", field = "number", type = List.class)
    private List<String> numbers;

}
