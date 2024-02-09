package com.ramenreviewers.blog;

import com.ramenreviewers.blog.model.Review;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReviewYmlParser {

    private final static String REVIEW_FILE_NAME = "review.yaml";
    private final static String THUMBNAIL_FILE_NAME = "thumbnail.png";
    private final static String RESOURCE_PATH = "src/main/resources/reviews";

    public static Review parseReview(Path reviewDirectory) {
        try {
            // Read the YAML for the review data
            File reviewYamlFile = Paths.get(reviewDirectory.toString(), REVIEW_FILE_NAME).toFile();
            FileReader reader = new FileReader(reviewYamlFile);

            // create a review object by parsing the yaml
            var loaderOptions = new LoaderOptions();
            TagInspector tagInspector = tag -> tag.getClassName().equals(Review.class.getName());
            loaderOptions.setTagInspector(tagInspector);

            var yaml = new Yaml(new Constructor(Review.class, loaderOptions));
            var review = yaml.loadAs(reader, Review.class);

            // get all the images in the same directory
            var images = loadAllImagesInDirectory(parsedDirectoryPath);
            review.setPicturePath(images);

            return review;

        } catch (IOException e) {
            throw new RuntimeException("Error parsing the review in directory " + reviewDirectory, e);
        }
    }

    private static List<String> loadAllImagesInDirectory(String directory) {
        // set up a file filter to filter for files with the specified extensions
        final String[] Extensions = {".png", ".jpg"};
        final FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                for (final String ext : Extensions) {
                    if(name.endsWith(ext)) return true;
                }
                return false;
            }
        };

        // return all the files as list
        var dir = new File(directory);
        if(!dir.isDirectory()) return null;
        return Arrays.stream(Objects.requireNonNull(dir.list(filter)))
                .map(file -> directory + file).toList();
    }
}
