/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.ChainedFilter;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.AnalyzerType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.analyzer.IAViewTextCasNoPuncAnalyser;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.analyzer.IAViewTextCasPuncAnalyser;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.analyzer.IAViewTextGenAnalyser;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.analyzer.IAViewTextNoCasNoPuncAnalyser;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration dedicated to Lucene:<br/>
 * provides all necessary beans (directory, reader, etc)
 * 
 * @author jcharlet
 *
 */
@Configuration
@ConfigurationProperties(prefix = "lucene.index")
@EnableConfigurationProperties
public class LuceneIAViewConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(LuceneIAViewConfiguration.class);

    private String iaviewCollectionPath;
    private String version;
    private double iaViewMaxMergeSizeMB;
    private double iaViewMaxCachedMB;

    private String queryFilterSourceValues;

    private String defaultTaxonomyField;

    @PostConstruct
    public void init() {
	BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
    }

    /**
     ************************* IA Views
     */

    public @Bean Directory iaViewDirectory() throws IOException {
        logger.info("lucene index location: {}", iaviewCollectionPath );
	Directory fsDir = FSDirectory.open(new File(iaviewCollectionPath));
	return new NRTCachingDirectory(fsDir, iaViewMaxMergeSizeMB, iaViewMaxCachedMB);
    }

    public @Bean IndexReader iaViewIndexReader() throws IOException {
	return DirectoryReader.open(iaViewDirectory());
    }

    public @Bean IndexSearcher iaviewSearcher() throws IOException {
	return new IndexSearcher(iaViewIndexReader());
    }

    public @Bean SearcherManager iaviewSearcherManager() throws IOException {
	return new SearcherManager(iaViewDirectory(), null);
    }

    /**
     ************************* FilterFactories and Analyzers
     */

    public @Bean StopFilterFactory stopFilterFactory() {
	Map<String, String> stopFilterArgs = new HashMap<String, String>();
	stopFilterArgs.put("words", "stopwords.txt");
	stopFilterArgs.put("enablePositionIncrements", "true");
	stopFilterArgs.put("luceneMatchVersion", version);

	StopFilterFactory stopFilterFactory = new StopFilterFactory(stopFilterArgs);

	try {
	    ResourceLoader loader = new ClasspathResourceLoader(getClass());
	    stopFilterFactory.inform(loader);
	} catch (IOException e) {
	    logger.error(".stopFilterFactory: an error occured while creating the Filter factory: {}", e.getMessage());
	}
	return stopFilterFactory;
    }

    public @Bean SynonymFilterFactory synonymFilterFactory() {
	Map<String, String> synonymFilterArgs = new HashMap<String, String>();
	synonymFilterArgs.put("synonyms", "synonyms.txt");
	synonymFilterArgs.put("expand", "true");
	synonymFilterArgs.put("ignoreCase", "true");
	synonymFilterArgs.put("luceneMatchVersion", version);
	SynonymFilterFactory synonymFilterFactory = new SynonymFilterFactory(synonymFilterArgs);

	try {
	    ResourceLoader loader = new ClasspathResourceLoader(getClass());
	    synonymFilterFactory.inform(loader);
	} catch (IOException e) {
	    logger.error(".synonymFilterFactory: an error occured while creating the Filter factory: {}",
		    e.getMessage());
	}
	return synonymFilterFactory;

    }

    public @Bean WordDelimiterFilterFactory wordDelimiterFilterFactory() {
	Map<String, String> wordDelimiterFilterArgs = new HashMap<String, String>();
	wordDelimiterFilterArgs.put("preserveOriginal", "1");
	wordDelimiterFilterArgs.put("generateWordParts", "1");
	wordDelimiterFilterArgs.put("catenateWords", "1");
	wordDelimiterFilterArgs.put("luceneMatchVersion", version);
	WordDelimiterFilterFactory wordDelimiterFilterFactory = new WordDelimiterFilterFactory(wordDelimiterFilterArgs);

	try {
	    ResourceLoader loader = new ClasspathResourceLoader(getClass());
	    wordDelimiterFilterFactory.inform(loader);
	} catch (IOException e) {
	    logger.error(".wordDelimiterFilterFactory: an error occured while creating the Filter factory: {}",
		    e.getMessage());
	}
	return wordDelimiterFilterFactory;

    }

    /**
     * Analyzer dedicated to running the category queries and finding documents
     * in IAView Index
     * 
     * @return
     * @throws ParseException
     */
    public @Bean Analyzer iaViewSearchAnalyser() throws ParseException {
	Map<String, Analyzer> mapOfAnalyzerPerField = getMapOfAnalyzersForAnalyzerType(AnalyzerType.QUERY);

	PerFieldAnalyzerWrapper iaViewSearchAnalyser = new PerFieldAnalyzerWrapper(
		mapOfAnalyzerPerField.get(defaultTaxonomyField), mapOfAnalyzerPerField);

	return iaViewSearchAnalyser;
    }

    /**
     * Analyzer dedicated to indexing a document to categorise in memory
     * database
     * 
     * @return
     * @throws ParseException
     */
    public @Bean Analyzer iaViewIndexAnalyser() throws ParseException {
	Map<String, Analyzer> mapOfAnalyzerPerField = getMapOfAnalyzersForAnalyzerType(AnalyzerType.INDEX);

	PerFieldAnalyzerWrapper iaViewIndexAnalyser = new PerFieldAnalyzerWrapper(
		mapOfAnalyzerPerField.get(defaultTaxonomyField), mapOfAnalyzerPerField);

	return iaViewIndexAnalyser;

    }

    private Map<String, Analyzer> getMapOfAnalyzersForAnalyzerType(AnalyzerType query) throws ParseException {
	Map<String, Analyzer> mapOfAnalyzerPerField = new HashMap<String, Analyzer>();

	IAViewTextGenAnalyser textGenAnalyser = new IAViewTextGenAnalyser(Version.parseLeniently(version),
		synonymFilterFactory(), wordDelimiterFilterFactory(), query);
	textGenAnalyser.setPositionIncrementGap(100);
	mapOfAnalyzerPerField.put(InformationAssetViewFields.CATDOCREF.toString(), textGenAnalyser);
	mapOfAnalyzerPerField.put(InformationAssetViewFields.DESCRIPTION.toString(), textGenAnalyser);
	mapOfAnalyzerPerField.put(InformationAssetViewFields.TITLE.toString(), textGenAnalyser);

	IAViewTextNoCasNoPuncAnalyser textNoCasNoPuncAnalyser = new IAViewTextNoCasNoPuncAnalyser(
		Version.parseLeniently(version), synonymFilterFactory(), wordDelimiterFilterFactory(), query);
	textNoCasNoPuncAnalyser.setPositionIncrementGap(100);
	mapOfAnalyzerPerField.put(InformationAssetViewFields.textnocasnopunc.toString(), textNoCasNoPuncAnalyser);

	IAViewTextCasNoPuncAnalyser textCasNoPuncAnalyser = new IAViewTextCasNoPuncAnalyser(
		Version.parseLeniently(version), synonymFilterFactory(), wordDelimiterFilterFactory(), query);
	textCasNoPuncAnalyser.setPositionIncrementGap(100);
	mapOfAnalyzerPerField.put(InformationAssetViewFields.textcasnopunc.toString(), textCasNoPuncAnalyser);

	IAViewTextCasPuncAnalyser textCasPuncAnalyser = new IAViewTextCasPuncAnalyser(stopFilterFactory(),
		synonymFilterFactory(), query);
	textCasPuncAnalyser.setPositionIncrementGap(100);
	mapOfAnalyzerPerField.put(InformationAssetViewFields.textcaspunc.toString(), textCasPuncAnalyser);
	return mapOfAnalyzerPerField;
    }

    /**
     * Filters
     */

    /**
     * w
     * 
     * @return
     */
    public @Bean Filter catalogueFilter() {
	if (!StringUtils.isEmpty(queryFilterSourceValues)) {

	    String[] arrayOfSourceValues = queryFilterSourceValues.split(",");

	    if (arrayOfSourceValues.length == 1) {
		return getSourceFilterForValue(arrayOfSourceValues[0]);
	    } else {
		return getChainOfSourceFiltersForValues(arrayOfSourceValues);
	    }
	}
	return null;
    }

    private Filter getChainOfSourceFiltersForValues(String[] arrayOfSourceValues) {
	List<Filter> chainOfFilters = new ArrayList<Filter>();
	for (String sourceValue : arrayOfSourceValues) {
	    TermFilter sourceFilter = getSourceFilterForValue(sourceValue);
	    chainOfFilters.add(sourceFilter);
	}
	return new ChainedFilter(chainOfFilters.toArray(new Filter[0]), ChainedFilter.OR);
    }

    private TermFilter getSourceFilterForValue(String sourceValue) {
	Integer intCatalogueSourceValue = Integer.valueOf(sourceValue);
	BytesRefBuilder bytes = new BytesRefBuilder();
	NumericUtils.intToPrefixCoded(intCatalogueSourceValue, 0, bytes);
	TermFilter termFilter = new TermFilter(new Term(InformationAssetViewFields.SOURCE.toString(), bytes.get()));
	return termFilter;
    }

    /**
     ************************* Setters
     */

    public void setIaviewCollectionPath(String iaviewCollectionPath) {
	this.iaviewCollectionPath = iaviewCollectionPath;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    public void setIaViewMaxMergeSizeMB(double iaViewMaxMergeSizeMB) {
	this.iaViewMaxMergeSizeMB = iaViewMaxMergeSizeMB;
    }

    public void setIaViewMaxCachedMB(double iaViewMaxCachedMB) {
	this.iaViewMaxCachedMB = iaViewMaxCachedMB;
    }

    public void setQueryFilterSourceValues(String queryFilterSourceValues) {
	this.queryFilterSourceValues = queryFilterSourceValues;
    }

    public void setDefaultTaxonomyField(String defaultTaxonomyField) {
	this.defaultTaxonomyField = defaultTaxonomyField;
    }

}