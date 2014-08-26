package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.query.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.array.TLongArrayList;

/**
 * Interface representing an index for a field encountered in queries and documents. The data types and methods for
 * matching vary from index type to index type.
 */
public interface FieldIndex {
    public String getName();

    /**
     * Finds all QueryPosting that match the given Field and returns them as a List
     *
     * @param field Field instance from Document to match against
     * @return query postings matching field
     */
    public TLongArrayList match(Field field);

    /**
     * Add a single QueryPosting that will be matched if token is present in the field in match()
     *
     * @param token   Token to match on later on
     * @param posting query posting
     */
    public void addPosting(Token token, Long posting);

    /**
     * Get FieldType of this FieldIndex
     *
     * @return type of this field
     */
    public FieldType getFieldType();

    public void trim();
}
