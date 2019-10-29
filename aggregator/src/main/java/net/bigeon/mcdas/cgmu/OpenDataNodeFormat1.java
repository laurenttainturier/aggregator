package net.bigeon.mcdas.cgmu;

import org.codehaus.jackson.annotate.JsonProperty;

/** A CGMU open data node.
 *
 * @author Emmanuel Bigeon */
public class OpenDataNodeFormat1 {
    private String format;
    private String description;
    private String CreateUtc;
    private String ExpireUtc;
    private String unit;
    private String status;
    private double value;

    public OpenDataNodeFormat1() {
    }

    public OpenDataNodeFormat1(String format, String description, String createUTC,
                               String expireUTC, String unit, String status,
                               double value) {
        super();
        this.format = format;
        this.description = description;
        this.CreateUtc = createUTC;
        this.ExpireUtc = expireUTC;
        this.unit = unit;
        this.status = status;
        this.value = value;
    }

    @JsonProperty("CreateUtc")
    public String getCreateUtc() {
        return CreateUtc;
    }

    @JsonProperty("Desc")
    public String getDescription() {
        return description;
    }

    @JsonProperty("ExpiryUtc")
    public String getExpireUtc() {
        return ExpireUtc;
    }

    @JsonProperty("Format")
    public String getFormat() {
        return format;
    }

    @JsonProperty("Status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("Unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty("Value")
    public double getValue() {
        return value;
    }

    @JsonProperty("CreateUtc")
    public void setCreateUtc(String CreateUtc) {
        this.CreateUtc = CreateUtc;
    }

    @JsonProperty("Desc")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("ExpiryUtc")
    public void setExpireUtc(String ExpireUtc) {
        this.ExpireUtc = ExpireUtc;
    }

    @JsonProperty("Format")
    public void setFormat(String format) {
        this.format = format;
    }

    @JsonProperty("Status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("Unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @JsonProperty("Value")
    public void setValue(double value) {
        this.value = value;
    }
}
