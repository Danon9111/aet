/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.worker.drivers.chrome;

import com.cognifide.aet.job.api.collector.HttpRequestBuilder;
import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nullable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

public class ChromeCommunicationWrapperImpl implements WebCommunicationWrapper {

  private static final LogEntryToJsError LOG_ENTRY_TO_JS_ERROR = new LogEntryToJsError();
  private static final Predicate<LogEntry> ONLY_SEVERE_ERRORS = new Predicate<LogEntry>() {
    @Override
    public boolean apply(LogEntry input) {
      return Level.SEVERE.equals(input.getLevel());
    }
  };
  private final WebDriver webDriver;

  private final ProxyServerWrapper proxyServer;

  private final HttpRequestBuilder builder;

  public ChromeCommunicationWrapperImpl(WebDriver webDriver, ProxyServerWrapper server,
      HttpRequestBuilder builder) {
    this.webDriver = webDriver;
    this.proxyServer = server;
    this.builder = builder;
  }

  @Override
  public WebDriver getWebDriver() {
    return webDriver;
  }

  @Override
  public ProxyServerWrapper getProxyServer() {
    return proxyServer;
  }

  @Override
  public boolean isUseProxy() {
    return proxyServer != null;
  }

  @Override
  public Set<JsErrorLog> getJSErrorLogs() {
    LogEntries logEntries = webDriver.manage().logs().get(LogType.BROWSER);
    return FluentIterable.from(logEntries)
        .filter(ONLY_SEVERE_ERRORS)
        .transform(LOG_ENTRY_TO_JS_ERROR)
        .toSortedSet(Ordering.natural());
  }

  @Override
  public HttpRequestBuilder getHttpRequestBuilder() {
    return builder;
  }

}
