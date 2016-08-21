package models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class Extension extends ObjectModel {

    @FieldMapper("info.firstName")
    private String firstName;

    @FieldMapper("info.lastName")
    private String lastName;

    @FieldMapper("info.directNumbers.number")
    private List<String> numbers;

}
