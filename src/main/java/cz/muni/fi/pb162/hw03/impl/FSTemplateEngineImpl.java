package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.template.FSTemplateEngine;
import cz.muni.fi.pb162.hw03.template.TemplateEngine;
import cz.muni.fi.pb162.hw03.template.TemplateException;
import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Ladislav Husty
 */
public class FSTemplateEngineImpl extends TemplateEngineImpl implements FSTemplateEngine {
    /**
     * Load template from given file -- using filename without {@code ext}
     * as template name
     * <p>
     * Given {code "tpl"} as value of parameter {@code ext}
     * and file with absolute path /main/templates/resume.html.tpl
     * Then the name of the stored raw template will be "resume.html"
     *
     * @param file input file
     * @param cs   file charset encoding
     * @param ext  extension stripped from the filename
     */
    @Override
    public void loadTemplate(Path file, Charset cs, String ext) {
        String fileName = file.getFileName().toString();
        String templateName = fileName.substring(0, fileName.lastIndexOf("." + ext));
        try (BufferedReader reader = Files.newBufferedReader(file, cs)) {
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append(System.lineSeparator());
            }
            String content = fileContent.toString();
            this.loadTemplate(templateName, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load templates from files in given directory.
     * The method is not recursive.
     *
     * @param inDir input directory
     * @param cs    file charset encoding
     * @param ext   suffix stripped from filename
     */

    @Override
    public void loadTemplateDir(Path inDir, Charset cs, String ext) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(inDir)) {
            List<Path> fileNames = new ArrayList<>();
            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    this.loadTemplate(path, cs, ext);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Evaluates template with given model and writes it into files
     *
     * @param name  template name
     * @param model model used for evaluation
     * @param file  output file
     * @param cs    file charset encoding
     * @throws TemplateException if there is no loaded template with given name
     */
    @Override
    public void writeTemplate(String name, TemplateModel model, Path file, Charset cs) throws TemplateException {
        String evaluatedTemplate = this.evaluateTemplate(name, model);

        try (BufferedWriter writer = Files.newBufferedWriter(file, cs)) {
            String[] lines = evaluatedTemplate.split(System.lineSeparator());
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException te){
            throw new TemplateException("No template found with name: " + name);
        }
    }


    /**
     * Evaluates all loaded templates with given model and writes
     * them into output directory (name of the template is
     * used as filename)
     *
     * @param model  model used for evaluation
     * @param outDir output directory
     * @param cs     file charset encoding
     */
    @Override
    public void writeTemplates(TemplateModel model, Path outDir, Charset cs) {
        TemplateEngine templateEngine = new TemplateEngineImpl();
        Collection<String> templateNames = templateEngine.getTemplateNames();
        templateNames.forEach(templateName -> writeTemplate(templateName, model, outDir, cs));
    }
}
