package models;


import java.lang.annotation.ElementType;

@java.lang.annotation.Target({ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface FieldMapper {

    String value() default "";

}
