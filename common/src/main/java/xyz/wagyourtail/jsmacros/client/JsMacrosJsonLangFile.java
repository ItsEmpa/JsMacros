package xyz.wagyourtail.jsmacros.client;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JsMacrosJsonLangFile {
    private static final Map<String, Set<String>> langResources = new HashMap<>();

    static {
        URI uri = null;
        try {
            uri = JsMacrosJsonLangFile.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            File f = Paths.get(uri.toString().split("!")[0].replace("jar:file:", "")).toFile();
            try {
                ZipFile zf = new ZipFile(f);
                Enumeration<? extends ZipEntry> entries = zf.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry ze = entries.nextElement();
                    if (ze.getName().matches("assets\\/.+?\\/lang\\/.+?\\.json")) {
                        String lang = ze.getName().substring(
                            ze.getName().lastIndexOf('/') + 1,
                            ze.getName().length() - 5
                        );
                        langResources.computeIfAbsent(lang.toLowerCase(Locale.ROOT), (l) -> new HashSet<>()).add(
                            "/" + ze.getName());
                    }
                }
            } catch (IOException e) {
                System.err.println(f + " is not a valid zip file");
                e.printStackTrace();
            }
        } catch (IllegalArgumentException | URISyntaxException e) {
            System.err.println("bad URI: " + uri);
            e.printStackTrace();
        }
    }

    public static Set<String> getLangResources(String lang) {
        return langResources.getOrDefault(lang.toLowerCase(Locale.ROOT), new HashSet<>());
    }


}
