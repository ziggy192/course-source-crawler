
package config.model;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Edumall" type="{www.ParserConfig.com}DomainType"/>
 *         &lt;element name="Unica" type="{www.ParserConfig.com}DomainType"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "ParserConfig", namespace = "www.ParserConfig.com")
public class ParserConfig {

	@XmlElement(name = "Edumall", namespace = "www.ParserConfig.com", required = true)
	protected DomainType edumall;
	@XmlElement(name = "Unica", namespace = "www.ParserConfig.com", required = true)
	protected DomainType unica;
	@XmlElement(name = "KhoaHocOnline", namespace = "www.ParserConfig.com", required = true)
	protected DomainType khoaHocOnline;
	@XmlElement(name = "Emoon", namespace = "www.ParserConfig.com", required = true)
	protected DomainType emoon;
	@XmlElement(name = "TuyenSinh247", namespace = "www.ParserConfig.com", required = true)
	protected DomainType tuyenSinh247;


	/**
	 * Gets the value of the edumall property.
	 *
	 * @return possible object is
	 * {@link DomainType }
	 */
	public DomainType getEdumall() {
		return edumall;
	}

	/**
	 * Sets the value of the edumall property.
	 *
	 * @param value allowed object is
	 *              {@link DomainType }
	 */
	public void setEdumall(DomainType value) {
		this.edumall = value;
	}

	/**
	 * Gets the value of the unica property.
	 *
	 * @return possible object is
	 * {@link DomainType }
	 */
	public DomainType getUnica() {
		return unica;
	}

	/**
	 * Sets the value of the unica property.
	 *
	 * @param value allowed object is
	 *              {@link DomainType }
	 */
	public void setUnica(DomainType value) {
		this.unica = value;
	}

	/**
	 * Gets the value of the khoahoconline property.
	 *
	 * @return possible object is
	 * {@link DomainType }
	 */
	public DomainType getKhoaHocOnline() {
		return khoaHocOnline;
	}


	/**
	 * Sets the value of the khoaHocOnline property.
	 *
	 * @param value allowed object is
	 *              {@link DomainType }
	 */
	public void setKhoaHocOnline(DomainType value) {


		this.khoaHocOnline = value;
	}

	public DomainType getEmoon() {
		return emoon;
	}

	public void setEmoon(DomainType emoon) {
		this.emoon = emoon;
	}


	public DomainType getTuyenSinh247() {
		return tuyenSinh247;
	}

	public void setTuyenSinh247(DomainType tuyenSinh247) {
		this.tuyenSinh247 = tuyenSinh247;
	}

	@Override
	public String toString() {
		return "ParserConfig{" +
				"edumall=" + edumall +
				", unica=" + unica +
				", khoaHocOnline=" + khoaHocOnline +
				", emoon=" + emoon +
				'}';
	}
}
