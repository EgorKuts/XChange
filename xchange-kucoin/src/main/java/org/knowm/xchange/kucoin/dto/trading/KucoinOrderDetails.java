
package org.knowm.xchange.kucoin.dto.trading;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.knowm.xchange.dto.Order;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "oid",
    "type",
    "userOid",
    "coinType",
    "coinTypePair",
    "direction",
    "price",
    "dealAmount",
    "pendingAmount",
    "createdAt",
    "updatedAt"
})
public class KucoinOrderDetails {

    @JsonProperty("oid")
    private String oid;
    @JsonProperty("type")
    private Order.OrderType type;
    @JsonProperty("userOid")
    private Object userOid;
    @JsonProperty("coinType")
    private String coinType;
    @JsonProperty("coinTypePair")
    private String coinTypePair;
    @JsonProperty("direction")
    private String direction;
    @JsonProperty("price")
    private BigDecimal price;
    @JsonProperty("dealAmount")
    private BigDecimal dealAmount;
    @JsonProperty("pendingAmount")
    private BigDecimal pendingAmount;
    @JsonProperty("createdAt")
    @JsonDeserialize(using = KucoinDateDeserializer.class)
    private Date createdAt;
    @JsonProperty("updatedAt")
    @JsonDeserialize(using = KucoinDateDeserializer.class)
    private Date updatedAt;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("oid")
    public String getOid() {
        return oid;
    }

    @JsonProperty("oid")
    public void setOid(String oid) {
        this.oid = oid;
    }

    @JsonProperty("userOid")
    public Object getUserOid() {
        return userOid;
    }

    @JsonProperty("userOid")
    public void setUserOid(Object userOid) {
        this.userOid = userOid;
    }

    @JsonProperty("coinType")
    public String getCoinType() {
        return coinType;
    }

    @JsonProperty("coinType")
    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    @JsonProperty("coinTypePair")
    public String getCoinTypePair() {
        return coinTypePair;
    }

    @JsonProperty("coinTypePair")
    public void setCoinTypePair(String coinTypePair) {
        this.coinTypePair = coinTypePair;
    }

    @JsonProperty("direction")
    public String getDirection() {
        return direction;
    }

    @JsonProperty("direction")
    public void setDirection(String direction) {
        this.direction = direction;
    }

    @JsonProperty("price")
    public BigDecimal getPrice() {
        return price;
    }

    @JsonProperty("price")
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @JsonProperty("dealAmount")
    public BigDecimal getDealAmount() {
        return dealAmount;
    }

    @JsonProperty("dealAmount")
    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }

    @JsonProperty("pendingAmount")
    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }

    @JsonProperty("pendingAmount")
    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    @JsonProperty("createdAt")
    @JsonDeserialize(using = KucoinDateDeserializer.class)
    public Date getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("createdAt")
    @JsonDeserialize(using = KucoinDateDeserializer.class)
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updatedAt")
    @JsonDeserialize(using = KucoinDateDeserializer.class)
    public Date getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updatedAt")
    @JsonDeserialize(using = KucoinDateDeserializer.class)
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
