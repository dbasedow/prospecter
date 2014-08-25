package de.danielbasedow.prospecter.core.schema;

import de.danielbasedow.prospecter.core.*;
import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.document.DocumentBuilder;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.index.FieldIndex;
import de.danielbasedow.prospecter.core.persistence.QueryStorage;
import de.danielbasedow.prospecter.core.query.Query;
import de.danielbasedow.prospecter.core.query.QueryManager;
import de.danielbasedow.prospecter.core.query.build.QueryBuilder;
import gnu.trove.list.array.TLongArrayList;

/**
 * A schema represents a schema describing the available fields of a document. These fields are backed by indices that
 * are used to store queries on these fields. Schemas are the main component by which to use prospecter. They supply
 * everything else like DocumentBuilder and QueryBuilder.
 */
public interface Schema {
    /**
     * Add a new FieldIndex to the schema
     *
     * @param fieldName name of the field
     * @param index     index instance used to back this field
     */
    public void addFieldIndex(String fieldName, FieldIndex index) throws SchemaConfigurationError;

    /**
     * Get matches for a specific field. Useful for debugging index implementations.
     *
     * @param fieldIndexName name of the field
     * @param field          field instance from a Document
     * @return matching query postings
     * @throws UndefinedIndexFieldException
     */
    public TLongArrayList matchField(String fieldIndexName, Field field) throws UndefinedIndexFieldException;

    /**
     * Add query to index. The postings will be added to the corresponding field indices.
     *
     * @param query the parsed query
     * @throws UndefinedIndexFieldException
     */
    public void addQuery(Query query) throws UndefinedIndexFieldException;

    /**
     * Add query to index. The supplied String will be parsed as JSON and the resulting Query object is passed to
     * addQuery(Query query)
     *
     * @param json raw JSON string
     * @throws UndefinedIndexFieldException
     */
    public void addQuery(String json) throws UndefinedIndexFieldException, MalformedQueryException;

    /**
     * Collect all matches for a document
     *
     * @param doc     document instance
     * @param matcher an unused matcher
     * @return matcher
     */
    public Matcher matchDocument(Document doc, Matcher matcher);

    /**
     * Collect all matches from a document. A new Matcher instance is automatically created
     *
     * @param doc document instance
     * @return matcher
     */
    public Matcher matchDocument(Document doc);

    public int getFieldCount();

    public FieldIndex getFieldIndex(String name);

    public QueryBuilder getQueryBuilder();

    public DocumentBuilder getDocumentBuilder();

    public Matcher getMatcher();

    public QueryManager getQueryManager();

    public void setQueryStorage(QueryStorage queryStorage);

    /**
     * Tidy up before shutting down, close files and network connections
     */
    public void close();

    /**
     * execute any code that should run after the SchemaBuilder is finished. For example loading queries from disk
     */
    public void init();

    public void deleteQuery(Integer queryId);

    /**
     * optimize storage requirements
     */
    public void trim();
}
