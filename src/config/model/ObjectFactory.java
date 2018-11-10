
package config.model;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the config.model_to_delete package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: config.model_to_delete
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DomainType }
     *
     */
    public DomainType createDomainType() {
        return new DomainType();
    }

    /**
     * Create an instance of {@link DomainType.CategoryMappingList }
     *
     */
    public DomainType.CategoryMappingList createDomainTypeCategoryMappingList() {
        return new DomainType.CategoryMappingList();
    }

    /**
     * Create an instance of {@link ParserConfig }
     *
     */
    public ParserConfig createParserConfig() {
        return new ParserConfig();
    }

    /**
     * Create an instance of {@link SignType }
     *
     */
    public SignType createSignType() {
        return new SignType();
    }

    /**
     * Create an instance of {@link DomainType.CategoryMappingList.CategoryMapping }
     *
     */
    public DomainType.CategoryMappingList.CategoryMapping createDomainTypeCategoryMappingListCategoryMapping() {
        return new DomainType.CategoryMappingList.CategoryMapping();
    }

}
