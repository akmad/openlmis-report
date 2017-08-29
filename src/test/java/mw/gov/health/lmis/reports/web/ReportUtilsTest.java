package mw.gov.health.lmis.reports.web;

import mw.gov.health.lmis.reports.dto.external.RequisitionTemplateColumnDto;
import mw.gov.health.lmis.utils.ReportUtils;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static mw.gov.health.lmis.reports.dto.external.RequisitionLineItemDto.ADJUSTED_CONSUMPTION;
import static mw.gov.health.lmis.reports.dto.external.RequisitionLineItemDto.AVERAGE_CONSUMPTION;
import static mw.gov.health.lmis.reports.dto.external.RequisitionLineItemDto.BEGINNING_BALANCE;
import static net.sf.jasperreports.engine.JRParameter.REPORT_LOCALE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportUtilsTest {

  private static final int FIELD_ONE_WIDTH = 10;
  private static final int FIELD_TWO_WIDTH = 10;
  private static final int FIELD_THREE_WIDTH = 6;

  private static final int WIDTH_WITHOUT_MARGIN = 26;
  private static final int WIDTH = 30;
  private static final int MARGIN = 2;

  private static final int DELTA = 1000;


  @Test
  public void shouldCreateDefaultParametersMap() {
    // when
    Map<String, Object> map = ReportUtils.createParametersMap();

    // then
    String formatParam = "format";

    assertTrue(map.containsKey(formatParam));
    assertEquals(map.get(formatParam), "pdf");

    Locale currentLocale = LocaleContextHolder.getLocale();
    assertTrue(map.containsKey(REPORT_LOCALE));
    assertEquals(map.get(REPORT_LOCALE), currentLocale);
  }

  @Test
  public void shouldReturnSortedColumnsMap() {
    // given
    Map<String, RequisitionTemplateColumnDto> map = new HashMap<>();

    RequisitionTemplateColumnDto firstColumn = mock(RequisitionTemplateColumnDto.class);
    stubDisplay(firstColumn, 1);
    map.put("first", firstColumn);

    RequisitionTemplateColumnDto secondColumn = mock(RequisitionTemplateColumnDto.class);
    stubDisplay(secondColumn, 5);
    map.put("second", secondColumn);

    RequisitionTemplateColumnDto thirdColumn = mock(RequisitionTemplateColumnDto.class);
    stubDisplay(thirdColumn, 10);
    map.put("third", thirdColumn);

    // when
    Map<String, RequisitionTemplateColumnDto> result =
        ReportUtils.getSortedTemplateColumnsForPrint(map);

    // then
    assertEquals(map.size(), result.size());

    List<RequisitionTemplateColumnDto> columns = result.values().stream()
            .collect(Collectors.toList());
    assertEquals(columns.get(0), firstColumn);
    assertEquals(columns.get(1), secondColumn);
    assertEquals(columns.get(2), thirdColumn);
  }

  @Test
  public void shouldFilterOutSkippedColumn() {
    // given
    Map<String, RequisitionTemplateColumnDto> map = new HashMap<>();
    map.put("skipped", mock(RequisitionTemplateColumnDto.class));

    // when
    Map<String, RequisitionTemplateColumnDto> result =
        ReportUtils.getSortedTemplateColumnsForPrint(map);

    // then
    assertTrue(result.isEmpty());
  }

  @Test
  public void shouldSetProperFieldsWidthWhenSomeFieldNotInRequisitionTemplate() {
    // given
    JRBand jrBand = mock(JRBand.class);
    JRDesignTextField fieldOne = getField(ADJUSTED_CONSUMPTION, FIELD_ONE_WIDTH);
    JRDesignTextField fieldTwo = getField(AVERAGE_CONSUMPTION, FIELD_TWO_WIDTH);
    JRDesignTextField fieldThree = getField(BEGINNING_BALANCE, FIELD_THREE_WIDTH);

    stubJrFields(jrBand, fieldOne, fieldTwo, fieldThree);

    // when
    ReportUtils.customizeBandWithTemplateFields(jrBand,
        getReqTemplateColumnsMapWithoutFieldTwo(), WIDTH, MARGIN);

    // then
    assertEquals(WIDTH_WITHOUT_MARGIN, fieldOne.getWidth() + fieldThree.getWidth());
    assertEquals(getExpectedWidth(FIELD_ONE_WIDTH), fieldOne.getWidth(), DELTA);
    assertEquals(getExpectedWidth(FIELD_THREE_WIDTH), fieldThree.getWidth(), DELTA);
  }

  @Test
  public void shouldSetProperFieldsWidthWhenAllFieldsInRequisitionTemplate() {
    // given
    JRBand jrBand = mock(JRBand.class);
    JRDesignTextField fieldOne = getField(ADJUSTED_CONSUMPTION, FIELD_ONE_WIDTH);
    JRDesignTextField fieldTwo = getField(AVERAGE_CONSUMPTION, FIELD_TWO_WIDTH);
    JRDesignTextField fieldThree = getField(BEGINNING_BALANCE, FIELD_THREE_WIDTH);

    stubJrFields(jrBand, fieldOne, fieldTwo, fieldThree);

    // when
    ReportUtils.customizeBandWithTemplateFields(jrBand,
        getReqTemplateColumnsMapWithAllFields(), WIDTH, MARGIN);

    // then
    assertEquals(WIDTH_WITHOUT_MARGIN,
        fieldOne.getWidth() + fieldOne.getWidth() + fieldThree.getWidth());
    assertEquals(FIELD_ONE_WIDTH, fieldOne.getWidth(), DELTA);
    assertEquals(FIELD_TWO_WIDTH, fieldOne.getWidth(), DELTA);
    assertEquals(FIELD_THREE_WIDTH, fieldThree.getWidth(), DELTA);
  }

  private void stubDisplay(RequisitionTemplateColumnDto column, int displayOrder) {
    when(column.getDisplayOrder()).thenReturn(displayOrder);
    when(column.getIsDisplayed()).thenReturn(true);
  }

  private Map<String, RequisitionTemplateColumnDto> getReqTemplateColumnsMapWithAllFields() {
    Map<String, RequisitionTemplateColumnDto> columnMap = new LinkedHashMap<>();
    columnMap.put(ADJUSTED_CONSUMPTION, new RequisitionTemplateColumnDto());
    columnMap.put(BEGINNING_BALANCE, new RequisitionTemplateColumnDto());
    columnMap.put(AVERAGE_CONSUMPTION, new RequisitionTemplateColumnDto());
    return columnMap;
  }

  private Map<String, RequisitionTemplateColumnDto> getReqTemplateColumnsMapWithoutFieldTwo() {
    Map<String, RequisitionTemplateColumnDto> columnMap = getReqTemplateColumnsMapWithAllFields();
    columnMap.remove(AVERAGE_CONSUMPTION);
    return columnMap;
  }

  private JRDesignTextField getField(String columnName, int width) {
    JRDesignTextField field = new JRDesignTextField();
    field.setKey(columnName);
    field.setWidth(width);
    return field;
  }

  private void stubJrFields(JRBand jrBand, JRDesignTextField fieldOne, JRDesignTextField fieldTwo,
                            JRDesignTextField fieldThree) {
    LinkedList<JRChild> children = new LinkedList<>(Arrays.asList(fieldOne, fieldTwo, fieldThree));
    when(jrBand.getChildren()).thenReturn(children);
    when(jrBand.getElementByKey(ADJUSTED_CONSUMPTION)).thenReturn(fieldOne);
    when(jrBand.getElementByKey(AVERAGE_CONSUMPTION)).thenReturn(fieldTwo);
    when(jrBand.getElementByKey(BEGINNING_BALANCE)).thenReturn(fieldThree);
  }

  private double getExpectedWidth(int width) {
    return (double)width * WIDTH_WITHOUT_MARGIN / (WIDTH_WITHOUT_MARGIN - FIELD_TWO_WIDTH);
  }
}
