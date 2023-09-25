package com.yinmk;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

/**
 * @author yinminkai
 * @since 2023/9/25 10:16
 */
@Mojo(name = "banner", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class BannerMojo extends AbstractMojo {

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${basedir}", required = true)
    private File baseDirectory;

    private static final String BANNER_NAME = "banner.txt";

    private static final String DEFAULT_BANNER = "__                                                                                   __            _\n" +
            "   / /_  ____ _____  ____  ___  _____      ____ ___  ____ __   _____  ____        ____  / /_  ______ _(_)___\n" +
            "  / __ \\/ __ `/ __ \\/ __ \\/ _ \\/ ___/_____/ __ `__ \\/ __ `/ | / / _ \\/ __ \\______/ __ \\/ / / / / __ `/ / __ \\\n" +
            " / /_/ / /_/ / / / / / / /  __/ /  /_____/ / / / / / /_/ /| |/ /  __/ / / /_____/ /_/ / / /_/ / /_/ / / / / /\n" +
            "/_.___/\\__,_/_/ /_/_/ /_/\\___/_/        /_/ /_/ /_/\\__,_/ |___/\\___/_/ /_/     / .___/_/\\__,_/\\__, /_/_/ /_/\n" +
            "                                                                              /_/            /____/\n" +
            "build at = ${build.time}\n";

    public void execute() {
        String banner = readBanner();
        banner = handelBannerContent(banner);
        getLog().info(banner);
        writeBannerFile(banner);
    }

    private String handelBannerContent(String banner) {
        banner = banner.replace("${build.time}", new Date().toString());
        return banner;
    }

    private void writeBannerFile(String banner) {
        getLog().info("Write banner file to " + outputDirectory.getAbsolutePath());
        try {
            FileUtils.writeStringToFile(new File(outputDirectory.getAbsolutePath() + File.separator + BANNER_NAME), banner);
        } catch (IOException e) {
            getLog().error("Write banner file error :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String readBanner() {
        Collection<File> collection = FileUtils.listFiles(baseDirectory, new NameFileFilter(BANNER_NAME), null);
        Optional<File> first = collection.stream().findFirst();
        if (first.isPresent()) {
            File file = first.get();
            try {
                getLog().info("Found banner file " + file.getAbsolutePath());
                return FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            } catch (IOException e) {
                getLog().error("Read banner file error " + file.getAbsolutePath());
                throw new RuntimeException(e);
            }
        } else {
            getLog().info(String.format("File %s not found, use default banner", BANNER_NAME));
            return DEFAULT_BANNER;
        }
    }


}
