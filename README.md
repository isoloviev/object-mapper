**Input**
```json
{
   "info":{
      "firstName":"Sean",
      "directNumbers":[
         {
            "number":1234567890,
            "id":123
         }
      ]
   }
}
```

**Class**

```java
public class Extension extends ObjectModel {
    @FieldMapper("info.firstName")
    private String firstName;
    
    @FieldMapper("info.lastName")
    private String lastName;
    
    @FieldMapper("info.directNumbers.number")
    private List<String> numbers;
}
```

**Output**

```
Extension(firstName=Sean, lastName=null, numbers=[1234567890])
```