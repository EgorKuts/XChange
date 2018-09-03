
package org.knowm.xchange.kucoin.dto.trading;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "SELL",
    "BUY"
})
public class KucoinActiveOrdersInKv {

    @JsonProperty("SELL")
    private List<KucoinOrderDetails> sells = null;
    @JsonProperty("BUY")
    private List<KucoinOrderDetails> buys = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("SELL")
    public List<KucoinOrderDetails> getSELL() {
        return sells;
    }

    @JsonProperty("SELL")
    public void setSELL(List<KucoinOrderDetails> sELL) {
        this.sells = sELL;
    }

    @JsonProperty("BUY")
    public List<KucoinOrderDetails> getBUY() {
        return buys;
    }

    @JsonProperty("BUY")
    public void setBUY(List<KucoinOrderDetails> bUY) {
        this.buys = bUY;
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
