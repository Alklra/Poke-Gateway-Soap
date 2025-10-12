//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.10.12 a las 04:30:52 PM CST 
//


package com.pokeapi.application.soap.generated;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="heldItems" type="{http://www.pokegateway.com/soap/gen}heldItems"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "heldItems"
})
@XmlRootElement(name = "getHeldItemsResponse")
public class GetHeldItemsResponse {

    @XmlElement(required = true)
    protected HeldItems heldItems;

    /**
     * Obtiene el valor de la propiedad heldItems.
     * 
     * @return
     *     possible object is
     *     {@link HeldItems }
     *     
     */
    public HeldItems getHeldItems() {
        return heldItems;
    }

    /**
     * Define el valor de la propiedad heldItems.
     * 
     * @param value
     *     allowed object is
     *     {@link HeldItems }
     *     
     */
    public void setHeldItems(HeldItems value) {
        this.heldItems = value;
    }

}
