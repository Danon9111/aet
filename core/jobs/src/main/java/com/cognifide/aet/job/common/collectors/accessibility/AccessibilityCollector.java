/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.job.common.collectors.accessibility;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.osgi.framework.BundleContext;

public class AccessibilityCollector implements CollectorJob {

  private static final String DOCUMENT_OUTER_HTML_SCRIPT = "return document.documentElement.outerHTML;";
  public static final String NAME = "accessibility";
  private static final String PARAM_STANDARD = "standard";
  private static final String DEFAULT_STANDARD = "WCAG2AA";
  private final ArtifactsDAO artifactsDAO;
  private final WebDriver webDriver;
  private final BundleContext context;
  private final CollectorProperties properties;
  private String standard = DEFAULT_STANDARD;

  public AccessibilityCollector(ArtifactsDAO artifactsDAO, CollectorProperties collectorProperties,
      WebDriver webDriver, BundleContext context) {
    this.artifactsDAO = artifactsDAO;
    this.webDriver = webDriver;
    this.context = context;
    this.properties = collectorProperties;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    String script = getScriptFromFile();
    final String html = (String) ((JavascriptExecutor) webDriver)
        .executeScript(DOCUMENT_OUTER_HTML_SCRIPT);
    final String json = (String) ((JavascriptExecutor) webDriver).executeScript(script, standard);
    List<AccessibilityIssue> issues = parseIssues(json);
    getElementsPositions(issues, html);

    String resultId = artifactsDAO.saveArtifactInJsonFormat(properties, issues);

    return CollectorStepResult.newCollectedResult(resultId);
  }

  private String getScriptFromFile() throws ProcessingException {
    String script;
    try {
      URL entry = context.getBundle().getEntry("/collectors/accessibility/htmlcs.min.js");
      if (entry != null) {
        script = IOUtils.toString(entry.openStream(), Charsets.UTF_8);
      } else {
        throw new ProcessingException("Can't find accessibility htmlcs scripts in bundle context!");
      }
    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    }
    return script;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    if (params.containsKey(PARAM_STANDARD)) {
      standard = params.get(PARAM_STANDARD);
    }
  }

  private List<AccessibilityIssue> parseIssues(String json) {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(AccessibilityIssue.class, new AccessibilityIssueDeserializer())
        .create();
    Type list = new TypeToken<List<AccessibilityIssue>>() {
    }.getType();
    return gson.fromJson(json, list);
  }

  private void getElementsPositions(List<AccessibilityIssue> issues, final String html) {
    for (AccessibilityIssue issue : issues) {
      int indexOfElement = html.indexOf(issue.getElementString());
      if (indexOfElement >= 0) {
        String beforeOccurrence = html.substring(0, indexOfElement);
        int lineBreaks = StringUtils.countMatches(beforeOccurrence, "\n");
        int columnNumber;
        if (lineBreaks > 0) {
          int indexOfLastLineBreak = beforeOccurrence.lastIndexOf('\n');
          columnNumber = beforeOccurrence.substring(indexOfLastLineBreak).length();
        } else {
          columnNumber = beforeOccurrence.length();
        }
        issue.setLineNumber(lineBreaks + 1);
        issue.setColumnNumber(columnNumber);
      }
    }
  }

}
