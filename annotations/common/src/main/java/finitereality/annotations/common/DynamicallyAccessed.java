package finitereality.annotations.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a type or member that is dynamically accessed, such as through
 * reflection.
 */
@Target({
    ElementType.TYPE,
    ElementType.CONSTRUCTOR,
    ElementType.FIELD,
    ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface DynamicallyAccessed
{ }
