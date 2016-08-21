package models;


import java.lang.annotation.ElementType;

@java.lang.annotation.Target({ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface FieldMapperEx {

    String value() default "";

    String field() default "";

    Class type() default String.class;

}
