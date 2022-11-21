package de.alphaomega.it.maven;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenLibraries {

    @Nonnull
    MavenLibrary[] value() default {};
}
