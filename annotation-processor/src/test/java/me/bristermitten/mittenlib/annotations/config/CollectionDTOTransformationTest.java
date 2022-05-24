package me.bristermitten.mittenlib.annotations.config;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

class CollectionDTOTransformationTest {

    @Test
    void generateFullConfigClassName() {
        Compilation compilation = javac()
                .withProcessors(new ConfigProcessor())
                .compile(JavaFileObjects.forSourceString("me.bristermitten.mittenlib.tests.CollectionConfigDTO",
                        """
                                package me.bristermitten.mittenlib.tests;
                                import java.util.Map;
                                @me.bristermitten.mittenlib.config.names.NamingPattern(value = me.bristermitten.mittenlib.config.names.NamingPatterns.LOWER_KEBAB_CASE)
                                @me.bristermitten.mittenlib.config.Source(value = "lang.yml")
                                @me.bristermitten.mittenlib.config.Config
                                public final class CollectionConfigDTO {
                                    public final Map<String, SubConfigDTO> map = null;
                                    @me.bristermitten.mittenlib.config.Config
                                    public static final class SubConfigDTO {
                                        int i;
                                        String s;
                                    }
                                }
                                """));

        assertThat(compilation).succeededWithoutWarnings();
    }
}

