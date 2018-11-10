
package config.model;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for SignType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BeginSign" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="EndSign" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignType", namespace = "www.ParserConfig.com", propOrder = {
    "beginSign",
    "endSign"
})
public class SignType {

    @XmlElement(name = "BeginSign", namespace = "www.ParserConfig.com", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String beginSign;
    @XmlElement(name = "EndSign", namespace = "www.ParserConfig.com", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String endSign;

    /**
     * Gets the value of the beginSign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeginSign() {
        return beginSign;
    }

    /**
     * Sets the value of the beginSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeginSign(String value) {
        this.beginSign = value;
    }

    /**
     * Gets the value of the endSign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndSign() {
        return endSign;
    }

    /**
     * Sets the value of the endSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndSign(String value) {
        this.endSign = value;
    }

    @Override
    public String toString() {
        return "SignType{" +
                "beginSign='" + beginSign + '\'' +
                ", endSign='" + endSign + '\'' +
                '}';
    }
}
