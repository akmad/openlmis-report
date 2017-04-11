package mw.gov.health.lmis.reports.domain;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Defines a parameter of Jasper Template, meant to be passed on printing.
 * <p>
 * selectExpression is used to indicate how the parameter values should be retrieved (API path).
 * selectProperty indicates which property of objects should be passed as parameter (ex. ID).
 * displayProperty is used to indicate which property of object should be displayed for choice.
 * </p>
 */
@Entity
@Table(name = "template_parameters")
@NoArgsConstructor
public class JasperTemplateParameter extends BaseEntity {

  @ManyToOne(cascade = CascadeType.REFRESH)
  @JoinColumn(name = "templateId", nullable = false)
  @Getter
  @Setter
  private JasperTemplate template;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String name;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String displayName;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String defaultValue;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String dataType;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String selectExpression;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String selectProperty;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String displayProperty;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String description;

  @Column(nullable = false)
  @Getter
  @Setter
  private Boolean required;

  /**
   * Create new instance of JasperTemplateParameter based on given
   * {@link Importer}
   *
   * @param importer instance of {@link Importer}
   * @return instance of JasperTemplateParameter.
   */
  public static JasperTemplateParameter newInstance(Importer importer) {
    JasperTemplateParameter jasperTemplateParameter = new JasperTemplateParameter();

    jasperTemplateParameter.setId(importer.getId());
    jasperTemplateParameter.setName(importer.getName());
    jasperTemplateParameter.setDisplayName(importer.getDisplayName());
    jasperTemplateParameter.setDefaultValue(importer.getDefaultValue());
    jasperTemplateParameter.setSelectExpression(importer.getSelectExpression());
    jasperTemplateParameter.setDescription(importer.getDescription());
    jasperTemplateParameter.setDataType(importer.getDataType());
    jasperTemplateParameter.setSelectProperty(importer.getSelectProperty());
    jasperTemplateParameter.setDisplayProperty(importer.getDisplayProperty());
    jasperTemplateParameter.setRequired(importer.getRequired());

    return jasperTemplateParameter;
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setId(id);
    exporter.setName(name);
    exporter.setDescription(description);
    exporter.setDataType(dataType);
    exporter.setDefaultValue(defaultValue);
    exporter.setDisplayName(displayName);
    exporter.setSelectExpression(selectExpression);
    exporter.setSelectProperty(selectProperty);
    exporter.setDisplayProperty(displayProperty);
    exporter.setRequired(required);
  }

  public interface Exporter {
    void setId(UUID id);

    void setName(String name);

    void setDisplayName(String displayName);

    void setDefaultValue(String defaultValue);

    void setDataType(String dataType);

    void setSelectExpression(String selectExpression);

    void setDescription(String description);

    void setSelectProperty(String selectProperty);

    void setDisplayProperty(String displayProperty);

    void setRequired(Boolean required);

  }

  public interface Importer {
    UUID getId();

    String getName();

    String getDisplayName();

    String getDefaultValue();

    String getDataType();

    String getSelectExpression();

    String getDescription();

    String getSelectProperty();

    String getDisplayProperty();

    Boolean getRequired();

  }
}
