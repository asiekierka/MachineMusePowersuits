package numina.client.util.lang;

import lehjr.numina.common.base.NuminaLogger;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import numina.client.config.DatagenConfig;
import numina.client.util.ResourceList;
import numina.client.util.lang.translators.ITranslator;
import numina.client.util.lang.translators.Language;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class MulitiLanguageProvider implements DataProvider {
    public TranslationMap translationMap;
    Map<Language, LanguageProviderHelper> languageProviderMap;
    DatagenConfig config;

//    Map<Language, Map<String, String>> overrides = new HashMap<>();
    Map<Language, LangMapWrapper> langMapWrappers = new HashMap<>();
    ITranslator translator;

    LanguageProviderHelper mainProvider;


    public MulitiLanguageProvider(DataGenerator gen, String modid, DatagenConfig config, String root, ITranslator translator) {
        this.config = config;
        languageProviderMap = new HashMap<>();
        List<Language> targetLanguages = config.getLanguageCodesUsed();
        targetLanguages.forEach(locale -> languageProviderMap.put(locale, new LanguageProviderHelper(gen, modid, locale, config)));
        languageProviderMap.put(config.getMainLanguageCode(), new LanguageProviderHelper(gen, modid, config.getMainLanguageCode(), config));
        translationMap = new TranslationMap();
        this.translator = translator;

        // load existing language files
        if (!root.isBlank()) {
            Path src = gen.getOutputFolder().getParent().getParent();
            File langFolder = src.resolve(root).resolve("resources/assets").resolve(modid).resolve("lang").toFile();
            NuminaLogger.logDebug("source folder: " +langFolder);

            if (!langFolder.exists()) {
                langFolder.mkdirs();
            }

            if (langFolder.exists() && langFolder.isDirectory()) {
                ArrayList<File> files = ResourceList.getResourcesFromDirectory(langFolder, Pattern.compile(".json", Pattern.CASE_INSENSITIVE));
                Language sourceLanguage = config.getMainLanguageCode();
                // setup overrides
                files.forEach(file -> {
                    String mc_code = FilenameUtils.getBaseName(file.getName());
                    Language lang = config.getLanguageFromMC_Code(mc_code);
                    langMapWrappers.put(lang, new LangMapWrapper(file));
                });

                // create new target language translation holder if it doesn't already exist
                targetLanguages.stream().filter(language -> language!= sourceLanguage).forEach(language -> {
                    if (!langMapWrappers.containsKey(language)) {
                        langMapWrappers.put(language, new LangMapWrapper(langFolder, language));
                    }
                });

                NuminaLogger.logDebug("modID: " + modid + ", output folder: " + gen.getOutputFolder());
//                langMapWrappers.forEach(wrapper -> wrapper.savetoOutputFolder(cache, gen.getOutputFolder().resolve("assets/" + modid + "/src/datagen/powersuits/resources/assets/lang/")));
            } else {
                NuminaLogger.logDebug("lang folder not found !!!:");
            }
        }
    }

    /**
     * extend this and populate translationMap first before calling this
     * @param pOutput
     * @throws IOException
     */
    @Override
    public void run(CachedOutput pOutput) throws IOException {
        Language sourceLanguage = config.getMainLanguageCode();
        Map<String, String> sourceMap = translationMap.getTranslationsForLocale(sourceLanguage);
//        sourceMap.forEach((key, value)-> mainProvider.add(key, value));
//        mainProvider.run(pOutput);

        // parse source map and fill in missing key,value pairs (there will be many, especially on the first run)
        List<Language> languageList = config.getLanguageCodesUsed();
        sourceMap.forEach((translationKey, value)->{
            translator.setInputLanguage(sourceLanguage);
            Map<Language, String> translations = translationMap.getTranslationsForKey(translationKey);
            languageList.forEach(lang -> {

                String translatedString = "";

                if (langMapWrappers.containsKey(lang)) {
                    translatedString = langMapWrappers.get(lang).getTranslation(translationKey);
                }

                if (translatedString.isBlank() && translations.containsKey(lang)) {
                    translatedString = translations.getOrDefault(lang, "");
                }

                if (translatedString.isBlank()) {
                    NuminaLogger.logDebug("translationKey: " + translationKey + ", language: " + lang.mc_label());
                    String inputString = sourceMap.getOrDefault(translationKey, "");
                    NuminaLogger.logDebug("inputString: " + inputString);
                    if (!inputString.isBlank()) {
                        translator.setInputString(inputString);
                        translator.setOutputLanguage(lang);
                        translatedString = translator.getOutputText();
                        if (langMapWrappers.containsKey(lang)) {
                            langMapWrappers.get(lang).addTranslation(translationKey, translatedString);
                        }
                    }
                }
            });
        });
        NuminaLogger.logDebug("finished translationg");

        // finally generate the output files
        languageProviderMap.forEach((locale, provider) -> {
                try {
                    if (locale != sourceLanguage) {
                        NuminaLogger.logDebug("locale: " + locale.mc_label());
                        provider.setTranslations(translationMap.getTranslationsForLocale(locale));
                        if (langMapWrappers.containsKey(locale)) {
                            Map<String, String> langMapTmp = langMapWrappers.get(locale).getData();
                            Map<String, String> langMap = new HashMap<>();
                            sourceMap.keySet().stream().filter(langKey -> !langMapTmp.getOrDefault(langKey, "").isBlank()).toList().forEach(langKey -> langMap.put(langKey, langMap.get(langKey)));
                            provider.setTranslations(langMap);
                        }
                    } else {
                        provider.setTranslations(sourceMap);
                    }
                    provider.run(pOutput);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        });
    }

    public Language mcCodeToLang(String mc_code) {
        return config.getLanguageFromMC_Code(mc_code);
    }

    public Map<String, String> getTranslationsForLanguage(Language language) {
        return translationMap.getTranslationsForLocale(language);
    }

    public void add(String translationKey, Language language, String translation) {
        translationMap.addTranslation(translationKey, language, translation);
    }

    public void add(Item key, Language language, String translation) {
        add(key.getDescriptionId(), language, translation);
    }

    public void addItemDescriptions(Item key, Language language, String translation) {
        add(new StringBuilder(key.getDescriptionId()).append(".desc").toString(), language, translation);
    }

    public void add(Supplier<? extends EntityType<?>> key, Language language, String translation) {
        add(key.get(), language, translation);
    }

    public void add(EntityType<?> key, Language language, String translation) {
        add(key.getDescriptionId(), language, translation);
    }

    public void add(Block key, Language language, String translation) {
        add(key.getDescriptionId(), language, translation);
    }

    public void addTranslationTopAll(String key, String value) {
        config.getLanguageCodesUsed().forEach(language-> add(key, language, value));
        add(key, config.getMainLanguageCode(), value);
    }

    @Override
    public String getName() {
        return "MultiLanguageProvider";
    }
}
